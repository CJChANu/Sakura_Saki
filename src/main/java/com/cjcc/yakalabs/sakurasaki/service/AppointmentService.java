package com.cjcc.yakalabs.sakurasaki.service;

import com.cjcc.yakalabs.sakurasaki.model.*;
import com.cjcc.yakalabs.sakurasaki.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepo;
    private final CustomerRepository customerRepo;
    private final SalonServiceRepository serviceRepo;
    private final StaffRepository staffRepo;

    public AppointmentService(AppointmentRepository appointmentRepo,
                              CustomerRepository customerRepo,
                              SalonServiceRepository serviceRepo,
                              StaffRepository staffRepo) {
        this.appointmentRepo = appointmentRepo;
        this.customerRepo = customerRepo;
        this.serviceRepo = serviceRepo;
        this.staffRepo = staffRepo;
    }

    /**
     * Create a new appointment with double-booking prevention.
     */
    public Appointment createAppointment(Long customerId, Long serviceId, Long staffId,
                                         LocalDate date, LocalTime time, String notes) {
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        SalonService service = serviceRepo.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found"));
        Staff staff = staffRepo.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        // Double-booking prevention: check for time overlap
        if (!isStaffAvailable(staffId, date, time, service.getDurationMinutes())) {
            throw new RuntimeException("This staff member is already booked during that time slot. Please choose a different time or staff member.");
        }

        Appointment appointment = new Appointment(customer, service, staff, date, time);
        appointment.setNotes(notes);
        return appointmentRepo.save(appointment);
    }

    /**
     * Check if a staff member is available at the given date/time for the given duration.
     * Uses overlap detection: new appointment [startTime, endTime) must not overlap
     * with any existing appointment's [existingStart, existingEnd).
     */
    public boolean isStaffAvailable(Long staffId, LocalDate date, LocalTime startTime, int durationMinutes) {
        LocalTime endTime = startTime.plusMinutes(durationMinutes);

        // Get all non-cancelled appointments for this staff on this date
        List<Appointment> existing = appointmentRepo.findByStaffIdAndAppointmentDate(staffId, date);

        for (Appointment a : existing) {
            if ("CANCELLED".equals(a.getStatus())) continue;

            LocalTime existingStart = a.getAppointmentTime();
            LocalTime existingEnd = existingStart.plusMinutes(a.getService().getDurationMinutes());

            // Overlap check: two intervals overlap if start1 < end2 AND start2 < end1
            if (startTime.isBefore(existingEnd) && existingStart.isBefore(endTime)) {
                return false;
            }
        }
        return true;
    }

    public Optional<Appointment> findById(Long id) {
        return appointmentRepo.findById(id);
    }

    public List<Appointment> findAll() {
        return appointmentRepo.findAll();
    }

    public List<Appointment> findByCustomer(Long customerId) {
        return appointmentRepo.findByCustomerIdOrderByAppointmentDateDesc(customerId);
    }

    public List<Appointment> findByStaff(Long staffId) {
        return appointmentRepo.findByStaffId(staffId);
    }

    public List<Appointment> findByDate(LocalDate date) {
        return appointmentRepo.findByAppointmentDate(date);
    }

    public List<Appointment> findByStatus(String status) {
        return appointmentRepo.findByStatus(status);
    }

    /**
     * Reschedule an appointment with conflict checking.
     */
    public Appointment reschedule(Long id, LocalDate newDate, LocalTime newTime) {
        Appointment a = appointmentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!isStaffAvailable(a.getStaff().getId(), newDate, newTime, a.getService().getDurationMinutes())) {
            throw new RuntimeException("Staff member is not available at the new time. Please choose another time.");
        }

        a.setAppointmentDate(newDate);
        a.setAppointmentTime(newTime);
        return appointmentRepo.save(a);
    }

    /**
     * Change appointment status: SCHEDULED → COMPLETED or CANCELLED.
     */
    public Appointment changeStatus(Long id, String newStatus) {
        Appointment a = appointmentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        a.setStatus(newStatus);
        return appointmentRepo.save(a);
    }

    public void cancelAppointment(Long id) {
        changeStatus(id, "CANCELLED");
    }
}

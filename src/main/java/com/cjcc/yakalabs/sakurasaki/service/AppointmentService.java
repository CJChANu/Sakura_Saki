package com.cjcc.yakalabs.sakurasaki.service;

import com.cjcc.yakalabs.sakurasaki.model.*;
import com.cjcc.yakalabs.sakurasaki.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
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

        // Prevent booking in the past
        LocalDate today = LocalDate.now();
        if (date.isBefore(today)) {
            throw new RuntimeException("Cannot book an appointment on a past date. Please select today or a future date.");
        }
        if (date.isEqual(today) && time.isBefore(LocalTime.now())) {
            throw new RuntimeException("Cannot book an appointment at a past time. Please select a future time slot.");
        }

        // Shop hours validation (09:00 - 20:00)
        LocalTime endTime = time.plusMinutes(service.getDurationMinutes());
        if (time.isBefore(LocalTime.of(9, 0)) || endTime.isAfter(LocalTime.of(20, 0))) {
            throw new RuntimeException("Appointment must fall within shop hours (09:00 - 20:00).");
        }

        // Staff working-day validation
        if (staff.getWorkingDays() != null && !staff.getWorkingDays().isBlank()) {
            String dayAbbrev = getDayAbbreviation(date.getDayOfWeek());
            boolean worksOnDay = Arrays.stream(staff.getWorkingDays().split(","))
                    .map(String::trim)
                    .anyMatch(d -> d.equalsIgnoreCase(dayAbbrev));
            if (!worksOnDay) {
                throw new RuntimeException("Staff member " + staff.getFirstName() + " does not work on " + date.getDayOfWeek() + ". Please choose a different date or staff member.");
            }
        }

        // Staff shift-time validation
        if (staff.getStartTime() != null && staff.getEndTime() != null) {
            if (time.isBefore(staff.getStartTime()) || endTime.isAfter(staff.getEndTime())) {
                throw new RuntimeException("Appointment must fall within this staff member's shift (" + staff.getStartTime() + " - " + staff.getEndTime() + ").");
            }
        }

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
        
        Staff staff = staffRepo.findById(staffId).orElse(null);
        if (staff == null) return false;

        // Staff working-day validation
        if (staff.getWorkingDays() != null && !staff.getWorkingDays().isBlank()) {
            String dayAbbrev = getDayAbbreviation(date.getDayOfWeek());
            boolean worksOnDay = Arrays.stream(staff.getWorkingDays().split(","))
                    .map(String::trim)
                    .anyMatch(d -> d.equalsIgnoreCase(dayAbbrev));
            if (!worksOnDay) return false;
        }

        // Staff shift-time validation
        if (staff.getStartTime() != null && staff.getEndTime() != null) {
            if (startTime.isBefore(staff.getStartTime()) || endTime.isAfter(staff.getEndTime())) {
                return false;
            }
        }

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
    public Page<Appointment> findAll(Pageable pageable) {
        return appointmentRepo.findAll(pageable);
    }

    public List<Appointment> findByCustomer(Long customerId) {
        return appointmentRepo.findByCustomerIdOrderByAppointmentDateDesc(customerId);
    }
    public Page<Appointment> findByCustomer(Long customerId, Pageable pageable) {
        return appointmentRepo.findByCustomerIdOrderByAppointmentDateDesc(customerId, pageable);
    }

    public List<Appointment> findByStaff(Long staffId) {
        return appointmentRepo.findByStaffId(staffId);
    }
    public Page<Appointment> findByStaff(Long staffId, Pageable pageable) {
        return appointmentRepo.findByStaffId(staffId, pageable);
    }

    public List<Appointment> findByDate(LocalDate date) {
        return appointmentRepo.findByAppointmentDate(date);
    }
    public Page<Appointment> findByDate(LocalDate date, Pageable pageable) {
        return appointmentRepo.findByAppointmentDate(date, pageable);
    }

    public List<Appointment> findByStatus(String status) {
        return appointmentRepo.findByStatus(status);
    }
    public Page<Appointment> findByStatus(String status, Pageable pageable) {
        return appointmentRepo.findByStatus(status, pageable);
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

    public void cancelAppointment(Long id, String reason) {
        Appointment a = appointmentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
                
        // 2-day buffer policy
        if (LocalDate.now().plusDays(2).isAfter(a.getAppointmentDate())) {
            throw new RuntimeException("Appointments can only be cancelled up to 2 days before the appointment day.");
        }
        
        changeStatus(id, "CANCELLED");

        // Simulate email notifications
        String reasonStr = (reason != null && !reason.isBlank()) ? " Reason: " + reason : "";
        simulateEmail(a.getCustomer().getEmail(), 
            "Appointment Cancelled", 
            "Your appointment on " + a.getAppointmentDate() + " at " + a.getAppointmentTime() + " has been cancelled." + reasonStr);
            
        simulateEmail(a.getStaff().getEmail(), 
            "Appointment Cancelled", 
            "Your appointment on " + a.getAppointmentDate() + " at " + a.getAppointmentTime() + " with " + a.getCustomer().getFirstName() + " has been cancelled." + reasonStr);
    }

    private void simulateEmail(String to, String subject, String body) {
        System.out.println("==================================================");
        System.out.println("SIMULATED EMAIL");
        System.out.println("To: " + to);
        System.out.println("Subject: " + subject);
        System.out.println("Body: " + body);
        System.out.println("==================================================");
    }

    public List<Appointment> getLatestAppointments() {
        return appointmentRepo.findTop5ByOrderByAppointmentDateDescAppointmentTimeDesc();
    }

    public Optional<Appointment> getNextAppointmentForCustomer(Long customerId) {
        return appointmentRepo.findFirstByCustomerIdAndAppointmentDateGreaterThanEqualAndStatusOrderByAppointmentDateAscAppointmentTimeAsc(
                customerId, LocalDate.now(), "SCHEDULED");
    }

    public long countCompletedForCustomer(Long customerId) {
        return appointmentRepo.countByCustomerIdAndStatus(customerId, "COMPLETED");
    }

    /**
     * Convert DayOfWeek to the 3-letter abbreviation used in Staff.workingDays.
     */
    private String getDayAbbreviation(DayOfWeek day) {
        return switch (day) {
            case MONDAY    -> "MON";
            case TUESDAY   -> "TUE";
            case WEDNESDAY -> "WED";
            case THURSDAY  -> "THU";
            case FRIDAY    -> "FRI";
            case SATURDAY  -> "SAT";
            case SUNDAY    -> "SUN";
        };
    }
}

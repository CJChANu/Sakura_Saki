package com.cjcc.yakalabs.sakurasaki.validator;

import com.cjcc.yakalabs.sakurasaki.model.Appointment;
import com.cjcc.yakalabs.sakurasaki.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class ScheduleValidator {
    private static final LocalTime SALON_OPEN = LocalTime.of(8, 0);
    private static final LocalTime SALON_CLOSE = LocalTime.of(20, 0);

    @Autowired
    private AppointmentRepository appointmentRepository;

    public List<String> validate(Appointment appointment, boolean isUpdate) {
        List<String> errors = new ArrayList<>();
        validateRequiredFields(appointment, errors);
        validateServiceOrPackage(appointment, errors);
        validateNotPastDate(appointment, errors);
        validateWithinWorkingHours(appointment, errors);

        if (errors.isEmpty()) {
            Long excludeId = isUpdate ? appointment.getAppointmentId() : 0L;
            LocalTime endTime = calcEndTime(appointment);
            validateStaffAvailability(appointment, endTime, excludeId, errors);
            validateCustomerAvailability(appointment, endTime, excludeId, errors);
        }
        return errors;
    }

    private void validateRequiredFields(Appointment a, List<String> errors) {
        if (a.getUserId() == null || a.getUserId().isBlank()) errors.add("Customer ID is required.");
        if (a.getStaffId() == null || a.getStaffId().isBlank()) errors.add("Staff ID is required.");
        if (a.getAppointmentDate() == null) errors.add("Appointment date is required.");
        if (a.getAppointmentTime() == null) errors.add("Appointment time is required.");
        if (a.getDurationMinutes() <= 0) errors.add("Duration must be greater than zero.");
        if (a.getTotalAmount() < 0) errors.add("Total amount cannot be negative.");
    }

    private void validateServiceOrPackage(Appointment a, List<String> errors) {
        boolean hasService = a.getServiceId() != null && !a.getServiceId().isBlank();
        boolean hasPackage = a.getPackageId() != null && !a.getPackageId().isBlank();
        if (!hasService && !hasPackage) errors.add("Please select either a service or a package.");
        if (hasService && hasPackage) errors.add("Select only one: a service OR a package, not both.");
    }

    private void validateNotPastDate(Appointment a, List<String> errors) {
        if (a.getAppointmentDate() != null && a.getAppointmentDate().isBefore(LocalDate.now())) {
            errors.add("Appointment date cannot be in the past.");
        }
    }

    private void validateWithinWorkingHours(Appointment a, List<String> errors) {
        if (a.getAppointmentTime() == null || a.getDurationMinutes() <= 0) return;
        LocalTime start = a.getAppointmentTime();
        LocalTime end = calcEndTime(a);
        if (start.isBefore(SALON_OPEN)) errors.add("Appointment cannot start before salon opens at " + SALON_OPEN + ".");
        if (end.isAfter(SALON_CLOSE)) errors.add("Appointment would finish after salon closes at " + SALON_CLOSE + ". Please choose an earlier time or a shorter service.");
    }

    private boolean overlaps(LocalTime s1, LocalTime e1, LocalTime s2, LocalTime e2) {
        return s1.isBefore(e2) && e1.isAfter(s2);
    }

    private void validateStaffAvailability(Appointment a, LocalTime endTime, Long excludeId, List<String> errors) {
        List<Appointment> dayAppts = appointmentRepository.findByStaffIdAndAppointmentDateAndStatusNot(
                a.getStaffId(), a.getAppointmentDate(), "CANCELLED"
        );
        for (Appointment clash : dayAppts) {
            if (clash.getAppointmentId().equals(excludeId)) continue;
            LocalTime clashEnd = clash.getAppointmentTime().plusMinutes(clash.getDurationMinutes());
            if (overlaps(a.getAppointmentTime(), endTime, clash.getAppointmentTime(), clashEnd)) {
                errors.add("Staff " + a.getStaffId() + " is already booked from " + clash.getAppointmentTime() + " for " + clash.getDurationMinutes() + " mins on " + a.getAppointmentDate() + ".");
                break;
            }
        }
    }

    private void validateCustomerAvailability(Appointment a, LocalTime endTime, Long excludeId, List<String> errors) {
        List<Appointment> dayAppts = appointmentRepository.findByUserIdAndAppointmentDateAndStatusNot(
                a.getUserId(), a.getAppointmentDate(), "CANCELLED"
        );
        for (Appointment clash : dayAppts) {
            if (clash.getAppointmentId().equals(excludeId)) continue;
            LocalTime clashEnd = clash.getAppointmentTime().plusMinutes(clash.getDurationMinutes());
            if (overlaps(a.getAppointmentTime(), endTime, clash.getAppointmentTime(), clashEnd)) {
                errors.add("Customer " + a.getUserId() + " already has an appointment at " + clash.getAppointmentTime() + " on " + a.getAppointmentDate() + ".");
                break;
            }
        }
    }

    public LocalTime calcEndTime(Appointment a) {
        return a.getAppointmentTime().plusMinutes(a.getDurationMinutes());
    }
}

package com.cjcc.yakalabs.sakurasaki.validator;

import com.cjcc.yakalabs.sakurasaki.model.Appointment;
import com.cjcc.yakalabs.sakurasaki.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ════════════════════════════════════════════════════════════
 *  Member 4 — ScheduleValidator.java
 *
 *  A Spring @Component responsible for ALL schedule conflict
 *  and business-rule validation before an Appointment is
 *  saved or updated.
 *
 *  Checks performed:
 *   1. Required field presence
 *   2. Appointment date is not in the past
 *   3. Appointment time is within salon working hours
 *   4. Staff is not already booked in an overlapping slot
 *   5. Customer is not already booked in an overlapping slot
 *   6. Exactly one of serviceId / packageId must be provided
 * ════════════════════════════════════════════════════════════
 */
@Component
public class ScheduleValidator {

    // Salon working hours — adjust to match actual business hours
    private static final LocalTime SALON_OPEN  = LocalTime.of(8, 0);   // 08:00 AM
    private static final LocalTime SALON_CLOSE = LocalTime.of(20, 0);  // 08:00 PM

    @Autowired
    private AppointmentRepository appointmentRepository;

    // ─────────────────────────────────────────────────────────────────────
    //  Public entry point — call this before save / update
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Validates an Appointment object and returns a list of error messages.
     * An empty list means the appointment is valid and safe to save.
     *
     * @param appointment  The appointment to validate
     * @param isUpdate     Pass true when editing an existing record
     *                     (excludes itself from conflict checks)
     * @return             List of human-readable error strings (empty = OK)
     */
    public List<String> validate(Appointment appointment, boolean isUpdate) {
        List<String> errors = new ArrayList<>();

        validateRequiredFields(appointment, errors);
        validateServiceOrPackage(appointment, errors);
        validateNotPastDate(appointment, errors);
        validateWithinWorkingHours(appointment, errors);

        // Only run DB conflict checks if basic field checks passed
        if (errors.isEmpty()) {
            Long excludeId = isUpdate ? appointment.getAppointmentId() : 0L;
            LocalTime endTime = calcEndTime(appointment);

            validateStaffAvailability(appointment, endTime, excludeId, errors);
            validateCustomerAvailability(appointment, endTime, excludeId, errors);
        }

        return errors;
    }

    // ─────────────────────────────────────────────────────────────────────
    //  1. Required field presence
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Checks that userId, staffId, appointmentDate, appointmentTime,
     * durationMinutes, and totalAmount are all present.
     */
    public void validateRequiredFields(Appointment a, List<String> errors) {
        if (a.getUserId() == null || a.getUserId().isBlank())
            errors.add("Customer ID is required.");

        if (a.getStaffId() == null || a.getStaffId().isBlank())
            errors.add("Staff ID is required.");

        if (a.getAppointmentDate() == null)
            errors.add("Appointment date is required.");

        if (a.getAppointmentTime() == null)
            errors.add("Appointment time is required.");

        if (a.getDurationMinutes() <= 0)
            errors.add("Duration must be greater than zero.");

        if (a.getTotalAmount() < 0)
            errors.add("Total amount cannot be negative.");
    }

    // ─────────────────────────────────────────────────────────────────────
    //  2. Exactly one of serviceId / packageId
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Enforces that the appointment references EITHER a service
     * (Member 3 serviceId) OR a package (Member 3 packageId), not both
     * and not neither.
     */
    public void validateServiceOrPackage(Appointment a, List<String> errors) {
        boolean hasService = a.getServiceId() != null && !a.getServiceId().isBlank();
        boolean hasPackage = a.getPackageId() != null && !a.getPackageId().isBlank();

        if (!hasService && !hasPackage)
            errors.add("Please select either a service or a package.");

        if (hasService && hasPackage)
            errors.add("Select only one: a service OR a package, not both.");
    }

    // ─────────────────────────────────────────────────────────────────────
    //  3. Date must not be in the past
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Rejects any appointment date that is before today.
     */
    public void validateNotPastDate(Appointment a, List<String> errors) {
        if (a.getAppointmentDate() == null) return; // already caught above

        if (a.getAppointmentDate().isBefore(LocalDate.now()))
            errors.add("Appointment date cannot be in the past.");
    }

    // ─────────────────────────────────────────────────────────────────────
    //  4. Time must be within salon working hours
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Checks that both the start time AND the projected end time
     * fall within SALON_OPEN – SALON_CLOSE.
     */
    public void validateWithinWorkingHours(Appointment a, List<String> errors) {
        if (a.getAppointmentTime() == null || a.getDurationMinutes() <= 0) return;

        LocalTime start = a.getAppointmentTime();
        LocalTime end   = calcEndTime(a);

        if (start.isBefore(SALON_OPEN))
            errors.add("Appointment cannot start before salon opens at "
                    + SALON_OPEN + ".");

        if (end.isAfter(SALON_CLOSE))
            errors.add("Appointment would finish after salon closes at "
                    + SALON_CLOSE + ". Please choose an earlier time or a shorter service.");
    }

    // ─────────────────────────────────────────────────────────────────────
    //  5. Staff availability (no double-booking)
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Queries the appointments table via AppointmentRepository to detect
     * any existing CONFIRMED or PENDING appointment for the same staff
     * member that overlaps the requested time window.
     *
     * Uses Member 2's staffId as the key.
     */
    public void validateStaffAvailability(Appointment a, LocalTime endTime,
                                          Long excludeId, List<String> errors) {
        List<Appointment> conflicts = appointmentRepository.findStaffConflicts(
                a.getStaffId(),
                a.getAppointmentDate(),
                a.getAppointmentTime(),
                endTime,
                excludeId
        );

        if (!conflicts.isEmpty()) {
            Appointment clash = conflicts.get(0);
            errors.add("Staff " + a.getStaffId() +
                       " is already booked from " + clash.getAppointmentTime() +
                       " for " + clash.getDurationMinutes() + " mins on " +
                       a.getAppointmentDate() + ". Please choose a different time or staff.");
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    //  6. Customer availability (prevent same-slot double-booking)
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Ensures the same customer (Member 1 userId) doesn't have another
     * overlapping appointment on the same date.
     */
    public void validateCustomerAvailability(Appointment a, LocalTime endTime,
                                             Long excludeId, List<String> errors) {
        List<Appointment> conflicts = appointmentRepository.findCustomerConflicts(
                a.getUserId(),
                a.getAppointmentDate(),
                a.getAppointmentTime(),
                endTime,
                excludeId
        );

        if (!conflicts.isEmpty()) {
            errors.add("Customer " + a.getUserId() +
                       " already has an appointment at " +
                       conflicts.get(0).getAppointmentTime() +
                       " on " + a.getAppointmentDate() + ".");
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    //  Helper — calculate end time from start + duration
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Returns appointmentTime + durationMinutes as a LocalTime.
     * Used internally for overlap calculations.
     */
    public LocalTime calcEndTime(Appointment a) {
        return a.getAppointmentTime().plusMinutes(a.getDurationMinutes());
    }
}

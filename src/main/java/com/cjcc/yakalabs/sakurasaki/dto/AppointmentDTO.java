package com.cjcc.yakalabs.sakurasaki.dto;

import com.cjcc.yakalabs.sakurasaki.model.Appointment;

import java.time.LocalDate;
import java.time.LocalTime;



public class AppointmentDTO {


    /** Member 1 — Customer / User ID */
    private String userId;

    /** Member 2 — Staff ID */
    private String staffId;

    /**
     * Member 3 — SalonService ID.
     * Leave blank if the customer is booking a package instead.
     */
    private String serviceId;

    /**
     * Member 3 — ServicePackage ID.
     * Leave blank if the customer is booking an individual service instead.
     */
    private String packageId;

    // ── Appointment scheduling fields ─────────────────────────────────────

    private LocalDate appointmentDate;
    private LocalTime appointmentTime;

    /**
     * Duration in minutes.
     * Ideally auto-filled from the selected service/package duration,
     * but can be edited by admin if needed.
     */
    private int durationMinutes;

    // ── Pricing ───────────────────────────────────────────────────────────

    /**
     * Total amount (LKR).
     * Auto-filled from Member 3's service price or package finalPrice,
     * but kept editable for admin overrides.
     */
    private double totalAmount;

    // ── Optional ──────────────────────────────────────────────────────────

    /** Free-text notes / special requests from the customer. */
    private String notes;

    /**
     * Only used in the EDIT form DTO — carries the existing appointmentId
     * so the controller knows which record to update.
     * Null / 0 for new bookings.
     */
    private Long appointmentId;

    // ── Constructors ──────────────────────────────────────────────────────

    public AppointmentDTO() {}

    public AppointmentDTO(String userId, String staffId,
                          String serviceId, String packageId,
                          LocalDate appointmentDate, LocalTime appointmentTime,
                          int durationMinutes, double totalAmount, String notes) {
        this.userId          = userId;
        this.staffId         = staffId;
        this.serviceId       = serviceId;
        this.packageId       = packageId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.durationMinutes = durationMinutes;
        this.totalAmount     = totalAmount;
        this.notes           = notes;
    }

    // ── Mapping helpers

    /**
     * Convert this DTO to a new Appointment entity (for CREATE).
     * status and createdDate are set by AppointmentServiceImpl,
     * not from the form.
     *
     * @return  A new Appointment populated from this DTO
     */
    public Appointment toNewEntity() {
        Appointment a =
                new Appointment();
        a.setUserId(this.userId);
        a.setStaffId(this.staffId);
        a.setServiceId(this.serviceId != null && !this.serviceId.isBlank()
                ? this.serviceId : null);
        a.setPackageId(this.packageId != null && !this.packageId.isBlank()
                ? this.packageId : null);
        a.setAppointmentDate(this.appointmentDate);
        a.setAppointmentTime(this.appointmentTime);
        a.setDurationMinutes(this.durationMinutes);
        a.setTotalAmount(this.totalAmount);
        a.setNotes(this.notes);
        return a;
    }

    /**
     * Convert this DTO to an existing Appointment entity (for UPDATE).
     * Copies the appointmentId so JPA performs a merge, not an insert.
     *
     * @return  An Appointment with its ID set — ready for service.updateAppointment()
     */
    public Appointment toExistingEntity(
            Appointment existing) {
        existing.setUserId(this.userId);
        existing.setStaffId(this.staffId);
        existing.setServiceId(this.serviceId != null && !this.serviceId.isBlank()
                ? this.serviceId : null);
        existing.setPackageId(this.packageId != null && !this.packageId.isBlank()
                ? this.packageId : null);
        existing.setAppointmentDate(this.appointmentDate);
        existing.setAppointmentTime(this.appointmentTime);
        existing.setDurationMinutes(this.durationMinutes);
        existing.setTotalAmount(this.totalAmount);
        existing.setNotes(this.notes);
        return existing;
    }

    /**
     * Build an AppointmentDTO from an existing Appointment entity.
     * Used to pre-fill the EDIT form.
     *
     * @param entity  The entity loaded from the database
     * @return        DTO ready to bind to the Thymeleaf edit form
     */
    public static AppointmentDTO fromEntity(
            Appointment entity) {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setAppointmentId(entity.getAppointmentId());
        dto.setUserId(entity.getUserId());
        dto.setStaffId(entity.getStaffId());
        dto.setServiceId(entity.getServiceId());
        dto.setPackageId(entity.getPackageId());
        dto.setAppointmentDate(entity.getAppointmentDate());
        dto.setAppointmentTime(entity.getAppointmentTime());
        dto.setDurationMinutes(entity.getDurationMinutes());
        dto.setTotalAmount(entity.getTotalAmount());
        dto.setNotes(entity.getNotes());
        return dto;
    }

    // ── Getters & Setters

    public Long getAppointmentId()                             { return appointmentId; }
    public void setAppointmentId(Long appointmentId)           { this.appointmentId = appointmentId; }

    public String getUserId()                                  { return userId; }
    public void setUserId(String userId)                       { this.userId = userId; }

    public String getStaffId()                                 { return staffId; }
    public void setStaffId(String staffId)                     { this.staffId = staffId; }

    public String getServiceId()                               { return serviceId; }
    public void setServiceId(String serviceId)                 { this.serviceId = serviceId; }

    public String getPackageId()                               { return packageId; }
    public void setPackageId(String packageId)                 { this.packageId = packageId; }

    public LocalDate getAppointmentDate()                      { return appointmentDate; }
    public void setAppointmentDate(LocalDate appointmentDate)  { this.appointmentDate = appointmentDate; }

    public LocalTime getAppointmentTime()                      { return appointmentTime; }
    public void setAppointmentTime(LocalTime appointmentTime)  { this.appointmentTime = appointmentTime; }

    public int getDurationMinutes()                            { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes)        { this.durationMinutes = durationMinutes; }

    public double getTotalAmount()                             { return totalAmount; }
    public void setTotalAmount(double totalAmount)             { this.totalAmount = totalAmount; }

    public String getNotes()                                   { return notes; }
    public void setNotes(String notes)                         { this.notes = notes; }

    @Override
    public String toString() {
        return "AppointmentDTO{" +
               "appointmentId=" + appointmentId +
               ", userId='"    + userId    + "'" +
               ", staffId='"   + staffId   + "'" +
               ", serviceId='" + serviceId + "'" +
               ", packageId='" + packageId + "'" +
               ", date="       + appointmentDate +
               ", time="       + appointmentTime +
               ", duration="   + durationMinutes + "min" +
               ", total="      + totalAmount +
               "}";
    }
}

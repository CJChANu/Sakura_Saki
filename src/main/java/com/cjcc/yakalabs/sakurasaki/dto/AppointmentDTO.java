package com.cjcc.yakalabs.sakurasaki.dto;

import com.cjcc.yakalabs.sakurasaki.model.Appointment;

import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentDTO {
    private String userId;
    private String staffId;
    private String serviceId;
    private String packageId;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private int durationMinutes;
    private double totalAmount;
    private String notes;
    private Long appointmentId;

    public AppointmentDTO() {}

    public AppointmentDTO(String userId, String staffId, String serviceId, String packageId,
                          LocalDate appointmentDate, LocalTime appointmentTime,
                          int durationMinutes, double totalAmount, String notes) {
        this.userId = userId;
        this.staffId = staffId;
        this.serviceId = serviceId;
        this.packageId = packageId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.durationMinutes = durationMinutes;
        this.totalAmount = totalAmount;
        this.notes = notes;
    }

    public Appointment toNewEntity() {
        Appointment a = new Appointment();
        a.setUserId(this.userId);
        a.setStaffId(this.staffId);
        a.setServiceId(this.serviceId != null && !this.serviceId.isBlank() ? this.serviceId : null);
        a.setPackageId(this.packageId != null && !this.packageId.isBlank() ? this.packageId : null);
        a.setAppointmentDate(this.appointmentDate);
        a.setAppointmentTime(this.appointmentTime);
        a.setDurationMinutes(this.durationMinutes);
        a.setTotalAmount(this.totalAmount);
        a.setNotes(this.notes);
        return a;
    }

    public Appointment toExistingEntity(Appointment existing) {
        existing.setUserId(this.userId);
        existing.setStaffId(this.staffId);
        existing.setServiceId(this.serviceId != null && !this.serviceId.isBlank() ? this.serviceId : null);
        existing.setPackageId(this.packageId != null && !this.packageId.isBlank() ? this.packageId : null);
        existing.setAppointmentDate(this.appointmentDate);
        existing.setAppointmentTime(this.appointmentTime);
        existing.setDurationMinutes(this.durationMinutes);
        existing.setTotalAmount(this.totalAmount);
        existing.setNotes(this.notes);
        return existing;
    }

    public static AppointmentDTO fromEntity(Appointment entity) {
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

    public Long getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Long appointmentId) { this.appointmentId = appointmentId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }
    public String getServiceId() { return serviceId; }
    public void setServiceId(String serviceId) { this.serviceId = serviceId; }
    public String getPackageId() { return packageId; }
    public void setPackageId(String packageId) { this.packageId = packageId; }
    public LocalDate getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; }
    public LocalTime getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(LocalTime appointmentTime) { this.appointmentTime = appointmentTime; }
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}

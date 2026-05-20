package com.cjcc.yakalabs.sakurasaki.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "appointments")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id")
    private Long appointmentId;

    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @Column(name = "staff_id", nullable = false, length = 50)
    private String staffId;

    @Column(name = "service_id", length = 50)
    private String serviceId;

    @Column(name = "package_id", length = 50)
    private String packageId;

    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    @Column(name = "appointment_time", nullable = false)
    private LocalTime appointmentTime;

    @Column(name = "duration_minutes", nullable = false)
    private int durationMinutes;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "CONFIRMED";

    @Column(name = "total_amount", nullable = false)
    private double totalAmount;

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "created_date", nullable = false)
    private LocalDate createdDate = LocalDate.now();

    @Column(name = "is_booked")
    private Boolean isBooked = true;

    @Column(name = "is_confirmed")
    private Boolean isConfirmed = false;

    @Column(name = "is_rescheduled")
    private Boolean isRescheduled = false;

    @Column(name = "is_completed")
    private Boolean isCompleted = false;

    @Column(name = "is_cancelled")
    private Boolean isCancelled = false;

    public Appointment() {}

    public Appointment(String userId, String staffId, String serviceId, String packageId,
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
    public String getStatus() { return status; }
    public void setStatus(String status) { 
        this.status = status;
        if (status != null) {
            String s = status.toUpperCase();
            if (s.equals("CONFIRMED")) this.isConfirmed = true;
            else if (s.equals("RESCHEDULED")) this.isRescheduled = true;
            else if (s.equals("COMPLETED")) this.isCompleted = true;
            else if (s.equals("CANCELLED")) this.isCancelled = true;
        }
    }
    
    public Boolean getIsBooked() { return isBooked == null ? false : isBooked; }
    public void setIsBooked(Boolean booked) { this.isBooked = booked; }
    public Boolean getIsConfirmed() { return isConfirmed == null ? false : isConfirmed; }
    public void setIsConfirmed(Boolean confirmed) { this.isConfirmed = confirmed; }
    public Boolean getIsRescheduled() { return isRescheduled == null ? false : isRescheduled; }
    public void setIsRescheduled(Boolean rescheduled) { this.isRescheduled = rescheduled; }
    public Boolean getIsCompleted() { return isCompleted == null ? false : isCompleted; }
    public void setIsCompleted(Boolean completed) { this.isCompleted = completed; }
    public Boolean getIsCancelled() { return isCancelled == null ? false : isCancelled; }
    public void setIsCancelled(Boolean cancelled) { this.isCancelled = cancelled; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public LocalDate getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDate createdDate) { this.createdDate = createdDate; }

    @Override
    public String toString() {
        return "Appointment{id=" + appointmentId + ", user='" + userId + "', staff='" + staffId + "', date=" + appointmentDate + ", time=" + appointmentTime + ", status='" + status + "'}";
    }
}
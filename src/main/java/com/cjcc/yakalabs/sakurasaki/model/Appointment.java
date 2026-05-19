package com.cjcc.yakalabs.sakurasaki.model;

public class Appointment {

    private String appointmentId;
    private String customerId;
    private String serviceId;
    private String staffId;
    private String date;
    private String time;
    private String status;

    public Appointment() {
    }

    public Appointment(String appointmentId, String customerId, String serviceId,
                       String staffId, String date, String time, String status) {
        this.appointmentId = appointmentId;
        this.customerId = customerId;
        this.serviceId = serviceId;
        this.staffId = staffId;
        this.date = date;
        this.time = time;
        this.status = status;
    }

    public String toFileString() {
        return appointmentId + "|" + customerId + "|" + serviceId + "|" + staffId + "|" +
                date + "|" + time + "|" + status;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;


@Entity
@Table(name = "appointments")
public class Appointment {

    // ── Primary Key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id")
    private Long appointmentId;

    // ── Foreign IDs from other Members

    /** Member 1 — Customer / User ID */
    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    /** Member 2 — Staff ID */
    @Column(name = "staff_id", nullable = false, length = 50)
    private String staffId;

    /**
     * Member 3 — SalonService ID
     * Nullable: customer books EITHER a service OR a package, not both.
     */
    @Column(name = "service_id", length = 50)
    private String serviceId;

    /**
     * Member 3 — ServicePackage ID
     * Nullable: customer books EITHER a service OR a package, not both.
     */
    @Column(name = "package_id", length = 50)
    private String packageId;

    // ── Appointment Details

    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    @Column(name = "appointment_time", nullable = false)
    private LocalTime appointmentTime;

    /**
     * Duration in minutes — pulled from the linked SalonService or
     * calculated by Member 3's ServicePackage at booking time.
     */
    @Column(name = "duration_minutes", nullable = false)
    private int durationMinutes;

    /**
     * Lifecycle status:
     * PENDING → CONFIRMED → COMPLETED
     *         ↘ CANCELLED
     */
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    /** Total charged amount (LKR). Copied from service/package price at booking time. */
    @Column(name = "total_amount", nullable = false)
    private double totalAmount;

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "created_date", nullable = false,
            columnDefinition = "DATE DEFAULT (CURRENT_DATE)")
    private LocalDate createdDate;

    // ── Constructors ──────────────────────────────────────────────────────

    public Appointment() {}

    public Appointment(String userId, String staffId,
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
        this.status          = "PENDING";
        this.createdDate     = LocalDate.now();
    }

    // ── File I/O helpers
    /**
     * Serialise to pipe-delimited line for appointments.txt
     *
     * Format:
     * appointmentId | userId | staffId | serviceId | packageId |
     * appointmentDate | appointmentTime | durationMinutes |
     * status | totalAmount | notes | createdDate
     */
    public String toFileString() {
        return appointmentId                              + "|" +
                userId                                     + "|" +
                staffId                                    + "|" +
                (serviceId  != null ? serviceId  : "null") + "|" +
                (packageId  != null ? packageId  : "null") + "|" +
                appointmentDate                            + "|" +
                appointmentTime                            + "|" +
                durationMinutes                            + "|" +
                status                                     + "|" +
                totalAmount                                + "|" +
                (notes != null ? notes.replace("|", " ") : "") + "|" +
                createdDate;
    }

    /**
     * Deserialise a pipe-delimited line from appointments.txt
     * back into an Appointment object.
     */
    public static Appointment fromFileString(String line) {
        String[] p = line.split("\\|", -1);
        Appointment a = new Appointment();
        a.appointmentId   = Long.parseLong(p[0]);
        a.userId          = p[1];
        a.staffId         = p[2];
        a.serviceId       = "null".equals(p[3]) ? null : p[3];
        a.packageId       = "null".equals(p[4]) ? null : p[4];
        a.appointmentDate = LocalDate.parse(p[5]);
        a.appointmentTime = LocalTime.parse(p[6]);
        a.durationMinutes = Integer.parseInt(p[7]);
        a.status          = p[8];
        a.totalAmount     = Double.parseDouble(p[9]);
        a.notes           = p[10];
        a.createdDate     = LocalDate.parse(p[11]);
        return a;
    }

    //  Getters & Setters

    public Long getAppointmentId()                           { return appointmentId; }
    public void setAppointmentId(Long appointmentId)         { this.appointmentId = appointmentId; }

    public String getUserId()                                { return userId; }
    public void setUserId(String userId)                     { this.userId = userId; }

    public String getStaffId()                               { return staffId; }
    public void setStaffId(String staffId)                   { this.staffId = staffId; }

    public String getServiceId()                             { return serviceId; }
    public void setServiceId(String serviceId)               { this.serviceId = serviceId; }

    public String getPackageId()                             { return packageId; }
    public void setPackageId(String packageId)               { this.packageId = packageId; }

    public LocalDate getAppointmentDate()                    { return appointmentDate; }
    public void setAppointmentDate(LocalDate appointmentDate){ this.appointmentDate = appointmentDate; }

    public LocalTime getAppointmentTime()                    { return appointmentTime; }
    public void setAppointmentTime(LocalTime appointmentTime){ this.appointmentTime = appointmentTime; }

    public int getDurationMinutes()                          { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes)      { this.durationMinutes = durationMinutes; }

    public String getStatus()                                { return status; }
    public void setStatus(String status)                     { this.status = status; }

    public double getTotalAmount()                           { return totalAmount; }
    public void setTotalAmount(double totalAmount)           { this.totalAmount = totalAmount; }

    public String getNotes()                                 { return notes; }
    public void setNotes(String notes)                       { this.notes = notes; }

    public LocalDate getCreatedDate()                        { return createdDate; }
    public void setCreatedDate(LocalDate createdDate)        { this.createdDate = createdDate; }

    @Override
    public String toString() {
        return "Appointment{id=" + appointmentId +
                ", user='" + userId + "'" +
                ", staff='" + staffId + "'" +
                ", date=" + appointmentDate +
                ", time=" + appointmentTime +
                ", status='" + status + "'" +
                ", total=" + totalAmount + "}";
    }
}
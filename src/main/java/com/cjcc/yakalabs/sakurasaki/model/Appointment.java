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
    }
}
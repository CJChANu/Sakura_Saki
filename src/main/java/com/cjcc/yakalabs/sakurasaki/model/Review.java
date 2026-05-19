package com.cjcc.yakalabs.sakurasaki.model;

public abstract class Review {
    private String reviewId;
    private String appointmentId;
    private String customerId;
    private String serviceId;
    private String staffId;
    private int rating;
    private String comment;
    private String status; // PENDING, APPROVED, REJECTED, HIDDEN
    private String date;

    public Review() {
    }

    public Review(String reviewId, String appointmentId, String customerId, String serviceId,
                  String staffId, int rating, String comment, String status, String date) {
        this.reviewId = reviewId;
        this.appointmentId = appointmentId;
        this.customerId = customerId;
        this.serviceId = serviceId;
        this.staffId = staffId;
        this.rating = rating;
        this.comment = comment;
        this.status = status;
        this.date = date;
    }

    public abstract String getReviewType();

    public String toFileString() {
        return reviewId + "|" + appointmentId + "|" + customerId + "|" + serviceId + "|" + staffId + "|" +
                getReviewType() + "|" + rating + "|" + comment + "|" + status + "|" + date;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        if (rating >= 1 && rating <= 5) {
            this.rating = rating;
        }
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

package com.cjcc.yakalabs.sakurasaki.model;

public class StaffReview extends Review {

    public StaffReview() {
        super();
    }

    public StaffReview(String reviewId, String appointmentId, String customerId, String serviceId,
                       String staffId, int rating, String comment, String status, String date) {
        super(reviewId, appointmentId, customerId, serviceId, staffId, rating, comment, status, date);
    }

    @Override
    public String getReviewType() {
        return "STAFF";
    }
}
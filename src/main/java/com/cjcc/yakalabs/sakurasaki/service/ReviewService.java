package com.cjcc.yakalabs.sakurasaki.service;

import com.cjcc.yakalabs.sakurasaki.model.Review;
import com.cjcc.yakalabs.sakurasaki.model.ServiceReview;
import com.cjcc.yakalabs.sakurasaki.model.StaffReview;
import com.cjcc.yakalabs.sakurasaki.util.ReviewFileHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewFileHandler reviewFileHandler;

    private static final String APPOINTMENT_FILE = "src/main/resources/data/appointments.txt";

    public List<Review> getAllReviews() {
        return reviewFileHandler.readAllReviews();
    }

    public List<Review> getApprovedReviews() {
        List<Review> approved = new ArrayList<>();
        for (Review review : getAllReviews()) {
            if ("APPROVED".equalsIgnoreCase(review.getStatus())) {
                approved.add(review);
            }
        }
        return approved;
    }

    public List<Review> getPendingReviews() {
        List<Review> pending = new ArrayList<>();
        for (Review review : getAllReviews()) {
            if ("PENDING".equalsIgnoreCase(review.getStatus())) {
                pending.add(review);
            }
        }
        return pending;
    }

    public List<Review> getRejectedReviews() {
        List<Review> rejected = new ArrayList<>();
        for (Review review : getAllReviews()) {
            if ("REJECTED".equalsIgnoreCase(review.getStatus())) {
                rejected.add(review);
            }
        }
        return rejected;
    }

    public List<Review> getHiddenReviews() {
        List<Review> hidden = new ArrayList<>();
        for (Review review : getAllReviews()) {
            if ("HIDDEN".equalsIgnoreCase(review.getStatus())) {
                hidden.add(review);
            }
        }
        return hidden;
    }

    public List<Review> getReviewsByCustomer(String customerId) {
        List<Review> customerReviews = new ArrayList<>();
        for (Review review : getAllReviews()) {
            if (review.getCustomerId().equalsIgnoreCase(customerId)) {
                customerReviews.add(review);
            }
        }
        return customerReviews;
    }

    public Review getReviewById(String reviewId) {
        for (Review review : getAllReviews()) {
            if (review.getReviewId().equalsIgnoreCase(reviewId)) {
                return review;
            }
        }
        return null;
    }

    public List<Review> getApprovedServiceReviews(String serviceId) {
        List<Review> serviceReviews = new ArrayList<>();
        for (Review review : getApprovedReviews()) {
            if ("SERVICE".equalsIgnoreCase(review.getReviewType())
                    && review.getServiceId().equalsIgnoreCase(serviceId)) {
                serviceReviews.add(review);
            }
        }
        return serviceReviews;
    }

    public List<Review> getApprovedStaffReviews(String staffId) {
        List<Review> staffReviews = new ArrayList<>();
        for (Review review : getApprovedReviews()) {
            if ("STAFF".equalsIgnoreCase(review.getReviewType())
                    && review.getStaffId().equalsIgnoreCase(staffId)) {
                staffReviews.add(review);
            }
        }
        return staffReviews;
    }

    public boolean saveReview(String reviewId, String appointmentId, String customerId, String serviceId,
                              String staffId, String reviewType, int rating, String comment) {

        String date = LocalDate.now().toString();

        Review review;
        if ("SERVICE".equalsIgnoreCase(reviewType)) {
            review = new ServiceReview(
                    reviewId, appointmentId, customerId, serviceId, staffId,
                    rating, comment, "PENDING", date
            );
        } else {
            review = new StaffReview(
                    reviewId, appointmentId, customerId, serviceId, staffId,
                    rating, comment, "PENDING", date
            );
        }

        reviewFileHandler.appendReview(review);
        return true;
    }

    public boolean updateReview(String reviewId, String customerId, int rating, String comment) {
        List<Review> reviews = getAllReviews();
        boolean updated = false;

        for (Review review : reviews) {
            if (review.getReviewId().equalsIgnoreCase(reviewId)
                    && review.getCustomerId().equalsIgnoreCase(customerId)) {
                review.setRating(rating);
                review.setComment(comment);
                review.setStatus("PENDING"); // edited review must be approved again
                updated = true;
                break;
            }
        }

        if (updated) {
            reviewFileHandler.writeAllReviews(reviews);
        }

        return updated;
    }

    public boolean adminUpdateReview(String reviewId, int rating, String comment) {
        List<Review> reviews = getAllReviews();
        boolean updated = false;

        for (Review review : reviews) {
            if (review.getReviewId().equalsIgnoreCase(reviewId)) {
                review.setRating(rating);
                review.setComment(comment);
                // Admin editing does not reset the status to pending, mostly fixing typos or inappropriate words but keeping it safe
                updated = true;
                break;
            }
        }

        if (updated) {
            reviewFileHandler.writeAllReviews(reviews);
        }

        return updated;
    }

    public boolean deleteReview(String reviewId, String customerId) {
        List<Review> reviews = getAllReviews();

        boolean removed = reviews.removeIf(review ->
                review.getReviewId().equalsIgnoreCase(reviewId)
                        && review.getCustomerId().equalsIgnoreCase(customerId));

        if (removed) {
            reviewFileHandler.writeAllReviews(reviews);
        }

        return removed;
    }

    public boolean approveReview(String reviewId) {
        return updateStatus(reviewId, "APPROVED");
    }

    public boolean rejectReview(String reviewId) {
        return updateStatus(reviewId, "REJECTED");
    }

    public boolean hideReview(String reviewId) {
        return updateStatus(reviewId, "HIDDEN");
    }

    public boolean unhideReview(String reviewId) {
        return updateStatus(reviewId, "APPROVED");
    }

    private boolean updateStatus(String reviewId, String status) {
        List<Review> reviews = getAllReviews();
        boolean updated = false;

        for (Review review : reviews) {
            if (review.getReviewId().equalsIgnoreCase(reviewId)) {
                review.setStatus(status);
                updated = true;
                break;
            }
        }

        if (updated) {
            reviewFileHandler.writeAllReviews(reviews);
        }

        return updated;
    }

    public boolean hasReviewForAppointment(String appointmentId) {
        for (Review review : getAllReviews()) {
            if (review.getAppointmentId().equalsIgnoreCase(appointmentId)) {
                return true;
            }
        }
        return false;
    }

    public boolean isAppointmentCompleted(String appointmentId) {
        Path path = Paths.get(APPOINTMENT_FILE);

        try {
            if (!Files.exists(path)) {
                return false;
            }

            List<String> lines = Files.readAllLines(path);

            for (String line : lines) {
                if (line == null || line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split("\\|");

                // Expected format:
                // A001|C001|SV001|ST001|2026-03-15|10:00|COMPLETED
                if (parts.length >= 7) {
                    String fileAppointmentId = parts[0];
                    String status = parts[6];

                    if (fileAppointmentId.equalsIgnoreCase(appointmentId)
                            && "COMPLETED".equalsIgnoreCase(status)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private String generateNextReviewId() {
        List<Review> reviews = getAllReviews();
        int max = 0;

        for (Review review : reviews) {
            try {
                String numericPart = review.getReviewId().replace("R", "");
                int current = Integer.parseInt(numericPart);
                if (current > max) {
                    max = current;
                }
            } catch (NumberFormatException ignored) {
            }
        }

        return String.format("R%03d", max + 1);
    }

    public double getAverageApprovedRating() {
        List<Review> approvedReviews = getApprovedReviews();

        if (approvedReviews.isEmpty()) {
            return 0.0;
        }

        int total = 0;
        for (Review review : approvedReviews) {
            total += review.getRating();
        }

        return (double) total / approvedReviews.size();
    }
}

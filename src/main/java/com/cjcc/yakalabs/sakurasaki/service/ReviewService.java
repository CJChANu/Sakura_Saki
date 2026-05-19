package com.cjcc.yakalabs.sakurasaki.service;

import com.cjcc.yakalabs.sakurasaki.model.Appointment;
import com.cjcc.yakalabs.sakurasaki.model.Customer;
import com.cjcc.yakalabs.sakurasaki.model.Review;
import com.cjcc.yakalabs.sakurasaki.model.SalonService;
import com.cjcc.yakalabs.sakurasaki.model.User;
import com.cjcc.yakalabs.sakurasaki.repository.AppointmentRepository;
import com.cjcc.yakalabs.sakurasaki.repository.CustomerRepository;
import com.cjcc.yakalabs.sakurasaki.repository.ReviewRepository;
import com.cjcc.yakalabs.sakurasaki.repository.SalonServiceRepository;
import com.cjcc.yakalabs.sakurasaki.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final CustomerRepository customerRepository;
    private final SalonServiceRepository serviceRepository;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         CustomerRepository customerRepository,
                         SalonServiceRepository serviceRepository,
                         UserRepository userRepository,
                         AppointmentRepository appointmentRepository) {
        this.reviewRepository = reviewRepository;
        this.customerRepository = customerRepository;
        this.serviceRepository = serviceRepository;
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public List<Review> getApprovedReviews() {
        return reviewRepository.findByStatus("APPROVED");
    }

    public List<Review> getPendingReviews() {
        return reviewRepository.findByStatus("PENDING");
    }

    public List<Review> getRejectedReviews() {
        return reviewRepository.findByStatus("REJECTED");
    }

    public List<Review> getHiddenReviews() {
        return reviewRepository.findByStatus("HIDDEN");
    }

    public List<Review> getReviewsByCustomer(Long customerId) {
        return reviewRepository.findByCustomer_Id(customerId);
    }

    public Review getReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId).orElse(null);
    }

    public List<Review> getApprovedServiceReviews(Long serviceId) {
        return reviewRepository.findByReviewTypeAndService_IdAndStatus("SERVICE", serviceId, "APPROVED");
    }

    public List<Review> getApprovedStaffReviews(Long staffId) {
        return reviewRepository.findByReviewTypeAndStaff_IdAndStatus("STAFF", staffId, "APPROVED");
    }

    public boolean saveReview(Long appointmentId, Long customerId, Long serviceId,
                              Long staffId, String reviewType, int rating, String comment) {

        if (hasReviewForAppointment(appointmentId)) {
            return false;
        }
        if (!isAppointmentCompleted(appointmentId)) {
            return false;
        }

        Customer customer = customerRepository.findById(customerId).orElse(null);
        Appointment appointment = appointmentRepository.findById(appointmentId).orElse(null);
        SalonService service = (serviceId != null) ? serviceRepository.findById(serviceId).orElse(null) : null;
        User staff = (staffId != null) ? userRepository.findById(staffId).orElse(null) : null;

        if (customer == null || appointment == null) {
            return false;
        }

        Review review = new Review(customer, appointment, service, staff, reviewType, rating, comment);
        reviewRepository.save(review);
        return true;
    }

    public boolean updateReview(Long reviewId, Long customerId, int rating, String comment) {
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);
        if (optionalReview.isPresent()) {
            Review review = optionalReview.get();
            if (review.getCustomer().getId().equals(customerId)) {
                review.setRating(rating);
                review.setComment(comment);
                review.setStatus("PENDING"); // Edited review must be approved again
                reviewRepository.save(review);
                return true;
            }
        }
        return false;
    }

    public boolean adminUpdateReview(Long reviewId, int rating, String comment) {
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);
        if (optionalReview.isPresent()) {
            Review review = optionalReview.get();
            review.setRating(rating);
            review.setComment(comment);
            // Admin editing does not reset the status to pending
            reviewRepository.save(review);
            return true;
        }
        return false;
    }

    public boolean deleteReview(Long reviewId, Long customerId) {
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);
        if (optionalReview.isPresent()) {
            Review review = optionalReview.get();
            if (review.getCustomer().getId().equals(customerId)) {
                reviewRepository.delete(review);
                return true;
            }
        }
        return false;
    }

    public boolean approveReview(Long reviewId) {
        return updateStatus(reviewId, "APPROVED");
    }

    public boolean rejectReview(Long reviewId) {
        return updateStatus(reviewId, "REJECTED");
    }

    public boolean hideReview(Long reviewId) {
        return updateStatus(reviewId, "HIDDEN");
    }

    public boolean unhideReview(Long reviewId) {
        return updateStatus(reviewId, "APPROVED");
    }

    private boolean updateStatus(Long reviewId, String status) {
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);
        if (optionalReview.isPresent()) {
            Review review = optionalReview.get();
            review.setStatus(status);
            reviewRepository.save(review);
            return true;
        }
        return false;
    }

    public boolean hasReviewForAppointment(Long appointmentId) {
        return reviewRepository.existsByAppointment_AppointmentId(appointmentId);
    }

    public boolean isAppointmentCompleted(Long appointmentId) {
        Optional<Appointment> appointment = appointmentRepository.findById(appointmentId);
        return appointment.isPresent() && "COMPLETED".equalsIgnoreCase(appointment.get().getStatus());
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

package com.cjcc.yakalabs.sakurasaki.controller;

import com.cjcc.yakalabs.sakurasaki.model.Review;
import com.cjcc.yakalabs.sakurasaki.model.Appointment;
import com.cjcc.yakalabs.sakurasaki.service.AppointmentService;
import com.cjcc.yakalabs.sakurasaki.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping("/submit")
    public String showSubmitReviewForm(@RequestParam(required = false) Long appointmentId, Model model) {
        model.addAttribute("completedAppointments", appointmentService.getAppointmentsByStatus("COMPLETED"));
        if (appointmentId != null) {
            try {
                Appointment appointment = appointmentService.getAppointmentById(appointmentId);
                model.addAttribute("selectedAppointment", appointment);
            } catch (NumberFormatException ignored) {}
        }
        return "submit-review";
    }

    @PostMapping("/save")
    public String saveReview(@RequestParam Long appointmentId,
                             @RequestParam Long customerId,
                             @RequestParam(required = false) Long serviceId,
                             @RequestParam(required = false) Long staffId,
                             @RequestParam String reviewType,
                             @RequestParam int rating,
                             @RequestParam String comment,
                             Model model) {

        boolean success = reviewService.saveReview(
                appointmentId, customerId, serviceId, staffId, reviewType, rating, comment
        );

        if (success) {
            model.addAttribute("successMessage",
                    "Review submitted successfully. It has been sent to the admin for approval.");
        } else {
            model.addAttribute("errorMessage",
                    "Review could not be submitted. Appointment may not be completed or a review already exists.");
        }

        return "submit-review";
    }

    @GetMapping("/my")
    public String myReviews(@RequestParam Long customerId, Model model) {
        model.addAttribute("reviews", reviewService.getReviewsByCustomer(customerId));
        model.addAttribute("customerId", customerId);
        return "my-reviews";
    }

    @GetMapping("/edit/{reviewId}")
    public String editReviewForm(@PathVariable Long reviewId, Model model) {
        Review review = reviewService.getReviewById(reviewId);
        model.addAttribute("review", review);
        return "edit-review";
    }

    @PostMapping("/update")
    public String updateReview(@RequestParam Long reviewId,
                               @RequestParam Long customerId,
                               @RequestParam int rating,
                               @RequestParam String comment) {
        reviewService.updateReview(reviewId, customerId, rating, comment);
        return "redirect:/reviews/my?customerId=" + customerId;
    }

    @GetMapping("/delete/{reviewId}")
    public String deleteReview(@PathVariable Long reviewId,
                               @RequestParam Long customerId) {
        reviewService.deleteReview(reviewId, customerId);
        return "redirect:/reviews/my?customerId=" + customerId;
    }

    @GetMapping("/service/{serviceId}")
    public String viewApprovedServiceReviews(@PathVariable Long serviceId, Model model) {
        model.addAttribute("reviews", reviewService.getApprovedServiceReviews(serviceId));
        model.addAttribute("serviceId", serviceId);
        return "service-reviews";
    }

    @GetMapping("/staff/{staffId}")
    public String viewApprovedStaffReviews(@PathVariable Long staffId, Model model) {
        model.addAttribute("reviews", reviewService.getApprovedStaffReviews(staffId));
        model.addAttribute("staffId", staffId);
        return "staff-reviews";
    }

    @GetMapping("/admin")
    public String adminReviewPanel(Model model) {
        model.addAttribute("pendingReviews", reviewService.getPendingReviews());
        model.addAttribute("allReviews", reviewService.getAllReviews());

        model.addAttribute("totalReviews", reviewService.getAllReviews().size());
        model.addAttribute("pendingCount", reviewService.getPendingReviews().size());
        model.addAttribute("approvedCount", reviewService.getApprovedReviews().size());
        model.addAttribute("rejectedCount", reviewService.getRejectedReviews().size());
        model.addAttribute("hiddenCount", reviewService.getHiddenReviews().size());

        return "review-dashboard";
    }

    @GetMapping("/admin/edit/{reviewId}")
    public String adminEditReviewForm(@PathVariable Long reviewId, Model model) {
        Review review = reviewService.getReviewById(reviewId);
        model.addAttribute("review", review);
        return "admin-edit-review";
    }

    @PostMapping("/admin/update")
    public String adminUpdateReview(@RequestParam Long reviewId,
                                    @RequestParam int rating,
                                    @RequestParam String comment) {
        reviewService.adminUpdateReview(reviewId, rating, comment);
        return "redirect:/reviews/admin";
    }
    
    @GetMapping("/admin/analytics")
    public String adminAnalytics(Model model) {
        var allReviews = reviewService.getAllReviews();

        // Status counts
        model.addAttribute("approvedCount", reviewService.getApprovedReviews().size());
        model.addAttribute("pendingCount", reviewService.getPendingReviews().size());
        model.addAttribute("rejectedCount", reviewService.getRejectedReviews().size());
        model.addAttribute("hiddenCount", reviewService.getHiddenReviews().size());

        // Rating counts
        long star1 = allReviews.stream().filter(r -> r.getRating() == 1).count();
        long star2 = allReviews.stream().filter(r -> r.getRating() == 2).count();
        long star3 = allReviews.stream().filter(r -> r.getRating() == 3).count();
        long star4 = allReviews.stream().filter(r -> r.getRating() == 4).count();
        long star5 = allReviews.stream().filter(r -> r.getRating() == 5).count();

        model.addAttribute("star1", star1);
        model.addAttribute("star2", star2);
        model.addAttribute("star3", star3);
        model.addAttribute("star4", star4);
        model.addAttribute("star5", star5);

        // Type counts
        long serviceReviews = allReviews.stream().filter(r -> "SERVICE".equalsIgnoreCase(r.getReviewType())).count();
        long staffReviews = allReviews.stream().filter(r -> "STAFF".equalsIgnoreCase(r.getReviewType())).count();

        model.addAttribute("serviceReviews", serviceReviews);
        model.addAttribute("staffReviews", staffReviews);

        return "review-analytics";
    }
    
    @GetMapping("/approve/{reviewId}")
    public String approveReview(@PathVariable Long reviewId) {
        reviewService.approveReview(reviewId);
        return "redirect:/reviews/admin";
    }

    @GetMapping("/reject/{reviewId}")
    public String rejectReview(@PathVariable Long reviewId) {
        reviewService.rejectReview(reviewId);
        return "redirect:/reviews/admin";
    }

    @GetMapping("/hide/{reviewId}")
    public String hideReview(@PathVariable Long reviewId) {
        reviewService.hideReview(reviewId);
        return "redirect:/reviews/admin";
    }

    @GetMapping("/unhide/{reviewId}")
    public String unhideReview(@PathVariable Long reviewId) {
        reviewService.unhideReview(reviewId);
        return "redirect:/reviews/admin";
    }

    @GetMapping("/public")
    public String publicReviews(Model model) {
        model.addAttribute("approvedReviews", reviewService.getApprovedReviews());
        model.addAttribute("totalReviews", reviewService.getApprovedReviews().size());

        double averageRating = reviewService.getApprovedReviews().stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
        model.addAttribute("averageRating", averageRating);

        return "public-reviews";
    }
}

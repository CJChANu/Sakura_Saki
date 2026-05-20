package com.cjcc.yakalabs.sakurasaki.controller;

import com.cjcc.yakalabs.sakurasaki.service.ReviewService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/reviews")
public class AdminReviewController {
    private final ReviewService reviewService;

    public AdminReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public String index(Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        model.addAttribute("reviews", reviewService.getAll());
        return "admin/reviews";
    }

    @PostMapping("/{id}/toggle-visibility")
    public String toggleVisibility(@PathVariable Long id) {
        reviewService.toggleVisible(id);
        return "redirect:/admin/reviews";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        reviewService.delete(id);
        return "redirect:/admin/reviews";
    }
}

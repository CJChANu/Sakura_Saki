package com.cjcc.yakalabs.sakurasaki.controller;

import com.cjcc.yakalabs.sakurasaki.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("approvedReviews", reviewService.getApprovedReviews());
        model.addAttribute("totalReviews", reviewService.getApprovedReviews().size());
        model.addAttribute("averageRating", reviewService.getAverageApprovedRating());
        return "public-reviews";
    }
}
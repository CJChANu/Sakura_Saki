package com.cjcc.yakalabs.sakurasaki.controller;

import com.cjcc.yakalabs.sakurasaki.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import com.cjcc.yakalabs.sakurasaki.repository.ReviewRepository;
import org.springframework.security.core.Authentication;
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
    private final ReviewRepository reviewRepo;

    public HomeController(ReviewRepository reviewRepo) {
        this.reviewRepo = reviewRepo;
    }

    @GetMapping("/")
    public String index(Authentication auth, Model model) {
        // Pass auth state to template
        boolean loggedIn = (auth != null && auth.isAuthenticated()
                && !auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ANONYMOUS")));
        model.addAttribute("isLoggedIn", loggedIn);
        if (loggedIn) {
            model.addAttribute("username", auth.getName());
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            boolean isStaff = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_STAFF"));
            model.addAttribute("isAdmin", isAdmin);
            model.addAttribute("isStaff", isStaff);
        }

        // Recent reviews for homepage
        model.addAttribute("recentReviews", reviewRepo.findTop6ByVisibleOrderByCreatedAtDesc(true));

        return "index";
    }
}

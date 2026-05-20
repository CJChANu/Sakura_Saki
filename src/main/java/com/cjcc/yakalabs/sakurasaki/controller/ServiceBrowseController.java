package com.cjcc.yakalabs.sakurasaki.controller;

import com.cjcc.yakalabs.sakurasaki.repository.ReviewRepository;
import com.cjcc.yakalabs.sakurasaki.service.SalonServiceService;
import com.cjcc.yakalabs.sakurasaki.service.ServicePackageService;
import com.cjcc.yakalabs.sakurasaki.service.StaffService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ServiceBrowseController {

    private final SalonServiceService salonServiceService;
    private final ServicePackageService packageService;
    private final StaffService staffService;
    private final ReviewRepository reviewRepo;

    public ServiceBrowseController(SalonServiceService salonServiceService,
                                    ServicePackageService packageService,
                                    StaffService staffService,
                                    ReviewRepository reviewRepo) {
        this.salonServiceService = salonServiceService;
        this.packageService = packageService;
        this.staffService = staffService;
        this.reviewRepo = reviewRepo;
    }

    @GetMapping("/services")
    public String browseServices(@RequestParam(required = false) String search,
                                  Authentication auth, Model model) {
        if (search != null && !search.isBlank()) {
            model.addAttribute("services", salonServiceService.searchByName(search));
            model.addAttribute("search", search);
        } else {
            model.addAttribute("services", salonServiceService.findActive());
        }
        model.addAttribute("packages", packageService.findActive());

        // Auth state for nav
        boolean loggedIn = (auth != null && auth.isAuthenticated());
        model.addAttribute("isLoggedIn", loggedIn);
        if (loggedIn) model.addAttribute("username", auth.getName());

        return "public/services";
    }

    @GetMapping("/services/{id}")
    public String serviceDetail(@PathVariable Long id, Authentication auth, Model model) {
        var service = salonServiceService.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));
        model.addAttribute("service", service);

        // Load reviews for this service
        model.addAttribute("reviews", reviewRepo.findByServiceIdAndVisible(id, true));
        model.addAttribute("avgRating", reviewRepo.averageRatingByServiceId(id));

        // Load staff for inline booking
        model.addAttribute("staffList", staffService.findActive());

        // Auth state
        boolean loggedIn = (auth != null && auth.isAuthenticated());
        model.addAttribute("isLoggedIn", loggedIn);
        if (loggedIn) {
            model.addAttribute("username", auth.getName());
            boolean isStaff = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_STAFF"));
            model.addAttribute("isStaff", isStaff);
        } else {
            model.addAttribute("isStaff", false);
        }

        return "public/service-detail";
    }

    @GetMapping("/services/package/{id}")
    public String packageDetail(@PathVariable Long id, Authentication auth, Model model) {
        var pkg = packageService.findById(id)
                .orElseThrow(() -> new RuntimeException("Package not found"));
        model.addAttribute("pkg", pkg);

        boolean loggedIn = (auth != null && auth.isAuthenticated());
        model.addAttribute("isLoggedIn", loggedIn);
        if (loggedIn) model.addAttribute("username", auth.getName());

        return "public/package-detail";
    }
}

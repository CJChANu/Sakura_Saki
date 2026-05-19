package com.cjcc.yakalabs.sakurasaki.controller;

import com.cjcc.yakalabs.sakurasaki.model.User;
import com.cjcc.yakalabs.sakurasaki.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for customer profile viewing and editing.
 * OOP: Abstraction — delegates all business logic to UserService.
 */
@Controller
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Display the customer's profile page.
     */
    @GetMapping("/profile")
    public String viewProfile(Authentication auth, Model model) {
        User user = userService.findByUsername(auth.getName());
        model.addAttribute("user", user);
        model.addAttribute("username", auth.getName());

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("isAdmin", isAdmin);

        return "customer/profile";
    }

    /**
     * Display the profile edit form.
     */
    @GetMapping("/profile/edit")
    public String editProfileForm(Authentication auth, Model model) {
        User user = userService.findByUsername(auth.getName());
        model.addAttribute("user", user);
        model.addAttribute("username", auth.getName());
        return "customer/edit-profile";
    }

    /**
     * Handle profile update form submission.
     */
    @PostMapping("/profile/edit")
    public String updateProfile(
            Authentication auth,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String phone,
            @RequestParam(required = false) String newPassword,
            RedirectAttributes redirectAttributes) {

        try {
            userService.updateProfile(auth.getName(), firstName, lastName, phone, newPassword);
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/profile/edit";
        }

        return "redirect:/profile";
    }
}

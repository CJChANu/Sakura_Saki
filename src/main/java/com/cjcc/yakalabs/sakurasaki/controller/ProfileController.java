package com.cjcc.yakalabs.sakurasaki.controller;

import com.cjcc.yakalabs.sakurasaki.model.Customer;
import com.cjcc.yakalabs.sakurasaki.model.Staff;
import com.cjcc.yakalabs.sakurasaki.model.User;
import com.cjcc.yakalabs.sakurasaki.repository.UserRepository;
import com.cjcc.yakalabs.sakurasaki.service.CustomerService;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final CustomerService customerService;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public ProfileController(CustomerService customerService, UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.customerService = customerService;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String viewProfile(Authentication auth, Model model) {
        User user = userRepo.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("user", user);
        model.addAttribute("username", auth.getName());

        // Staff users get a staff-specific profile page
        if (user instanceof Staff staffMember) {
            model.addAttribute("staff", staffMember);
            return "staff/profile";
        }

        // Customer users
        Customer customer = customerService.findByEmail(user.getEmail()).orElse(null);
        model.addAttribute("customer", customer);
        return "customer/profile";
    }

    @GetMapping("/edit")
    public String editProfileForm(Authentication auth, Model model) {
        User user = userRepo.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Staff profiles are managed by admin — redirect back
        if (user instanceof Staff) {
            return "redirect:/profile";
        }

        Customer customer = customerService.findByEmail(user.getEmail()).orElse(null);
        model.addAttribute("user", user);
        model.addAttribute("customer", customer);
        model.addAttribute("username", auth.getName());
        return "customer/edit-profile";
    }

    @PostMapping("/update")
    public String updateProfile(@RequestParam String firstName,
                                @RequestParam String lastName,
                                @RequestParam String phone,
                                Authentication auth,
                                RedirectAttributes redirectAttributes) {
        User user = userRepo.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Customer customer = customerService.findByEmail(user.getEmail()).orElse(null);

        if (customer != null) {
            customerService.updateCustomer(customer.getId(), firstName, lastName, customer.getEmail(), phone);
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Customer profile not found.");
        }
        return "redirect:/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 Authentication auth,
                                 RedirectAttributes redirectAttributes) {
        User user = userRepo.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "Current password is incorrect.");
            return "redirect:/profile";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
        redirectAttributes.addFlashAttribute("success", "Password changed successfully!");
        return "redirect:/profile";
    }
}

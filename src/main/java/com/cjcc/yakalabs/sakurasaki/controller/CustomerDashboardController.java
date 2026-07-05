package com.cjcc.yakalabs.sakurasaki.controller;

import com.cjcc.yakalabs.sakurasaki.model.Appointment;
import com.cjcc.yakalabs.sakurasaki.model.Customer;
import com.cjcc.yakalabs.sakurasaki.model.User;
import com.cjcc.yakalabs.sakurasaki.repository.AppointmentRepository;
import com.cjcc.yakalabs.sakurasaki.repository.ReviewRepository;
import com.cjcc.yakalabs.sakurasaki.repository.CustomerRepository;
import com.cjcc.yakalabs.sakurasaki.repository.UserRepository;
import com.cjcc.yakalabs.sakurasaki.service.AppointmentService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.cjcc.yakalabs.sakurasaki.service.CustomerService;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/customer")
public class CustomerDashboardController {

    private final UserRepository userRepo;
    private final CustomerRepository customerRepo;
    private final AppointmentService appointmentService;
    private final CustomerService customerService;
    private final PasswordEncoder passwordEncoder;
    private final AppointmentRepository appointmentRepo;
    private final ReviewRepository reviewRepo;
    private final com.cjcc.yakalabs.sakurasaki.repository.RedeemedRewardRepository rewardRepo;

    public CustomerDashboardController(UserRepository userRepo,
                                        CustomerRepository customerRepo,
                                        AppointmentService appointmentService,
                                        CustomerService customerService,
                                        PasswordEncoder passwordEncoder,
                                        AppointmentRepository appointmentRepo,
                                        ReviewRepository reviewRepo,
                                        com.cjcc.yakalabs.sakurasaki.repository.RedeemedRewardRepository rewardRepo) {
        this.userRepo = userRepo;
        this.customerRepo = customerRepo;
        this.appointmentService = appointmentService;
        this.customerService = customerService;
        this.passwordEncoder = passwordEncoder;
        this.appointmentRepo = appointmentRepo;
        this.reviewRepo = reviewRepo;
        this.rewardRepo = rewardRepo;
    }

    private Customer getCustomer(Authentication auth) {
        User user = userRepo.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return customerRepo.findByEmail(user.getEmail()).orElse(null);
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        Customer customer = getCustomer(auth);
        model.addAttribute("customer", customer);
        model.addAttribute("nextAppointment", null);
        model.addAttribute("recentHistory", List.of());
        model.addAttribute("completedCount", 0L);
        model.addAttribute("loyaltyPoints", 0);
        model.addAttribute("loyaltyProgress", 0);

        if (customer != null) {
            int loyaltyPoints = customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : 0;
            model.addAttribute("loyaltyPoints", loyaltyPoints);
            model.addAttribute("loyaltyProgress", (loyaltyPoints % 1000) / 10);

            // Next upcoming appointment
            Optional<Appointment> nextAppt = appointmentService.getNextAppointmentForCustomer(customer.getId());
            model.addAttribute("nextAppointment", nextAppt.orElse(null));

            // Recent appointment history
            List<Appointment> allAppointments = appointmentService.findByCustomer(customer.getId());
            model.addAttribute("recentHistory", allAppointments.stream().limit(3).toList());

            // Completed count for display
            long completedCount = appointmentService.countCompletedForCustomer(customer.getId());
            model.addAttribute("completedCount", completedCount);
        }

        return "customer/dashboard";
    }

    @GetMapping("/bookings")
    public String bookings(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size,
                           Authentication auth, Model model) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        model.addAttribute("username", auth.getName());
        Customer customer = getCustomer(auth);
        model.addAttribute("customer", customer);

        if (customer != null) {
            org.springframework.data.domain.Page<Appointment> appointments = appointmentService.findByCustomer(customer.getId(), pageable);
            model.addAttribute("appointments", appointments);

            // Get ALL scheduled appointments for the calendar, regardless of pagination
            List<Appointment> allAppointments = appointmentService.findByCustomer(customer.getId());
            List<String> upcomingDates = allAppointments.stream()
                    .filter(a -> "SCHEDULED".equals(a.getStatus()))
                    .map(a -> a.getAppointmentDate().toString())
                    .toList();
            model.addAttribute("allUpcomingAppointments", upcomingDates);

            // Collect IDs of appointments that already have a review
            Set<Long> reviewedIds = appointments.getContent().stream()
                    .filter(a -> "COMPLETED".equals(a.getStatus()))
                    .filter(a -> reviewRepo.existsByAppointmentId(a.getId()))
                    .map(Appointment::getId)
                    .collect(Collectors.toSet());
            model.addAttribute("reviewedIds", reviewedIds);
        }

        return "customer/bookings";
    }

    @GetMapping("/loyalty")
    public String loyalty(Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        Customer customer = getCustomer(auth);
        model.addAttribute("customer", customer);

        if (customer != null) {
            long completedCount = appointmentService.countCompletedForCustomer(customer.getId());
            model.addAttribute("completedCount", completedCount);

            List<com.cjcc.yakalabs.sakurasaki.model.RedeemedReward> vouchers = 
                rewardRepo.findByCustomerIdOrderByRedeemedDateDesc(customer.getId()).stream()
                .filter(v -> !v.isUsed())
                .collect(Collectors.toList());
            model.addAttribute("vouchers", vouchers);
        }

        return "customer/loyalty";
    }

    @PostMapping("/loyalty/redeem")
    public String redeemLoyaltyPoints(@RequestParam("reward") String reward, 
                                      @RequestParam("points") int points, 
                                      Authentication auth, 
                                      RedirectAttributes redirectAttributes) {
        Customer customer = getCustomer(auth);
        if (customer != null) {
            try {
                customerService.redeemPoints(customer, points, reward);
                redirectAttributes.addFlashAttribute("success", "Reward redeemed successfully: " + reward + ". Your voucher is now active.");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", e.getMessage());
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Customer profile not found.");
        }
        return "redirect:/customer/loyalty";
    }

    @PostMapping("/loyalty/use-voucher")
    public String useVoucher(@RequestParam("voucherId") Long voucherId, 
                             @RequestParam("staffCode") String staffCode,
                             Authentication auth, RedirectAttributes redirectAttributes) {
        try {
            if (!"8888".equals(staffCode)) {
                throw new RuntimeException("Invalid Staff PIN.");
            }
            customerService.markRewardAsUsed(voucherId);
            redirectAttributes.addFlashAttribute("success", "Voucher marked as used successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/customer/loyalty";
    }

    @GetMapping("/rituals")
    public String rituals(Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        Customer customer = getCustomer(auth);
        model.addAttribute("customer", customer);
        return "customer/rituals";
    }

    @GetMapping("/settings")
    public String settings(Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        Customer customer = getCustomer(auth);
        model.addAttribute("customer", customer);
        
        User user = userRepo.findByUsername(auth.getName()).orElse(null);
        model.addAttribute("user", user);
        
        return "customer/settings";
    }

    @PostMapping("/settings/update")
    public String updateSettings(@RequestParam String firstName,
                                 @RequestParam String lastName,
                                 @RequestParam String phone,
                                 Authentication auth,
                                 RedirectAttributes redirectAttributes) {
        Customer customer = getCustomer(auth);
        if (customer != null) {
            customerService.updateCustomer(customer.getId(), firstName, lastName, customer.getEmail(), phone);
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Customer profile not found.");
        }
        return "redirect:/customer/settings";
    }

    @PostMapping("/settings/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 Authentication auth,
                                 RedirectAttributes redirectAttributes) {
        User user = userRepo.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "Current password is incorrect.");
            return "redirect:/customer/settings";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
        redirectAttributes.addFlashAttribute("success", "Password changed successfully!");
        return "redirect:/customer/settings";
    }

    @PostMapping("/settings/delete-account")
    public String deleteAccount(Authentication auth, jakarta.servlet.http.HttpServletRequest request) throws jakarta.servlet.ServletException {
        User user = userRepo.findByUsername(auth.getName()).orElse(null);
        if (user != null) {
            // Soft delete: disable account and anonymize personal data
            // This preserves historical appointments and reviews for reporting
            user.setEnabled(false);
            user.setFirstName("Deleted");
            user.setLastName("User");
            user.setPhone(null);
            // Scramble username and email to free up uniqueness constraints
            String anonymizedSuffix = "_deleted_" + user.getId();
            user.setUsername("deleted_user" + anonymizedSuffix);
            user.setEmail("deleted" + anonymizedSuffix + "@removed.local");
            userRepo.save(user);
        }
        request.logout();
        return "redirect:/?deleted=true";
    }
}

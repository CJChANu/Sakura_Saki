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

    public CustomerDashboardController(UserRepository userRepo,
                                        CustomerRepository customerRepo,
                                        AppointmentService appointmentService,
                                        CustomerService customerService,
                                        PasswordEncoder passwordEncoder,
                                        AppointmentRepository appointmentRepo,
                                        ReviewRepository reviewRepo) {
        this.userRepo = userRepo;
        this.customerRepo = customerRepo;
        this.appointmentService = appointmentService;
        this.customerService = customerService;
        this.passwordEncoder = passwordEncoder;
        this.appointmentRepo = appointmentRepo;
        this.reviewRepo = reviewRepo;
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

        if (customer != null) {
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
    public String bookings(Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        Customer customer = getCustomer(auth);
        model.addAttribute("customer", customer);

        if (customer != null) {
            model.addAttribute("appointments", appointmentService.findByCustomer(customer.getId()));
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
        }

        return "customer/loyalty";
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
            // Hard delete: delete related appointments and reviews first
            appointmentRepo.deleteAll(appointmentRepo.findByCustomerId(user.getId()));
            reviewRepo.deleteAll(reviewRepo.findByCustomerId(user.getId()));
            
            // Now safe to hard delete the user
            userRepo.delete(user);
        }
        request.logout();
        return "redirect:/?deleted=true";
    }
}

package com.cjcc.yakalabs.sakurasaki.controller;

import com.cjcc.yakalabs.sakurasaki.model.Customer;
import com.cjcc.yakalabs.sakurasaki.model.User;
import com.cjcc.yakalabs.sakurasaki.repository.CustomerRepository;
import com.cjcc.yakalabs.sakurasaki.repository.UserRepository;
import com.cjcc.yakalabs.sakurasaki.service.AppointmentService;
import com.cjcc.yakalabs.sakurasaki.service.SalonServiceService;
import com.cjcc.yakalabs.sakurasaki.service.StaffService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;

@Controller
public class BookingController {

    private final AppointmentService appointmentService;
    private final SalonServiceService salonServiceService;
    private final StaffService staffService;
    private final UserRepository userRepo;
    private final CustomerRepository customerRepo;

    public BookingController(AppointmentService appointmentService,
                             SalonServiceService salonServiceService,
                             StaffService staffService,
                             UserRepository userRepo,
                             CustomerRepository customerRepo) {
        this.appointmentService = appointmentService;
        this.salonServiceService = salonServiceService;
        this.staffService = staffService;
        this.userRepo = userRepo;
        this.customerRepo = customerRepo;
    }

    @GetMapping("/booking")
    public String bookingForm(@RequestParam(required = false) Long serviceId,
                              Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        model.addAttribute("services", salonServiceService.findActive());
        model.addAttribute("staffList", staffService.findActive());
        if (serviceId != null) {
            model.addAttribute("selectedServiceId", serviceId);
        }
        return "booking/form";
    }

    @PostMapping("/booking")
    public String submitBooking(@RequestParam Long serviceId,
                                @RequestParam Long staffId,
                                @RequestParam String date,
                                @RequestParam String time,
                                @RequestParam(required = false) String notes,
                                @RequestParam(required = false) String returnTo,
                                Authentication auth,
                                RedirectAttributes redirectAttributes) {
        try {
            // Customer IS a User now — find by username via CustomerRepository
            User user = userRepo.findByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Customer customer = customerRepo.findByEmail(user.getEmail())
                    .orElseThrow(() -> new RuntimeException("Please complete your profile before booking."));

            LocalDate appointmentDate = LocalDate.parse(date);
            LocalTime appointmentTime = LocalTime.parse(time);

            appointmentService.createAppointment(customer.getId(), serviceId, staffId,
                    appointmentDate, appointmentTime, notes);
            redirectAttributes.addFlashAttribute("success", "Appointment booked successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            if (returnTo != null && !returnTo.isBlank()) {
                return "redirect:" + returnTo;
            }
        }
        return "redirect:/my-appointments";
    }

    @GetMapping("/my-appointments")
    public String myAppointments(Authentication auth, Model model) {
        User user = userRepo.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Customer customer = customerRepo.findByEmail(user.getEmail()).orElse(null);

        model.addAttribute("username", auth.getName());
        if (customer != null) {
            model.addAttribute("appointments", appointmentService.findByCustomer(customer.getId()));
        }
        model.addAttribute("customer", customer);
        return "booking/my-appointments";
    }

    @PostMapping("/my-appointments/{id}/cancel")
    public String cancelAppointment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            appointmentService.cancelAppointment(id);
            redirectAttributes.addFlashAttribute("success", "Appointment cancelled.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/my-appointments";
    }
}

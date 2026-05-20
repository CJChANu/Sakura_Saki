package com.cjcc.yakalabs.sakurasaki.controller;

import com.cjcc.yakalabs.sakurasaki.model.*;
import com.cjcc.yakalabs.sakurasaki.model.Package;
import com.cjcc.yakalabs.sakurasaki.repository.UserRepository;
import com.cjcc.yakalabs.sakurasaki.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Controller
public class CustomerController {
    private final ServiceService serviceService;
    private final PackageService packageService;
    private final StaffService staffService;
    private final AppointmentService appointmentService;
    private final ReviewService reviewService;
    private final UserRepository userRepository;

    public CustomerController(ServiceService serviceService, PackageService packageService,
                              StaffService staffService, AppointmentService appointmentService,
                              ReviewService reviewService, UserRepository userRepository) {
        this.serviceService = serviceService;
        this.packageService = packageService;
        this.staffService = staffService;
        this.appointmentService = appointmentService;
        this.reviewService = reviewService;
        this.userRepository = userRepository;
    }

    @GetMapping("/services")
    public String publicServices(Authentication auth, Model model) {
        setAuthModel(auth, model);
        model.addAttribute("services", serviceService.getActive());
        model.addAttribute("packages", packageService.getActive());
        return "services";
    }

    @GetMapping("/staff")
    public String publicStaff(Authentication auth, Model model) {
        setAuthModel(auth, model);
        model.addAttribute("staffList", staffService.getActive());
        return "staff";
    }

    @GetMapping("/profile")
    public String profile(@RequestParam(required = false) String select, Authentication auth, Model model) {
        if (auth == null || !auth.isAuthenticated()) return "redirect:/login";
        User user = userRepository.findByUsername(auth.getName()).orElse(null);
        if (user == null) return "redirect:/login";
        model.addAttribute("username", auth.getName());
        model.addAttribute("user", user);
        model.addAttribute("services", serviceService.getActive());
        model.addAttribute("packages", packageService.getActive());
        model.addAttribute("staffList", staffService.getActive());
        model.addAttribute("bookings", appointmentService.getAppointmentsByUserId(auth.getName()));
        model.addAttribute("selectedItem", select);
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("isAdmin", isAdmin);
        return "profile";
    }

    @GetMapping("/booking")
    public String booking(@RequestParam(required = false) String select,
                          @RequestParam(required = false) Long reschedule,
                          Authentication auth, Model model) {
        if (auth == null || !auth.isAuthenticated()) return "redirect:/login";
        model.addAttribute("username", auth.getName());
        model.addAttribute("services", serviceService.getActive());
        model.addAttribute("packages", packageService.getActive());
        model.addAttribute("staffList", staffService.getActive());
        model.addAttribute("selectedItem", select);
        model.addAttribute("rescheduleId", reschedule);
        model.addAttribute("bookings", appointmentService.getAppointmentsByUserId(auth.getName()));
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("isAdmin", isAdmin);
        return "booking";
    }

    @GetMapping("/appointments/history")
    public String history(Authentication auth, Model model) {
        if (auth == null || !auth.isAuthenticated()) return "redirect:/login";
        User user = userRepository.findByUsername(auth.getName()).orElse(null);
        if (user == null) return "redirect:/login";
        model.addAttribute("username", auth.getName());
        model.addAttribute("user", user);
        model.addAttribute("bookings", appointmentService.getAppointmentsByUserId(auth.getName()));
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("isAdmin", isAdmin);
        return "history";
    }

    @PostMapping("/appointments/book")
    public String book(@RequestParam(required = false) Long appointmentId,
                       @RequestParam String selectionId, @RequestParam String staffId,
                       @RequestParam String date, @RequestParam String time,
                       @RequestParam(required = false) String notes,
                       Authentication auth, RedirectAttributes redir) {
        if (auth == null) return "redirect:/login";
        try {
            Appointment oldAppointment = null;
            boolean isConfirming = false;
            
            if (appointmentId != null) {
                oldAppointment = appointmentService.getAppointmentById(appointmentId);
                if (oldAppointment == null || !oldAppointment.getUserId().equals(auth.getName())) {
                    throw new IllegalArgumentException("Invalid appointment.");
                }
                isConfirming = true;
            }
            
            // Always create a new row for the new time
            Appointment a = new Appointment();
            a.setUserId(auth.getName());
            a.setStaffId(staffId);
            a.setAppointmentDate(LocalDate.parse(date));
            a.setAppointmentTime(LocalTime.parse(time));
            a.setNotes(notes);
            
            a.setStatus("CONFIRMED");

            if (selectionId.startsWith("SVC_")) {
                Service s = serviceService.getById(Long.parseLong(selectionId.substring(4)));
                if (s == null) throw new IllegalArgumentException("Service not found.");
                a.setServiceId(s.getName());
                a.setDurationMinutes(s.getDurationMinutes());
                a.setTotalAmount(s.getPrice());
                a.setPackageId(null); // Clear package if service selected
            } else if (selectionId.startsWith("PKG_")) {
                Package p = packageService.getById(Long.parseLong(selectionId.substring(4)));
                if (p == null) throw new IllegalArgumentException("Package not found.");
                a.setPackageId(p.getName());
                a.setDurationMinutes(p.getTotalDuration());
                a.setTotalAmount(p.getDiscountedPrice());
                a.setServiceId(null); // Clear service if package selected
            } else {
                throw new IllegalArgumentException("Invalid selection.");
            }

            if (isConfirming) {
                oldAppointment.setStatus("RESCHEDULED");
                appointmentService.updateAppointment(oldAppointment); // Save old row with new status
                appointmentService.saveAppointment(a); // Save the new row
                redir.addFlashAttribute("success", "Ritual rescheduled successfully! We look forward to serving you.");
            } else {
                appointmentService.saveAppointment(a); // Save the new row
                redir.addFlashAttribute("success", "Ritual booked successfully! We are excited to serve you.");
            }
        } catch (Exception e) {
            redir.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/appointments/history";
    }

    @PostMapping("/appointments/{id}/cancel")
    public String cancel(@PathVariable Long id, Authentication auth, RedirectAttributes redir) {
        if (auth == null) return "redirect:/login";
        Appointment a = appointmentService.getAppointmentById(id);
        if (a != null && a.getUserId().equals(auth.getName())) {
            appointmentService.updateStatus(id, "CANCELLED");
            redir.addFlashAttribute("success", "Ritual cancelled successfully.");
        }
        return "redirect:/appointments/history";
    }

    @PostMapping("/reviews/submit")
    public String submitReview(@RequestParam Long appointmentId, @RequestParam int rating,
                               @RequestParam String comment, Authentication auth, RedirectAttributes redir) {
        if (auth == null) return "redirect:/login";
        Appointment a = appointmentService.getAppointmentById(appointmentId);
        User u = userRepository.findByUsername(auth.getName()).orElse(null);
        if (a != null && u != null && a.getUserId().equals(auth.getName()) && "COMPLETED".equals(a.getStatus())) {
            Review r = new Review();
            r.setCustomer(u);
            r.setRating(rating);
            r.setComment(comment);
            Optional<Service> sOpt = serviceService.getActive().stream().filter(s -> s.getName().equals(a.getServiceId())).findFirst();
            sOpt.ifPresent(r::setService);
            reviewService.save(r);
            redir.addFlashAttribute("success", "Thank you! Your review has been submitted for moderation.");
        }
        return "redirect:/profile";
    }

    private void setAuthModel(Authentication auth, Model model) {
        boolean loggedIn = (auth != null && auth.isAuthenticated()
                && !auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ANONYMOUS")));
        model.addAttribute("isLoggedIn", loggedIn);
        if (loggedIn) {
            model.addAttribute("username", auth.getName());
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            model.addAttribute("isAdmin", isAdmin);
        }
    }
}

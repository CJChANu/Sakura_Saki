package com.cjcc.yakalabs.sakurasaki.controller;

import com.cjcc.yakalabs.sakurasaki.model.Staff;
import com.cjcc.yakalabs.sakurasaki.repository.StaffRepository;
import com.cjcc.yakalabs.sakurasaki.repository.UserRepository;
import com.cjcc.yakalabs.sakurasaki.service.AppointmentService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/staff")
public class StaffDashboardController {

    private final UserRepository userRepo;
    private final StaffRepository staffRepo;
    private final AppointmentService appointmentService;

    public StaffDashboardController(UserRepository userRepo, StaffRepository staffRepo,
                                     AppointmentService appointmentService) {
        this.userRepo = userRepo;
        this.staffRepo = staffRepo;
        this.appointmentService = appointmentService;
    }

    @GetMapping("/dashboard")
    public String staffDashboard(Authentication auth, Model model) {
        var user = userRepo.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("username", auth.getName());

        // Find the Staff entity for this user (Staff extends User, same ID)
        if (user instanceof Staff staffMember) {
            model.addAttribute("staff", staffMember);
            var appointments = appointmentService.findByStaff(staffMember.getId());
            model.addAttribute("appointments", appointments);
            // Count today's appointments
            long todayCount = appointments.stream()
                    .filter(a -> a.getAppointmentDate().equals(LocalDate.now()))
                    .count();
            model.addAttribute("todayCount", todayCount);
        } else {
            // Admin viewing staff dashboard — show all staff appointments
            model.addAttribute("allStaffAppointments", appointmentService.findAll());
        }
        return "staff/dashboard";
    }

    @PostMapping("/appointments/{id}/complete")
    public String completeAppointment(@PathVariable Long id, Authentication auth,
                                       RedirectAttributes redirectAttributes) {
        try {
            appointmentService.changeStatus(id, "COMPLETED");
            redirectAttributes.addFlashAttribute("success", "Appointment marked as completed.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/staff/dashboard";
    }
}

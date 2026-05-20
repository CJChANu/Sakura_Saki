package com.cjcc.yakalabs.sakurasaki.controller;

import com.cjcc.yakalabs.sakurasaki.model.User;
import com.cjcc.yakalabs.sakurasaki.service.AdminService;
import com.cjcc.yakalabs.sakurasaki.service.DashboardService;
import com.cjcc.yakalabs.sakurasaki.service.UserService;
import com.cjcc.yakalabs.sakurasaki.service.AppointmentService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final AdminService adminService;
    private final DashboardService dashboardService;
    private final AppointmentService appointmentService;

    public AdminController(UserService userService, AdminService adminService, DashboardService dashboardService, AppointmentService appointmentService) {
        this.userService = userService;
        this.adminService = adminService;
        this.dashboardService = dashboardService;
        this.appointmentService = appointmentService;
    }

    // ---- Dashboard (Thymeleaf view) ----
    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        model.addAttribute("summary", dashboardService.getSummary());
        model.addAttribute("appointments", appointmentService.getAllAppointments());
        return "admin/dashboard";
    }

    // ---- Reports (Thymeleaf view) ----
    @GetMapping("/reports")
    public String reports(Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        model.addAttribute("summary", dashboardService.getSummary());
        return "admin/reports";
    }

    // ---- User Management (Thymeleaf view) ----
    @GetMapping("/users")
    public String users(@RequestParam(required = false) String search, Authentication auth, Model model) {
        List<User> users;
        if (search != null && !search.isBlank()) {
            users = adminService.searchUsers(search);
        } else {
            users = userService.findAll();
        }
        model.addAttribute("username", auth.getName());
        model.addAttribute("users", users);
        model.addAttribute("search", search);
        return "admin/users";
    }

    // ---- Make a user admin ----
    @PostMapping("/users/{id}/make-admin")
    public String makeAdmin(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.makeAdmin(id);
        redirectAttributes.addFlashAttribute("success", "User promoted to Administrator.");
        return "redirect:/admin/users";
    }

    // ---- Toggle user enabled/disabled ----
    @PostMapping("/users/{id}/toggle-enabled")
    public String toggleEnabled(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.toggleEnabled(id);
            redirectAttributes.addFlashAttribute("success", "User status updated.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    // ---- Admin Management (Thymeleaf view) ----
    @GetMapping("/manage")
    public String manageAdmins(Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        model.addAttribute("admins", adminService.listAdmins());
        return "admin/manage";
    }

    // ---- Create a new admin ----
    @PostMapping("/manage/create")
    public String createAdmin(@RequestParam String username,
                              @RequestParam String password,
                              @RequestParam String email,
                              @RequestParam(required = false, defaultValue = "ADMIN") String adminLevel,
                              RedirectAttributes redirectAttributes) {
        try {
            adminService.createAdmin(username, password, email, adminLevel);
            redirectAttributes.addFlashAttribute("success", "Admin '" + username + "' created successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create admin: " + e.getMessage());
        }
        return "redirect:/admin/manage";
    }

    // ---- Update admin details ----
    @PostMapping("/manage/{id}/update")
    public String updateAdmin(@PathVariable Long id,
                              @RequestParam String username,
                              @RequestParam String email,
                              @RequestParam(required = false) String adminLevel,
                              RedirectAttributes redirectAttributes) {
        adminService.updateAdmin(id, username, email, adminLevel);
        redirectAttributes.addFlashAttribute("success", "Admin updated successfully.");
        return "redirect:/admin/manage";
    }

    // ---- Deactivate (soft-delete) an admin ----
    @PostMapping("/manage/{id}/deactivate")
    public String deactivateAdmin(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.deactivateAdmin(id);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/manage";
    }

    // ---- Demote admin to regular user ----
    @PostMapping("/manage/{id}/demote")
    public String demoteAdmin(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.changeRole(id, "ROLE_USER");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/manage";
    }
}
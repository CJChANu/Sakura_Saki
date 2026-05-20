package com.cjcc.yakalabs.sakurasaki.controller;

import com.cjcc.yakalabs.sakurasaki.model.User;
import com.cjcc.yakalabs.sakurasaki.service.AdminService;
import com.cjcc.yakalabs.sakurasaki.service.DashboardService;
import com.cjcc.yakalabs.sakurasaki.service.UserService;
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

    public AdminController(UserService userService, AdminService adminService, DashboardService dashboardService) {
        this.userService = userService;
        this.adminService = adminService;
        this.dashboardService = dashboardService;
    }

    // ---- Dashboard (Thymeleaf view) ----
    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        model.addAttribute("summary", dashboardService.getSummary());
        return "admin/dashboard";
    }

    // ---- Redirects to Service Module ----
    @GetMapping("/services")
    public String servicesRedirect() {
        return "redirect:/services";
    }

    @GetMapping("/packages")
    public String packagesRedirect() {
        return "redirect:/packages";
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
    public String makeAdmin(@PathVariable Long id) {
        userService.makeAdmin(id);
        return "redirect:/admin/users";
    }

    // ---- Toggle user enabled/disabled ----
    @PostMapping("/users/{id}/toggle-enabled")
    public String toggleEnabled(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.toggleEnabled(id);
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
                              RedirectAttributes redirectAttributes) {
        try {
            adminService.createAdmin(username, password, email);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create admin: " + e.getMessage());
        }
        return "redirect:/admin/manage";
    }

    // ---- Update admin details ----
    @PostMapping("/manage/{id}/update")
    public String updateAdmin(@PathVariable Long id,
                              @RequestParam String username,
                              @RequestParam String email) {
        adminService.updateAdmin(id, username, email);
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

    // ===== Customer Management (Member 1 — Customer & Authentication Module) =====

    /**
     * List all customers with optional search.
     */
    @GetMapping("/customers")
    public String customers(@RequestParam(required = false) String search,
                            Authentication auth, Model model) {
        List<User> customers;
        if (search != null && !search.isBlank()) {
            customers = userService.searchCustomers(search);
        } else {
            customers = userService.findAllCustomers();
        }
        model.addAttribute("username", auth.getName());
        model.addAttribute("customers", customers);
        model.addAttribute("search", search);
        return "admin/customer-list";
    }

    /**
     * Deactivate a customer account (soft delete).
     */
    @PostMapping("/customers/{id}/deactivate")
    public String deactivateCustomer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deactivateUser(id);
            redirectAttributes.addFlashAttribute("success", "Customer deactivated successfully.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/customers";
    }

    /**
     * Activate a customer account.
     */
    @PostMapping("/customers/{id}/activate")
    public String activateCustomer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.activateUser(id);
            redirectAttributes.addFlashAttribute("success", "Customer activated successfully.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/customers";
    }

    /**
     * Permanently delete a customer account.
     */
    @PostMapping("/customers/{id}/delete")
    public String deleteCustomer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "Customer deleted permanently.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/customers";
    }
}
package com.cjcc.yakalabs.sakurasaki.controller;

import com.cjcc.yakalabs.sakurasaki.model.User;
import com.cjcc.yakalabs.sakurasaki.service.AdminService;
import com.cjcc.yakalabs.sakurasaki.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final AdminService adminService;

    public AdminController(UserService userService, AdminService adminService) {
        this.userService = userService;
        this.adminService = adminService;
    }

    // ---- Dashboard (Thymeleaf view) ----
    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        model.addAttribute("usersCount", userService.findAll().size());
        return "admin/dashboard";
    }

    // ---- User Management (Thymeleaf view) ----
    @GetMapping("/users")
    public String users(@RequestParam(required = false) String search, Model model) {
        List<User> users;
        if (search != null && !search.isBlank()) {
            users = adminService.searchUsers(search);
        } else {
            users = userService.findAll();
        }
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
    public String toggleEnabled(@PathVariable Long id) {
        adminService.toggleEnabled(id);
        return "redirect:/admin/users";
    }

    // ---- Admin Management (Thymeleaf view) ----
    @GetMapping("/manage")
    public String manageAdmins(Model model) {
        model.addAttribute("admins", adminService.listAdmins());
        return "admin/manage";
    }

    // ---- Create a new admin ----
    @PostMapping("/manage/create")
    public String createAdmin(@RequestParam String username,
                              @RequestParam String password,
                              @RequestParam String email,
                              Model model) {
        try {
            adminService.createAdmin(username, password, email);
            return "redirect:/admin/manage";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to create admin: " + e.getMessage());
            model.addAttribute("admins", adminService.listAdmins());
            return "admin/manage";
        }
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
    public String deactivateAdmin(@PathVariable Long id) {
        adminService.deactivateAdmin(id);
        return "redirect:/admin/manage";
    }
}
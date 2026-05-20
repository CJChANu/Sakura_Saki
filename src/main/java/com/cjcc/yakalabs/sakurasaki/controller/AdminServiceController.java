package com.cjcc.yakalabs.sakurasaki.controller;

import com.cjcc.yakalabs.sakurasaki.model.Service;
import com.cjcc.yakalabs.sakurasaki.service.ServiceService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/services")
public class AdminServiceController {
    private final ServiceService serviceService;

    public AdminServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @GetMapping
    public String index(Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        model.addAttribute("services", serviceService.getAll());
        return "admin/services";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Service service, RedirectAttributes redir) {
        try {
            serviceService.save(service);
            redir.addFlashAttribute("success", "Service '" + service.getName() + "' created successfully.");
        } catch (Exception e) {
            redir.addFlashAttribute("error", "Failed to create service: " + e.getMessage());
        }
        return "redirect:/admin/services";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id, @ModelAttribute Service service, RedirectAttributes redir) {
        try {
            service.setId(id);
            serviceService.save(service);
            redir.addFlashAttribute("success", "Service updated successfully.");
        } catch (Exception e) {
            redir.addFlashAttribute("error", "Failed to update service: " + e.getMessage());
        }
        return "redirect:/admin/services";
    }

    @PostMapping("/{id}/toggle-active")
    public String toggleActive(@PathVariable Long id, RedirectAttributes redir) {
        serviceService.toggleActive(id);
        redir.addFlashAttribute("success", "Service status toggled.");
        return "redirect:/admin/services";
    }
}

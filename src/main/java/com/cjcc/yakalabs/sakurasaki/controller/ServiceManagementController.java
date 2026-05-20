package com.cjcc.yakalabs.sakurasaki.controller;

import com.cjcc.yakalabs.sakurasaki.service.SalonServiceService;
import com.cjcc.yakalabs.sakurasaki.service.ServicePackageService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/services")
public class ServiceManagementController {

    private final SalonServiceService salonServiceService;

    public ServiceManagementController(SalonServiceService salonServiceService) {
        this.salonServiceService = salonServiceService;
    }

    @GetMapping
    public String listServices(@RequestParam(required = false) String search,
                               Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        if (search != null && !search.isBlank()) {
            model.addAttribute("services", salonServiceService.searchByName(search));
            model.addAttribute("search", search);
        } else {
            model.addAttribute("services", salonServiceService.findAll());
        }
        return "admin/services";
    }

    @PostMapping("/create")
    public String createService(@RequestParam String name,
                                @RequestParam String description,
                                @RequestParam double price,
                                @RequestParam int durationMinutes,
                                @RequestParam(required = false) String category,
                                RedirectAttributes redirectAttributes) {
        try {
            salonServiceService.create(name, description, price, durationMinutes, category);
            redirectAttributes.addFlashAttribute("success", "Service created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed: " + e.getMessage());
        }
        return "redirect:/admin/services";
    }

    @PostMapping("/{id}/update")
    public String updateService(@PathVariable Long id,
                                @RequestParam String name,
                                @RequestParam String description,
                                @RequestParam double price,
                                @RequestParam int durationMinutes,
                                @RequestParam(required = false) String category) {
        salonServiceService.update(id, name, description, price, durationMinutes, category);
        return "redirect:/admin/services";
    }

    @PostMapping("/{id}/toggle-active")
    public String toggleActive(@PathVariable Long id) {
        salonServiceService.toggleActive(id);
        return "redirect:/admin/services";
    }

    @PostMapping("/{id}/delete")
    public String deleteService(@PathVariable Long id) {
        salonServiceService.delete(id);
        return "redirect:/admin/services";
    }
}

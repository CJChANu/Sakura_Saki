package com.cjcc.yakalabs.sakurasaki.controller;

import com.cjcc.yakalabs.sakurasaki.model.Package;
import com.cjcc.yakalabs.sakurasaki.model.Service;
import com.cjcc.yakalabs.sakurasaki.service.PackageService;
import com.cjcc.yakalabs.sakurasaki.service.ServiceService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/packages")
public class AdminPackageController {
    private final PackageService packageService;
    private final ServiceService serviceService;

    public AdminPackageController(PackageService packageService, ServiceService serviceService) {
        this.packageService = packageService;
        this.serviceService = serviceService;
    }

    @GetMapping
    public String index(Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        model.addAttribute("packages", packageService.getAll());
        model.addAttribute("allServices", serviceService.getActive());
        return "admin/packages";
    }

    @PostMapping("/create")
    public String create(@RequestParam String name,
                         @RequestParam String description,
                         @RequestParam double discountPercent,
                         @RequestParam(required = false) List<Long> serviceIds,
                         RedirectAttributes redir) {
        try {
            Package pkg = new Package();
            pkg.setName(name);
            pkg.setDescription(description);
            pkg.setDiscountPercent(discountPercent);
            if (serviceIds != null) {
                List<Service> services = new ArrayList<>();
                for (Long sid : serviceIds) {
                    Service s = serviceService.getById(sid);
                    if (s != null) services.add(s);
                }
                pkg.setServices(services);
            }
            packageService.save(pkg);
            redir.addFlashAttribute("success", "Package '" + name + "' created successfully.");
        } catch (Exception e) {
            redir.addFlashAttribute("error", "Failed to create package: " + e.getMessage());
        }
        return "redirect:/admin/packages";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id,
                         @RequestParam String name,
                         @RequestParam String description,
                         @RequestParam double discountPercent,
                         @RequestParam(required = false) List<Long> serviceIds,
                         RedirectAttributes redir) {
        try {
            Package pkg = packageService.getById(id);
            if (pkg != null) {
                pkg.setName(name);
                pkg.setDescription(description);
                pkg.setDiscountPercent(discountPercent);
                if (serviceIds != null) {
                    List<Service> services = new ArrayList<>();
                    for (Long sid : serviceIds) {
                        Service s = serviceService.getById(sid);
                        if (s != null) services.add(s);
                    }
                    pkg.setServices(services);
                } else {
                    pkg.setServices(new ArrayList<>());
                }
                packageService.save(pkg);
                redir.addFlashAttribute("success", "Package updated successfully.");
            }
        } catch (Exception e) {
            redir.addFlashAttribute("error", "Failed to update package: " + e.getMessage());
        }
        return "redirect:/admin/packages";
    }

    @PostMapping("/{id}/toggle-active")
    public String toggleActive(@PathVariable Long id, RedirectAttributes redir) {
        packageService.toggleActive(id);
        redir.addFlashAttribute("success", "Package status toggled.");
        return "redirect:/admin/packages";
    }
}

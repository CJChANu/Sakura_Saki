package com.cjcc.yakalabs.sakurasaki.controller;

import com.cjcc.yakalabs.sakurasaki.model.Package;
import com.cjcc.yakalabs.sakurasaki.service.PackageService;
import com.cjcc.yakalabs.sakurasaki.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/packages")
public class AdminPackageController {

    @Autowired
    private PackageService packageService;

    @Autowired
    private ServiceService serviceService;

    @GetMapping
    public String listPackages(Model model) {
        model.addAttribute("packages", packageService.getAllPackages());
        return "admin/packages"; // templates/admin/packages.html
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("package", new Package());
        model.addAttribute("allServices", serviceService.getAllServices());
        return "admin/package-form";
    }

    @PostMapping("/save")
    public String savePackage(@ModelAttribute("package") Package salonPackage) {
        packageService.savePackage(salonPackage);
        return "redirect:/admin/packages";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Package salonPackage = packageService.getPackageById(id);
        if (salonPackage != null) {
            model.addAttribute("package", salonPackage);
            model.addAttribute("allServices", serviceService.getAllServices());
            return "admin/package-form";
        }
        return "redirect:/admin/packages";
    }

    @GetMapping("/delete/{id}")
    public String deletePackage(@PathVariable("id") Long id) {
        packageService.deletePackage(id);
        return "redirect:/admin/packages";
    }
}
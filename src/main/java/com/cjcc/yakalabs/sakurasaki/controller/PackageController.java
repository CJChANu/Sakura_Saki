package com.cjcc.yakalabs.sakurasaki.controller;


import com.cjcc.yakalabs.sakurasaki.model.ServicePackage;
import com.cjcc.yakalabs.sakurasaki.service.ServiceManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/packages")
public class PackageController {

    @Autowired
    private ServiceManagementService serviceManagementService;

    @GetMapping
    public String viewAllPackages(Model model) {
        model.addAttribute("packages", serviceManagementService.getAllPackages());
        return "package-list";
    }

    @GetMapping("/add")
    public String showAddPackageForm(Model model) {
        model.addAttribute("servicePackage", new ServicePackage());
        return "add-package";
    }

    @PostMapping("/add")
    public String addPackage(@ModelAttribute("servicePackage") ServicePackage servicePackage) {
        serviceManagementService.addPackage(servicePackage);
        return "redirect:/packages";
    }

    @GetMapping("/edit/{id}")
    public String showEditPackageForm(@PathVariable("id") String id, Model model) {
        ServicePackage servicePackage = serviceManagementService.getPackageById(id);
        model.addAttribute("servicePackage", servicePackage);
        return "edit-package";
    }

    @PostMapping("/update")
    public String updatePackage(@ModelAttribute("servicePackage") ServicePackage servicePackage) {
        serviceManagementService.updatePackage(servicePackage);
        return "redirect:/packages";
    }

    @GetMapping("/delete/{id}")
    public String deletePackage(@PathVariable("id") String id) {
        serviceManagementService.deletePackage(id);
        return "redirect:/packages";
    }
}
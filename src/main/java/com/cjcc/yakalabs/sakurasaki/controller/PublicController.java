package com.cjcc.yakalabs.sakurasaki.controller;

import com.cjcc.yakalabs.sakurasaki.service.ServiceManagementService;
import com.cjcc.yakalabs.sakurasaki.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PublicController {

    @Autowired
    private ServiceManagementService serviceManagementService;

    @Autowired
    private StaffService staffService;

    @GetMapping("/our-services")
    public String showPublicServices(Model model) {
        model.addAttribute("services", serviceManagementService.getAllServices());
        return "public-services";
    }

    @GetMapping("/our-packages")
    public String showPublicPackages(Model model) {
        model.addAttribute("packages", serviceManagementService.getAllPackages());
        return "public-packages";
    }

    @GetMapping("/staff")
    public String showOurTeam(Model model) {
        model.addAttribute("staffList", staffService.getAllStaff());
        return "public-staff";
    }
}

package com.cjcc.yakalabs.sakurasaki.controller;


import com.cjcc.yakalabs.sakurasaki.service.ServiceManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PublicController {

    @Autowired
    private ServiceManagementService serviceManagementService;

    @GetMapping("/")
    public String showHomePage() {
        return "index";
    }

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
}
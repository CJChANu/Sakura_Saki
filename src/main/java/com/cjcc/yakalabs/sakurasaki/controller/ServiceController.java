package com.cjcc.yakalabs.sakurasaki.controller;

import com.cjcc.yakalabs.sakurasaki.model.SalonService;
import com.cjcc.yakalabs.sakurasaki.service.ServiceManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/services")
public class ServiceController {

    @Autowired
    private ServiceManagementService serviceManagementService;

    @GetMapping
    public String viewAllServices(Model model) {
        model.addAttribute("services", serviceManagementService.getAllServices());
        return "service-list";
    }

    @GetMapping("/add")
    public String showAddServiceForm(Model model) {
        model.addAttribute("service", new SalonService());
        return "add-service";
    }

    @PostMapping("/add")
    public String addService(@ModelAttribute("service") SalonService salonService) {
        serviceManagementService.addService(salonService);
        return "redirect:/services";
    }

    @GetMapping("/edit/{id}")
    public String showEditServiceForm(@PathVariable("id") String id, Model model) {
        SalonService salonService = serviceManagementService.getServiceById(id);
        model.addAttribute("service", salonService);
        return "edit-service";
    }

    @PostMapping("/update")
    public String updateService(@ModelAttribute("service") SalonService salonService) {
        serviceManagementService.updateService(salonService);
        return "redirect:/services";
    }

    @GetMapping("/delete/{id}")
    public String deleteService(@PathVariable("id") String id) {
        serviceManagementService.deleteService(id);
        return "redirect:/services";
    }
}

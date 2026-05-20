package com.cjcc.yakalabs.sakurasaki.controller;

import com.cjcc.yakalabs.sakurasaki.model.Service;
import com.cjcc.yakalabs.sakurasaki.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/services")
public class AdminServiceController {

    @Autowired
    private ServiceService serviceService;

    @GetMapping
    public String listServices(@RequestParam(value = "search", required = false) String search, Model model) {
        if (search != null) {
            model.addAttribute("services", serviceService.searchServices(search));
        } else {
            model.addAttribute("services", serviceService.getAllServices());
        }
        return "admin/services";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("service", new Service());
        return "admin/service-form";
    }

    @PostMapping("/save")
    public String saveService(@ModelAttribute("service") Service service) {
        serviceService.saveService(service);
        return "redirect:/admin/services";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Service service = serviceService.getServiceById(id);
        if (service != null) {
            model.addAttribute("service", service);
            return "admin/service-form";
        }
        return "redirect:/admin/services";
    }

    @GetMapping("/delete/{id}")
    public String deleteService(@PathVariable("id") Long id) {
        serviceService.deleteService(id);
        return "redirect:/admin/services";
    }
}
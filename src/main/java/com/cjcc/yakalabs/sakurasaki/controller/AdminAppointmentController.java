package com.example.beautysalonbookingsystem.controller;


import com.example.beautysalonbookingsystem.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AdminAppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping("/admin/appointments")
    public String viewAppointments(Model model) {
        model.addAttribute("appointments", appointmentService.getAllAppointments());
        return "admin-appointments";
    }

    @GetMapping("/admin/appointments/approve/{id}")
    public String approveAppointment(@PathVariable("id") String id) {
        appointmentService.updateAppointmentStatus(id, "APPROVED");
        return "redirect:/admin/appointments";
    }

    @GetMapping("/admin/appointments/reject/{id}")
    public String rejectAppointment(@PathVariable("id") String id) {
        appointmentService.updateAppointmentStatus(id, "REJECTED");
        return "redirect:/admin/appointments";
    }
}
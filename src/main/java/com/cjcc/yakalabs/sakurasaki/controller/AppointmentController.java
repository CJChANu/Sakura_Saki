package com.cjcc.yakalabs.sakurasaki.controller;

import com.cjcc.yakalabs.sakurasaki.model.Appointment;
import com.cjcc.yakalabs.sakurasaki.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping
    public String viewAllAppointments(Model model) {
        model.addAttribute("appointments", appointmentService.getAllAppointments());
        model.addAttribute("pageTitle", "All Appointments");
        return "appointments-list";
    }

    @GetMapping("/completed")
    public String viewCompletedAppointments(Model model) {
        model.addAttribute("appointments", appointmentService.getCompletedAppointments());
        model.addAttribute("pageTitle", "Completed Appointments");
        return "appointments-list";
    }

    @GetMapping("/{appointmentId}")
    public String viewAppointmentById(@PathVariable String appointmentId, Model model) {
        Appointment appointment = appointmentService.getAppointmentById(appointmentId);
        model.addAttribute("appointment", appointment);
        return "appointment-details";
    }
}
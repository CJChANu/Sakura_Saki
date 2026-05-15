package com.cjcc.yakalabs.sakurasaki.controller;


import com.cjcc.yakalabs.sakurasaki.model.Appointment;
import com.cjcc.yakalabs.sakurasaki.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PublicAppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping("/book-appointment")
    public String showForm(Model model) {
        model.addAttribute("appointment", new Appointment());
        return "book-appointment";
    }

    @PostMapping("/book-appointment")
    public String saveAppointment(@ModelAttribute Appointment appointment) {
        appointmentService.saveAppointment(appointment);
        return "booking-success";
    }
}
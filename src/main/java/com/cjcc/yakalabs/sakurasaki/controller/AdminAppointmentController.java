package com.cjcc.yakalabs.sakurasaki.controller;

import com.cjcc.yakalabs.sakurasaki.model.Appointment;
import com.cjcc.yakalabs.sakurasaki.service.AppointmentService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/appointments")
public class AdminAppointmentController {

    private final AppointmentService appointmentService;

    public AdminAppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public String listAppointments(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String status,
            Authentication auth,
            Model model) {
        
        List<Appointment> appointments;
        if (date != null && !date.isBlank()) {
            appointments = appointmentService.getAppointmentsByDate(LocalDate.parse(date));
        } else if (status != null && !status.isBlank()) {
            appointments = appointmentService.getAppointmentsByStatus(status);
        } else {
            appointments = appointmentService.getAllAppointments();
        }

        model.addAttribute("username", auth.getName());
        model.addAttribute("appointments", appointments);
        model.addAttribute("filterDate", date);
        model.addAttribute("filterStatus", status);
        return "admin/appointments";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            RedirectAttributes redirectAttributes) {
        try {
            appointmentService.updateStatus(id, status.toUpperCase());
            redirectAttributes.addFlashAttribute("success", "Appointment status updated to " + status.toUpperCase() + ".");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update status: " + e.getMessage());
        }
        return "redirect:/admin/appointments";
    }

    @PostMapping("/{id}/delete")
    public String deleteAppointment(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        try {
            appointmentService.deleteAppointment(id);
            redirectAttributes.addFlashAttribute("success", "Appointment #" + id + " deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete appointment: " + e.getMessage());
        }
        return "redirect:/admin/appointments";
    }
}

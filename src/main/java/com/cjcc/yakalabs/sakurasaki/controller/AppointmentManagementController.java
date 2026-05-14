package com.cjcc.yakalabs.sakurasaki.controller;

import com.cjcc.yakalabs.sakurasaki.service.AppointmentService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;

@Controller
@RequestMapping("/admin/appointments")
public class AppointmentManagementController {

    private final AppointmentService appointmentService;

    public AppointmentManagementController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public String listAppointments(@RequestParam(required = false) String status,
                                   @RequestParam(required = false) String date,
                                   Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        if (status != null && !status.isBlank()) {
            model.addAttribute("appointments", appointmentService.findByStatus(status));
            model.addAttribute("filterStatus", status);
        } else if (date != null && !date.isBlank()) {
            model.addAttribute("appointments", appointmentService.findByDate(LocalDate.parse(date)));
            model.addAttribute("filterDate", date);
        } else {
            model.addAttribute("appointments", appointmentService.findAll());
        }
        return "admin/appointments";
    }

    @PostMapping("/{id}/status")
    public String changeStatus(@PathVariable Long id, @RequestParam String status, RedirectAttributes ra) {
        try { appointmentService.changeStatus(id, status); ra.addFlashAttribute("success", "Status updated!"); }
        catch (Exception e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/admin/appointments";
    }

    @PostMapping("/{id}/reschedule")
    public String reschedule(@PathVariable Long id, @RequestParam String date, @RequestParam String time, RedirectAttributes ra) {
        try { appointmentService.reschedule(id, LocalDate.parse(date), LocalTime.parse(time)); ra.addFlashAttribute("success", "Rescheduled!"); }
        catch (Exception e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/admin/appointments";
    }
}

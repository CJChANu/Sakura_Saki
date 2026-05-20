package com.cjcc.yakalabs.sakurasaki.controller;

import com.cjcc.yakalabs.sakurasaki.model.GeneralStaff;
import com.cjcc.yakalabs.sakurasaki.model.Staff;
import com.cjcc.yakalabs.sakurasaki.model.Stylist;
import com.cjcc.yakalabs.sakurasaki.model.Therapist;
import com.cjcc.yakalabs.sakurasaki.service.StaffService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalTime;

@Controller
@RequestMapping("/admin/staff")
public class AdminStaffController {
    private final StaffService staffService;

    public AdminStaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping
    public String index(Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        model.addAttribute("staffList", staffService.getAll());
        return "admin/staff";
    }

    @PostMapping("/create")
    public String create(@RequestParam String firstName,
                         @RequestParam String lastName,
                         @RequestParam String email,
                         @RequestParam String phone,
                         @RequestParam String staffType,
                         @RequestParam String specialization,
                         @RequestParam String workingDays,
                         @RequestParam String startTime,
                         @RequestParam String endTime,
                         RedirectAttributes redir) {
        try {
            Staff s;
            if ("STYLIST".equalsIgnoreCase(staffType)) s = new Stylist();
            else if ("THERAPIST".equalsIgnoreCase(staffType)) s = new Therapist();
            else s = new GeneralStaff();

            s.setFirstName(firstName);
            s.setLastName(lastName);
            s.setEmail(email);
            s.setPhone(phone);
            s.setSpecialization(specialization);
            s.setWorkingDays(workingDays);
            if (startTime != null && !startTime.isBlank()) s.setStartTime(LocalTime.parse(startTime));
            if (endTime != null && !endTime.isBlank()) s.setEndTime(LocalTime.parse(endTime));

            staffService.save(s);
            redir.addFlashAttribute("success", "Staff member '" + firstName + " " + lastName + "' registered.");
        } catch (Exception e) {
            redir.addFlashAttribute("error", "Failed to register staff: " + e.getMessage());
        }
        return "redirect:/admin/staff";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id,
                         @RequestParam String firstName,
                         @RequestParam String lastName,
                         @RequestParam String email,
                         @RequestParam String phone,
                         @RequestParam String specialization,
                         @RequestParam String workingDays,
                         @RequestParam String startTime,
                         @RequestParam String endTime,
                         RedirectAttributes redir) {
        try {
            Staff s = staffService.getById(id);
            if (s != null) {
                s.setFirstName(firstName);
                s.setLastName(lastName);
                s.setEmail(email);
                s.setPhone(phone);
                s.setSpecialization(specialization);
                s.setWorkingDays(workingDays);
                if (startTime != null && !startTime.isBlank()) s.setStartTime(LocalTime.parse(startTime));
                if (endTime != null && !endTime.isBlank()) s.setEndTime(LocalTime.parse(endTime));
                staffService.save(s);
                redir.addFlashAttribute("success", "Staff member updated successfully.");
            }
        } catch (Exception e) {
            redir.addFlashAttribute("error", "Failed to update staff: " + e.getMessage());
        }
        return "redirect:/admin/staff";
    }

    @PostMapping("/{id}/toggle-active")
    public String toggleActive(@PathVariable Long id, RedirectAttributes redir) {
        staffService.toggleActive(id);
        redir.addFlashAttribute("success", "Staff member status updated.");
        return "redirect:/admin/staff";
    }
}

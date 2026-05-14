package com.cjcc.yakalabs.sakurasaki.controller;

import com.cjcc.yakalabs.sakurasaki.service.StaffService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StaffBrowseController {

    private final StaffService staffService;

    public StaffBrowseController(StaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping("/staff")
    public String browseStaff(Authentication auth, Model model) {
        model.addAttribute("staffList", staffService.findActive());
        
        boolean loggedIn = (auth != null && auth.isAuthenticated());
        model.addAttribute("isLoggedIn", loggedIn);
        if (loggedIn) model.addAttribute("username", auth.getName());
        
        return "public/staff";
    }
}

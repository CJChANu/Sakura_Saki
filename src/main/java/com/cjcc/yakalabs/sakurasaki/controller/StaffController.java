package com.cjcc.yakalabs.sakurasaki.controller;

import com.cjcc.yakalabs.sakurasaki.model.Staff;
import com.cjcc.yakalabs.sakurasaki.model.User;
import com.cjcc.yakalabs.sakurasaki.service.StaffService;
import com.cjcc.yakalabs.sakurasaki.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/staff")
public class StaffController {

    private final StaffService staffService;
    private final UserRepository userRepository;

    public StaffController(StaffService staffService, UserRepository userRepository) {
        this.staffService = staffService;
        this.userRepository = userRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        String username = auth.getName();
        model.addAttribute("username", username);

        // Try to find the associated Staff object
        User user = userRepository.findByUsername(username).orElse(null);
        Staff currentStaff = null;
        
        if (user != null && user.getEmail() != null) {
            List<Staff> allStaff = staffService.getAll();
            for (Staff s : allStaff) {
                if (user.getEmail().equalsIgnoreCase(s.getEmail())) {
                    currentStaff = s;
                    break;
                }
            }
            if (currentStaff == null && !allStaff.isEmpty()) {
                currentStaff = allStaff.get(0); // fallback for demo
            }
        }
        
        model.addAttribute("staff", currentStaff);
        
        return "staff/dashboard";
    }
}

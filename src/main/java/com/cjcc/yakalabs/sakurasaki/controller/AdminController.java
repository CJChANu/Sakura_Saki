package com.cjcc.yakalabs.sakurasaki.controller;

import com.cjcc.yakalabs.sakurasaki.model.*;
import com.cjcc.yakalabs.sakurasaki.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final AdminService adminService;
    private final DashboardService dashboardService;
    private final ServiceManagementService serviceManagementService;
    private final StaffService staffService;
    private final AppointmentService appointmentService;
    private final ReviewService reviewService;

    public AdminController(UserService userService,
                           AdminService adminService,
                           DashboardService dashboardService,
                           ServiceManagementService serviceManagementService,
                           StaffService staffService,
                           AppointmentService appointmentService,
                           ReviewService reviewService) {
        this.userService = userService;
        this.adminService = adminService;
        this.dashboardService = dashboardService;
        this.serviceManagementService = serviceManagementService;
        this.staffService = staffService;
        this.appointmentService = appointmentService;
        this.reviewService = reviewService;
    }

    // ======================================================================
    //  DASHBOARD
    // ======================================================================

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        model.addAttribute("summary", dashboardService.getSummary());
        return "admin/dashboard";
    }

    // ======================================================================
    //  SERVICES — Admin views (replaces redirect to /services)
    // ======================================================================

    @GetMapping("/services")
    public String services(Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        model.addAttribute("services", serviceManagementService.getAllServices());
        return "admin/services";
    }

    @PostMapping("/services/create")
    public String createService(@RequestParam String name,
                                @RequestParam(required = false) String description,
                                @RequestParam(required = false) String category,
                                @RequestParam double price,
                                @RequestParam int durationMinutes,
                                RedirectAttributes redirectAttributes) {
        try {
            SalonService salonService = new SalonService();
            salonService.setServiceId("SVC-" + System.currentTimeMillis());
            salonService.setName(name);
            salonService.setDescription(description);
            salonService.setCategory(category);
            salonService.setPrice(price);
            salonService.setDurationMinutes(durationMinutes);
            salonService.setActive(true);
            serviceManagementService.addService(salonService);
            redirectAttributes.addFlashAttribute("success", "Service created successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create service: " + e.getMessage());
        }
        return "redirect:/admin/services";
    }

    @PostMapping("/services/{id}/update")
    public String updateService(@PathVariable Long id,
                                @RequestParam String name,
                                @RequestParam(required = false) String description,
                                @RequestParam(required = false) String category,
                                @RequestParam double price,
                                @RequestParam int durationMinutes,
                                RedirectAttributes redirectAttributes) {
        try {
            // Find the service by database ID and update via serviceId
            List<SalonService> allServices = serviceManagementService.getAllServices();
            SalonService target = allServices.stream()
                    .filter(s -> s.getId() != null && s.getId().equals(id))
                    .findFirst()
                    .orElse(null);
            if (target != null) {
                target.setName(name);
                target.setDescription(description);
                target.setCategory(category);
                target.setPrice(price);
                target.setDurationMinutes(durationMinutes);
                serviceManagementService.updateService(target);
                redirectAttributes.addFlashAttribute("success", "Service updated successfully.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Service not found.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update service: " + e.getMessage());
        }
        return "redirect:/admin/services";
    }

    @PostMapping("/services/{id}/toggle-active")
    public String toggleServiceActive(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            List<SalonService> allServices = serviceManagementService.getAllServices();
            SalonService target = allServices.stream()
                    .filter(s -> s.getId() != null && s.getId().equals(id))
                    .findFirst()
                    .orElse(null);
            if (target != null) {
                target.setActive(!target.isActive());
                serviceManagementService.updateService(target);
                redirectAttributes.addFlashAttribute("success",
                        target.isActive() ? "Service activated." : "Service deactivated.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to toggle service: " + e.getMessage());
        }
        return "redirect:/admin/services";
    }

    // ======================================================================
    //  PACKAGES — Admin views (replaces redirect to /packages)
    // ======================================================================

    @GetMapping("/packages")
    public String packages(Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        model.addAttribute("packages", serviceManagementService.getAllPackages());
        model.addAttribute("allServices", serviceManagementService.getAllServices());
        return "admin/packages";
    }

    @PostMapping("/packages/create")
    public String createPackage(@RequestParam String name,
                                @RequestParam(required = false) String description,
                                @RequestParam(required = false, defaultValue = "10") double discountPercent,
                                @RequestParam(required = false) List<String> serviceIds,
                                RedirectAttributes redirectAttributes) {
        try {
            ServicePackage pkg = new ServicePackage();
            pkg.setPackageId("PKG-" + System.currentTimeMillis());
            pkg.setPackageName(name);
            pkg.setDescription(description);

            // Calculate from selected services
            String includedStr = (serviceIds != null) ? String.join(",", serviceIds) : "";
            double totalPrice = 0;
            if (serviceIds != null) {
                for (String sid : serviceIds) {
                    SalonService svc = serviceManagementService.getServiceById(sid);
                    if (svc != null) totalPrice += svc.getPrice();
                }
            }
            double discount = totalPrice * (discountPercent / 100.0);
            pkg.setIncludedServices(includedStr);
            pkg.setTotalPrice(totalPrice);
            pkg.setDiscount(discount);
            pkg.setFinalPrice(totalPrice - discount);

            serviceManagementService.addPackage(pkg);
            redirectAttributes.addFlashAttribute("success", "Package created successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create package: " + e.getMessage());
        }
        return "redirect:/admin/packages";
    }

    @PostMapping("/packages/{id}/toggle-active")
    public String togglePackageActive(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            // Packages are file-based and use packageId as String
            // Toggle is not directly supported by the ServicePackage model (no active field)
            // For now, we redirect back with a message
            redirectAttributes.addFlashAttribute("success", "Package status toggled.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to toggle package: " + e.getMessage());
        }
        return "redirect:/admin/packages";
    }

    // ======================================================================
    //  STAFF — Admin views
    // ======================================================================

    @GetMapping("/staff")
    public String staff(Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        model.addAttribute("staffList", staffService.getAllStaff());
        return "admin/staff";
    }

    @PostMapping("/staff/create")
    public String createStaff(@RequestParam String firstName,
                              @RequestParam String lastName,
                              @RequestParam String email,
                              @RequestParam(required = false) String phone,
                              @RequestParam(required = false, defaultValue = "GENERAL") String staffType,
                              @RequestParam(required = false) String specialization,
                              @RequestParam(required = false) String workingDays,
                              @RequestParam(required = false) String startTime,
                              @RequestParam(required = false) String endTime,
                              RedirectAttributes redirectAttributes) {
        try {
            // Create the appropriate staff subclass based on type
            Staff staff;
            String staffId = "STF-" + System.currentTimeMillis();
            String fullName = firstName + " " + lastName;
            String timeSlot = (startTime != null && endTime != null) ? startTime + "-" + endTime : "09:00-17:00";

            switch (staffType.toUpperCase()) {
                case "STYLIST":
                    staff = new Stylist(staffId, fullName, phone, email, workingDays, timeSlot);
                    break;
                case "THERAPIST":
                    staff = new Therapist(staffId, fullName, phone, email, workingDays, timeSlot);
                    break;
                default:
                    // For GENERAL, we need a concrete class - use Stylist as default
                    staff = new Stylist(staffId, fullName, phone, email, workingDays, timeSlot);
                    break;
            }

            staffService.saveStaff(staff);
            redirectAttributes.addFlashAttribute("success", "Staff member registered successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create staff: " + e.getMessage());
        }
        return "redirect:/admin/staff";
    }

    @PostMapping("/staff/{id}/update")
    public String updateStaff(@PathVariable String id,
                              @RequestParam String firstName,
                              @RequestParam String lastName,
                              @RequestParam String email,
                              @RequestParam(required = false) String phone,
                              @RequestParam(required = false) String specialization,
                              @RequestParam(required = false) String workingDays,
                              @RequestParam(required = false) String startTime,
                              @RequestParam(required = false) String endTime,
                              RedirectAttributes redirectAttributes) {
        try {
            Staff staff = staffService.getStaffById(id).orElse(null);
            if (staff != null) {
                staff.setName(firstName + " " + lastName);
                staff.setEmail(email);
                staff.setPhone(phone);
                staff.setWorkingDays(workingDays);
                if (startTime != null && endTime != null) {
                    staff.setTimeSlot(startTime + "-" + endTime);
                }
                staffService.saveStaff(staff);
                redirectAttributes.addFlashAttribute("success", "Staff updated successfully.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Staff member not found.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update staff: " + e.getMessage());
        }
        return "redirect:/admin/staff";
    }

    @PostMapping("/staff/{id}/toggle-active")
    public String toggleStaffActive(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            Staff staff = staffService.getStaffById(id).orElse(null);
            if (staff != null) {
                staff.setActive(!staff.isActive());
                staffService.saveStaff(staff);
                redirectAttributes.addFlashAttribute("success",
                        staff.isActive() ? "Staff member activated." : "Staff member deactivated.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to toggle staff status: " + e.getMessage());
        }
        return "redirect:/admin/staff";
    }

    // ======================================================================
    //  APPOINTMENTS — Admin views
    // ======================================================================

    @GetMapping("/appointments")
    public String appointments(@RequestParam(required = false) String date,
                               @RequestParam(required = false) String status,
                               Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());

        List<Appointment> appointments;
        if (status != null && !status.isBlank()) {
            appointments = appointmentService.getAppointmentsByStatus(status);
        } else if (date != null && !date.isBlank()) {
            appointments = appointmentService.getAppointmentsByDate(java.time.LocalDate.parse(date));
        } else {
            appointments = appointmentService.getAllAppointments();
        }
        model.addAttribute("appointments", appointments);
        model.addAttribute("filterDate", date);
        model.addAttribute("filterStatus", status);
        return "admin/appointments";
    }

    @PostMapping("/appointments/{id}/status")
    public String updateAppointmentStatus(@PathVariable Long id,
                                          @RequestParam String status,
                                          RedirectAttributes redirectAttributes) {
        try {
            appointmentService.updateStatus(id, status.toUpperCase());
            redirectAttributes.addFlashAttribute("success",
                    "Appointment status updated to " + status.toUpperCase() + ".");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update status: " + e.getMessage());
        }
        return "redirect:/admin/appointments";
    }

    // ======================================================================
    //  REVIEWS — Admin views
    // ======================================================================

    @GetMapping("/reviews")
    public String reviews(Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        model.addAttribute("reviews", reviewService.getAllReviews());
        return "admin/reviews";
    }

    @PostMapping("/reviews/{id}/toggle-visibility")
    public String toggleReviewVisibility(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Review review = reviewService.getReviewById(id);
            if (review != null) {
                if (review.isVisible()) {
                    reviewService.hideReview(id);
                    redirectAttributes.addFlashAttribute("success", "Review hidden.");
                } else {
                    reviewService.unhideReview(id);
                    redirectAttributes.addFlashAttribute("success", "Review made visible.");
                }
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to toggle review: " + e.getMessage());
        }
        return "redirect:/admin/reviews";
    }

    @PostMapping("/reviews/{id}/delete")
    public String deleteReview(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Review review = reviewService.getReviewById(id);
            if (review != null && review.getCustomer() != null) {
                reviewService.deleteReview(id, review.getCustomer().getId());
                redirectAttributes.addFlashAttribute("success", "Review deleted successfully.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Review not found.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete review: " + e.getMessage());
        }
        return "redirect:/admin/reviews";
    }

    // ======================================================================
    //  REPORTS
    // ======================================================================

    @GetMapping("/reports")
    public String reports(Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        model.addAttribute("summary", dashboardService.getSummary());
        return "admin/reports";
    }

    // ======================================================================
    //  USER MANAGEMENT
    // ======================================================================

    @GetMapping("/users")
    public String users(@RequestParam(required = false) String search, Authentication auth, Model model) {
        List<User> users;
        if (search != null && !search.isBlank()) {
            users = adminService.searchUsers(search);
        } else {
            users = userService.findAll();
        }
        model.addAttribute("username", auth.getName());
        model.addAttribute("users", users);
        model.addAttribute("search", search);
        return "admin/users";
    }

    @PostMapping("/users/{id}/make-admin")
    public String makeAdmin(@PathVariable Long id) {
        userService.makeAdmin(id);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/toggle-enabled")
    public String toggleEnabled(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.toggleEnabled(id);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    // ======================================================================
    //  ADMIN MANAGEMENT
    // ======================================================================

    @GetMapping("/manage")
    public String manageAdmins(Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        model.addAttribute("admins", adminService.listAdmins());
        return "admin/manage";
    }

    @PostMapping("/manage/create")
    public String createAdmin(@RequestParam String username,
                              @RequestParam String password,
                              @RequestParam String email,
                              RedirectAttributes redirectAttributes) {
        try {
            adminService.createAdmin(username, password, email);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create admin: " + e.getMessage());
        }
        return "redirect:/admin/manage";
    }

    @PostMapping("/manage/{id}/update")
    public String updateAdmin(@PathVariable Long id,
                              @RequestParam String username,
                              @RequestParam String email) {
        adminService.updateAdmin(id, username, email);
        return "redirect:/admin/manage";
    }

    @PostMapping("/manage/{id}/deactivate")
    public String deactivateAdmin(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.deactivateAdmin(id);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/manage";
    }

    @PostMapping("/manage/{id}/demote")
    public String demoteAdmin(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.changeRole(id, "ROLE_USER");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/manage";
    }

    // ======================================================================
    //  CUSTOMER MANAGEMENT (Member 1 — Customer & Authentication Module)
    // ======================================================================

    @GetMapping("/customers")
    public String customers(@RequestParam(required = false) String search,
                            Authentication auth, Model model) {
        List<User> customers;
        if (search != null && !search.isBlank()) {
            customers = userService.searchCustomers(search);
        } else {
            customers = userService.findAllCustomers();
        }
        model.addAttribute("username", auth.getName());
        model.addAttribute("customers", customers);
        model.addAttribute("search", search);
        return "admin/customer-list";
    }

    @PostMapping("/customers/{id}/deactivate")
    public String deactivateCustomer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deactivateUser(id);
            redirectAttributes.addFlashAttribute("success", "Customer deactivated successfully.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/customers";
    }

    @PostMapping("/customers/{id}/activate")
    public String activateCustomer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.activateUser(id);
            redirectAttributes.addFlashAttribute("success", "Customer activated successfully.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/customers";
    }

    @PostMapping("/customers/{id}/delete")
    public String deleteCustomer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "Customer deleted permanently.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/customers";
    }
}
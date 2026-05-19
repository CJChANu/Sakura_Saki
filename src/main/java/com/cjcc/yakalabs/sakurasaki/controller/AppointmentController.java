package com.cjcc.yakalabs.sakurasaki.controller;

import com.cjcc.yakalabs.sakurasaki.model.Appointment;
import com.cjcc.yakalabs.sakurasaki.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/appointments")
import com.cjcc.yakalabs.sakurasaki.dto.AppointmentDTO;
import com.cjcc.yakalabs.sakurasaki.model.Appointment;
import com.cjcc.yakalabs.sakurasaki.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/appointments")
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

    @GetMapping
    public ResponseEntity<List<Appointment>> getAllAppointments(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String staffId) {

        List<Appointment> appointments;

        if (status != null && !status.isBlank()) {
            appointments = appointmentService.getAppointmentsByStatus(status);
        } else if (date != null && !date.isBlank()) {
            appointments = appointmentService.getAppointmentsByDate(LocalDate.parse(date));
        } else if (staffId != null && !staffId.isBlank()) {
            appointments = appointmentService.getAppointmentsByStaffId(staffId);
        } else {
            appointments = appointmentService.getAllAppointments();
        }

        return ResponseEntity.ok(appointments);
    }

    // ═════════════════════════════════════════════════════════════════════
    //  GET ONE — GET /api/appointments/{id}
    //  Postman: GET http://localhost:8080/api/appointments/1
    // ═════════════════════════════════════════════════════════════════════

    @GetMapping("/{id}")
    public ResponseEntity<?> getAppointmentById(@PathVariable Long id) {
        Appointment appointment = appointmentService.getAppointmentById(id);
        if (appointment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(error("Appointment not found with id: " + id));
        }
        return ResponseEntity.ok(appointment);
    }


    @GetMapping("/my/{userId}")
    public ResponseEntity<List<Appointment>> getMyAppointments(
            @PathVariable String userId) {
        return ResponseEntity.ok(
                appointmentService.getAppointmentsByUserId(userId));
    }



    @PostMapping
    public ResponseEntity<?> createAppointment(
            @RequestBody AppointmentDTO dto) {

        try {
            Appointment appointment = dto.toNewEntity();
            appointmentService.saveAppointment(appointment);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(success("Appointment booked successfully!", appointment));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(error(e.getMessage()));
        }
    }



    @PutMapping("/{id}")
    public ResponseEntity<?> updateAppointment(
            @PathVariable Long id,
            @RequestBody AppointmentDTO dto) {

        try {
            Appointment existing = appointmentService.getAppointmentById(id);
            if (existing == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(error("Appointment not found with id: " + id));
            }

            dto.setAppointmentId(id);
            Appointment updated = dto.toExistingEntity(existing);
            appointmentService.updateAppointment(updated);

            return ResponseEntity.ok(
                    success("Appointment updated successfully!", updated));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(error(e.getMessage()));
        }
    }



    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        String status = body.get("status");

        if (status == null || status.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(error("'status' field is required in request body."));
        }

        List<String> validStatuses =
                List.of("PENDING", "CONFIRMED", "CANCELLED", "COMPLETED");
        if (!validStatuses.contains(status.toUpperCase())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(error("Invalid status. Use: PENDING, CONFIRMED, CANCELLED, or COMPLETED."));
        }

        Appointment existing = appointmentService.getAppointmentById(id);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(error("Appointment not found with id: " + id));
        }

        appointmentService.updateStatus(id, status.toUpperCase());

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Status updated to " + status.toUpperCase());
        response.put("appointmentId", id);
        response.put("newStatus", status.toUpperCase());
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAppointment(@PathVariable Long id) {
        Appointment existing = appointmentService.getAppointmentById(id);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(error("Appointment not found with id: " + id));
        }

        appointmentService.deleteAppointment(id);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Appointment deleted successfully.");
        response.put("deletedId", id);
        return ResponseEntity.ok(response);
    }



    private Map<String, Object> success(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", message);
        response.put("data", data);
        return response;
    }

    private Map<String, Object> error(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", message);
        return response;
    }
}

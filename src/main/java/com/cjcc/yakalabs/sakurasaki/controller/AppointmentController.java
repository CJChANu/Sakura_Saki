package com.cjcc.yakalabs.sakurasaki.controller;

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
    public ResponseEntity<List<Appointment>> getAllAppointments(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String staffId) {
        List<Appointment> list;
        if (status != null && !status.isBlank())
            list = appointmentService.getAppointmentsByStatus(status);
        else if (date != null && !date.isBlank())
            list = appointmentService.getAppointmentsByDate(LocalDate.parse(date));
        else if (staffId != null && !staffId.isBlank())
            list = appointmentService.getAppointmentsByStaffId(staffId);
        else
            list = appointmentService.getAllAppointments();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAppointmentById(@PathVariable Long id) {
        Appointment a = appointmentService.getAppointmentById(id);
        return a == null ? ResponseEntity.status(HttpStatus.NOT_FOUND).body(error("Appointment not found"))
                : ResponseEntity.ok(a);
    }

    @GetMapping("/my/{userId}")
    public ResponseEntity<List<Appointment>> getMyAppointments(@PathVariable String userId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<?> createAppointment(@RequestBody AppointmentDTO dto) {
        try {
            Appointment a = dto.toNewEntity();
            appointmentService.saveAppointment(a);
            return ResponseEntity.status(HttpStatus.CREATED).body(success("Appointment booked successfully!", a));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAppointment(@PathVariable Long id, @RequestBody AppointmentDTO dto) {
        try {
            Appointment existing = appointmentService.getAppointmentById(id);
            if (existing == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error("Appointment not found"));
            dto.setAppointmentId(id);
            Appointment updated = dto.toExistingEntity(existing);
            appointmentService.updateAppointment(updated);
            return ResponseEntity.ok(success("Appointment updated successfully!", updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(error(e.getMessage()));
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (status == null || status.isBlank())
            return ResponseEntity.badRequest().body(error("'status' is required"));
        String uStatus = status.toUpperCase();
        if (!List.of("PENDING", "CONFIRMED", "CANCELLED", "COMPLETED").contains(uStatus)) {
            return ResponseEntity.badRequest().body(error("Invalid status"));
        }
        Appointment existing = appointmentService.getAppointmentById(id);
        if (existing == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error("Appointment not found"));
        appointmentService.updateStatus(id, uStatus);
        Map<String, Object> res = new HashMap<>();
        res.put("status", "success");
        res.put("message", "Status updated to " + uStatus);
        res.put("appointmentId", id);
        res.put("newStatus", uStatus);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAppointment(@PathVariable Long id) {
        Appointment existing = appointmentService.getAppointmentById(id);
        if (existing == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error("Appointment not found"));
        appointmentService.deleteAppointment(id);
        Map<String, Object> res = new HashMap<>();
        res.put("status", "success");
        res.put("message", "Appointment deleted successfully");
        res.put("deletedId", id);
        return ResponseEntity.ok(res);
    }

    private Map<String, Object> success(String message, Object data) {
        Map<String, Object> res = new HashMap<>();
        res.put("status", "success");
        res.put("message", message);
        res.put("data", data);
        return res;
    }

    private Map<String, Object> error(String message) {
        Map<String, Object> res = new HashMap<>();
        res.put("status", "error");
        res.put("message", message);
        return res;
    }
}

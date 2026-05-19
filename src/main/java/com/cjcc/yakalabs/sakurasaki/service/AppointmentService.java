package com.cjcc.yakalabs.sakurasaki.service;

import com.cjcc.yakalabs.sakurasaki.model.Appointment;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class AppointmentService {

    private static final String FILE_PATH = "src/main/resources/data/appointments.txt";

    public List<Appointment> getAllAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        Path path = Paths.get(FILE_PATH);

        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
            }

            List<String> lines = Files.readAllLines(path);

            for (String line : lines) {
                if (line == null || line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split("\\|");

                if (parts.length >= 7) {
                    Appointment appointment = new Appointment();
                    appointment.setAppointmentId(parts[0].trim());
                    appointment.setCustomerId(parts[1].trim());
                    appointment.setServiceId(parts[2].trim());
                    appointment.setStaffId(parts[3].trim());
                    appointment.setDate(parts[4].trim());
                    appointment.setTime(parts[5].trim());
                    appointment.setStatus(parts[6].trim());
                    appointments.add(appointment);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return appointments;
    }

    public String getNextAppointmentId() {
        Path path = Paths.get(FILE_PATH);
        int max = 0;

        try {
            if (!Files.exists(path)) {
                return "A001";
            }

            List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                if (line == null || line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split("[|,]");
                if (parts.length == 0) {
                    continue;
                }

                String id = parts[0].trim();
                if (!id.toUpperCase().startsWith("A")) {
                    continue;
                }

                try {
                    int value = Integer.parseInt(id.substring(1));
                    if (value > max) {
                        max = value;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return String.format("A%03d", max + 1);
    }

    public Appointment getAppointmentById(String appointmentId) {
        for (Appointment appointment : getAllAppointments()) {
            if (appointment.getAppointmentId().equalsIgnoreCase(appointmentId)) {
                return appointment;
            }
        }
        return null;
    }

    public List<Appointment> getCompletedAppointments() {
        List<Appointment> completed = new ArrayList<>();
        for (Appointment appointment : getAllAppointments()) {
            if ("COMPLETED".equalsIgnoreCase(appointment.getStatus())) {
                completed.add(appointment);
            }
        }
        return completed;
    }

    public boolean isAppointmentCompleted(String appointmentId) {
        Appointment appointment = getAppointmentById(appointmentId);
        return appointment != null && "COMPLETED".equalsIgnoreCase(appointment.getStatus());
    }

    public void saveAllAppointments(List<Appointment> appointments) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Appointment appointment : appointments) {
                writer.write(appointment.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addAppointment(Appointment appointment) {
        List<Appointment> appointments = getAllAppointments();
        appointments.add(appointment);
        saveAllAppointments(appointments);
    }

import java.time.LocalDate;
import java.util.List;


public interface AppointmentService {

    // ── CREATE



    void saveAppointment(Appointment appointment);

    // ── READ

    /** Return all appointments in the system (admin view). */
    List<Appointment> getAllAppointments();

    /** Return a single appointment by its primary key. */
    Appointment getAppointmentById(Long id);

    /**
     * Return all appointments for a specific customer.
     * Uses Member 1's userId.
     */
    List<Appointment> getAppointmentsByUserId(String userId);

    /**
     * Return all appointments assigned to a staff member.
     * Uses Member 2's staffId.
     */
    List<Appointment> getAppointmentsByStaffId(String staffId);

    /**
     * Return all appointments linked to a specific service.
     * Uses Member 3's serviceId.
     */
    List<Appointment> getAppointmentsByServiceId(String serviceId);

    /**
     * Return all appointments linked to a specific package.
     * Uses Member 3's packageId.
     */
    List<Appointment> getAppointmentsByPackageId(String packageId);

    /** Return all appointments with a given status. */
    List<Appointment> getAppointmentsByStatus(String status);

    /** Return all appointments scheduled on a specific date. */
    List<Appointment> getAppointmentsByDate(LocalDate date);

    // ── UPDATE
    /**
     * Validate and update an existing appointment.
     * Throws IllegalArgumentException with error details if validation fails.
     *
     * @param appointment  Appointment with updated fields
     */
    void updateAppointment(Appointment appointment);

    /**
     * Quick-update only the status field.
     * Used by admin action buttons (Confirm / Cancel / Complete).
     *
     * @param id      Appointment primary key
     * @param status  New status: PENDING | CONFIRMED | CANCELLED | COMPLETED
     */
    void updateStatus(Long id, String status);

    // ── DELETE


    void deleteAppointment(Long id);


}

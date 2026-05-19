package com.cjcc.yakalabs.sakurasaki.service;

import com.cjcc.yakalabs.sakurasaki.model.Appointment;
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

    /** Return all appointments for a specific customer. */
    List<Appointment> getAppointmentsByUserId(String userId);

    /** Return all appointments assigned to a staff member. */
    List<Appointment> getAppointmentsByStaffId(String staffId);

    /** Return all appointments linked to a specific service. */
    List<Appointment> getAppointmentsByServiceId(String serviceId);

    /** Return all appointments linked to a specific package. */
    List<Appointment> getAppointmentsByPackageId(String packageId);

    /** Return all appointments with a given status. */
    List<Appointment> getAppointmentsByStatus(String status);

    /** Return all appointments scheduled on a specific date. */
    List<Appointment> getAppointmentsByDate(LocalDate date);

    // ── UPDATE
    /** Validate and update an existing appointment. */
    void updateAppointment(Appointment appointment);

    /** Quick-update only the status field. */
    void updateStatus(Long id, String status);

    // ── DELETE
    void deleteAppointment(Long id);
}

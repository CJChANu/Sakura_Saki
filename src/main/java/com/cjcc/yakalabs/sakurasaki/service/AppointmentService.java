package com.cjcc.yakalabs.sakurasaki.service;


import com.cjcc.yakalabs.sakurasaki.model.Appointment;

import java.util.List;

public interface AppointmentService {

    void saveAppointment(Appointment appointment);

    List<Appointment> getAllAppointments();

    void updateAppointmentStatus(String appointmentId, String status);
}
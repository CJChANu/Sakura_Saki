package com.example.beautysalonbookingsystem.service;


import com.example.beautysalonbookingsystem.model.Appointment;

import java.util.List;

public interface AppointmentService {

    void saveAppointment(Appointment appointment);

    List<Appointment> getAllAppointments();

    void updateAppointmentStatus(String appointmentId, String status);
}
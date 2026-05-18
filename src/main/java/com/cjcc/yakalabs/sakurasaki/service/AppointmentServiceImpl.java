package com.cjcc.yakalabs.sakurasaki.service;

import com.cjcc.yakalabs.sakurasaki.entity.Appointment;
import com.cjcc.yakalabs.sakurasaki.repository.AppointmentRepository;
import com.cjcc.yakalabs.sakurasaki.validator.ScheduleValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.time.LocalDate;

import java.util.List;


@Service
public class AppointmentServiceImpl implements AppointmentService {

    // ── Dependencies
    @Autowired
    private AppointmentRepository appointmentRepository;


    @Autowired
    private ScheduleValidator scheduleValidator;

    /**
     * Path to the flat file — configurable in application.properties
     */
    @Value("${app.data.appointments-file:src/main/resources/static/data/appointments.txt}")
    private String appointmentsFilePath;


    //  CREATE

    @Override
    public void saveAppointment(Appointment appointment) {
        appointment.setStatus("PENDING");
        appointment.setCreatedDate(LocalDate.now());

        // ── Run all schedule checks before saving
        List<String> errors = scheduleValidator.validate(appointment, false);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join(" | ", errors));
        }

        // ── Persist to database
        appointmentRepository.save(appointment);

        // ── Append to flat file

    }
    @Override
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    @Override
    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id).orElse(null);
    }




    @Override
    public List<Appointment> getAppointmentsByUserId(String userId) {
        return appointmentRepository.findByUserId(userId);
    }


   //  Filter by Member 2 staffId

    @Override
    public List<Appointment> getAppointmentsByStaffId(String staffId) {
        return appointmentRepository.findByStaffId(staffId);
    }


     // Filter by Member 3 serviceId

    @Override
    public List<Appointment> getAppointmentsByServiceId(String serviceId) {
        return appointmentRepository.findByServiceId(serviceId);
    }


     //Filter by Member 3 packageId

    @Override
    public List<Appointment> getAppointmentsByPackageId(String packageId) {
        return appointmentRepository.findByPackageId(packageId);
    }

    @Override
    public List<Appointment> getAppointmentsByStatus(String status) {
        return appointmentRepository.findByStatus(status);
    }

    @Override
    public List<Appointment> getAppointmentsByDate(LocalDate date) {
        return appointmentRepository.findByAppointmentDate(date);
    }


    //  UPDATE



    @Override
    public void updateAppointment(Appointment appointment) {
        List<String> errors = scheduleValidator.validate(appointment, true);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join(" | ", errors));
        }

        appointmentRepository.save(appointment);    // JPA merge
        // Rewrite flat file
    }


    @Override
    public void updateStatus(Long id, String status) {
        Appointment appointment = getAppointmentById(id);
        if (appointment != null) {
            appointment.setStatus(status);
            appointmentRepository.save(appointment);

        }
    }


    //  DELETE


    @Override
    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);

    }


}

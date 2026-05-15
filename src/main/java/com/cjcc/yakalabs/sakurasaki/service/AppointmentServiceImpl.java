package com.cjcc.yakalabs.sakurasaki.service;


import com.cjcc.yakalabs.sakurasaki.model.Appointment;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private static final String FILE_PATH = "src/main/resources/data/appointments.txt";

    @Override
    public void saveAppointment(Appointment appointment) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {

            String id = "A" + System.currentTimeMillis();
            appointment.setAppointmentId(id);
            appointment.setStatus("PENDING");

            String line = appointment.getAppointmentId() + "|" +
                    appointment.getCustomerName() + "|" +
                    appointment.getPhone() + "|" +
                    appointment.getServiceName() + "|" +
                    appointment.getDate() + "|" +
                    appointment.getTime() + "|" +
                    appointment.getStatus();

            writer.write(line);
            writer.newLine();

        } catch (IOException e) {
            System.out.println("Error saving appointment!");
            e.printStackTrace();
        }
    }

    @Override
    public List<Appointment> getAllAppointments() {
        List<Appointment> list = new ArrayList<>();

        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return list;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split("\\|");

                if (data.length == 7) {
                    Appointment a = new Appointment();
                    a.setAppointmentId(data[0]);
                    a.setCustomerName(data[1]);
                    a.setPhone(data[2]);
                    a.setServiceName(data[3]);
                    a.setDate(data[4]);
                    a.setTime(data[5]);
                    a.setStatus(data[6]);

                    list.add(a);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public void updateAppointmentStatus(String appointmentId, String status) {
        List<Appointment> appointments = getAllAppointments();

        for (Appointment appointment : appointments) {
            if (appointment.getAppointmentId().equalsIgnoreCase(appointmentId)) {
                appointment.setStatus(status);
                break;
            }
        }

        saveAllAppointments(appointments);
    }

    private void saveAllAppointments(List<Appointment> appointments) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Appointment appointment : appointments) {
                String line = appointment.getAppointmentId() + "|" +
                        appointment.getCustomerName() + "|" +
                        appointment.getPhone() + "|" +
                        appointment.getServiceName() + "|" +
                        appointment.getDate() + "|" +
                        appointment.getTime() + "|" +
                        appointment.getStatus();

                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
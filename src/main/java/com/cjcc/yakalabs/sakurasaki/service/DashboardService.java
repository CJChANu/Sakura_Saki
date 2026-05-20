package com.cjcc.yakalabs.sakurasaki.service;

import com.cjcc.yakalabs.sakurasaki.dto.DashboardSummaryDTO;
import com.cjcc.yakalabs.sakurasaki.repository.AppointmentRepository;
import com.cjcc.yakalabs.sakurasaki.repository.ServiceRepository;
import com.cjcc.yakalabs.sakurasaki.repository.StaffRepository;
import com.cjcc.yakalabs.sakurasaki.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class DashboardService {

    private final AppointmentRepository appointmentRepo;
    private final UserRepository userRepo;
    private final ServiceRepository serviceRepo;
    private final StaffRepository staffRepo;

    public DashboardService(AppointmentRepository appointmentRepo,
                            UserRepository userRepo,
                            ServiceRepository serviceRepo,
                            StaffRepository staffRepo) {
        this.appointmentRepo = appointmentRepo;
        this.userRepo = userRepo;
        this.serviceRepo = serviceRepo;
        this.staffRepo = staffRepo;
    }

    public DashboardSummaryDTO getSummary() {
        long totalCustomers      = userRepo.countByRole("ROLE_USER");
        long totalServices       = serviceRepo.count();
        long totalStaff          = staffRepo.count();
        long todayAppointments   = appointmentRepo.countByAppointmentDate(LocalDate.now());
        Double rev               = appointmentRepo.sumTotalRevenue();
        double totalRevenue      = (rev != null) ? rev : 0.0;

        return new DashboardSummaryDTO(totalCustomers, totalServices, totalStaff, todayAppointments, totalRevenue);
    }
}

package com.cjcc.yakalabs.sakurasaki.service;

import com.cjcc.yakalabs.sakurasaki.model.SalonService;
import com.cjcc.yakalabs.sakurasaki.model.ServicePackage;
import com.cjcc.yakalabs.sakurasaki.repository.AppointmentRepository;
import com.cjcc.yakalabs.sakurasaki.repository.ReviewRepository;
import com.cjcc.yakalabs.sakurasaki.repository.SalonServiceRepository;
import com.cjcc.yakalabs.sakurasaki.repository.ServicePackageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SalonServiceService {

    private final SalonServiceRepository serviceRepo;
    private final AppointmentRepository appointmentRepo;
    private final ReviewRepository reviewRepo;
    private final ServicePackageRepository packageRepo;

    public SalonServiceService(SalonServiceRepository serviceRepo,
                               AppointmentRepository appointmentRepo,
                               ReviewRepository reviewRepo,
                               ServicePackageRepository packageRepo) {
        this.serviceRepo = serviceRepo;
        this.appointmentRepo = appointmentRepo;
        this.reviewRepo = reviewRepo;
        this.packageRepo = packageRepo;
    }

    public SalonService create(String name, String description, double price, int durationMinutes, String category) {
        SalonService s = new SalonService(name, description, price, durationMinutes, category);
        return serviceRepo.save(s);
    }

    public List<SalonService> findAll() {
        return serviceRepo.findAll();
    }

    public List<SalonService> findActive() {
        return serviceRepo.findByActive(true);
    }

    public Optional<SalonService> findById(Long id) {
        return serviceRepo.findById(id);
    }

    public List<SalonService> searchByName(String keyword) {
        return serviceRepo.findByNameContainingIgnoreCase(keyword);
    }

    public SalonService update(Long id, String name, String description, double price, int durationMinutes, String category) {
        SalonService s = serviceRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found with id: " + id));
        if (name != null && !name.isBlank()) s.setName(name);
        if (description != null) s.setDescription(description);
        s.setPrice(price);
        s.setDurationMinutes(durationMinutes);
        if (category != null) s.setCategory(category);
        return serviceRepo.save(s);
    }

    public void toggleActive(Long id) {
        SalonService s = serviceRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found with id: " + id));
        s.setActive(!s.isActive());
        serviceRepo.save(s);
    }

    /**
     * Hard-delete a service along with all related reviews, appointments,
     * and package associations to avoid foreign key constraint violations.
     */
    @Transactional
    public void delete(Long id) {
        SalonService service = serviceRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found with id: " + id));

        // 1. Delete all reviews referencing this service
        reviewRepo.deleteAll(reviewRepo.findByServiceId(id));

        // 2. Delete all appointments referencing this service
        appointmentRepo.deleteAll(appointmentRepo.findByServiceId(id));

        // 3. Remove this service from any packages (ManyToMany join table)
        List<ServicePackage> packages = packageRepo.findAll();
        for (ServicePackage pkg : packages) {
            if (pkg.getServices().remove(service)) {
                packageRepo.save(pkg);
            }
        }

        // 4. Delete the service itself
        serviceRepo.delete(service);
    }
}


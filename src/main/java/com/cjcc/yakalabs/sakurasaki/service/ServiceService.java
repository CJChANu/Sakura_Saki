package com.cjcc.yakalabs.sakurasaki.service;

import com.cjcc.yakalabs.sakurasaki.model.Service;
import com.cjcc.yakalabs.sakurasaki.repository.ServiceRepository;
import java.util.List;

@org.springframework.stereotype.Service
public class ServiceService {
    private final ServiceRepository serviceRepository;

    public ServiceService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public List<Service> getAll() {
        return serviceRepository.findAll();
    }

    public List<Service> getActive() {
        return serviceRepository.findByActive(true);
    }

    public Service getById(Long id) {
        return serviceRepository.findById(id).orElse(null);
    }

    public Service save(Service service) {
        return serviceRepository.save(service);
    }

    public void delete(Long id) {
        serviceRepository.deleteById(id);
    }

    public void toggleActive(Long id) {
        serviceRepository.findById(id).ifPresent(s -> {
            s.setActive(!s.isActive());
            serviceRepository.save(s);
        });
    }
}

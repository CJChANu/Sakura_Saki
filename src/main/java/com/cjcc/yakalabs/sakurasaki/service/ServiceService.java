package com.cjcc.yakalabs.sakurasaki.service;

import com.cjcc.yakalabs.sakurasaki.model.Service;
import com.cjcc.yakalabs.sakurasaki.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@org.springframework.stereotype.Service
public class ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    public List<Service> getActive() {
        return serviceRepository.findAll();
    }

    public Service getById(Long id) {
        return serviceRepository.findById(id).orElse(null);
    }

    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }

    public List<Service> searchServices(String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            return serviceRepository.findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(keyword, keyword);
        }
        return serviceRepository.findAll();
    }

    public Service getServiceById(Long id) {
        return serviceRepository.findById(id).orElse(null);
    }

    public Service saveService(Service service) {
        return serviceRepository.save(service);
    }

    public void deleteService(Long id) {
        serviceRepository.deleteById(id);
    }
}
package com.cjcc.yakalabs.sakurasaki.service;

import com.cjcc.yakalabs.sakurasaki.model.SalonService;
import com.cjcc.yakalabs.sakurasaki.model.ServicePackage;
import com.cjcc.yakalabs.sakurasaki.repository.SalonServiceRepository;
import com.cjcc.yakalabs.sakurasaki.repository.ServicePackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceManagementServiceImpl implements ServiceManagementService {

    @Autowired
    private SalonServiceRepository salonServiceRepository;

    @Autowired
    private ServicePackageRepository servicePackageRepository;

    // ---- Service CRUD ----

    @Override
    public void addService(SalonService salonService) {
        salonServiceRepository.save(salonService);
    }

    @Override
    public List<SalonService> getAllServices() {
        return salonServiceRepository.findAll();
    }

    @Override
    public SalonService getServiceById(Long id) {
        return salonServiceRepository.findById(id).orElse(null);
    }

    @Override
    public void updateService(SalonService updatedService) {
        salonServiceRepository.save(updatedService);
    }

    @Override
    public void deleteService(Long id) {
        salonServiceRepository.deleteById(id);
    }

    // ---- Package CRUD ----

    @Override
    public void addPackage(ServicePackage servicePackage) {
        // Recalculate finalPrice before saving to ensure consistency
        servicePackage.setFinalPrice(servicePackage.getTotalPrice() - servicePackage.getDiscount());
        servicePackageRepository.save(servicePackage);
    }

    @Override
    public List<ServicePackage> getAllPackages() {
        return servicePackageRepository.findAll();
    }

    @Override
    public ServicePackage getPackageById(Long id) {
        return servicePackageRepository.findById(id).orElse(null);
    }

    @Override
    public void updatePackage(ServicePackage updatedPackage) {
        updatedPackage.setFinalPrice(updatedPackage.getTotalPrice() - updatedPackage.getDiscount());
        servicePackageRepository.save(updatedPackage);
    }

    @Override
    public void deletePackage(Long id) {
        servicePackageRepository.deleteById(id);
    }
}

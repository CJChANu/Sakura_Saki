package com.cjcc.yakalabs.sakurasaki.service;

import com.cjcc.yakalabs.sakurasaki.model.SalonService;
import com.cjcc.yakalabs.sakurasaki.model.ServicePackage;
import com.cjcc.yakalabs.sakurasaki.repository.SalonServiceRepository;
import com.cjcc.yakalabs.sakurasaki.repository.ServicePackageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceManagementServiceImpl implements ServiceManagementService {

    private final SalonServiceRepository salonServiceRepository;
    private final ServicePackageRepository servicePackageRepository;

    public ServiceManagementServiceImpl(SalonServiceRepository salonServiceRepository, ServicePackageRepository servicePackageRepository) {
        this.salonServiceRepository = salonServiceRepository;
        this.servicePackageRepository = servicePackageRepository;
    }

    @Override
    public void addService(SalonService salonService) {
        salonServiceRepository.save(salonService);
    }

    @Override
    public List<SalonService> getAllServices() {
        return salonServiceRepository.findAll();
    }

    @Override
    public SalonService getServiceById(String serviceId) {
        try {
            Long id = Long.parseLong(serviceId);
            return salonServiceRepository.findById(id).orElse(null);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public void updateService(SalonService updatedService) {
        salonServiceRepository.save(updatedService);
    }

    @Override
    public void deleteService(String serviceId) {
        try {
            Long id = Long.parseLong(serviceId);
            salonServiceRepository.deleteById(id);
        } catch (NumberFormatException e) {
            // ignore
        }
    }

    @Override
    public void addPackage(ServicePackage servicePackage) {
        servicePackageRepository.save(servicePackage);
    }

    @Override
    public List<ServicePackage> getAllPackages() {
        return servicePackageRepository.findAll();
    }

    @Override
    public ServicePackage getPackageById(String packageId) {
        try {
            Long id = Long.parseLong(packageId);
            return servicePackageRepository.findById(id).orElse(null);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public void updatePackage(ServicePackage updatedPackage) {
        servicePackageRepository.save(updatedPackage);
    }

    @Override
    public void deletePackage(String packageId) {
        try {
            Long id = Long.parseLong(packageId);
            servicePackageRepository.deleteById(id);
        } catch (NumberFormatException e) {
            // ignore
        }
    }
}

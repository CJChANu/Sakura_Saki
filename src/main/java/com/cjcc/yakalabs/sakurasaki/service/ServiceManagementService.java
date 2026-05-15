package com.cjcc.yakalabs.sakurasaki.service;


import com.cjcc.yakalabs.sakurasaki.model.SalonService;
import com.cjcc.yakalabs.sakurasaki.model.ServicePackage;

import java.util.List;

public interface ServiceManagementService {

    // Service methods
    void addService(SalonService salonService);
    List<SalonService> getAllServices();
    SalonService getServiceById(String serviceId);
    void updateService(SalonService updatedService);
    void deleteService(String serviceId);

    // Package methods
    void addPackage(ServicePackage servicePackage);
    List<ServicePackage> getAllPackages();
    ServicePackage getPackageById(String packageId);
    void updatePackage(ServicePackage updatedPackage);
    void deletePackage(String packageId);
}
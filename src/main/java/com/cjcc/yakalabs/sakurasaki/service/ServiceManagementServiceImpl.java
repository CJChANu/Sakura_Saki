package com.cjcc.yakalabs.sakurasaki.service;

import com.cjcc.yakalabs.sakurasaki.model.SalonService;
import com.cjcc.yakalabs.sakurasaki.model.ServicePackage;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


@Service
public class ServiceManagementServiceImpl implements ServiceManagementService {

    private static final String SERVICE_FILE = "src/main/resources/data/services.txt";
    private static final String PACKAGE_FILE = "src/main/resources/data/packages.txt";

    @Override
    public void addService(SalonService salonService) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SERVICE_FILE, true))) {
            writer.write(salonService.toFileString());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<SalonService> getAllServices() {
        List<SalonService> services = new ArrayList<>();

        File file = new File(SERVICE_FILE);
        if (!file.exists()) {
            return services;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\\|");

                if (data.length == 7) {
                    SalonService service = new SalonService();
                    service.setServiceId(data[0]);
                    service.setServiceName(data[1]);
                    service.setCategory(data[2]);
                    service.setDuration(Integer.parseInt(data[3]));
                    service.setPrice(Double.parseDouble(data[4]));
                    service.setDescription(data[5]);
                    service.setAvailable(Boolean.parseBoolean(data[6]));

                    services.add(service);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return services;
    }

    @Override
    public SalonService getServiceById(String serviceId) {
        List<SalonService> services = getAllServices();

        for (SalonService service : services) {
            if (service.getServiceId().equalsIgnoreCase(serviceId)) {
                return service;
            }
        }
        return null;
    }

    @Override
    public void updateService(SalonService updatedService) {
        List<SalonService> services = getAllServices();

        for (int i = 0; i < services.size(); i++) {
            if (services.get(i).getServiceId().equalsIgnoreCase(updatedService.getServiceId())) {
                services.set(i, updatedService);
                break;
            }
        }

        saveAllServices(services);
    }

    @Override
    public void deleteService(String serviceId) {
        List<SalonService> services = getAllServices();
        services.removeIf(service -> service.getServiceId().equalsIgnoreCase(serviceId));
        saveAllServices(services);
    }

    private void saveAllServices(List<SalonService> services) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SERVICE_FILE))) {
            for (SalonService service : services) {
                writer.write(service.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addPackage(ServicePackage servicePackage) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PACKAGE_FILE, true))) {
            writer.write(servicePackage.toFileString());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<ServicePackage> getAllPackages() {
        List<ServicePackage> packages = new ArrayList<>();

        File file = new File(PACKAGE_FILE);
        if (!file.exists()) {
            return packages;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\\|");

                if (data.length == 7) {
                    ServicePackage servicePackage = new ServicePackage();
                    servicePackage.setPackageId(data[0]);
                    servicePackage.setPackageName(data[1]);
                    servicePackage.setIncludedServices(data[2]);
                    servicePackage.setTotalPrice(Double.parseDouble(data[3]));
                    servicePackage.setDiscount(Double.parseDouble(data[4]));
                    servicePackage.setFinalPrice(Double.parseDouble(data[5]));
                    servicePackage.setDescription(data[6]);

                    packages.add(servicePackage);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return packages;
    }

    @Override
    public ServicePackage getPackageById(String packageId) {
        List<ServicePackage> packages = getAllPackages();

        for (ServicePackage servicePackage : packages) {
            if (servicePackage.getPackageId().equalsIgnoreCase(packageId)) {
                return servicePackage;
            }
        }
        return null;
    }

    @Override
    public void updatePackage(ServicePackage updatedPackage) {
        List<ServicePackage> packages = getAllPackages();

        for (int i = 0; i < packages.size(); i++) {
            if (packages.get(i).getPackageId().equalsIgnoreCase(updatedPackage.getPackageId())) {
                packages.set(i, updatedPackage);
                break;
            }
        }

        saveAllPackages(packages);
    }

    @Override
    public void deletePackage(String packageId) {
        List<ServicePackage> packages = getAllPackages();
        packages.removeIf(pkg -> pkg.getPackageId().equalsIgnoreCase(packageId));
        saveAllPackages(packages);
    }

    private void saveAllPackages(List<ServicePackage> packages) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PACKAGE_FILE))) {
            for (ServicePackage servicePackage : packages) {
                writer.write(servicePackage.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
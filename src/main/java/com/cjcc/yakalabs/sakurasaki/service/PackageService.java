package com.cjcc.yakalabs.sakurasaki.service;

import com.cjcc.yakalabs.sakurasaki.model.Package;
import com.cjcc.yakalabs.sakurasaki.model.Service;
import com.cjcc.yakalabs.sakurasaki.repository.PackageRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@org.springframework.stereotype.Service
public class PackageService {

    @Autowired
    private PackageRepository packageRepository;

    public List<Package> getActive() {
        return packageRepository.findAll();
    }

    public Package getById(Long id) {
        return packageRepository.findById(id).orElse(null);
    }

    public List<Package> getAllPackages() {
        return packageRepository.findAll();
    }

    public Package getPackageById(Long id) {
        return packageRepository.findById(id).orElse(null);
    }

    public Package savePackage(Package salonPackage) {
        int totalDuration = 0;
        if (salonPackage.getServices() != null) {
            for (Service s : salonPackage.getServices()) {
                totalDuration += s.getDuration();
            }
        }
        salonPackage.setTotalDuration(totalDuration);
        return packageRepository.save(salonPackage);
    }

    public void deletePackage(Long id) {
        packageRepository.deleteById(id);
    }
}
package com.cjcc.yakalabs.sakurasaki.service;

import com.cjcc.yakalabs.sakurasaki.model.Package;
import com.cjcc.yakalabs.sakurasaki.repository.PackageRepository;

import java.util.List;

@org.springframework.stereotype.Service
public class PackageService {
    private final PackageRepository packageRepository;

    public PackageService(PackageRepository packageRepository) {
        this.packageRepository = packageRepository;
    }

    public List<Package> getAll() {
        return packageRepository.findAll();
    }

    public List<Package> getActive() {
        return packageRepository.findByActive(true);
    }

    public Package getById(Long id) {
        return packageRepository.findById(id).orElse(null);
    }

    public Package save(Package pkg) {
        return packageRepository.save(pkg);
    }

    public void delete(Long id) {
        packageRepository.deleteById(id);
    }

    public void toggleActive(Long id) {
        packageRepository.findById(id).ifPresent(p -> {
            p.setActive(!p.isActive());
            packageRepository.save(p);
        });
    }
}

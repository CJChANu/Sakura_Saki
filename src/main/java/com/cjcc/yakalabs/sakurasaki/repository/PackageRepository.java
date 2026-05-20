package com.cjcc.yakalabs.sakurasaki.repository;

import com.cjcc.yakalabs.sakurasaki.model.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PackageRepository extends JpaRepository<Package, Long> {
}
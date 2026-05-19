package com.cjcc.yakalabs.sakurasaki.repository;

import com.cjcc.yakalabs.sakurasaki.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Customer entities (subtype of User with discriminator "CUSTOMER").
 * Provides customer-specific queries for the authentication and admin modules.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByUsername(String username);

    Optional<Customer> findByEmail(String email);

    List<Customer> findByUsernameContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String username, String firstName, String lastName);

    List<Customer> findByEnabled(boolean enabled);

    @Query("SELECT c FROM Customer c WHERE c.enabled = :enabled ORDER BY c.createdAt DESC")
    List<Customer> findAllByEnabledOrderByCreatedAtDesc(@Param("enabled") boolean enabled);

    long countByEnabled(boolean enabled);
}

package com.cjcc.yakalabs.sakurasaki.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Customer extends User — represents a registered salon customer.
 * OOP: Inheritance — Customer IS-A User with additional customer-specific
 * fields.
 * OOP: Polymorphism — Different behavior for CUSTOMER vs ADMIN/STAFF roles.
 */
@Entity
@DiscriminatorValue("CUSTOMER")
public class Customer extends User {

    private String address;

    @Column(name = "loyalty_points")
    private Integer loyaltyPoints = 0;

    public Customer() {
    }

    // --- Getters and Setters (OOP: Encapsulation) ---

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(Integer loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }
}

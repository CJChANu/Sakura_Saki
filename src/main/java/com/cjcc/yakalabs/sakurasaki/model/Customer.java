package com.cjcc.yakalabs.sakurasaki.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Customer extends User — inherits username, password, email, firstName, lastName, phone.
 * Demonstrates inheritance: Customer IS-A User.
 */
@Entity
@DiscriminatorValue("CUSTOMER")
public class Customer extends User {

    public Customer() {}

    public Customer(String firstName, String lastName, String email, String phone) {
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setEmail(email);
        this.setPhone(phone);
        this.setRole("ROLE_USER");
        this.setEnabled(true);
    }
}

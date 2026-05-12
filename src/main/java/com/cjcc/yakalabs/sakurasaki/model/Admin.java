package com.cjcc.yakalabs.sakurasaki.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends User {

    // Inherits all fields from User (including role).
    // No duplicate 'role' field here — the role is managed via User.setRole("ROLE_ADMIN").
}

package com.cjcc.yakalabs.sakurasaki.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("THERAPIST")
public class Therapist extends Staff {
    @Override
    public String getStaffType() { return "THERAPIST"; }
    @Override
    public String getRole() { return "Therapist"; }
}

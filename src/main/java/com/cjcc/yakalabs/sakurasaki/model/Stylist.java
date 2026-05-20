package com.cjcc.yakalabs.sakurasaki.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("STYLIST")
public class Stylist extends Staff {
    @Override
    public String getStaffType() { return "STYLIST"; }
    @Override
    public String getRole() { return "Stylist"; }
}

package com.cjcc.yakalabs.sakurasaki.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("GENERAL")
public class GeneralStaff extends Staff {
    @Override
    public String getStaffType() { return "GENERAL"; }
    @Override
    public String getRole() { return "General"; }
}

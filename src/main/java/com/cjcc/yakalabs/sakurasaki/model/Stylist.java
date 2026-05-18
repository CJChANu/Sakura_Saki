package com.cjcc.yakalabs.sakurasaki.model;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("Stylist")
public class Stylist extends Staff {

    public Stylist() { super(); }

    public Stylist(String staffId, String name, String phone,
                   String email, String workingDays, String timeSlot) {
        super(staffId, name, phone, email, workingDays, timeSlot);
    }

    @Override
    public String getRole() { return "Stylist"; }
}
package com.cjcc.yakalabs.sakurasaki.model;
import jakarta.persistence.*;

@Entity
@DiscriminatorValue("Therapist")
public class Therapist extends Staff {

    public Therapist() { super(); }

    public Therapist(String staffId, String name, String phone,
                     String email, String workingDays, String timeSlot) {
        super(staffId, name, phone, email, workingDays, timeSlot);
    }

    @Override
    public String getRole() { return "Therapist"; }
}
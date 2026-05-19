package com.cjcc.yakalabs.sakurasaki.model;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("HAIR")
public class HairService extends SalonService {

    private String hairType;

    public HairService() {
        super();
    }

    public HairService(String serviceName, int duration, double price,
                       String description, boolean available, String hairType) {
        super(serviceName, "Hair", duration, price, description, available);
        this.hairType = hairType;
    }

    public String getHairType() { return hairType; }
    public void setHairType(String hairType) { this.hairType = hairType; }

    @Override
    public String getCategoryDetails() {
        return "Hair service for " + hairType + " hair type";
    }
}

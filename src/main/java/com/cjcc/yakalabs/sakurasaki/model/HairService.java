package com.cjcc.yakalabs.sakurasaki.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("HAIR")
public class HairService extends SalonService {
    private String hairType;

    public HairService() {
        super();
    }

    public HairService(String serviceId, String serviceName, int duration, double price,
                       String description, boolean available, String hairType) {
        super(serviceId, serviceName, "Hair", duration, price, description, available);
        this.hairType = hairType;
    }

    public String getHairType() {
        return hairType;
    }

    public void setHairType(String hairType) {
        this.hairType = hairType;
    }

    @Override
    public String getCategoryDetails() {
        return "Hair service for " + hairType + " hair type";
    }
}

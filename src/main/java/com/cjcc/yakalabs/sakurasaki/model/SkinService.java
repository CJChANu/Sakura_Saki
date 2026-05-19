package com.cjcc.yakalabs.sakurasaki.model;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("SKIN")
public class SkinService extends SalonService {

    private String skinType;

    public SkinService() {
        super();
    }

    public SkinService(String serviceName, int duration, double price,
                       String description, boolean available, String skinType) {
        super(serviceName, "Skin", duration, price, description, available);
        this.skinType = skinType;
    }

    public String getSkinType() { return skinType; }
    public void setSkinType(String skinType) { this.skinType = skinType; }

    @Override
    public String getCategoryDetails() {
        return "Skin service suitable for " + skinType + " skin";
    }
}

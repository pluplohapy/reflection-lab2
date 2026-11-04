package org.example.testmodel;

import org.example.generator.Generatable;

@Generatable
public class Parcel implements Shipment {

    private String trackingNumber;
    private int weightInGrams;

    public Parcel() {
    }

    @Override
    public String trackingNumber() {
        return trackingNumber;
    }

    @Override
    public int weightInGrams() {
        return weightInGrams;
    }
}

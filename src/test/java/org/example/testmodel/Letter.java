package org.example.testmodel;

public class Letter implements Shipment {

    private String trackingNumber;
    private int weightInGrams;

    public Letter() {
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

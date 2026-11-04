package org.example.testmodel;

public class BikeCourier implements Courier {

    private String route;

    public BikeCourier() {
    }

    @Override
    public String deliver() {
        return route;
    }
}

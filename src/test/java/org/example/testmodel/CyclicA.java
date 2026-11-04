package org.example.testmodel;

public class CyclicA {

    private CyclicB b;

    public CyclicA() {
    }

    public CyclicB getB() {
        return b;
    }
}

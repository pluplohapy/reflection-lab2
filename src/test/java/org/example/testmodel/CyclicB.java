package org.example.testmodel;

public class CyclicB {

    private CyclicA a;

    public CyclicB() {
    }

    public CyclicA getA() {
        return a;
    }
}

package org.example.testmodel;

import org.example.generator.SelfValidating;

public class NeverValid implements SelfValidating {

    private int value;

    public NeverValid() {
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean isValid() {
        return false;
    }
}

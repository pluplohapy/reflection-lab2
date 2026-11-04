package org.example.testmodel;

public class BrokenConstructor {

    public BrokenConstructor() {
        throw new IllegalStateException("boom");
    }
}

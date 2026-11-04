package org.example.testmodel;

import org.example.generator.Generatable;

@Generatable
public class Dog extends Animal {

    public Dog(String name) {
        super(name);
    }

    @Override
    public String sound() {
        return "woof";
    }
}

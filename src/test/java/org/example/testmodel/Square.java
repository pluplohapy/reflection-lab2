package org.example.testmodel;

import org.example.classes.Shape;
import org.example.generator.SelfValidating;

public class Square implements Shape, SelfValidating {

    private final double side;

    public Square(double side) {
        this.side = side;
    }

    public double getSide() {
        return side;
    }

    @Override
    public double getArea() {
        return side * side;
    }

    @Override
    public double getPerimeter() {
        return 4.0d * side;
    }

    @Override
    public boolean isValid() {
        return Double.isFinite(side) && side > 0;
    }
}

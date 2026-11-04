package org.example.classes;

import org.example.generator.Generatable;
import org.example.generator.SelfValidating;

@Generatable
public class Rectangle implements Shape, SelfValidating {

    private final double length;
    private final double width;

    public Rectangle(double length, double width) {
        this.length = length;
        this.width = width;
    }

    public double getLength() {
        return length;
    }

    public double getWidth() {
        return width;
    }

    @Override
    public double getArea() {
        return length * width;
    }

    @Override
    public double getPerimeter() {
        return 2 * (length + width);
    }

    @Override
    public boolean isValid() {

        return Double.isFinite(length) && Double.isFinite(width) && length > 0 && width > 0;
    }

    @Override
    public String toString() {
        return "Rectangle(length=" + length + ", width=" + width + ", area=" + getArea() + ")";
    }
}

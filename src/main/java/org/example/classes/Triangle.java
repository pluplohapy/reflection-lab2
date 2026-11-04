package org.example.classes;

import org.example.generator.Generatable;
import org.example.generator.SelfValidating;

@Generatable
public class Triangle implements Shape, SelfValidating {

    private final double sideA;
    private final double sideB;
    private final double sideC;

    public Triangle(double sideA, double sideB, double sideC) {
        this.sideA = sideA;
        this.sideB = sideB;
        this.sideC = sideC;
    }

    public double getSideA() {
        return sideA;
    }

    public double getSideB() {
        return sideB;
    }

    public double getSideC() {
        return sideC;
    }

    @Override
    public double getArea() {

        double semiPerimeter = getPerimeter() / 2.0d;
        return Math.sqrt(semiPerimeter
                * (semiPerimeter - sideA)
                * (semiPerimeter - sideB)
                * (semiPerimeter - sideC));
    }

    @Override
    public double getPerimeter() {
        return sideA + sideB + sideC;
    }

    @Override
    public boolean isValid() {

        if (!Double.isFinite(sideA) || !Double.isFinite(sideB) || !Double.isFinite(sideC)) {
            return false;
        }
        if (sideA <= 0 || sideB <= 0 || sideC <= 0) {
            return false;
        }
        return sideA + sideB > sideC && sideB + sideC > sideA && sideA + sideC > sideB;
    }

    @Override
    public String toString() {
        return "Triangle(sideA=" + sideA + ", sideB=" + sideB + ", sideC=" + sideC
                + ", area=" + getArea() + ")";
    }
}

package org.example.generator;

import org.example.classes.Shape;
import org.example.testmodel.Animal;
import org.example.testmodel.BikeCourier;
import org.example.testmodel.Courier;
import org.example.testmodel.Letter;
import org.example.testmodel.Parcel;
import org.example.testmodel.Shipment;
import org.example.testmodel.Square;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GeneratableSemanticsTest {

    private final Generator generator = new Generator(6L);

    @Test
    void onlyAnnotatedImplementationIsUsed() {
        for (int i = 0; i < 50; i++) {
            assertInstanceOf(Parcel.class, generator.generateValueOfType(Shipment.class));
        }
    }

    @Test
    void unannotatedClassCanStillBeRequestedDirectly() {
        Letter letter = generator.generateValueOfType(Letter.class);
        assertNotNull(letter);
        assertNotNull(letter.trackingNumber());
    }

    @Test
    void unannotatedShapeImplementationIsNotInTheCatalogue() {
        for (int i = 0; i < 200; i++) {
            assertFalse(generator.generateValueOfType(Shape.class) instanceof Square);
        }
    }

    @Test
    void explicitRegistrationAddsAnImplementation() {
        Generator custom = new Generator(7L);
        assertThrows(GenerationException.class, () -> custom.generateValueOfType(Courier.class));

        custom.register(BikeCourier.class);

        assertInstanceOf(BikeCourier.class, custom.generateValueOfType(Courier.class));
    }

    @Test
    void registrationRejectsNonConcreteTypes() {

        assertThrows(IllegalArgumentException.class, () -> new Generator(1L).register(Shipment.class));

        assertThrows(IllegalArgumentException.class, () -> new Generator(1L).register(Animal.class));

        assertThrows(IllegalArgumentException.class, () -> new Generator(1L).register(int.class));
    }
}

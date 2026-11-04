package org.example;

import org.example.classes.BinaryTreeNode;
import org.example.classes.Cart;
import org.example.classes.Color;
import org.example.classes.Example;
import org.example.classes.Product;
import org.example.classes.Rectangle;
import org.example.classes.Shape;
import org.example.classes.Triangle;
import org.example.generator.GenerationException;
import org.example.generator.Generator;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class GenerateExample {

    private GenerateExample() {
    }

    public static void main(String[] args) {
        Generator generator = new Generator(2025L);

        section("Примитивы и обёртки");
        System.out.println("int      -> " + generator.generateValueOfType(int.class));
        System.out.println("double   -> " + generator.generateValueOfType(double.class));
        System.out.println("boolean  -> " + generator.generateValueOfType(boolean.class));
        System.out.println("char     -> '" + generator.generateValueOfType(char.class) + "'");
        System.out.println("Integer  -> " + generator.generateValueOfType(Integer.class));

        section("Строки и перечисления");
        System.out.println("String   -> " + generator.generateValueOfType(String.class));
        System.out.println("Color    -> " + generator.generateValueOfType(Color.class));

        section("Массивы");
        System.out.println("int[]       -> " + Arrays.toString(generator.generateValueOfType(int[].class)));
        System.out.println("String[]    -> " + Arrays.toString(generator.generateValueOfType(String[].class)));
        System.out.println("Color[]     -> " + Arrays.toString(generator.generateValueOfType(Color[].class)));
        System.out.println("Rectangle[] -> " + Arrays.toString(generator.generateValueOfType(Rectangle[].class)));
        System.out.println("int[][]     -> " + Arrays.deepToString(generator.generateValueOfType(int[][].class)));

        section("Коллекции");
        List<?> items = generator.generateValueOfType(List.class);
        Map<?, ?> entries = generator.generateValueOfType(Map.class);
        System.out.println("List -> " + items);
        System.out.println("Map  -> " + entries);

        section("Пользовательские объекты");
        System.out.println("Rectangle      -> " + generator.generateValueOfType(Rectangle.class));
        System.out.println("Triangle       -> " + generator.generateValueOfType(Triangle.class));
        System.out.println("Example        -> " + generator.generateValueOfType(Example.class));
        Product product = generator.generateValueOfType(Product.class);
        System.out.println("Product        -> name=" + product.getName() + ", price=" + product.getPrice());
        Cart cart = generator.generateValueOfType(Cart.class);
        System.out.println("Cart           -> " + cart.getItems().size() + " товаров");
        BinaryTreeNode tree = generator.generateValueOfType(BinaryTreeNode.class);
        System.out.println("BinaryTreeNode -> root=" + tree.getData() + ", глубина=" + depthOf(tree));

        section("Интерфейсы");
        for (int i = 0; i < 5; i++) {
            Shape shape = generator.generateValueOfType(Shape.class);
            System.out.printf(Locale.ROOT, "  %-9s площадь=%10.2f периметр=%10.2f%n",
                    shape.getClass().getSimpleName(), shape.getArea(), shape.getPerimeter());
        }

        section("Предметные ограничения");
        for (int i = 0; i < 3; i++) {
            Rectangle rectangle = generator.generateValueOfType(Rectangle.class);
            Triangle triangle = generator.generateValueOfType(Triangle.class);
            System.out.println("  rectangle: " + rectangle);
            System.out.println("  triangle:  " + triangle);
        }

        section("Ошибки");
        try {
            generator.generateValueOfType(void.class);
        } catch (GenerationException e) {
            System.out.println("void.class   -> " + e.getMessage());
        }
        try {
            generator.generateValueOfType(Thread.class);
        } catch (GenerationException e) {
            System.out.println("Thread.class -> " + e.getMessage());
        }
    }

    private static int depthOf(BinaryTreeNode node) {
        if (node == null) {
            return 0;
        }
        return 1 + Math.max(depthOf(node.getLeft()), depthOf(node.getRight()));
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}

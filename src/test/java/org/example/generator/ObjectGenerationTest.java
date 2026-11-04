package org.example.generator;

import org.example.classes.BinaryTreeNode;
import org.example.classes.Cart;
import org.example.classes.Example;
import org.example.classes.Product;
import org.example.classes.Rectangle;
import org.example.classes.Triangle;
import org.example.testmodel.Book;
import org.example.testmodel.Library;
import org.example.testmodel.PartialFields;
import org.example.testmodel.Point;
import org.example.testmodel.SecretHolder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ObjectGenerationTest {

    private final Generator generator = new Generator(4L);

    @Test
    void generatesTemplateClasses() {
        Rectangle rectangle = generator.generateValueOfType(Rectangle.class);
        assertTrue(rectangle.isValid());
        assertTrue(rectangle.getArea() > 0);

        Triangle triangle = generator.generateValueOfType(Triangle.class);
        assertTrue(triangle.isValid());

        Product product = generator.generateValueOfType(Product.class);
        assertNotNull(product.getName());
        assertFalse(product.getName().isEmpty());

        Example example = generator.generateValueOfType(Example.class);
        assertNotNull(example);
        assertTrue(example.toString().startsWith("Example("));
    }

    @Test
    void generatesNewClassWithGenericFields() {
        Library library = generator.generateValueOfType(Library.class);
        assertNotNull(library.getName());

        List<Book> books = library.getBooks();
        assertNotNull(books);
        assertFalse(books.isEmpty());
        for (Book book : books) {
            assertInstanceOf(Book.class, book);
            assertNotNull(book.getTitle());
        }
        assertNotNull(library.getShelfSizes());
        assertFalse(library.getShelfSizes().isEmpty());
    }

    @Test
    void supportsRecords() {
        Point point = generator.generateValueOfType(Point.class);
        assertNotNull(point);
        assertTrue(point.x() >= -1000 && point.x() <= 1000);
        assertTrue(point.y() >= -1000 && point.y() <= 1000);
    }

    @Test
    void supportsPrivateConstructor() {
        SecretHolder holder = generator.generateValueOfType(SecretHolder.class);
        assertNotNull(holder);
        assertNotNull(holder.revealSecret());
        assertFalse(holder.revealSecret().isEmpty());
    }

    @Test
    void unsupportedFieldKeepsDefaultValue() {
        PartialFields value = generator.generateValueOfType(PartialFields.class);
        assertNotNull(value.getName());
        assertNull(value.getWorker(), "поле неподдерживаемого типа остаётся null");
    }

    @Test
    void generatesClassesWithConstructorParameters() {
        Cart cart = generator.generateValueOfType(Cart.class);
        assertNotNull(cart.getItems());
        for (Product item : cart.getItems()) {
            assertInstanceOf(Product.class, item);
            assertNotNull(item.getName());
        }
    }

    @Test
    @Timeout(10)
    void selfReferentialClassIsDepthLimited() {
        BinaryTreeNode tree = generator.generateValueOfType(BinaryTreeNode.class);
        assertNotNull(tree);
        assertTrue(depthOf(tree) <= 5, "глубина дерева должна быть ограничена");
    }

    @Test
    @Timeout(20)
    void repeatedGenerationDoesNotOverflow() {
        for (int i = 0; i < 200; i++) {
            assertNotNull(generator.generateValueOfType(BinaryTreeNode.class));
        }
    }

    private static int depthOf(BinaryTreeNode node) {
        if (node == null) {
            return 0;
        }
        return 1 + Math.max(depthOf(node.getLeft()), depthOf(node.getRight()));
    }
}

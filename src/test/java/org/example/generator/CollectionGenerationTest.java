package org.example.generator;

import org.example.testmodel.Book;
import org.example.testmodel.Library;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CollectionGenerationTest {

    private final Generator generator = new Generator(3L);

    @Test
    void rawListIsNotEmpty() {
        List<?> items = generator.generateValueOfType(List.class);
        assertNotNull(items);
        assertFalse(items.isEmpty(), "список не должен быть пустым");
        assertTrue(items.size() <= 5);
        for (Object item : items) {
            assertNotNull(item);
        }
    }

    @Test
    void rawMapIsNotEmptyAndHasUniqueKeys() {
        Map<?, ?> entries = generator.generateValueOfType(Map.class);
        assertNotNull(entries);
        assertFalse(entries.isEmpty(), "отображение не должно быть пустым");
        assertEquals(entries.size(), entries.keySet().size());
        for (Map.Entry<?, ?> entry : entries.entrySet()) {
            assertNotNull(entry.getKey());
            assertNotNull(entry.getValue());
        }
    }

    @Test
    void genericFieldsUseTheirDeclaredTypes() {
        Library library = generator.generateValueOfType(Library.class);
        assertNotNull(library.getName());

        List<Book> books = library.getBooks();
        assertNotNull(books);
        assertFalse(books.isEmpty());
        for (Book book : books) {
            assertInstanceOf(Book.class, book);
            assertNotNull(book.getTitle());
        }

        Map<String, Integer> shelfSizes = library.getShelfSizes();
        assertNotNull(shelfSizes);
        assertFalse(shelfSizes.isEmpty());
        for (Map.Entry<String, Integer> entry : shelfSizes.entrySet()) {
            assertInstanceOf(String.class, entry.getKey());
            assertInstanceOf(Integer.class, entry.getValue());
        }
    }
}

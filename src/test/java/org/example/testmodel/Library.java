package org.example.testmodel;

import java.util.List;
import java.util.Map;

public class Library {

    private String name;
    private List<Book> books;
    private Map<String, Integer> shelfSizes;

    public Library() {
    }

    public String getName() {
        return name;
    }

    public List<Book> getBooks() {
        return books;
    }

    public Map<String, Integer> getShelfSizes() {
        return shelfSizes;
    }
}

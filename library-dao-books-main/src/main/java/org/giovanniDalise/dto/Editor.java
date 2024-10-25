package org.giovanniDalise.dto;

import java.util.Objects;
import java.util.Set;

public class Editor {
    private Long id;
    private String name;
    private Set<Book> books;

    public Editor(){};

    public Editor(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Book> getBooks() {
        return books;
    }

    public void setBooks(Set<Book> books) {
        this.books = books;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Editor editor = (Editor) o;
        return Objects.equals(name, editor.name);
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

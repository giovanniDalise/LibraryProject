package org.giovanniDalise.dto;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Book {
    private Long id;
    private String title;
    private String ISBN;
    private Editor editor;
    private Set<Author> authors = new HashSet<>();


    public Book(){};

    public Book(String title, String ISBN, Editor editor, Author[] authors) {
        this.title = title;
        this.ISBN = ISBN;
        this.editor = editor;
        if(authors != null) {
            this.authors.addAll(Arrays.asList(authors));
        }else{
            this.authors = new HashSet<>();
        }
    }
    public Author[] getAuthorsArray() {
        return authors.toArray(new Author[authors.size()]);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public Editor getEditor() {
        return editor;
    }

    public void setEditor(Editor editor) {
        this.editor = editor;
    }

    public Set<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<Author> authors) {
        this.authors = authors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(id, book.id);
    }

    @Override
    public String toString(){
        return title +", "+ ISBN +", "+ editor +", "+authors.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}

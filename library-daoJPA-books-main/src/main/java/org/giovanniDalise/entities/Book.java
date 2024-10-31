package org.giovanniDalise.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "book")
public class Book implements Serializable{

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "editor")
    private Editor editor;

    @ManyToMany(fetch = FetchType.EAGER, cascade =  {CascadeType.PERSIST, CascadeType.MERGE})// gestisco anche le modifiche su entità esistenti con all, diversamente da persist che agisce solo su entità non esistenti
    @JoinTable(
            name = "books_authors",
            joinColumns = @JoinColumn(name = "book"),
            inverseJoinColumns = @JoinColumn(name = "author")
    )
    private Set<Author> authors = new HashSet<>();

    @Column(name = "title")
    private String title;

    @Column(name = "ISBN")
    private String isbn;

    public Book() {}

    public Book(String title, String isbn, Editor editor, Author[] authors) {
        this.title = title;
        this.isbn = isbn;
        this.editor = editor;
        if(authors != null) {
            this.authors.addAll(Arrays.asList(authors));
        }else{
            this.authors = new HashSet<>();
        }
    }

    public Editor getEditor() {
        return editor;
    }

    public void setEditor(Editor editor) {
        this.editor = editor;
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

    public Set<Author> getAuthorsSet() {
        return authors;
    }

    public void setAuthors(Set<Author> authors) {
        this.authors = authors;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsbn() {
        return isbn;
    }

    public Set<Author> getAuthors() {
        return authors;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(id, book.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public String toString(){
        return title +", "+ isbn +", "+ editor +", "+authors.toString();
    }


}
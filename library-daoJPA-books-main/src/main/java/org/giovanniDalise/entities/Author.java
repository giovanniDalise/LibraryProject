package org.giovanniDalise.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name="author")
public class Author implements Serializable {

    private String name;
    private String surname;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "author_id")
    private Long id;

    @ManyToMany(mappedBy = "authors")
    private Set<Book> books;

    public Author(){}

    public Author(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public boolean equals(Object appObj){
        if(this==appObj) return true;
        if(!(appObj instanceof Author)) return false;
        Author a= (Author) appObj;
        return this.name.equals(a.name) && this.surname.equals(a.surname);
    }

    public String toString(){
        return this.name+" "+this.surname;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
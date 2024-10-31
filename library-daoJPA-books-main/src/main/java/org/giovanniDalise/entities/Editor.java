package org.giovanniDalise.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "editor")
public class Editor implements Serializable {

    @Column(name = "name")
    private String name;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "editor_id")
    private Long id;

    @OneToMany(mappedBy = "editor")
    private Set<Book> books = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Editor() {
    }

    public Editor(String nome) {
        this.name = nome;
    }

    public boolean equals(Object appObj) {
        if (this == appObj) return true;
        if (!(appObj instanceof Editor)) return false;
        Editor e = (Editor) appObj;
        return this.name.equals(e.name);
    }

    public String toString() {
        return this.name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
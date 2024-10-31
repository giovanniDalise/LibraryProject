package org.giovanniDalise.dao;

import org.giovanniDalise.entities.Author;
import org.giovanniDalise.entities.Book;
import org.giovanniDalise.entities.Editor;
import org.giovanniDalise.exception.DaoException;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class DaoBooks implements IDao<Book,Long>{

    EntityManagerFactory emf;

    public DaoBooks() {
        emf= Persistence.createEntityManagerFactory("libraryPU");
    }

    public DaoBooks(String persistenceUnitName) {
        emf= Persistence.createEntityManagerFactory(persistenceUnitName);
    }

    @Override
    public Long create(Book book) throws DaoException {

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(book);
            em.getTransaction().commit();
            return book.getId();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
            throw new DaoException("Error creating book "+e);
        } finally {
            em.close();
        }

    }

    //JPQL
    @Override
    public List<Book> read() throws DaoException {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Book> query = em.createQuery("SELECT b FROM Book b", Book.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new DaoException("Error reading books "+e);
        } finally {
            em.close();
        }
    }

    @Override
    public Long delete(Long id) throws DaoException {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Book book = em.find(Book.class, id);
            if (book != null) {
                em.remove(book);
                em.getTransaction().commit();
                return id;
            } else {
                em.getTransaction().rollback();
                throw new DaoException("Book not found with id " + id);
            }
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new DaoException("Error deleting book " + e);
        } finally {
            em.close();
        }
    }

    @Override
    public Long update(Long id, Book book) throws DaoException {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // Trova il libro esistente
            Book existingBook = em.find(Book.class, id);
            if (existingBook == null) {
                throw new DaoException("Book not found");
            }

            // Aggiorna solo i campi specificati
            existingBook.setTitle(book.getTitle());  // Aggiorna solo il titolo
            existingBook.setIsbn(book.getIsbn());    // Aggiorna solo l'ISBN

            // Aggiorna l'editore
            if (book.getEditor() != null) {
                existingBook.setEditor(em.merge(book.getEditor())); // Assicurati che l'editore sia gestito
            }

            // Aggiorna gli autori
            if (book.getAuthorsSet() != null) {
                existingBook.getAuthorsSet().clear(); // Rimuovi gli autori esistenti
                for (Author author : book.getAuthorsSet()) {
                    existingBook.getAuthorsSet().add(em.merge(author)); // Unisci ogni autore e aggiungilo al set
                }
            }

            em.merge(existingBook); // Fai il merge dell'entità esistente
            em.getTransaction().commit();
            return existingBook.getId();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new DaoException("Error updating book: " + e.getMessage()); // Includi l'eccezione originale
        } finally {
            em.close();
        }
    }





    @Override
    public Book getById(Long id) throws DaoException {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Book.class, id);
        } catch (Exception e) {
            throw new DaoException("Error getting book by id " + e);
        } finally {
            em.close();
        }
    }

    //JPQL
    @Override
    public List<Book> findByText(String searchText) throws DaoException {
        EntityManager em = emf.createEntityManager();
        try {
            String query = "SELECT b FROM Book b " +
                    "LEFT JOIN b.editor e " +
                    "LEFT JOIN b.authors a " +
                    "WHERE b.title LIKE :searchText " +
                    "OR b.isbn LIKE :searchText " +
                    "OR e.name LIKE :searchText " +
                    "OR a.name LIKE :searchText " +
                    "OR a.surname LIKE :searchText " +
                    "GROUP BY b.id";

            return em.createQuery(query, Book.class)
                    .setParameter("searchText", "%" + searchText + "%")
                    .getResultList();
        } catch (Exception e) {
            throw new DaoException("Error getting books by text: " + e);
        } finally {
            em.close();
        }
    }

    //Criteria Query
    @Override
    public List<Book> findByObject(Book searchBook) throws DaoException {
        EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Book> cq = cb.createQuery(Book.class);
        Root<Book> book = cq.from(Book.class);
        Join<Book, Editor> editor = book.join("editor", JoinType.LEFT);
        Join<Book, Author> authors = book.join("authors", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();

        // Aggiunge condizioni basate sui campi non nulli di searchBook
        if (searchBook.getTitle() != null && !searchBook.getTitle().isEmpty()) {
            predicates.add(cb.like(book.get("title"), "%" + searchBook.getTitle() + "%"));
        }
        if (searchBook.getIsbn() != null && !searchBook.getIsbn().isEmpty()) {
            predicates.add(cb.like(book.get("isbn"), "%" + searchBook.getIsbn() + "%"));
        }
        if (searchBook.getEditor() != null && searchBook.getEditor().getName() != null && !searchBook.getEditor().getName().isEmpty()) {
            predicates.add(cb.like(editor.get("name"), "%" + searchBook.getEditor().getName() + "%"));
        }

        if (searchBook.getAuthorsArray() != null) {
            for (Author author : searchBook.getAuthorsArray()) {
                if (author.getName() != null && !author.getName().isEmpty()) {
                    predicates.add(cb.like(authors.get("name"), "%" + author.getName() + "%"));
                }
                if (author.getSurname() != null && !author.getSurname().isEmpty()) {
                    predicates.add(cb.like(authors.get("surname"), "%" + author.getSurname() + "%"));
                }
            }
        }

        // Costruisce la query con i predicati
        cq.select(book).where(cb.and(predicates.toArray(new Predicate[0]))).distinct(true);

        // Esegue la query
        TypedQuery<Book> query = em.createQuery(cq);
        return query.getResultList();
    }

}

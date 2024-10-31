package it.alfasoft;
import org.giovanniDalise.entities.Book;

public class Updater {
    private long id;
    private Book book;

    public Updater() {

    }

    public long getId() {

        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Updater(long id, Book book) {
        this.id = id;
        this.book = book;
    }
}

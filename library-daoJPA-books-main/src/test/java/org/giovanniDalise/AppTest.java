package org.giovanniDalise;

import org.giovanniDalise.dao.DaoBooks;
import org.giovanniDalise.entities.Author;
import org.giovanniDalise.entities.Book;
import org.giovanniDalise.entities.Editor;
import org.giovanniDalise.exception.DaoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AppTest {

    DaoBooks dao;
    static int testNumber=1;

    @BeforeEach
    public void beforeEach() {
        System.out.println("test"+testNumber);
        dao = new DaoBooks("test"+testNumber);
        testNumber++;
    }

    @Test
    public void testCreate() throws DaoException {
        System.out.println("testCreate");
        Author[] authors = {new Author("Alice", "Smith"), new Author("Bob", "Smith")};
        Editor e =new Editor("test");
        Book book = new Book("titolo","123",e,authors);
        assertEquals(dao.create(book),6);
        List<Book> books = dao.read();
        for (Book b : books) {
            System.out.println(b);
        }
    }
    @Test
    public void testRead() throws DaoException {
        System.out.println("testRead");
        List<Book> books = dao.read();
        for (Book book : books) {
            System.out.println(book);
        }
        assertEquals(dao.read().size(), 5);
    }
    @Test
    public void testDelete() throws DaoException {
        System.out.println("testDelete");
        assertThrows(DaoException.class,()->dao.delete(6L));
        assertEquals(dao.delete(1L),1);
        List<Book> books = dao.read();
        for (Book book : books) {
            System.out.println(book);
        }
    }

    @Test
    public void testGetByIdBook() throws DaoException {
        System.out.println("testGetByIdBook");
        assertEquals(dao.getById(3L).getTitle(),"1984");
        assertEquals(dao.getById(1L).getTitle(),"The Great Gatsby");
        assertEquals(dao.getById(1L).getIsbn(),"9780743273565");
    }

    @Test
    public void testFindByString() throws DaoException {
        System.out.println("testFindByString");
        assertEquals(dao.findByText("1984").size(), 1);
        assertEquals(dao.findByText("1984").get(0).getIsbn(), "9780451524935");
        assertEquals(dao.findByText("1984").get(0).getEditor().getName(), "Michael Brown");
        assertEquals(dao.findByText("").size(), 5);
    }

    @Test
    public void testFindByBook() throws DaoException {
        System.out.println("testFindByBook");
        Book book = new Book("1984",null,null,null);
        assertEquals(dao.findByObject(book).get(0).getIsbn(), "9780451524935");

        Book book2 = new Book(null,"1234567890",null,null);
        assertEquals(dao.findByObject(book2).size(),0);

        Book book3 = new Book(null,null,new Editor("phia"),null);
        assertEquals(dao.findByObject(book3).get(0).getIsbn(), "9780141439518");

        Book book4 = new Book(null,null,null,null);
        assertEquals(dao.findByObject(book4).size(), 5);

    }

    @Test
    public void testUpdateBook() throws DaoException {
        System.out.println("testUpdateBook");
        assertEquals(dao.update(2L,new Book("title","1234",new Editor("test"),new Author[]{new Author("test", "test")})), 2L);
    }
}

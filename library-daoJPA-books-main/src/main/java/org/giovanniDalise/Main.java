package org.giovanniDalise;

import org.giovanniDalise.dao.DaoBooks;
import org.giovanniDalise.dao.IDao;
import org.giovanniDalise.entities.Author;
import org.giovanniDalise.entities.Book;
import org.giovanniDalise.entities.Editor;
import org.giovanniDalise.exception.DaoException;

import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws DaoException {

        DaoBooks dao = new DaoBooks();

        Long idLibroModificato = dao.update(2L,new Book("title","1234",new Editor("test"),new Author[]{new Author("test", "test")}));

        List<Book> libri = dao.read();
        System.out.println(libri);

    }
}
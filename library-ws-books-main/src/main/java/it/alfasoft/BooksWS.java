package it.alfasoft;


import org.giovanniDalise.dao.DaoBooks;
import org.giovanniDalise.dao.IDao;
import org.giovanniDalise.dto.Book;
import org.giovanniDalise.exception.DaoException;

import javax.inject.Inject;
import javax.jws.WebService;
import java.util.List;

@WebService(endpointInterface ="it.alfasoft.IBooksWS")
public class BooksWS implements IBooksWS {

    @Inject
    IDao<Book, Long> dao;

    @Override
    public List<Book> getBooks() throws WSBooksException {
        try {
            List<Book> result = dao.read();

            System.out.println("Libri restituiti: " + result);

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            throw new WSBooksException("Error in getBooks");
        }
    }

    @Override
    public Result insertBook(Book book) throws WSBooksException {
        Result result=new Result();

        try{

            long id= (long) dao.create(book);

            if(id!=-1){
                result.setMessage("Book successfully added to library");
            }else{
                result.setMessage("Insertion failed");
            }

            return result;

        }catch (DaoException e) {
            e.printStackTrace();
            throw new WSBooksException("Error in insertBook");
        }
    }

    @Override
    public Result deleteBook(long id) throws WSBooksException {
        Result result= new Result();
        try{

            long l = (long) dao.delete(id);

            if(l==0){
                result.setMessage("Book not present in the library");
            }else{
                result.setMessage("Book deleted successfully");
            }

        }catch (DaoException e){
            e.printStackTrace();
            throw new WSBooksException("Error in deleteBook");
        }

        return result;
    }

    @Override
    public Book getBookById(long id) throws WSBooksException {
        Book book=null;
        try {

            book = (Book) dao.getById(id);
            return book;

        } catch (DaoException e) {
            e.printStackTrace();
            throw new WSBooksException("Error in getBookById");
        }
    }

    @Override
    public Result updateBook(Updater u) throws WSBooksException {
        Result result = new Result();
        try {

            long updatedId = (long) dao.update(u.getId(), u.getBook());

            if (updatedId != -1) {
                result.setMessage("Book updated successfully");
            } else {
                result.setMessage("Update failed: Book not found");
            }

            return result;
        } catch (DaoException e) {
            e.printStackTrace();
            throw new WSBooksException("Error in updateBook");
        }
    }

    @Override
    public List<Book> findBooksByString(String param) throws WSBooksException {
        try {

            return dao.findByText(param);

        } catch (DaoException e) {
            e.printStackTrace();
            throw new WSBooksException("Error in findBooksByString");
        }
    }

    @Override
    public List<Book> findBooksByBook(Book book) throws WSBooksException {
        try {

            return dao.findByObject(book);

        } catch (DaoException e) {
            e.printStackTrace();
            throw new WSBooksException("Error in findBooksByBook");
        }
    }
}

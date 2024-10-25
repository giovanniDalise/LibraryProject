package it.alfasoft;

import org.giovanniDalise.dto.Book;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import java.util.List;


@WebService(targetNamespace = "http://alfasoft.it/")
public interface IBooksWS {

    @WebMethod(operationName= "getBooks")
    @WebResult(name = "libro")
    List<Book> getBooks() throws WSBooksException;

    @WebMethod(operationName= "insertBook")
    @WebResult(name = "result")
    Result insertBook(@WebParam(name= "book") Book book) throws WSBooksException;

    @WebMethod(operationName= "deleteBook")
    @WebResult(name = "result")
    Result deleteBook(@WebParam(name= "id") long id) throws WSBooksException;

    @WebMethod(operationName= "getBookById")
    @WebResult(name = "book")
    Book getBookById(@WebParam(name= "id") long id) throws WSBooksException;

    @WebMethod(operationName= "updateBook")
    @WebResult(name = "result")
    Result updateBook(@WebParam(name= "data") Updater u) throws WSBooksException;

    @WebMethod(operationName= "findBooksByString")
    @WebResult(name = "book")
    List<Book> findBooksByString(@WebParam(name= "param") String param) throws WSBooksException;

    @WebMethod(operationName= "findBooksByBook")
    @WebResult(name = "book")
    List<Book> findBooksByBook(@WebParam(name= "book") Book book) throws WSBooksException;
}

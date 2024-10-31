package it.alfasoft;

import alfasoft.it.BooksWSService;
import alfasoft.it.Book;


import java.util.List;


public class Client {
    public static void main(String[] args) {
        try {
            // Crea un'istanza del servizio
            BooksWSService wsClient = new BooksWSService();

            List<Book> result = wsClient.getBooksWSPort().getBooks();

            // Stampa i risultati ottenuti
            result.stream().forEach(r -> System.out.println(r.getTitle()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


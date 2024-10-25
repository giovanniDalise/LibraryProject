package org.giovanniDalise;

import org.giovanniDalise.dao.DaoBooks;
import org.giovanniDalise.dao.IDao;
import org.giovanniDalise.dto.Book;
import org.giovanniDalise.dto.Editor;
import org.giovanniDalise.dto.Author;

import org.giovanniDalise.exception.DaoException;

import java.util.List;
import java.util.Scanner;

public class BootStrap {
    private static IDao<Book, Long> dao = new DaoBooks();

    public static void main(String[] args) throws DaoException {
        Scanner input = new Scanner(System.in);
        int scelta = 0;

        do {
            System.out.println("Scegli un operazione:");
            System.out.println("1.Inserisci il libro");
            System.out.println("2.Visualizza i libri");
            System.out.println("3.Cancella un libro");
            System.out.println("4.Modifica un libro");
            System.out.println("5.Cerca per testo");
            System.out.println("6.Cerca per id");
            System.out.println("7.Cerca per libro");

            System.out.println("0.Esci");

            scelta = input.nextInt();
            input.nextLine();

            switch (scelta) {
                case 1:
                    inserisciLibro(input);
                    break;
                case 2:
                    visualizzaLibri();
                    break;
                case 3:
                    cancellaLibro(input);
                    break;
                case 4:
                    modificaLibro(input);
                    break;
                case 5:
                    cercaPerTesto(input);
                    break;
                case 6:
                    cercaPerId(input);
                    break;
                case 7:
                    cercaPerLibro(input);
                    break;
                case 0:
                    System.out.println("Arrivederci");
                    break;
                default:
                    System.out.println("Scelta non valida riprova!");
                    break;
            }
        } while (scelta != 0);
    }

    public static void inserisciLibro(Scanner input) throws DaoException {
        Book book = new Book("title","123",new Editor("nomeEditore"),new Author[]{new Author("nomeAutore","cognomeAutore")});
        Long idBookInserito = dao.create(book);
        System.out.println("Id Libro Inserito: " + idBookInserito);
    }

    public static void visualizzaLibri() throws DaoException{
        List<Book> elencoLibri = dao.read();
        for(Book book : elencoLibri){
            System.out.println(book.toString());
        }
    }
    public static void cercaPerTesto(Scanner input) throws DaoException {
        System.out.println("Inserisci il testo da cercare:");
        String titolo = input.nextLine();
        List<Book> elencoLibri = dao.findByText(titolo);
        if (elencoLibri.isEmpty()) {
            System.out.println("Nessun libro trovato.");
        } else {
            for (Book book : elencoLibri) {
                System.out.println(book.toString());
            }
        }
    }
    public static void cercaPerLibro(Scanner input) throws DaoException {
        if(dao.findByObject(new Book("barone", null, null, null)).get(0).getISBN().equals("9780743273565")) {
            System.out.println("libro trovato.");
        } else{
            System.out.println("libro non trovato.");
        }
    }

    public static void cercaPerId(Scanner input) throws DaoException{
        System.out.println("Inserisci l'id del libro da cercare:");
        Long idLibro = input.nextLong();
        Book book = dao.getById(idLibro);
        if (book == null) {
            System.out.println("Nessun libro trovato.");
        } else {
                System.out.println(book.toString());
            }
        }

    public static void cancellaLibro(Scanner input) throws DaoException{
        System.out.println("Inserisci l'id del libro da eliminare:");
        Long libroID = input.nextLong();
        dao.delete(libroID);
    }
    public static void modificaLibro(Scanner input) throws DaoException {
        dao.update(4L, new Book("title", "1234", new Editor("test"), new Author[]{new Author("test", "test")}));
    }
}


package org.giovanniDalise.dao;

import org.giovanniDalise.dto.Author;
import org.giovanniDalise.dto.Book;
import org.giovanniDalise.dto.Editor;
import org.giovanniDalise.exception.DaoException;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@ApplicationScoped
public class DaoBooks implements IDao<Book, Long> {
    private static final String URL = "URL";
    public static final String USERNAME = "USERNAME";
    public static final String PASSWORD = "PASSWORD";

    private Connection getConnection() throws DaoException {
        Properties properties = new Properties();

        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new DaoException("Unable to find config.properties file");
            }

            properties.load(input);

            String urlDB = properties.getProperty(URL);
            String username = properties.getProperty(USERNAME);
            String password = properties.getProperty(PASSWORD);

            return DriverManager.getConnection(urlDB, username, password);
        } catch (IOException e) {
            throw new DaoException("Error accessing config.properties: " + e.getMessage());
        } catch (SQLException e) {
            throw new DaoException("Database connection error: " + e.getMessage());
        }
    }

    protected Statement getStatement() throws SQLException, DaoException {
        return getConnection().createStatement();
    }

    private long getEditorID(Editor editor) throws SQLException, DaoException {
        long found = -1;
        if (editor != null) {
            String query = "SELECT editor_id FROM editor WHERE name = '" + editor.getName().replace("'", "''") + "';";
            ResultSet rs = getStatement().executeQuery(query);

            if (rs.next()) {
                found = rs.getLong("editor_id");
            }
        }

        return found;
    }

    private long getAuthorID(Author author) throws SQLException, DaoException {
        long found = -1;
        if (author != null) {
            String query = "SELECT author_id FROM author WHERE name='" + author.getName().replace("'", "''") + "' AND surname='" + author.getSurname().replace("'", "''") + "';";
            ResultSet rs = getStatement().executeQuery(query);

            if (rs.next()) {
                found = rs.getLong(1);
            }
        }

        return found;
    }
    public Long create(Book book) throws DaoException {
        String query = "";

        try {

            Statement stmt = getStatement();

            //controllo se editor esiste
            long id_editor = getEditorID(book.getEditor());
            if (id_editor == -1 && book.getEditor() != null) {//se non esiste lo creo
                query = "INSERT INTO editor (name) VALUES('" + book.getEditor().getName() + "');";
                stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    id_editor = rs.getLong(1);
                }
            }

            //controllo se authors esistono
            long[] authorId = null;
            if (book.getAuthorsArray() != null) {
                authorId = new long[book.getAuthorsArray().length];
                for (int i = 0; i < authorId.length; i++) {
                    authorId[i] = getAuthorID(book.getAuthorsArray()[i]);
                    if (authorId[i] == -1 && book.getAuthorsArray()[i] != null) {//se non esiste lo creo
                        query = "INSERT INTO author (name,surname) VALUES('" + book.getAuthorsArray()[i].getName() + "','" + book.getAuthorsArray()[i].getSurname() + "');";
                        stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
                        ResultSet rs = stmt.getGeneratedKeys();
                        if (rs.next()) {
                            authorId[i] = rs.getLong(1);
                        }
                    }
                }
            }


            //inserisco book
            query = "INSERT INTO Book (title,isbn,editor) VALUES('" + book.getTitle().replace("'", "''") + "'," + book.getISBN() + ",'" + id_editor + "');";
            stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = stmt.getGeneratedKeys();
            long id_book = -1;
            if (rs.next()) {
                id_book = rs.getLong(1);
            }

            //iserisco l'id book e l'id author nella tabella authors_books
            if (authorId != null) {
                for (long id_author : authorId) {
                    query = "INSERT INTO books_authors (book ,author) VALUES (" + id_book + "," + id_author + ");";
                    stmt.executeUpdate(query);
                }
            }


            return id_book;

        } catch (Exception e) {
            e.printStackTrace();
            throw new DaoException("Error during create");
        }

    }
    public List<Book> read() throws DaoException {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM book ORDER BY title";

        try {
            Statement stmt = getStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                int id_book = rs.getInt("book_id");
                String isbn = rs.getString("ISBN");
                String title = rs.getString("title");
                int id_editor = rs.getInt("editor");

                //cerco editor
                Editor editor = null;
                String editorQuery = "SELECT * FROM editor WHERE editor_id = " + id_editor;

                Statement editorStmt = getStatement();
                ResultSet editorRs = editorStmt.executeQuery(editorQuery);
                if (editorRs.next()) {
                    editor = new Editor(editorRs.getString("name"));
                }

                //trova authors
                Statement authorStmt = getStatement();
                ArrayList<Author> authorList = new ArrayList<>();
                String authorQuery = "SELECT * " +
                                     "FROM author " +
                                     "INNER JOIN books_authors  " +
                                     "ON author.author_id = books_authors.author" +
                                     " WHERE books_authors.book =" + id_book;

                ResultSet authorRs = authorStmt.executeQuery(authorQuery);
                while (authorRs.next()) {
                    Author author = new Author(authorRs.getString("name"), authorRs.getString("surname"));
                    authorList.add(author);
                }

                Author[] authors = authorList.toArray(new Author[0]);

                //aggiungo authors
                Book book = new Book(title, isbn, editor, authors);
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DaoException("Error during read");
        }

        return books;
    }

    public Long delete(Long id) throws DaoException {
        try {
            //elimino book da authors_books (per integrità referenziale)
            String query = "DELETE FROM books_authors WHERE book=" + id + ";";
            Statement statDeleteLA = getStatement();
            statDeleteLA.executeUpdate(query);

            //elimino book da books
            query = "DELETE FROM book WHERE book_id=" + id + ";";
            Statement statDeleteL = getStatement();
            long rowsAffectedL = statDeleteL.executeUpdate(query);
            return rowsAffectedL;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new DaoException("Error during read");
        }
    }

//ritorna -1 se non trova il libro
    public Long update(Long id, Book book) throws DaoException {
        try (Statement stmt = getStatement()) {

            // Update editor if necessary
            long id_editor = getEditorID(book.getEditor());
            if (id_editor == -1 && book.getEditor() != null) {
                String query = "INSERT INTO editor (name) VALUES('" + book.getEditor().getName() + "');";
                stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    id_editor = rs.getLong(1);
                }
            }

            // Update book
            String query = "UPDATE Book SET title='" + book.getTitle().replace("'", "''") +
                    "', isbn='" + book.getISBN() +
                    "', editor=" + id_editor +
                    " WHERE book_id=" + id + ";";
            int rowsUpdated = stmt.executeUpdate(query);
            if (rowsUpdated == 0) {
                return -1L; // No rows updated, return -1
            }

            // Delete existing authors for the book
            query = "DELETE FROM books_authors WHERE book=" + id + ";";
            stmt.executeUpdate(query);

            // Update authors if necessary
            long[] authorId = null;
            if (book.getAuthorsArray() != null) {
                authorId = new long[book.getAuthorsArray().length];
                for (int i = 0; i < authorId.length; i++) {
                    authorId[i] = getAuthorID(book.getAuthorsArray()[i]);
                    if (authorId[i] == -1 && book.getAuthorsArray()[i] != null) {
                        query = "INSERT INTO author (name, surname) VALUES('" +
                                book.getAuthorsArray()[i].getName() + "','" +
                                book.getAuthorsArray()[i].getSurname() + "');";
                        stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
                        ResultSet rs = stmt.getGeneratedKeys();
                        if (rs.next()) {
                            authorId[i] = rs.getLong(1);
                        }
                    }
                }
            }

            // Insert updated authors for the book
            if (authorId != null) {
                for (long id_author : authorId) {
                    query = "INSERT INTO books_authors (book, author) VALUES (" + id + "," + id_author + ");";
                    stmt.executeUpdate(query);
                }
            }

            return id;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new DaoException("Error during update");
        }
    }
    public List<Book> findByText(String searchText) throws DaoException {
        List<Book> books = new ArrayList<>();
        String query = "SELECT b.*, e.name AS editor_name " +
                "FROM book b " +
                "LEFT JOIN editor e ON b.editor = e.editor_id " +
                "LEFT JOIN books_authors ab ON b.book_id = ab.book " +
                "LEFT JOIN author a ON ab.author = a.author_id " +
                "WHERE b.title LIKE '%" + searchText.replace("'", "''") + "%' " +
                "OR b.isbn LIKE '%" + searchText.replace("'", "''") + "%' " +
                "OR e.name LIKE '%" + searchText.replace("'", "''") + "%' " +
                "OR a.name LIKE '%" + searchText.replace("'", "''") + "%' " +
                "OR a.surname LIKE '%" + searchText.replace("'", "''") + "%' " +
                "GROUP BY b.book_id ";

        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                int id_book = rs.getInt("book_id");
                String isbn = rs.getString("isbn");
                String title = rs.getString("title");
                String editorName = rs.getString("editor_name");

                // Find editor
                Editor editor = new Editor(editorName);

                // Find authors
                List<Author> authorList = new ArrayList<>();
                String authorQuery = "SELECT a.* FROM author a " +
                        "INNER JOIN books_authors ab ON a.author_id = ab.author " +
                        "WHERE ab.book = " + id_book;

                Statement authorStmt = conn.createStatement();
                ResultSet authorRs = authorStmt.executeQuery(authorQuery);
                while (authorRs.next()) {
                    Author author = new Author(authorRs.getString("name"), authorRs.getString("surname"));
                    authorList.add(author);
                }

                Author[] authors = authorList.toArray(new Author[0]);

                // Create the book object
                Book book = new Book(title, isbn, editor, authors);
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DaoException("Error during find by String");
        }

        return books;
    }
    public Book getById(Long index) throws DaoException {

        Book book = null;

        String query = "SELECT * FROM Book WHERE book_id = " + index;

        try {

            Statement stmt = getStatement();
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                String isbn = rs.getString("isbn");
                String title = rs.getString("title");
                int id_editor = rs.getInt("editor");

                // Find editor
                Editor editor = null;
                String editorQuery = "SELECT * FROM editor WHERE editor_id = " + id_editor;

                Statement editorStmt = getStatement();
                ResultSet editorRs = editorStmt.executeQuery(editorQuery);
                if (editorRs.next()) {
                    editor = new Editor(editorRs.getString("name"));
                }

                // Find authors
                List<Author> authorList = new ArrayList<>();
                String authorQuery = "SELECT * FROM author INNER JOIN books_authors ON author.author_id = books_authors.author WHERE books_authors.book = " + index;

                Statement authorStmt = getStatement();
                ResultSet authorRs = authorStmt.executeQuery(authorQuery);
                while (authorRs.next()) {
                    Author author = new Author(authorRs.getString("name"), authorRs.getString("surname"));
                    authorList.add(author);
                }

                Author[] authors = authorList.toArray(new Author[0]);

                book = new Book(title, isbn, editor, authors);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DaoException("Error during getById");
        }

        return book;
    }

    public List<Book> findByObject(Book searchBook) throws DaoException {
        List<Book> books = new ArrayList<>();
        List<String> conditions = new ArrayList<>();

        // Check and add conditions based on non-null fields of searchBook
        if (searchBook.getTitle() != null && !searchBook.getTitle().isEmpty()) {
            conditions.add("b.title LIKE '%" + searchBook.getTitle().replace("'", "''") + "%'");
        }

        if (searchBook.getISBN() != null && !searchBook.getISBN().isEmpty()) {
            conditions.add("b.isbn LIKE '%" + searchBook.getISBN().replace("'", "''") + "%'");
        }

        if (searchBook.getEditor() != null && searchBook.getEditor().getName() != null && !searchBook.getEditor().getName().isEmpty()) {
            conditions.add("e.name LIKE '%" + searchBook.getEditor().getName().replace("'", "''") + "%'");
        }

        if (searchBook.getAuthorsArray() != null) {
            for (Author author : searchBook.getAuthorsArray()) {
                if (author.getName() != null && !author.getName().isEmpty()) {
                    conditions.add("a.name LIKE '%" + author.getName().replace("'", "''") + "%'");
                }
                if (author.getSurname() != null && !author.getSurname().isEmpty()) {
                    conditions.add("a.surname LIKE '%" + author.getSurname().replace("'", "''") + "%'");
                }
            }
        }

        // Construct the SQL query
        StringBuilder queryBuilder = new StringBuilder("SELECT b.*, e.name AS editor_name ");
        queryBuilder.append("FROM book b ");
        queryBuilder.append("INNER JOIN editor e ON b.editor = e.editor_id ");
        queryBuilder.append("INNER JOIN books_authors ab ON b.book_id = ab.book ");
        queryBuilder.append("INNER JOIN author a ON ab.author = a.author_id ");

        if (!conditions.isEmpty()) {
            queryBuilder.append("WHERE ");
            queryBuilder.append(String.join(" AND ", conditions));
        }

        queryBuilder.append(" GROUP BY b.book_id");
        String query = queryBuilder.toString();

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int id_book = rs.getInt("book_id");
                String isbn = rs.getString("isbn");
                String title = rs.getString("title");
                String editorName = rs.getString("editor_name");

                // Find editor
                Editor editor = new Editor(editorName);

                // Find authors
                List<Author> authorList = new ArrayList<>();
                String authorQuery = "SELECT a.* FROM author a " +
                        "JOIN books_authors ab ON a.author_id = ab.author " +
                        "WHERE ab.book = " + id_book;

                try (Statement authorStmt = conn.createStatement(); ResultSet authorRs = authorStmt.executeQuery(authorQuery)) {
                    while (authorRs.next()) {
                        Author author = new Author(authorRs.getString("name"), authorRs.getString("surname"));
                        authorList.add(author);
                    }
                }

                Author[] authors = authorList.toArray(new Author[0]);

                // Create the book object
                Book book = new Book(title, isbn, editor, authors);
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DaoException("Error during find by Book");
        }

        return books;
    }

}

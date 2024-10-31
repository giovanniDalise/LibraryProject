-- Inserimento dati nella tabella editor
INSERT INTO editor (name) VALUES ('John Smith');
INSERT INTO editor (name) VALUES ('Emily Johnson');
INSERT INTO editor (name) VALUES ('Michael Brown');
INSERT INTO editor (name) VALUES ('Sophia Lee');
INSERT INTO editor (name) VALUES ('David Wilson');

-- Inserimento dati nella tabella book
INSERT INTO book (title, ISBN, editor) VALUES ('The Great Gatsby', '9780743273565', 1);
INSERT INTO book (title, ISBN, editor) VALUES ('To Kill a Mockingbird', '9780061120084', 2);
INSERT INTO book (title, ISBN, editor) VALUES ('1984', '9780451524935', 3);
INSERT INTO book (title, ISBN, editor) VALUES ('Pride and Prejudice', '9780141439518', 4);
INSERT INTO book (title, ISBN, editor) VALUES ('Harry Potter and the Sorcerer''s Stone', '9780590353427', 5);

-- Inserimento dati nella tabella author
INSERT INTO author (name, surname) VALUES ('Jane', 'Austen');
INSERT INTO author (name, surname) VALUES ('George', 'Orwell');
INSERT INTO author (name, surname) VALUES ('F. Scott', 'Fitzgerald');
INSERT INTO author (name, surname) VALUES ('Harper', 'Lee');
INSERT INTO author (name, surname) VALUES ('J.K.', 'Rowling');

-- Inserimento dati nella tabella books_authors (associazioni libri-autori)
INSERT INTO books_authors (book, author) VALUES (1, 1);  -- Jane Austen per "Pride and Prejudice"
INSERT INTO books_authors (book, author) VALUES (2, 4);  -- Harper Lee per "To Kill a Mockingbird"
INSERT INTO books_authors (book, author) VALUES (3, 2);  -- George Orwell per "1984"
INSERT INTO books_authors (book, author) VALUES (4, 1);  -- Jane Austen per "Emma"
INSERT INTO books_authors (book, author) VALUES (5, 5);  -- J.K. Rowling per "Harry Potter and the Sorcerer's Stone"

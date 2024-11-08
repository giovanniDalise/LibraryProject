import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterOutlet, RouterLink, Router } from '@angular/router';
import { BooksService } from '../../services/books.service';
import { Book } from '../../models/book.model';
import { ActivatedRoute } from '@angular/router';  // Importa ActivatedRoute


@Component({
  selector: 'app-books-view',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink],
  templateUrl: './books-view.component.html',
  styleUrls: ['./books-view.component.scss']
})
export class BooksViewComponent {

  constructor(private activatedRoute: ActivatedRoute,private booksService: BooksService, private router: Router) {}

  bookId? : number;
  books: Book[] = [];
  searchText?: string; 

  ngOnInit(): void {
    // Recupera il parametro bookId dalla URL (per ottenere un libro specifico)
    this.bookId = +this.activatedRoute.snapshot.paramMap.get('bookId')!;  // il + lo converte in numero
  
    // Controlla se è presente un parametro di query 'search' per la ricerca testuale
    this.activatedRoute.queryParamMap.subscribe(queryParams => {
      const searchText = queryParams.get('search');  // Ottiene il valore del parametro 'search'
  
      if (this.bookId) {
        // Se c'è un bookId nella URL, carica il libro per ID
        this.loadBookById();
      } else if (searchText) {
        // Se c'è un parametro 'search', carica i libri filtrati dalla ricerca
        this.loadBooksByText(searchText);
      } else {
        // Se non c'è né bookId né searchText, carica tutti i libri
        this.loadBooks();
      }
    });
  }
  

  loadBooks() {
    this.booksService.getBooks().subscribe((books: Book[]) => {
      this.books = books;
    });
  }

  loadBookById(): void {
    if (this.bookId !== undefined) {
      this.booksService.getBookById(this.bookId).subscribe((book: Book) => {
        this.books = [book];  
      });
    }
  }

  loadBooksByText(searchText: string): void {
    this.booksService.getBookByText(searchText).subscribe((books) => {
      this.books = books;
    });
  }
  
    // Metodo per eliminare il libro
    deleteBook(bookId: number): void {
      const bookIdNumber = Number(bookId);
      if (!isNaN(bookIdNumber)) {
        this.booksService.deleteBook(bookIdNumber).subscribe(response => {
          console.log("Book deleted:", response);
          // Dopo aver eliminato il libro, aggiorna la lista dei libri o naviga da qualche parte
          this.booksService.getBooks().subscribe(books => {
            this.books = books; // Ricarica i libri
          });
        }, error => {
          console.error('Error deleting book:', error);
        });
      } else {
        console.error('Invalid Book ID:', bookId);
      }
    }
}

import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Book } from '../../models/book.model';
import { FormArray, FormBuilder, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BooksService } from '../../services/books.service';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Author } from '../../models/author.model';
import { HttpHeaders } from '@angular/common/http';

@Component({
  selector: 'app-book-form',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, FormsModule],
  templateUrl: './book-form.component.html',
  styleUrl: './book-form.component.scss'
})
export class BookFormComponent {
  @Input() bookData: Book | null = null;
  bookForm: FormGroup;
  mode: 'create' | 'update' | 'search' = 'create';

  constructor(
    private booksService: BooksService,
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router
  ) {
    // Inizializza bookForm con un form per gestire un array di autori e un oggetto editor
    this.bookForm = this.fb.group({
      title: [''],
      isbn: [''],
      authors: this.fb.array([this.createAuthor()]),  // Un array per gli autori
      editor: this.fb.group({
        name: [''],
        address: ['']
      })
    });
  }

  // Getter per gli autori
  get authors(): FormArray {
    return this.bookForm.get('authors') as FormArray;
  }

  // Metodo per creare un form per un autore
  createAuthor(): FormGroup {
    return this.fb.group({
      name: [''],
      surname: ['']
    });
  }

  // Metodo per aggiungere un autore all'array
  addAuthor() {
    this.authors.push(this.createAuthor());
  }

  // Metodo per rimuovere un autore dall'array
  removeAuthor(index: number) {
    this.authors.removeAt(index);
  }

  ngOnInit(): void {
    // Imposta la modalità in base al parametro della rotta
    this.route.paramMap.subscribe(params => {
      this.mode = params.get('mode') as 'create' | 'update' | 'search' || 'create';
      
      // Se la modalità è 'update' e sono presenti dati del libro, popola i campi
      if (this.mode === 'update' && this.bookData) {
        this.bookForm.patchValue(this.bookData);
      }
    });
  }

  onSubmit(): void {
    if (this.bookForm.valid) {
      const book: Book = this.bookForm.value;

      if (this.mode === 'create') {
        this.createBook(book);
      } else if (this.mode === 'update') {
        this.updateBook(book);
      } else if (this.mode === 'search') {
        this.searchBook(book);
      }
    }
  }

  private createBook(book: Book): void {
    this.booksService.createBook(book).subscribe(response => {
      console.log("Book created:", response);
    });
  }

  private updateBook(book: Book): void {
    this.booksService.updateBook(book).subscribe(response => {
      console.log("Book updated:", response);
    });
  }

  private searchBook(criteria: Book): void {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'application/json' 
    });
  
    const searchCriteria = {
      title: criteria.title,
      isbn: criteria.isbn,
      authors: criteria.authors ? criteria.authors.map((author: Author) => ({
        name: author.name,
        surname: author.surname
      })) : [],
      editor: criteria.editor ? { name: criteria.editor.name } : {}
    };
  
    // Esegui la ricerca tramite il servizio
    this.booksService.findByObject(searchCriteria as Partial<Book>, { headers }).subscribe(results => {
      console.log("Search results:", results);

      if (results && results.length > 0) {
        // Supponiamo che la risposta contenga un array di libri. Prendi il primo libro trovato.
        const bookId = results[0]?.bookId;

        if (bookId) {
          // Naviga alla rotta 'books-view/:bookId' passando l'ID del libro
          this.router.navigate(['/books-view', bookId]);
        }
      } else {
        console.log('Nessun libro trovato');
      }
    }, error => {
      console.error('Errore nella ricerca:', error);
    });
  }
}

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
  bookId: string | null = null;

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

  // Getter per gli autori. La sintassi del getter è get<nomeProprietà>() e stai richiamando il get per accedere agli authors del formGroup
  // quando utilizzi this.authors sia in addAuthor che in removeAuthor()
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
    this.authors.push(this.createAuthor()); //Qui stai usando il getter authors per accedere al FormArray degli autori. Quando chiami this.authors, Angular invoca automaticamente il getter e ti restituisce il FormArray. Quindi, puoi aggiungere un nuovo autore con push.
  }

  // Metodo per rimuovere un autore dall'array
  removeAuthor(index: number) {
    this.authors.removeAt(index);
  }

  ngOnInit(): void {
    // Imposta la modalità in base al parametro della rotta
    this.route.paramMap.subscribe(params => {
      this.mode = params.get('mode') as 'create' | 'update' | 'search' || 'create';
      this.bookId = params.get('bookId');  // Recupera l'ID del libro
      
      // Se la modalità è 'update' e l'ID del libro è presente, carica il libro
      if (this.mode === 'update' && this.bookId) {
        const bookIdNumber = Number(this.bookId);  // Converte la stringa in numero
  
        if (!isNaN(bookIdNumber)) {  // Verifica che la conversione abbia avuto successo
          this.booksService.getBookById(bookIdNumber).subscribe(book => {
            this.bookData = book;
            this.bookForm.patchValue(book);
          });
        } else {
          console.error('Invalid book ID:', this.bookId);
        }
      }
    });
  }

  onSubmit(): void {   //quando premi il submit del form va a chiamare le differenti funzioni
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
    // Verifica che l'ID del libro sia disponibile
    if (this.bookId) {
      const bookIdNumber = Number(this.bookId);  // Trasforma bookId in numero
      if (!isNaN(bookIdNumber)) {
        console.log('Book ID:', bookIdNumber);  // Utilizza bookIdNumber
        const updatedBook = { ...book, bookId: bookIdNumber };  // Imposta l'ID del libro da bookIdNumber
        this.booksService.updateBook(updatedBook).subscribe(response => {
          console.log("Book updated:", response);
        });
      } else {
        console.error('Invalid Book ID:', this.bookId);
      }
    } else {
      console.error('Book ID is missing for update');
    }
  }
  
  

  private searchBook(criteria: Book): void {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'application/json' 
    });
    //In TypeScript, Partial<T> è un tipo generico che rende tutte le proprietà di un tipo opzionali.
    //In questo caso, Partial<Book> significa che tutte le proprietà dell'oggetto Book diventano opzionali. 
    //Quindi, quando usi searchCriteria as Partial<Book>, stai dicendo al compilatore TypeScript che searchCriteria 
    //può essere trattato come un oggetto che ha alcune (o nessuna) delle proprietà di Book, senza la necessità di includere 
    //tutte le proprietà. Quindi adatto per una ricerca con più campi opzionali come
    //quella del findByObject. searchCriteria è un oggetto creato al momento che poi facciamo diventare un Partial<T>
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

import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterOutlet, RouterLink, RouterLinkActive, Router } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive, FormsModule ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent {
  bookId: number | undefined; // Variabile per tenere traccia dell'ID inserito
  text: string = ''; // Variabile per il testo di ricerca

  constructor (private router:Router) {}

  imageFile = "img/libraryBackground.jpg";

  searchBookById(): void {
    if (this.bookId) {
      // Naviga alla pagina books-view passando l'ID
      this.router.navigate(['/books-view', this.bookId]);
    }
  }  

  searchBookByText(): void {
    if (this.text) {
        this.router.navigate(['/books-view'], { queryParams: { search: this.text } });
      }
  }

}

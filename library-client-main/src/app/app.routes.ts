import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { BooksViewComponent } from './components/books-view/books-view.component';
import { BookFormComponent } from './components/book-form/book-form.component';

export const routes: Routes = [
    {path:'home', component:HomeComponent},
    {path:'', component:HomeComponent},
    {path:'books-view', component:BooksViewComponent},
    {path: 'books-view/:bookId', component: BooksViewComponent },
    {path: 'book-form/:mode', component: BookFormComponent }, // Rotta con parametro mode
    { path: 'book-form/:bookId/:mode', component: BookFormComponent },  // Modifica la rotta per accettare l'ID del libro
];

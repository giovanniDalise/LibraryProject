import { Author } from "./author.model";
import { Editor } from "./editor.model";

export class Book {
    bookId:number;
    title:string;
    isbn:string;
    authors: Author[];
    editor: Editor;

    constructor(bookId:number, title:string, isbn:string, authors:Author[], editor: Editor){
        this.bookId = bookId;
        this.title = title;
        this.isbn = isbn;
        this.authors = authors;
        this.editor = editor;
    }
}

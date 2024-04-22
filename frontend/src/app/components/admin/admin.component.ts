import { Component, ElementRef, OnInit, Renderer2, ViewChild } from '@angular/core';
import { BookService } from '../../services/book.service';
import { Author } from '../../model/Author';
import { BookCover } from '../../model/BookCover';
import { CategoryService } from '../../services/category.service';
import { Category } from '../../model/Category';

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrl: './admin.component.css'
})
export class AdminComponent implements OnInit {
  form: any = {
    title: null,
    authorName: null,
    authorDescription: null,
    authorPicture: null,
    quantity: null,
    publisher: null,
    numberOfPages: null,
    edition: null,
    publicationYear: null,
    description: null,
    category: null,
    mainPicture: null,
    picture: null,
    price: null,
    cover: null
  };

  pictures: Array<string> = [];
  authors: Array<Author> = [];

  isSuccessful = false;
  errorMessage = '';

  selectedCategory: string = '';
  parentCategories: Array<Category> = [ ];
  subCategories: Array<Category> = [ ];

  constructor(private bookService: BookService,
              private categoryService: CategoryService,
              private renderer: Renderer2) { }

  ngOnInit(): void {
    this.categoryService
        .getCategories()
        .subscribe({
          next: data => this.parentCategories = data,
          error: err => console.log(err)
        })
  }

  selectCategory(category: string, index: string|undefined): void {
    this.selectedCategory = category;

    if(index) {
      this.form.category = index;

      if(this.subCategories && this.subCategories.length === 0) {
        this.categoryService.getSubCategories(index)
                            .subscribe({
                              next: data => this.subCategories = data,
                              error: err => console.log(err)
                            });
      } else {
        this.subCategories = [];
      }
    }
  }

  onSubmit(): void {
    const { title, quantity, price, publisher, numberOfPages, edition, publicationYear, description, category, mainPicture, cover } = this.form;

    this.bookService.create({
      title: title,
      price: price,
      pictures: this.pictures,
      quantity: quantity,
      authors: this.authors,
      publisher: publisher,
      numberOfPages: numberOfPages,
      edition: edition,
      publicationYear: publicationYear,
      description: description,
      category: category,
      mainPicture: mainPicture,
      cover: (cover === 'Hard') ? BookCover.HARD : BookCover.SOFT
    }).subscribe({
      next: data => { },
      error: err => { console.log(err); }
    });
  }

  storePicture(): void {
    const { picture } = this.form;
    console.log(picture);

    if(picture) {
      this.pictures.push(picture);
    }
  }

  removePicture(picture: string): void {
    this.pictures.splice(this.pictures.indexOf(picture), 1);
  }

  storeAuthor(): void {
    const { authorName, authorDescription, authorPicture } = this.form;

    if(authorName && authorDescription && authorPicture) {
      this.authors.push({
        name: authorName,
        description: authorDescription,
        picture: authorPicture,
        books: new Array()
      })
    }
  }

  removeAuthor(name: string): void {
    this.authors = this.authors.filter(author => author.name !== name);
  }
}

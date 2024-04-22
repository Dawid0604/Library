import { Component, OnInit } from '@angular/core';
import { Book } from '../../model/Book';
import { BasketService } from '../../services/basket.service';
import { BookService } from '../../services/book.service';

@Component({
  selector: 'app-basket',
  templateUrl: './basket.component.html',
  styleUrl: './basket.component.css'
})
export class BasketComponent implements OnInit {
  basketBooks: Array<Book> = [];

  constructor(private basketService: BasketService,
              private bookService: BookService) { }

  ngOnInit(): void {
    let content = this.basketService.getContent();

    if(content && content.length > 0) {
      this.bookService.fetchBooksByIds(content)
                      .subscribe({
                        next: data => this.basketBooks = data,
                        error: err => console.log(err)
                      });
    }
  }

  deleteBook(bookId: number) {
    this.basketBooks = this.basketBooks.filter(_book => _book.bookId !== bookId);
    this.basketService.removeBook(bookId);
    this.calculateCosts();
  }

  cleanBasket() {
    this.basketService.clean();
    this.basketBooks = [];
    this.calculateCosts();
  }

  calculateCosts(): number {
    let price: number = 0;

    for(let book of this.basketBooks) {
      if(book.price) {
        price += book.price;
      }
    } return Math.round(price * 100) / 100;
  }
}

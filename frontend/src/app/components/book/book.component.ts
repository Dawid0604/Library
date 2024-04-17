import { Component, OnInit } from '@angular/core';
import { BookService } from '../../services/book.service';
import { ActivatedRoute } from '@angular/router';
import { BookDetailsResponse } from '../../model/BookDetailsResponse';
import { BasketService } from '../../services/basket.service';

@Component({
  selector: 'app-book',
  templateUrl: './book.component.html',
  styleUrl: './book.component.css'
})
export class BookComponent implements OnInit {
  book: BookDetailsResponse = {} as BookDetailsResponse;
  basketContainsBookFlag: boolean = false;

  constructor(private bookService: BookService,
              private route: ActivatedRoute,
              private basketService: BasketService) { }

  ngOnInit(): void {
    this.route
        .params
        .subscribe(params => {
          this.bookService.fetchById(+params['id'])
                          .subscribe({
                            next: data => {
                              this.book = data;
                              this.basketContainsBook();
                            },
                            error: err => console.log(err)
                          })
        })
  }

  storeBookToBasket(bookId: number) {
    this.basketService.storeBook(bookId);
    this.basketContainsBook();
  }

  basketContainsBook() {
    this.basketContainsBookFlag = this.basketService.contains(this.book.bookId);
  }
}

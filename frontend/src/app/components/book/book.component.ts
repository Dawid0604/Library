import { Component, OnInit } from '@angular/core';
import { BookService } from '../../services/book.service';
import { ActivatedRoute } from '@angular/router';
import { BookDetailsResponse } from '../../model/BookDetailsResponse';
import { BasketService } from '../../services/basket.service';
import { UserService } from '../../services/user.service';
import { StorageService } from '../../services/storage.service';

@Component({
  selector: 'app-book',
  templateUrl: './book.component.html',
  styleUrl: './book.component.css'
})
export class BookComponent implements OnInit {
  book: BookDetailsResponse = {} as BookDetailsResponse;
  basketContainsBookFlag: boolean = false;
  isAdmin: boolean = true;

  constructor(private bookService: BookService,
              private route: ActivatedRoute,
              private basketService: BasketService,
              private userService: UserService,
              private storageService: StorageService) { }

  ngOnInit(): void {
    if(this.storageService.isLoggedIn()) {
      this.userService
          .getRoles()
          .subscribe({
            next: data => this.isAdmin = data.includes("ROLE_ADMIN"),
            error: err => {
              console.log(err)
              this.isAdmin = false;
            }
          })

    } else {
      this.isAdmin = false;
    }

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

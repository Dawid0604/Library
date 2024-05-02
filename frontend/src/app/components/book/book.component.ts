import { Component, OnInit } from '@angular/core';
import { BookService } from '../../services/book.service';
import { ActivatedRoute } from '@angular/router';
import { BookDetailsResponse } from '../../model/BookDetailsResponse';
import { BasketService } from '../../services/basket.service';
import { UserService } from '../../services/user.service';
import { StorageService } from '../../services/storage.service';
import { ReactionService } from '../../services/reaction.service';
import { Reaction, Statistics } from '../../model/BookReactionsResponse';

@Component({
  selector: 'app-book',
  templateUrl: './book.component.html',
  styleUrl: './book.component.css'
})
export class BookComponent implements OnInit {
  book: BookDetailsResponse = {} as BookDetailsResponse;
  reactions: Reaction[] = [];
  statistics: Statistics = {} as Statistics;
  userReaction: Reaction = {} as Reaction;

  basketContainsBookFlag: boolean = false;
  isAdmin: boolean = true;
  editBoxIsActive: boolean = false;

  form: any = {
    comment: null
  };

  bookId: number|null = null;
  currentPage: number = 1;
  numberOfpages: number = 1;
  isLast: boolean = false;
  isEmpty: boolean = false;
  isFirst: boolean = true;
  totalElements: number = 0;

  descSearchingFlag: boolean = true;
  commentSearchingFlag: boolean = true;

  defaultStarColor = '#474444';
  presentStarColor = "#35654d";

  firstStarIsSelected: boolean = false;
  secondStarIsSelected: boolean = false;
  thirdStarIsSelected: boolean = false;
  forthStarIsSelected: boolean = false;
  fifthStarIsSelected: boolean = false;

  firstStarIsPresent:  boolean = false;
  secondStarIsPresent: boolean = false;
  thirdStarIsPresent:  boolean = false;
  forthStarIsPresent:  boolean = false;
  fifthStarIsPresent:  boolean = false;

  constructor(private bookService: BookService,
              private route: ActivatedRoute,
              private basketService: BasketService,
              private userService: UserService,
              private storageService: StorageService,
              private reactionService: ReactionService) { }

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
          this.bookId = +params['id'];

          this.bookService.fetchById(this.bookId)
                          .subscribe({
                            next: data => {
                              this.book = data;
                              this.basketContainsBook();
                              this.fetchReactions();
                            },
                            error: err => console.log(err)
                          })
        })
  }

  userIsLogged(): boolean {
    return this.storageService.isLoggedIn();
  }

  storeBookToBasket(bookId: number) {
    this.basketService.storeBook(bookId);
    this.basketContainsBook();
  }

  basketContainsBook() {
    this.basketContainsBookFlag = this.basketService.contains(this.book.bookId);
  }

  fillStars(element: EventTarget | null) {
    if(element instanceof HTMLElement) {
      element.style.color = this.presentStarColor;
      let previousElements = element.previousElementSibling;

      while(previousElements) {
        (previousElements as HTMLElement).style.color = this.presentStarColor;
        previousElements = previousElements.previousElementSibling;
      }
    }
  }

  updateReaction() {
    const { comment } = this.form;
    let numberOfStars = null;

    if(this.firstStarIsSelected) {
      numberOfStars = 1;

    } else if(this.secondStarIsSelected) {
      numberOfStars = 2;

    } else if(this.thirdStarIsSelected) {
      numberOfStars = 3;

    } else if(this.forthStarIsSelected) {
      numberOfStars = 4;

    } else if(this.fifthStarIsSelected) {
      numberOfStars = 5;

    } else {
      numberOfStars = this.userReaction.numberOfStars;
    }

    this.reactionService
        .updateReaction({
          bookId: this.bookId,
          comment: comment,
          numberOfStars: numberOfStars
        })
        .subscribe({
          next: data => {
            this.fetchReactions();
            this.editBoxIsActive = false;
          },
          error: err => console.log(err)
        })
  }

  activeEditBox() {
    this.editBoxIsActive = !this.editBoxIsActive;

    if(this.userReaction.numberOfStars) {
      switch(this.userReaction.numberOfStars) {
        case 1: {
          this.firstStarIsPresent = true;  break;
        }

        case 2: {
          this.secondStarIsPresent = true;  break;
        }

        case 3: {
          this.thirdStarIsPresent = true;  break;
        }

        case 4: {
          this.forthStarIsPresent = true;  break;
        }

        case 5: {
          this.fifthStarIsPresent = true;  break;
        }
      }
    }
  }

  fetchNextReactions(): void {
    this.currentPage += 1;
    this.fetchReactions();
  }

  fetchPreviousReactions(): void {
    this.currentPage -= 1;
    this.fetchReactions();
  }

  fetchLastReactions(): void {
    this.currentPage = this.numberOfpages;
    this.fetchReactions();
  }

  changeDateSearchingOrder() {
    this.descSearchingFlag = !this.descSearchingFlag;
    this.fetchReactions();
  }

  changeCommentSearchingOrder() {
    this.commentSearchingFlag = !this.commentSearchingFlag;
    this.fetchReactions();
  }

  userReactionIsPresent(): boolean {
    return this.userReaction && this.userReaction.reactionId !== undefined;
  }

  deleteUserReaction() {
    this.reactionService
        .deleteReaction(this.userReaction.reactionId)
        .subscribe({
          next: data => this.fetchReactions(),
          error: err => console.log(err)
        })

    this.userReaction = {} as Reaction;
  }

  private fetchReactions() {
    if(!this.bookId) {
      return;
    }

    this.reactionService
              .getReactions(this.bookId, this.currentPage, this.commentSearchingFlag, this.descSearchingFlag)
              .subscribe({
                next: data => {
                  this.reactions = data.page.content || [];
                  this.statistics = data.statistics;

                  if(this.currentPage === 1 && data.userReaction && data.userReaction.reactionId) {
                    this.userReaction = data.userReaction;
                  }

                  if(this.reactions.length > 0) {
                      this.totalElements = data.page.totalElements;
                      this.currentPage = data.page.pageable.pageNumber + 1;
                      this.numberOfpages = data.page.totalPages;
                      this.isLast = data.page.last;
                      this.isFirst = data.page.first;

                  } else {
                    this.isEmpty = true;
                  }
                },
                error: err => console.log(err)
              })
  }

  onSubmit(): void {
    const { comment } = this.form;
    let numberOfStars = null;

    if(this.firstStarIsSelected) {
      numberOfStars = 1;

    } else if(this.secondStarIsSelected) {
      numberOfStars = 2;

    } else if(this.thirdStarIsSelected) {
      numberOfStars = 3;

    } else if(this.forthStarIsSelected) {
      numberOfStars = 4;

    } else if(this.fifthStarIsSelected) {
      numberOfStars = 5;
    }

    this.reactionService
        .addReaction({
          bookId: this.bookId,
          comment: comment,
          numberOfStars: numberOfStars
        })
        .subscribe({
          next: data => this.fetchReactions(),
          error: err => console.log(err)
        })
  }

  selectStar(stars: number) {
    switch(stars) {
      case 1: {
        this.firstStarIsSelected = true;

        (document.querySelector('span#firstStar') as HTMLElement).style.color = this.presentStarColor;
        (document.querySelector('span#secondStar') as HTMLElement).style.color = this.presentStarColor;
        (document.querySelector('span#thirdStar') as HTMLElement).style.color = this.presentStarColor;
        (document.querySelector('span#forthStar') as HTMLElement).style.color = this.presentStarColor;
        (document.querySelector('span#fifthStar') as HTMLElement).style.color = this.presentStarColor;
        (document.querySelector('span#firstStar') as HTMLElement).style.color = this.presentStarColor;

        // Reset
        (document.querySelector('span#secondStar') as HTMLElement).style.color = this.defaultStarColor;
        (document.querySelector('span#thirdStar') as HTMLElement).style.color = this.defaultStarColor;
        (document.querySelector('span#forthStar') as HTMLElement).style.color = this.defaultStarColor;
        (document.querySelector('span#fifthStar') as HTMLElement).style.color = this.defaultStarColor;  break;
      }

      case 2: {
        this.secondStarIsSelected = true;

        (document.querySelector('span#firstStar') as HTMLElement).style.color = this.presentStarColor;
        (document.querySelector('span#secondStar') as HTMLElement).style.color = this.presentStarColor;

        // Reset
        (document.querySelector('span#thirdStar') as HTMLElement).style.color = this.defaultStarColor;
        (document.querySelector('span#forthStar') as HTMLElement).style.color = this.defaultStarColor;
        (document.querySelector('span#fifthStar') as HTMLElement).style.color = this.defaultStarColor;  break;
      }

      case 3: {
        this.thirdStarIsSelected = true;

        (document.querySelector('span#firstStar') as HTMLElement).style.color = this.presentStarColor;
        (document.querySelector('span#secondStar') as HTMLElement).style.color = this.presentStarColor;
        (document.querySelector('span#thirdStar') as HTMLElement).style.color = this.presentStarColor;

        // Reset
        (document.querySelector('span#forthStar') as HTMLElement).style.color = this.defaultStarColor;
        (document.querySelector('span#fifthStar') as HTMLElement).style.color = this.defaultStarColor;  break;
      }

      case 4: {
        this.forthStarIsSelected = true;

        (document.querySelector('span#firstStar') as HTMLElement).style.color = this.presentStarColor;
        (document.querySelector('span#secondStar') as HTMLElement).style.color = this.presentStarColor;
        (document.querySelector('span#thirdStar') as HTMLElement).style.color = this.presentStarColor;
        (document.querySelector('span#forthStar') as HTMLElement).style.color = this.presentStarColor;

        // Reset
        (document.querySelector('span#fifthStar') as HTMLElement).style.color = this.defaultStarColor;  break;
      }

      case 5: {
        this.fifthStarIsSelected = true;

        (document.querySelector('span#firstStar') as HTMLElement).style.color = this.presentStarColor;
        (document.querySelector('span#secondStar') as HTMLElement).style.color = this.presentStarColor;
        (document.querySelector('span#thirdStar') as HTMLElement).style.color = this.presentStarColor;
        (document.querySelector('span#forthStar') as HTMLElement).style.color = this.presentStarColor;
        (document.querySelector('span#fifthStar') as HTMLElement).style.color = this.presentStarColor;  break;
      }
    }
  }

  resetBackgrounds() {
    this.firstStarIsSelected = false;
    this.secondStarIsSelected = false;
    this.thirdStarIsSelected = false;
    this.forthStarIsSelected = false;
    this.fifthStarIsSelected = false;

    (document.querySelector('span#firstStar') as HTMLElement).style.color = this.defaultStarColor;
    (document.querySelector('span#secondStar') as HTMLElement).style.color = this.defaultStarColor;
    (document.querySelector('span#thirdStar') as HTMLElement).style.color = this.defaultStarColor;
    (document.querySelector('span#forthStar') as HTMLElement).style.color = this.defaultStarColor;
    (document.querySelector('span#fifthStar') as HTMLElement).style.color = this.defaultStarColor;
  }
}

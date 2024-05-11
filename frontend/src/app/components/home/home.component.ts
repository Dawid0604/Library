import { Component, OnInit } from '@angular/core';
import { BookService } from '../../services/book.service';
import { Book } from '../../model/Book';
import { Category } from '../../model/Category';
import { CategoryService } from '../../services/category.service';
import { SearchBookRequest } from '../../model/SearchBookRequest';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  form: any = {
    page: null,
    category: null,
    priceFrom: null,
    priceTo: null,
    numberOfpagesFrom: null,
    numberOfpagesTo: null,
    publicationYearFrom: null,
    publicationYearTo: null,
    title: null,
    cover: null
  };

  books: Book[] = [];
  currentPage: number = 0;
  numberOfpages: number = 1;
  isLast: boolean = false;
  isFirst: boolean = true;
  totalElements: number = 0;

  showTitleBoxFlag: boolean = false;
  showSubCategoriesBoxFlag: boolean = false;
  showCategoryBoxFlag: boolean = false;
  showPriceBoxFlag: boolean = false;
  showNumberOfPagesBoxFlag: boolean = false;
  showPublicationYearBoxFlag: boolean = false;
  showCoverBoxFlag: boolean = false;

  minimumPublicationYear: number = new Date().getFullYear() - 15;

  selectedCategory: string = '';
  parentCategories: Array<Category> = [ ];
  subCategories: Array<Category> = [ ];

  searchBookRequest: SearchBookRequest = {
    page: 0
  } as SearchBookRequest;

  constructor(private bookService: BookService,
              private categoryService: CategoryService,
              private router: Router) { }

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

  unselectCategory(): void {
    this.selectedCategory = '';
    this.form.category = undefined;
    this.showSubCategoriesBoxFlag = false;
  }

  ngOnInit(): void {
    this.fetchBooks(this.searchBookRequest);
    this.categoryService.getCategories()
                        .subscribe({
                          next: data => this.parentCategories = data,
                          error: err => console.log(err)
    })
  }

  fetchNextBooks(): void {
    this.currentPage += 1;
    this.searchBookRequest.page = this.currentPage;
    this.fetchBooks(this.searchBookRequest);
  }

  fetchPreviousBooks(): void {
    this.currentPage -= 1;
    this.searchBookRequest.page = this.currentPage;
    this.fetchBooks(this.searchBookRequest);
  }

  fetchLastBooks(): void {
    this.searchBookRequest.page = this.numberOfpages - 1;
    this.fetchBooks(this.searchBookRequest);
  }

  openBook(title: string, bookId: number): void {
    this.router.navigate([ '/book', title, bookId ]);
  }

  private fetchBooks(searchBookRequest: SearchBookRequest): void {
    this.bookService.fetchBooks(searchBookRequest)
                    .subscribe({
                      next: data => {
                        this.books = data?.content || [];

                        if(this.books.length > 0) {
                          this.totalElements = data.totalElements;
                          this.currentPage = data.pageable.pageNumber;
                          this.numberOfpages = data.totalPages;
                          this.isLast = data.last;
                          this.isFirst = data.first;
                        }
                      }, error: err => console.log(err)
    });
  }

  onSubmit(): void {
    this.searchBookRequest.category = this.form.category;
    this.searchBookRequest.cover = this.form.cover?.toUpperCase();
    this.searchBookRequest.numberOfPagesFrom = this.form.numberOfPagesFrom;
    this.searchBookRequest.numberOfPagesTo = this.form.numberOfPagesTo;
    this.searchBookRequest.priceFrom = this.form.priceFrom;
    this.searchBookRequest.priceTo = this.form.priceTo;
    this.searchBookRequest.publicationYearFrom = this.form.publicationYearFrom;
    this.searchBookRequest.publicationYearTo = this.form.publicationYearTo;
    this.searchBookRequest.title = this.form.title;

    this.fetchBooks(this.searchBookRequest);

    // Clear values
    this.form.title = null;
    this.selectedCategory = '';
    this.form.page = null;
    this.form.category = null;
    this.form.priceFrom = null;
    this.form.priceTo = null;
    this.form.numberOfpagesFrom = null;
    this.form.numberOfPagesTo = null;
    this.form.publicationYearFrom = null;
    this.form.publicationYearTo = null;
    this.form.cover = null;
  }

  showPriceBox(): void {
    this.showPriceBoxFlag = !this.showPriceBoxFlag;
    this.showNumberOfPagesBoxFlag = false;
    this.showCategoryBoxFlag = false;
    this.showPublicationYearBoxFlag = false;
    this.showTitleBoxFlag = false;
  }

  showCategoryBox(): void {
    this.showCategoryBoxFlag = !this.showCategoryBoxFlag;
    this.showNumberOfPagesBoxFlag = false;
    this.showPriceBoxFlag = false;
    this.showPublicationYearBoxFlag = false;
    this.showTitleBoxFlag = false;
  }

  showNumberOfPagesBox(): void {
    this.showNumberOfPagesBoxFlag = !this.showNumberOfPagesBoxFlag;
    this.showPriceBoxFlag = false;
    this.showCategoryBoxFlag = false;
    this.showPublicationYearBoxFlag = false;
    this.showTitleBoxFlag = false
  }

  showPublicationYearBox(): void {
    this.showPublicationYearBoxFlag = !this.showPublicationYearBoxFlag;
    this.showPriceBoxFlag = false;
    this.showCategoryBoxFlag = false;
    this.showNumberOfPagesBoxFlag = false;
    this.showTitleBoxFlag = false;
  }

  showCoverBox(): void {
    this.showCoverBoxFlag = !this.showCoverBoxFlag;
    this.showPriceBoxFlag = false;
    this.showCategoryBoxFlag = false;
    this.showNumberOfPagesBoxFlag = false;
    this.showPublicationYearBoxFlag = false;
    this.showTitleBoxFlag = false;
  }

  showTitleBox(): void {
    this.showTitleBoxFlag = !this.showTitleBoxFlag;
    this.showPriceBoxFlag = false;
    this.showCategoryBoxFlag = false;
    this.showNumberOfPagesBoxFlag = false;
    this.showPublicationYearBoxFlag = false;
    this.showCoverBoxFlag = false;
  }
}

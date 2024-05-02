import { Component, OnInit } from '@angular/core';
import { Author } from '../../model/Author';
import { ActivatedRoute } from '@angular/router';
import { AuthorService } from '../../services/author.service';

@Component({
  selector: 'app-author',
  templateUrl: './author.component.html',
  styleUrl: './author.component.css'
})
export class AuthorComponent implements OnInit {
  author: Author = {} as Author;

  constructor(private router: ActivatedRoute,
              private authorService: AuthorService) { }

  ngOnInit(): void {
    this.router
        .params
        .subscribe({
          next: params => this.authorService
                              .findBooks(+params['id'])
                              .subscribe({
                                next: data => this.author = data,
                                error: err => console.log(err)
                              }),
          error: err => console.log(err)
        })
  }
}

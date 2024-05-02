import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PublisherService } from '../../services/publisher.service';
import { Publisher } from '../../model/Publisher';

@Component({
  selector: 'app-publisher',
  templateUrl: './publisher.component.html',
  styleUrl: './publisher.component.css'
})
export class PublisherComponent implements OnInit {
  publisher: Publisher = {} as Publisher;

  constructor(private router: ActivatedRoute,
              private publisherService: PublisherService) { }

  ngOnInit(): void {
    this.router
        .params
        .subscribe({
          next: params => this.publisherService
                              .findBooks(+params['id'])
                              .subscribe({
                                next: data => this.publisher = data,
                                error: err => console.log(err)
                              }),
          error: err => console.log(err)
        })
  }
}

import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PublisherService {
  private readonly BASE_API = 'http://localhost:8080/api/publisher';

  constructor(private httpClient: HttpClient) { }

  findBooks(publisherId: number): Observable<any> {
    return this.httpClient.get(`${this.BASE_API}/books`, {
      params: {
        "publisherId": publisherId
      }
    })
  }
}

import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthorService {
  private readonly BASE_API = 'http://localhost:8080/api/author';

  constructor(private httpClient: HttpClient) { }

  findBooks(authorId: number): Observable<any> {
    return this.httpClient.get(`${this.BASE_API}/books`, {
      params: {
        "authorId": authorId
      }
    })
  }

  findAll(): Observable<any> {
    return this.httpClient.get(`${this.BASE_API}/all`);
  }
}

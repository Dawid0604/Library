import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CategoryService {
  private readonly AUTH_API = 'http://localhost:8080/api/category';

  constructor(private httpClient: HttpClient) { }

  getCategories(): Observable<any> {
    return this.httpClient.get(`${this.AUTH_API}/fetch`);
  }

  getSubCategories(categoryParent: string): Observable<any> {
    return this.httpClient.get(`${this.AUTH_API}/fetch/sub`, {
      params: {
        parent: categoryParent
      }
    });
  }
}

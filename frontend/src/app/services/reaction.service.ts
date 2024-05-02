import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ReactionService {
  private readonly BASE_API = 'http://localhost:8080/api/reaction';

  constructor(private httpClient: HttpClient) { }

  getReactions(bookId: number, page: number,
               comments: boolean, descSearching: boolean): Observable<any> {

    return this.httpClient.get(`${this.BASE_API}/get-all`, {
      params: {
        "bookId": bookId,
        "page": page,
        "size": 2,
        "comments": comments,
        "sort": (descSearching ? "DESC" : "ASC")
      }
    });
  }

  addReaction(request: any): Observable<any> {
    return this.httpClient.post(`${this.BASE_API}/add`, request);
  }

  updateReaction(request: any): Observable<any> {
    return this.httpClient.put(`${this.BASE_API}/edit`, request);
  }

  deleteReaction(reactionId: number): Observable<any> {
    return this.httpClient.delete(`${this.BASE_API}/delete`, {
      params: {
        id: reactionId
      }
    })
  }
}

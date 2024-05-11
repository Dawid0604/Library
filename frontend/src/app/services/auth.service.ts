import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { StorageService } from './storage.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly BASE_API_URL = 'http://localhost:8080/api/auth';
  private readonly httpOptions = {
    headers: new HttpHeaders({
      "Content-Type": "Application/json"
    })
  };

  constructor(private httpClient: HttpClient,
              private storageService: StorageService) { }

  login(username: string, password: string): Observable<any> {
    return this.httpClient.post(`${this.BASE_API_URL}/login`, {
      username, password
    }, this.httpOptions);
  }

  register(username: string, password: string): Observable<any> {
    return this.httpClient.post(`${this.BASE_API_URL}/register`, {
      username, password
    }, this.httpOptions);
  }

  refreshToken(): Observable<any> {
    let refreshToken = this.storageService.retrieveRefreshToken();
    return this.httpClient.post(`${this.BASE_API_URL}/refresh`, { "refreshToken": refreshToken }, this.httpOptions);
  }

  delete(): Observable<any> {
    return this.httpClient.delete(`${this.BASE_API_URL}/delete`);
  }
}

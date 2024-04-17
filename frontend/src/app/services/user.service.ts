import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { StorageService } from './storage.service';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly BASE_API_URL = 'http://localhost:8080/api/user';

  constructor(private htppClient: HttpClient) { }

  getDetails(): Observable<any> {
    return this.htppClient.get(`${this.BASE_API_URL}/details`);
  }

  getRoles(): Observable<any> {
    return this.htppClient.get(`${this.BASE_API_URL}/roles`, { responseType: 'text' });
  }
}

import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, map } from 'rxjs';
import { StorageService } from './storage.service';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly BASE_API_URL = 'http://localhost:8080/api/user';

  public readonly ADMIN_BOARD_KEY = 'ADMIN_BOARD';
  public readonly USER_BOARD_KEY = 'USER_BOARD';

  constructor(private htppClient: HttpClient) { }

  getDetails(): Observable<any> {
    return this.htppClient.get(`${this.BASE_API_URL}/details`);
  }

  getRoles(): Observable<any> {
    return this.htppClient.get(`${this.BASE_API_URL}/roles`, { responseType: 'text' });
  }
}

import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { StorageService } from '../../services/storage.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit {
  form: any = {
    username: null,
    password: null
  };

  isLoggedIn = false;
  isLoginFailed = false;
  errorMessage = '';

  constructor(private authService: AuthService,
              private storageService: StorageService) { }

  ngOnInit(): void {
    this.isLoggedIn = this.storageService.isLoggedIn();
  }

  onSubmit(): void {
    const {username, password} = this.form;

    this.authService
        .login(username, password)
        .subscribe({
          next: data => {
            this.storageService.storeTokens(data);
            this.isLoginFailed = false;
            this.isLoggedIn = true;
            this.reloadPage();
          },
          error: err => {
            this.errorMessage = err.error.message;
            this.isLoginFailed = true;
          }
        });
  }

  reloadPage(): void {
    window.location
          .reload();
  }
}

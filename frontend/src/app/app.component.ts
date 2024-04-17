import { Component } from '@angular/core';
import { StorageService } from './services/storage.service';
import { UserService } from './services/user.service';
import { BasketService } from './services/basket.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  isLoggedIn = false;
  showProfileBoard = false;
  showAdminBoard = false;
  title = 'Library';

  constructor(private storageService: StorageService,
              private userService: UserService,
              private basketService: BasketService) { }

  ngOnInit(): void {
    if(this.storageService.isLoggedIn()) {
      this.userService
          .getRoles()
          .subscribe({
            next: data => {
              const roles = data;
              this.showAdminBoard = roles.includes("ROLE_ADMIN");
              this.showProfileBoard = !this.showAdminBoard;
              this.isLoggedIn = true;
            },

            error: err => {
              console.log(err);
            }
          });
    }
  }

  getBasketSize() {
    return this.basketService.size();
  }

  logout(): void {
    this.storageService.logout();
  }
}

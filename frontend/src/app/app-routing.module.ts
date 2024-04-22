import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { ProfileComponent } from './components/profile/profile.component';
import { AdminComponent } from './components/admin/admin.component';
import { BookComponent } from './components/book/book.component';
import { RoleGuard } from './role.guard';
import { UserRole } from './model/UserRole';
import { BasketComponent } from './components/basket/basket.component';
import { PublisherComponent } from './components/publisher/publisher.component';
import { AuthorComponent } from './components/author/author.component';

const routes: Routes = [
  { path: "home", component: HomeComponent },
  { path: "login", component: LoginComponent },
  { path: "register", component: RegisterComponent },
  { path: "book/:title/:id", component: BookComponent },
  { path: "basket", component: BasketComponent },
  { path: "publisher/:name/:id", component: PublisherComponent },
  { path: "author/:name/:id", component: AuthorComponent },
  { path: "", redirectTo: "home", pathMatch: "full"},

  { path: "profile", component: ProfileComponent,
                     canActivate: [RoleGuard],
                     data: {
                      roles: [ UserRole.ROLE_USER ]
                     } },

  { path: "admin", component: AdminComponent,
                   canActivate: [RoleGuard],
                   data: {
                    roles: [ UserRole.ROLE_ADMIN ]
                   } },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

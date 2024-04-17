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

const routes: Routes = [
  { path: "home", component: HomeComponent },
  { path: "login", component: LoginComponent },
  { path: "register", component: RegisterComponent },
  { path: "book/:title/:id", component: BookComponent },
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

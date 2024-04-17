import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { UserRole } from './model/UserRole';
import { UserService } from './services/user.service';
import { map, take } from 'rxjs';

export const RoleGuard: CanActivateFn = (route, state) => {
  const router: Router = inject(Router);
  const userService: UserService = inject(UserService);

  return userService.getRoles()
                    .pipe(take(1),
                          map(role => {
                            const expectedRoles: UserRole[] = route.data['roles'];
                            let userRole = (role === "ROLE_USER") ? UserRole.ROLE_USER : UserRole.ROLE_ADMIN;

                            if(expectedRoles.some(_role => userRole === _role)) {
                              return true;
                            }

                            router.navigate(['home']); return false;
                    }))
};

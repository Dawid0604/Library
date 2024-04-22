import { Injectable } from '@angular/core';
import { HttpEvent, HttpInterceptor, HttpHandler, HttpRequest, HTTP_INTERCEPTORS, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, map, switchMap, throwError } from 'rxjs';
import { StorageService } from '../services/storage.service';
import { AuthService } from '../services/auth.service';

@Injectable()
export class HttpRequestInterceptor implements HttpInterceptor {
  private isRefreshing = false;
  private readonly endpointsWithoutToken: string[] = [
    "/api/auth/",
    "/api/book/fetch",
    "/api/category",
    "/api/basket/fetch",
    "/api/author",
    "/api/publisher"
  ];

  constructor(private storageService: StorageService,
              private authService: AuthService) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    req =this.setTokenHeader(req);

    return next.handle(req).pipe(
      catchError((error) => {
        if (
          error instanceof HttpErrorResponse &&
          (error.status === 401 || error.status === 403)) {

          return this.handle401Error(req, next);
        }

        return throwError(() => error);
      })
    );
  }

  private setTokenHeader(req: HttpRequest<any>): HttpRequest<any> {
    const token = this.storageService.retrieveAccessToken();

    if(token && this.endpointsWithoutToken.every(endpoint => !req.url.includes(endpoint))) {
      req = req.clone({
        withCredentials: true,
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });

    } else {
      req = req.clone({
        withCredentials: true
      });
    }

    return req;
  }

  private handle401Error(request: HttpRequest<any>, next: HttpHandler) {
    if(!this.isRefreshing) {
      this.isRefreshing = true;

      if(this.storageService.isLoggedIn()) {
        return this.authService
                   .refreshToken()
                   .pipe(
                    map(response => {
                      this.storageService.storeTokens(response);
                    }),

                    switchMap(() => {
                      this.isRefreshing = false;
                      return next.handle(this.setTokenHeader(request));
                    }),

                    catchError(error => {
                      this.isRefreshing = false;
                      this.storageService.logout();

                      return throwError(() => error);
                    })
                   )
      }

    } return next.handle(request);
  }
}

export const httpInterceptorProviders = [
  { provide: HTTP_INTERCEPTORS, useClass: HttpRequestInterceptor, multi: true },
];

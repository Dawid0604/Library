import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class StorageService {
  private readonly ACCESS_TOKEN_KEY = "accessToken";
  private readonly REFRESH_TOKEN_KEY = "refreshToken";
  private readonly SHOPPING_BASKET_KEY = "basket";

  constructor() { }

  cleanTokens(): void {
    this.cleanByKey(this.ACCESS_TOKEN_KEY);
    this.cleanByKey(this.REFRESH_TOKEN_KEY);
  }

  cleanBasket(): void {
    this.cleanByKey(this.SHOPPING_BASKET_KEY);
  }

  private cleanByKey(key: string): void {
    const date = new Date();
          date.setTime(date.getTime() + (-1 * 24 * 60 * 60 * 1000));

    document.cookie = key + "=; expires=" + date.toUTCString() + "; path=/";
  }

  public storeTokens(tokens: any): void {
    this.cleanTokens();

    let accessToken = tokens.accessToken;
    let refreshToken = tokens.refreshToken;

    const date = new Date();
          date.setTime(date.getTime() + (2 * 24 * 60 * 60 * 1000));

    document.cookie = this.ACCESS_TOKEN_KEY + "=" + accessToken + "; expires=" + date.toUTCString() + "; path=/";
    document.cookie = this.REFRESH_TOKEN_KEY + "=" + refreshToken + "; expires=" + date.toUTCString() + "; path=/";
  }

  public removeBookFromBasket(bookId: number): void {
    let basketContent = this.retrieveShoppingBasket();

    if(!basketContent) {
      basketContent = JSON.stringify([bookId]);

    } else {
      basketContent = JSON.parse(basketContent);
      basketContent = basketContent.filter((_bookId: number) => _bookId !== bookId);
      basketContent = JSON.stringify(basketContent);
    }

    const date = new Date();
          date.setTime(date.getTime() + (365 * 24 * 60 * 60 * 1000));

    document.cookie = this.SHOPPING_BASKET_KEY + "=" + basketContent + "; expires=" + date.toUTCString() + "; path=/";
  }

  public storeBookToBasket(bookId: number): void {
    let basketContent = this.retrieveShoppingBasket();

    if(!basketContent) {
      basketContent = JSON.stringify([bookId]);

    } else {
      basketContent = JSON.parse(basketContent);

      if(basketContent.includes(bookId)) {
        return;
      }

      basketContent.push(bookId);
      basketContent = JSON.stringify(basketContent);
    }

    const date = new Date();
          date.setTime(date.getTime() + (365 * 24 * 60 * 60 * 1000));

    document.cookie = this.SHOPPING_BASKET_KEY + "=" + basketContent + "; expires=" + date.toUTCString() + "; path=/";
  }

  public retrieveAccessToken(): any {
    const value = "; " + document.cookie;
    const parts = value.split("; " + this.ACCESS_TOKEN_KEY + "=");

    if(parts.length == 2) {
      return parts.pop()
                 ?.split(";")
                  .shift();
    }
  }

  public retrieveRefreshToken(): any {
    const value = "; " + document.cookie;
    const parts = value.split("; " + this.REFRESH_TOKEN_KEY + "=");

    if(parts.length == 2) {
      return parts.pop()
                 ?.split(";")
                  .shift();
    }
  }

  public retrieveShoppingBasket(): any {
    const value = "; " + document.cookie;
    const parts = value.split("; " + this.SHOPPING_BASKET_KEY + "=");

    if(parts.length == 2) {
      return parts.pop()
                 ?.split(";")
                  .shift();
    }
  }

  public isLoggedIn(): boolean {
    return (this.retrieveAccessToken() &&
            this.retrieveRefreshToken()) ? true : false;
  }

  public logout(): void {
    this.cleanTokens();
  }
}

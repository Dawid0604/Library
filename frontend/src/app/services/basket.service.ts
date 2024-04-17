import { Injectable } from '@angular/core';
import { StorageService } from './storage.service';

@Injectable({
  providedIn: 'root'
})
export class BasketService {

  constructor(private storageService: StorageService) { }

  storeBook(bookId: number) {
    this.storageService.storeBookToBasket(bookId);
  }

  size(): number {
    let result = this.storageService.retrieveShoppingBasket();
    return result ? (JSON.parse(result) as number[]).length
                  : 0;
  }

  contains(bookId: number): boolean {
    let result = this.storageService.retrieveShoppingBasket();
    return result && (result as number[]).includes(bookId);
  }
}

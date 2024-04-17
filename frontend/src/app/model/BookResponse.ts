import { Book } from "./Book";

export interface BookResponse {
  content: Book[],
  last: boolean,
  totalPages: number,
  size: number,
  first: boolean,
  numberOfElements: number,
  totalElements: number,
  pageable: {
    pageNumber: number
  }
}

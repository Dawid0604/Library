import { Author } from "./Author";
import { BookCover } from "./BookCover";

export interface CreateBookRequest {
  title: string,
  price: number,
  quantity: number,
  authors: Array<Author>,
  publisher: string,
  numberOfPages: number,
  edition: number,
  publicationYear: number,
  description: string,
  category: number,
  mainPicture: string,
  pictures: Array<string>,
  cover: BookCover
}

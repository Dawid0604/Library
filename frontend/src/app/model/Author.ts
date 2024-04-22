import { Book } from "./Book";

export interface Author {
  name: string,
  description: string,
  picture: string,
  books: Array<Book>
}

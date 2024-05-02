import { Book } from "./Book";

export interface Publisher {
  name: string,
  books: Array<Book>
}

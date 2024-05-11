import { Book } from "./Book";
import { BookResponse } from "./BookResponse";

export interface UserReaction {
  userId: number,
  book: Book,
  numberOfStars: number,
  comment: string,
  dateAdded: string
}

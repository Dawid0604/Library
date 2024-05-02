import { BookCover } from "./BookCover";

export interface SearchBookRequest {
  page: number;
  category: number|undefined;
  priceFrom: number|undefined;
  priceTo: number|undefined;
  numberOfPagesFrom: number|undefined,
  numberOfPagesTo: number|undefined,
  publicationYearFrom: number|undefined,
  publicationYearTo: number|undefined,
  cover: BookCover|undefined,
  title: string|undefined
}

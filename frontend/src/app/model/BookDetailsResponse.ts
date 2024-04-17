export interface BookDetailsResponse {
  bookId: number,
  title: string,
  price: number,
  originalPrice: number,
  quantity: number,
  publisher: PublisherDetailsResponse,
  authors: Array<AuthorDetailsResponse>,
  numberOfPages: number,
  edition: number,
  numberOfStars: number,
  publicationYear: number,
  description: string,
  category: string,
  subCategory: string,
  pictures: Array<string>,
  cover: string
}

export interface AuthorDetailsResponse {
  authorId: number,
  name: string,
  description: string,
  picture: string
}

export interface PublisherDetailsResponse {
  publisherId: number,
  name: string
}

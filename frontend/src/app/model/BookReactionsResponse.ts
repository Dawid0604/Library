export interface BookReactionsResponse {
  page: Page,
  userReaction: Reaction,
  statistics: Statistics
}

export interface Page {
  content: Reaction[],
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

export interface Reaction {
  reactionId: number,
  numberOfStars: number,
  comment: string,
  date: string,
  userUsername: string,
  userAvatar: string
}

export interface Statistics {
  numberOfOneStars: number,
  numberOfTwoStars: number,
  numberOfThreeStars: number,
  numberOfFourStars: number,
  numberOfFiveStars: number,
  numberOfComments: number,
  numberOfStars: number
}

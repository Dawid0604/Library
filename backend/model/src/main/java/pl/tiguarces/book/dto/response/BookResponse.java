package pl.tiguarces.book.dto.response;

import pl.tiguarces.book.entity.Book;

public record BookResponse(long bookId, String title, Double price, Double originalPrice,
                           String mainPicture) {

    public static BookResponse map(final Book book) {
        return new BookResponse(book.getBookId(), book.getTitle(), book.getPrice(), book.getOriginalPrice(),
                                book.getMainPicture());
    }
}

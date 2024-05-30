package pl.tiguarces.book.dto.response;

import pl.tiguarces.book.entity.Publisher;

import java.util.List;

public record BookDetailsResponse(long bookId, String title, double price, Double originalPrice,
                                  int quantity, Publisher publisher, List<Author> authors,
                                  int numberOfPages, String edition, Integer publicationYear, String description,
                                  String category, String subCategory, List<String> pictures, String cover) {

    public record Author(long authorId, String name, String description, String picture) { }

    public static BookDetailsResponse getEmptyObject() {
        return new BookDetailsResponse(1L, null, 1d, null, 1, null, null, 5, null, null,
                                           null, null, null, null, null);
    }
}

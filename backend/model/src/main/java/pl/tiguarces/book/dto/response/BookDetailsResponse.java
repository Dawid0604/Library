package pl.tiguarces.book.dto.response;

import pl.tiguarces.book.entity.Publisher;

import java.util.List;

public record BookDetailsResponse(long bookId, String title, double price, Double originalPrice,
                                  int quantity, Publisher publisher, List<Author> authors,
                                  int numberOfPages, int edition, Integer publicationYear, String description,
                                  String category, String subCategory, List<String> pictures, String cover) {

    public record Author(long authorId, String name, String description, String picture) { }
}

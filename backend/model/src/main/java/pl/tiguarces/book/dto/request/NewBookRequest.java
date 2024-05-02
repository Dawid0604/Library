package pl.tiguarces.book.dto.request;

import pl.tiguarces.book.entity.BookCover;

import java.util.List;

public record NewBookRequest(String title, Double price, int quantity, List<NewAuthorRequest> authors, String publisher, Integer numberOfPages, int edition,
                             int publicationYear, String description, String category, String mainPicture, List<String> pictures,
                             BookCover cover) {
}

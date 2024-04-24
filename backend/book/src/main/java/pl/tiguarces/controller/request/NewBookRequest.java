package pl.tiguarces.controller.request;

import pl.tiguarces.model.BookCover;

import java.util.List;

public record NewBookRequest(String title, Double price, int quantity, List<NewAuthorRequest> authors, String publisher, Integer numberOfPages, int edition,
                             int publicationYear, String description, String category, String mainPicture, List<String> pictures,
                             BookCover cover) {
}

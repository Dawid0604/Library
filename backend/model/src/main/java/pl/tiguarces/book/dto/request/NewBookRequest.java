package pl.tiguarces.book.dto.request;

import pl.tiguarces.book.entity.BookCover;

import java.util.List;

public record NewBookRequest(String title, double price, int quantity, List<NewAuthorRequest> authors, String publisher, int numberOfPages, int edition,
                             int publicationYear, String description, String category, String mainPicture, List<String> pictures,
                             BookCover cover) {

    public static NewBookRequest getEmptyOpject() {
        return new NewBookRequest(null, 1, 1, null, null, 1, 1, 1, null, null, null, null, null);
    }
}

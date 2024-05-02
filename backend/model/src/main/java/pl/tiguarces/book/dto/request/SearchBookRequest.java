package pl.tiguarces.book.dto.request;

import lombok.Getter;
import pl.tiguarces.book.entity.BookCover;

@Getter
public final class SearchBookRequest {
    private final int page;
    private final int size = 10;
    private final String category;
    private final Double priceFrom;
    private final Double priceTo;
    private final Integer numberOfPagesFrom;
    private final Integer numberOfPagesTo;
    private final Integer publicationYearFrom;
    private final Integer publicationYearTo;
    private final BookCover cover;

    public SearchBookRequest(Integer page, String category, Double priceFrom,
                             Double priceTo, Integer numberOfPagesFrom, Integer numberOfPagesTo,
                             Integer publicationYearFrom, Integer publicationYearTo, BookCover cover) {

        this.page = (page == null) ? 0 : page;
        this.category = category;
        this.priceFrom = priceFrom;
        this.priceTo = priceTo;
        this.numberOfPagesFrom = numberOfPagesFrom;
        this.numberOfPagesTo = numberOfPagesTo;
        this.publicationYearFrom = publicationYearFrom;
        this.publicationYearTo = publicationYearTo;
        this.cover = cover;
    }
}

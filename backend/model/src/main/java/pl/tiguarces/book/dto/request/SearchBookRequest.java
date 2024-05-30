package pl.tiguarces.book.dto.request;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import pl.tiguarces.book.entity.BookCover;

@Getter
@EqualsAndHashCode
public final class SearchBookRequest {
    private final int page;
    private final int size = 10;
    private final String title;
    private final String category;
    private final Double priceFrom;
    private final Double priceTo;
    private final Integer numberOfPagesFrom;
    private final Integer numberOfPagesTo;
    private final Integer publicationYearFrom;
    private final Integer publicationYearTo;
    private final BookCover cover;

    public SearchBookRequest(final Integer page, final String category, final Double priceFrom,
                             final Double priceTo, final Integer numberOfPagesFrom, final Integer numberOfPagesTo,
                             final Integer publicationYearFrom, final Integer publicationYearTo, final BookCover cover,
                             final String title) {

        this.page = (page == null) ? 0 : page;
        this.title = title;
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

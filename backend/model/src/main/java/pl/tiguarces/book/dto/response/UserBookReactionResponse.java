package pl.tiguarces.book.dto.response;

import pl.tiguarces.AppUtils;
import pl.tiguarces.book.entity.UserBookReaction;

public record UserBookReactionResponse(long reactionId, BookResponse book, int numberOfStars,
                                       String comment, String dateAdded) {
    public static UserBookReactionResponse map(final UserBookReaction userBookReaction, final BookResponse bookResponse) {
        return new UserBookReactionResponse(userBookReaction.getReactionId(), bookResponse, userBookReaction.getNumberOfStars(),
                                            userBookReaction.getComment(), AppUtils.formatDate(userBookReaction.getDate()));
    }
}

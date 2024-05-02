package pl.tiguarces.book.dto.response;

import org.springframework.data.domain.Page;
import pl.tiguarces.book.entity.UserBookReaction;

import static pl.tiguarces.AppUtils.formatDate;

public record BookReactionsResponse(Page<Reaction> page, Reaction userReaction, Statistics statistics) {

    public record Statistics(int numberOfOneStars, int numberOfTwoStars,
                             int numberOfThreeStars, int numberOfFourStars,
                             int numberOfFiveStars, int numberOfComments,
                             int numberOfStars) { }

    public record Reaction(long reactionId, int numberOfStars, String comment, String date,
                           String userUsername, String userAvatar) {
        public static Reaction map(final UserBookReaction reaction) {
            var user = reaction.getUser();
            return new Reaction(reaction.getReactionId(), reaction.getNumberOfStars(), reaction.getComment(), formatDate(reaction.getDate()),
                                user.getUsername(), user.getAvatar());
        }
    }
}

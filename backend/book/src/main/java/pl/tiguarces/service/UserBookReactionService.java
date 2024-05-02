package pl.tiguarces.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.tiguarces.book.dto.response.BookReactionsResponse;
import pl.tiguarces.book.dto.response.BookReactionsResponse.Reaction;
import pl.tiguarces.book.entity.UserBookReaction;
import pl.tiguarces.book.repository.BookRepository;
import pl.tiguarces.book.repository.UserBookReactionRepository;

import java.util.Objects;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static pl.tiguarces.book.dto.response.BookReactionsResponse.Reaction.map;

@Service
@RequiredArgsConstructor
public class UserBookReactionService {
    private final UserBookReactionRepository userBookReactionRepository;
    private final BookRepository bookRepository;
    private final AppUserService appUserService;

    @Transactional(readOnly = true)
    public Optional<UserBookReaction> getUserReaction(final long bookId) {
        var loggedUser = appUserService.getLoggedUserFromDb()
                                       .orElseThrow();

        return userBookReactionRepository.findByUserUserIdAndBookBookId(loggedUser.getUserId(), bookId);
    }

    @Transactional
    public void addBookReaction(final BookReactionRequest newBookReaction) {
        var user = appUserService.getLoggedUserFromDb();

        if(user.isPresent()) {
            var book = bookRepository.findById(newBookReaction.bookId())
                                     .orElseThrow(() -> new IllegalArgumentException("Book with given Id not found >> BookId: " + newBookReaction.bookId()));

            userBookReactionRepository.save(UserBookReaction.builder()
                                                            .book(book)
                                                            .comment(newBookReaction.comment())
                                                            .user(appUserService.getById(user.get().getUserId()))
                                                            .numberOfStars(newBookReaction.numberOfStars())
                                                            .build());
        }
    }

    @Transactional
    public void editBookReaction(final BookReactionRequest newBookReaction) {
        var existingUserReaction = getUserReaction(newBookReaction.bookId());

        if(existingUserReaction.isPresent()) {
            var reaction = existingUserReaction.get();
            boolean shouldSave = true;

            if(newBookReaction.numberOfStars() != null && !Objects.equals(reaction.getNumberOfStars(), newBookReaction.numberOfStars())) {
                reaction.setNumberOfStars(newBookReaction.numberOfStars());

            } else {
                shouldSave = false;
            }

            if(isNotBlank(newBookReaction.comment()) && !Objects.equals(reaction.getComment(), newBookReaction.comment())) {
                reaction.setComment(newBookReaction.comment());

            } else {
                shouldSave = false;
            }

            if(shouldSave) {
                userBookReactionRepository.save(reaction);
            }
        }
    }

    @Transactional(readOnly = true)
    public BookReactionsResponse getBookReactions(int page, final int size, final boolean comments,
                                                  String sort, final long bookId) {

        page = (page > 0) ? (page - 1) : 0;
        var sorting = !equalsIgnoreCase(sort, "ASC") ? Sort.Direction.DESC
                                                     : Sort.Direction.ASC;

        var pageable = PageRequest.of(page, size, sorting, "date");
        var result = userBookReactionRepository.findBookReactions(bookId, comments, pageable);

        var loggedUser = appUserService.getLoggedUser();
        Reaction userReaction = null;

        if(loggedUser != null) {
            String username = loggedUser.getUsername();
            var iterator = result.iterator();

            while(iterator.hasNext()) {
                var reaction = iterator.next();

                if(reaction.getUser().getUsername().equals(username)) {
                    userReaction = map(reaction);
                    iterator.remove();  break;
                }
            }
        } return new BookReactionsResponse(result.map(Reaction::map), userReaction, calculateStatistics(bookId));
    }

    private BookReactionsResponse.Statistics calculateStatistics(final long bookId) {
        int oneStars = userBookReactionRepository.countNumberOfStars(bookId, 1);
        int twoStars = userBookReactionRepository.countNumberOfStars(bookId, 2);
        int threeStars = userBookReactionRepository.countNumberOfStars(bookId, 3);
        int fourStars = userBookReactionRepository.countNumberOfStars(bookId, 4);
        int fiveStars = userBookReactionRepository.countNumberOfStars(bookId, 5);
        int comments = userBookReactionRepository.countComments(bookId);
        int totalStars = oneStars + twoStars + threeStars + fourStars + fiveStars;

        return new BookReactionsResponse.Statistics(oneStars, twoStars, threeStars, fourStars, fiveStars, comments, totalStars);
    }

    public void deleteReaction(final long reactionId) {
        userBookReactionRepository.deleteById(reactionId);
    }

    public record BookReactionRequest(long bookId, Integer numberOfStars, String comment) { }
}

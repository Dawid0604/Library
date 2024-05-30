package pl.tiguarces.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.tiguarces.book.dto.request.UserBookReactionRequest;
import pl.tiguarces.book.dto.response.BookReactionsResponse;
import pl.tiguarces.book.dto.response.BookReactionsResponse.Reaction;
import pl.tiguarces.book.dto.response.BookResponse;
import pl.tiguarces.book.dto.response.UserBookReactionResponse;
import pl.tiguarces.book.entity.UserBookReaction;
import pl.tiguarces.book.repository.BookRepository;
import pl.tiguarces.book.repository.UserBookReactionRepository;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toCollection;
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
    public Optional<UserBookReaction> findUserBookReaction(final long bookId) {
        var loggedUser = appUserService.getLoggedUserFromDb();
        return userBookReactionRepository.findByUserUserIdAndBookBookId(loggedUser.getUserId(), bookId);
    }

    @Transactional
    public void addBookReaction(final UserBookReactionRequest newBookReaction) {
        var user = appUserService.getLoggedUserFromDb();

        var book = bookRepository.findById(newBookReaction.bookId())
                                 .orElseThrow(() -> new IllegalArgumentException("Book with given Id not found >> BookId: " + newBookReaction.bookId()));

        userBookReactionRepository.save(UserBookReaction.builder()
                                  .book(book)
                                  .comment(newBookReaction.comment())
                                  .user(appUserService.findById(user.getUserId()))
                                  .numberOfStars(newBookReaction.numberOfStars() != null ? newBookReaction.numberOfStars() : 0)
                                  .build());
    }

    @Transactional
    public void editBookReaction(final UserBookReactionRequest newBookReaction) {
        var existingUserReaction = findUserBookReaction(newBookReaction.bookId());

        if(existingUserReaction.isPresent()) {
            var reaction = existingUserReaction.get();

            if(newBookReaction.numberOfStars() != null && !Objects.equals(reaction.getNumberOfStars(), newBookReaction.numberOfStars())) {
                reaction.setNumberOfStars(newBookReaction.numberOfStars());
            }

            if(isNotBlank(newBookReaction.comment()) && !Objects.equals(reaction.getComment(), newBookReaction.comment())) {
                reaction.setComment(newBookReaction.comment());
            }

            userBookReactionRepository.save(reaction);
        }
    }

    @Transactional(readOnly = true)
    public BookReactionsResponse findBookReactions(int page, final int size, final boolean comments,
                                                   final String sort, final long bookId) {

        page = (page > 0) ? (page - 1) : 0;
        var sorting = !equalsIgnoreCase(sort, "ASC") ? Sort.Direction.DESC
                                                     : Sort.Direction.ASC;

        var pageable = PageRequest.of(page, size, sorting, "date");
        var result = userBookReactionRepository.findBookReactions(bookId, comments, pageable);

        var loggedUser = appUserService.getLoggedUser();
        Reaction userReaction = null;

        if(loggedUser != null) {
            var iterator = result.iterator();

            while(iterator.hasNext()) {
                var reaction = iterator.next();

                if(reaction.getUser().getUsername().equals(loggedUser.getUsername())) {
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

    @Transactional(readOnly = true)
    public List<UserBookReactionResponse> findUserReactions() {
        var loggedUser = appUserService.getLoggedUserFromDb();

        return Stream.ofNullable(loggedUser.getBookReactions())
                     .limit(3)
                     .flatMap(List::stream)
                     .map(_reaction -> UserBookReactionResponse.map(_reaction, BookResponse.map(_reaction.getBook())))
                     .collect(toCollection(LinkedList::new));
    }
}

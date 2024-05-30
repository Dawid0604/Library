package pl.tiguarces.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import pl.tiguarces.book.dto.request.UserBookReactionRequest;
import pl.tiguarces.book.dto.response.BookReactionsResponse.Reaction;
import pl.tiguarces.book.entity.Book;
import pl.tiguarces.book.entity.UserBookReaction;
import pl.tiguarces.book.repository.BookRepository;
import pl.tiguarces.book.repository.UserBookReactionRepository;
import pl.tiguarces.user.entity.AppUser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.Mockito.verify;
import static pl.tiguarces.AppUtils.getCurrentDate;

@ExtendWith(MockitoExtension.class)
class UserBookReactionServiceTest {
    @Mock private UserBookReactionRepository userBookReactionRepository;
    @Mock private BookRepository bookRepository;
    @Mock private AppUserService appUserService;
    @InjectMocks private UserBookReactionService userBookReactionService;

    @Test
    void shouldFindUserBookReaction() {
        // Given
        long bookId = 2;
        long userId = 1;

        var foundUser = AppUser.builder()
                               .userId(userId)
                               .build();

        given(appUserService.getLoggedUserFromDb())
                .willReturn(foundUser);

        given(userBookReactionRepository.findByUserUserIdAndBookBookId(eq(userId), eq(bookId)))
                .willReturn(Optional.of(mock(UserBookReaction.class)));

        // When
        var result = userBookReactionService.findUserBookReaction(bookId);

        // Then
        assertTrue(result.isPresent());
    }

    @Test
    void shouldDeleteReaction() {
        // Given
        long reactionId = 1;

        // When
        userBookReactionService.deleteReaction(reactionId);

        // Then
        verify(userBookReactionRepository).deleteById(eq(reactionId));
    }

    @Test
    void shouldFindUserReactions() {
        // Given
        long userId = 1;

        var foundUser = AppUser.builder()
                               .userId(userId)
                               .build();

        var reaction = UserBookReaction.builder()
                                       .reactionId(1L)
                                       .user(foundUser)
                                       .numberOfStars(5)
                                       .book(Book.builder()
                                                 .bookId(1L)
                                                 .build())
                                       .build();

        foundUser.setBookReactions(List.of(reaction));

        given(appUserService.getLoggedUserFromDb())
                .willReturn(foundUser);

        // When
        var result = userBookReactionService.findUserReactions();

        // Then
        assertEquals(1, result.size());
    }

    @Test
    void shouldAddBookReaction() {
        // Given
        long userId = 1;
        long bookId = 2;

        var foundUser = AppUser.builder()
                               .userId(userId)
                               .build();

        var request = new UserBookReactionRequest(bookId, 5, "Any note content");

        var foundBook = Book.builder()
                            .bookId(bookId)
                            .build();

        var savedReaction = UserBookReaction.builder()
                                            .comment(request.comment())
                                            .user(foundUser)
                                            .numberOfStars(request.numberOfStars())
                                            .book(foundBook)
                                            .build();

        given(appUserService.getLoggedUserFromDb())
                .willReturn(foundUser);

        given(bookRepository.findById(eq(bookId)))
                .willReturn(Optional.of(foundBook));

        // When
        userBookReactionService.addBookReaction(request);

        // Then
        verify(appUserService).getLoggedUserFromDb();
        verify(bookRepository).findById(eq(request.bookId()));
        verify(userBookReactionRepository).save(eq(savedReaction));
    }

    @Test
    void shouldAddBookReactionWhenNoteIsNotPresent() {
        // Given
        long userId = 1;
        long bookId = 2;

        var foundUser = AppUser.builder()
                               .userId(userId)
                               .build();

        var request = new UserBookReactionRequest(bookId, 5, null);

        var foundBook = Book.builder()
                            .bookId(bookId)
                            .build();

        var savedReaction = UserBookReaction.builder()
                                            .comment(request.comment())
                                            .user(foundUser)
                                            .numberOfStars(request.numberOfStars())
                                            .book(foundBook)
                                            .build();

        given(appUserService.getLoggedUserFromDb())
                .willReturn(foundUser);

        given(bookRepository.findById(eq(bookId)))
                .willReturn(Optional.of(foundBook));

        // When
        userBookReactionService.addBookReaction(request);

        // Then
        verify(appUserService).getLoggedUserFromDb();
        verify(bookRepository).findById(eq(request.bookId()));
        verify(userBookReactionRepository).save(eq(savedReaction));
    }

    @Test
    void shouldAddBookReactionWhenNumberOfStarsAreNotPresent() {
        // Given
        long userId = 1;
        long bookId = 2;

        var foundUser = AppUser.builder()
                               .userId(userId)
                               .build();

        var request = new UserBookReactionRequest(bookId, null, "Any note content");

        var foundBook = Book.builder()
                            .bookId(bookId)
                            .build();

        var savedReaction = UserBookReaction.builder()
                                            .comment(request.comment())
                                            .user(foundUser)
                                            .numberOfStars(0)
                                            .book(foundBook)
                                            .build();

        given(appUserService.getLoggedUserFromDb())
                .willReturn(foundUser);

        given(bookRepository.findById(eq(bookId)))
                .willReturn(Optional.of(foundBook));

        // When
        userBookReactionService.addBookReaction(request);

        // Then
        verify(appUserService).getLoggedUserFromDb();
        verify(bookRepository).findById(eq(request.bookId()));
        verify(userBookReactionRepository).save(eq(savedReaction));
    }

    @Test
    void shouldEditBookReaction() {
        // Given
        long userId = 1;
        long bookId = 2;

        var foundUser = AppUser.builder()
                               .userId(userId)
                               .build();

        var request = new UserBookReactionRequest(bookId, 1, "Any note content");

        var foundBook = Book.builder()
                            .bookId(bookId)
                            .build();

        var foundReaction = UserBookReaction.builder()
                                            .comment("Any old note content")
                                            .user(foundUser)
                                            .numberOfStars(2)
                                            .book(foundBook)
                                            .date(LocalDateTime.of(2025, 5, 2, 12, 30))
                                            .build();

        ArgumentCaptor<UserBookReaction> argumentCaptor = ArgumentCaptor.forClass(UserBookReaction.class);

        given(userBookReactionRepository.findByUserUserIdAndBookBookId(eq(userId), eq(bookId)))
                .willReturn(Optional.of(foundReaction));

        given(appUserService.getLoggedUserFromDb())
                .willReturn(foundUser);

        given(userBookReactionRepository.save(argumentCaptor.capture()))
                .willAnswer(_inv -> argumentCaptor.getValue());

        // When
        userBookReactionService.editBookReaction(request);

        // Then
        verify(appUserService).getLoggedUserFromDb();
        verify(userBookReactionRepository).save(eq(foundReaction));
        assertTrue(() -> {
            var _savedReaction = argumentCaptor.getValue();
            return Objects.equals(_savedReaction.getComment(), request.comment())               &&
                   Objects.equals(_savedReaction.getNumberOfStars(), request.numberOfStars())   &&
                   Objects.equals(_savedReaction.getDate(), foundReaction.getDate())            &&
                   Objects.equals(_savedReaction.getBook(), foundReaction.getBook());
        });
    }

    @Test
    void shouldEditBookReactionWhenNoteIsSame() {
        // Given
        long userId = 1;
        long bookId = 2;

        var foundUser = AppUser.builder()
                               .userId(userId)
                               .build();

        var request = new UserBookReactionRequest(bookId, 1, "Any note content");

        var foundBook = Book.builder()
                            .bookId(bookId)
                            .build();

        var foundReaction = UserBookReaction.builder()
                                            .comment(request.comment())
                                            .user(foundUser)
                                            .numberOfStars(2)
                                            .book(foundBook)
                                            .date(LocalDateTime.of(2025, 5, 2, 12, 30))
                                            .build();

        ArgumentCaptor<UserBookReaction> argumentCaptor = ArgumentCaptor.forClass(UserBookReaction.class);

        given(userBookReactionRepository.findByUserUserIdAndBookBookId(eq(userId), eq(bookId)))
                .willReturn(Optional.of(foundReaction));

        given(appUserService.getLoggedUserFromDb())
                .willReturn(foundUser);

        given(userBookReactionRepository.save(argumentCaptor.capture()))
                .willAnswer(_inv -> argumentCaptor.getValue());

        // When
        userBookReactionService.editBookReaction(request);

        // Then
        verify(appUserService).getLoggedUserFromDb();
        verify(userBookReactionRepository).save(eq(foundReaction));
        assertTrue(() -> {
            var _savedReaction = argumentCaptor.getValue();
            return Objects.equals(_savedReaction.getComment(), request.comment())               &&
                   Objects.equals(_savedReaction.getNumberOfStars(), request.numberOfStars())   &&
                   Objects.equals(_savedReaction.getDate(), foundReaction.getDate())            &&
                   Objects.equals(_savedReaction.getBook(), foundReaction.getBook());
        });
    }

    @Test
    void shouldEditBookReactionWhenNumberOfStarsAreSame() {
        // Given
        long userId = 1;
        long bookId = 2;

        var foundUser = AppUser.builder()
                               .userId(userId)
                               .build();

        var request = new UserBookReactionRequest(bookId, 1, "Any note content");

        var foundBook = Book.builder()
                            .bookId(bookId)
                            .build();

        var foundReaction = UserBookReaction.builder()
                                            .comment(null)
                                            .user(foundUser)
                                            .numberOfStars(request.numberOfStars())
                                            .book(foundBook)
                                            .date(LocalDateTime.of(2025, 5, 2, 12, 30))
                                            .build();

        ArgumentCaptor<UserBookReaction> argumentCaptor = ArgumentCaptor.forClass(UserBookReaction.class);

        given(userBookReactionRepository.findByUserUserIdAndBookBookId(eq(userId), eq(bookId)))
                .willReturn(Optional.of(foundReaction));

        given(appUserService.getLoggedUserFromDb())
                .willReturn(foundUser);

        given(userBookReactionRepository.save(argumentCaptor.capture()))
                .willAnswer(_inv -> argumentCaptor.getValue());

        // When
        userBookReactionService.editBookReaction(request);

        // Then
        verify(appUserService).getLoggedUserFromDb();
        verify(userBookReactionRepository).save(eq(foundReaction));
        assertTrue(() -> {
            var _savedReaction = argumentCaptor.getValue();
            return Objects.equals(_savedReaction.getComment(), request.comment())               &&
                   Objects.equals(_savedReaction.getNumberOfStars(), request.numberOfStars())   &&
                   Objects.equals(_savedReaction.getDate(), foundReaction.getDate())            &&
                   Objects.equals(_savedReaction.getBook(), foundReaction.getBook());
        });
    }

    @ParameterizedTest
    @MethodSource("shouldFindBookReactionsDataProvider")
    void shouldFindBookReactions(final int page, final int expectedPage,
                                 final String sorting, final Sort.Direction expectedDirection) {

        // Given
        long bookId = 2;
        long userId = 1;
        int size = 25;

        var foundUser = AppUser.builder()
                               .userId(userId)
                               .username("anyUsername")
                               .build();

        var userReaction = UserBookReaction.builder()
                                           .reactionId(5L)
                                           .date(getCurrentDate())
                                           .user(foundUser)
                                           .build();

        var foundReactions = List.of(
                userReaction,
                UserBookReaction.builder()
                                .reactionId(6L)
                                .date(getCurrentDate().minusDays(5))
                                .user(AppUser.builder()
                                             .userId(15L)
                                             .username("anyUsername2")
                                             .build())
                                .build()
        );

        var pageable = PageRequest.of(expectedPage, size, expectedDirection, "date");

        given(userBookReactionRepository.findBookReactions(eq(bookId), eq(true), eq(pageable)))
                .willReturn(new PageImpl<>(foundReactions, pageable, foundReactions.size()));

        given(appUserService.getLoggedUser())
                .willReturn(foundUser);

        // When
        var result = userBookReactionService.findBookReactions(page, size, true, sorting, bookId);

        // Then
        assertEquals(result.userReaction(), Reaction.map(userReaction));
        assertEquals(1, result.page().getContent().size());
        assertNotNull(result.statistics());
        verify(userBookReactionRepository).countNumberOfStars(eq(bookId), eq(1));
        verify(userBookReactionRepository).countNumberOfStars(eq(bookId), eq(2));
        verify(userBookReactionRepository).countNumberOfStars(eq(bookId), eq(3));
        verify(userBookReactionRepository).countNumberOfStars(eq(bookId), eq(4));
        verify(userBookReactionRepository).countNumberOfStars(eq(bookId), eq(5));
        verify(userBookReactionRepository).countComments(eq(bookId));
    }

    public static Stream<Arguments> shouldFindBookReactionsDataProvider() {
        return Stream.of(
                Arguments.of(0, 0, "DESC", Sort.Direction.DESC),
                Arguments.of(0, 0, "ASC", Sort.Direction.ASC),
                Arguments.of(0, 0, "UNDEFINED", Sort.Direction.DESC),

                Arguments.of(1, 0, "DESC", Sort.Direction.DESC),
                Arguments.of(1, 0, "ASC", Sort.Direction.ASC),
                Arguments.of(1, 0, "UNDEFINED", Sort.Direction.DESC),

                Arguments.of(4, 3, "DESC", Sort.Direction.DESC),
                Arguments.of(4, 3, "ASC", Sort.Direction.ASC),
                Arguments.of(4, 3, "UNDEFINED", Sort.Direction.DESC)
        );
    }
}
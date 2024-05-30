package pl.tiguarces.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import pl.tiguarces.book.dto.request.NewAuthorRequest;
import pl.tiguarces.book.dto.request.NewBookRequest;
import pl.tiguarces.book.dto.request.SearchBookRequest;
import pl.tiguarces.book.entity.*;
import pl.tiguarces.book.repository.AuthorRepository;
import pl.tiguarces.book.repository.BookRepository;
import pl.tiguarces.book.repository.CategoryRepository;
import pl.tiguarces.book.repository.PublisherRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    @Mock private BookRepository bookRepository;
    @Mock private PublisherRepository publisherRepository;
    @Mock private AuthorRepository authorRepository;
    @Mock private CategoryRepository categoryRepository;
    @InjectMocks private BookService bookService;

    @Test
    void shouldFindAll() {
        // Given
        var request = new SearchBookRequest(1, "1;2", 15.50, null, 125, 555, null, 2024, BookCover.HARD, "anyTitle");
        var foundBooks = new PageImpl<>(List.of(mock(Book.class), mock(Book.class)));

        given(bookRepository.findAllByRequest(eq(request.getCategory()), eq(request.getPriceFrom()),
                                              eq(request.getPriceTo()), eq(request.getNumberOfPagesFrom()), eq(request.getNumberOfPagesTo()),
                                              eq(request.getPublicationYearFrom()), eq(request.getPublicationYearTo()), eq(request.getCover()),
                                              eq(request.getTitle()), eq(PageRequest.of(request.getPage(), request.getSize()))))
                .willReturn(foundBooks);

        // When
        var result = bookService.findAll(request);

        // Then
        assertEquals(foundBooks.getSize(), result.getTotalElements());
    }

    @Test
    void shouldFindAllByIds() {
        // Given
        List<Long> ids = List.of(15L, 30L, 45L, 5L);
        var foundBooks = List.of(mock(Book.class), mock(Book.class), mock(Book.class));

        given(bookRepository.findBooksById(eq(ids)))
                .willReturn(foundBooks);

        // When
        var result = bookService.findAllByIds(ids);

        // Then
        assertEquals(foundBooks, result);
    }

    @Test
    void shouldFindById() {
        // Given
        long bookId = 15L;

        String mainCategory = "1";
        String subCategory = "2";
        Category category = Category.builder()
                                    .categoryId(1L)
                                    .index(mainCategory + ';' + subCategory)
                                    .build();

        var authors = List.of(
                Author.builder()
                        .authorId(1L)
                        .name("John Doe")
                        .build()
        );

        var pictures = List.of(
                Picture.builder()
                        .pictureId(1L)
                        .path("firstImage.png")
                        .build(),

                Picture.builder()
                        .pictureId(2L)
                        .path("secondImage.png")
                        .build()
        );

        var foundBook = Book.builder()
                            .bookId(bookId)
                            .authors(authors)
                            .pictures(pictures)
                            .category(category)
                            .edition(1)
                            .mainPicture("mainPicture.png")
                            .cover(BookCover.SOFT)
                            .price(15.50d)
                            .quantity(5)
                            .numberOfPages(550)
                            .edition(1)
                            .publicationYear(2011)
                            .build();

        given(bookRepository.findById(eq(bookId)))
                .willReturn(Optional.of(foundBook));

        given(categoryRepository.findNameByIndex(eq(mainCategory + ";")))
                .willReturn("Any category name");

        given(categoryRepository.findNameByIndex(eq(category.getIndex())))
                .willReturn("Any sub category name");

        // When
        var result = bookService.findById(bookId);

        // Then
        assertTrue(() -> {
            if(result.isEmpty()) {
                return false;
            }

            var _book = result.get();
            return Objects.equals(_book.edition(), "I") &&
                   Objects.equals(_book.pictures().size(), pictures.size() + 1);
        });
    }

    @Test
    void shouldNotFindById() {
        // Given
        long bookId = 15L;

        // When
        var result = bookService.findById(bookId);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldFindByIdWhenSubCategoryIsNotPresent() {
        // Given
        long bookId = 15L;

        String mainCategory = "1";
        Category category = Category.builder()
                                    .categoryId(1L)
                                    .index(mainCategory + ';')
                                    .build();

        var authors = List.of(
                Author.builder()
                        .authorId(1L)
                        .name("John Doe")
                        .build()
        );

        var pictures = List.of(
                Picture.builder()
                        .pictureId(1L)
                        .path("firstImage.png")
                        .build(),

                Picture.builder()
                        .pictureId(2L)
                        .path("secondImage.png")
                        .build()
        );

        var foundBook = Book.builder()
                            .bookId(bookId)
                            .authors(authors)
                            .pictures(pictures)
                            .category(category)
                            .edition(1)
                            .mainPicture("mainPicture.png")
                            .cover(BookCover.SOFT)
                            .price(15.50d)
                            .quantity(5)
                            .numberOfPages(550)
                            .edition(1)
                            .publicationYear(2011)
                            .build();

        given(bookRepository.findById(eq(bookId)))
                .willReturn(Optional.of(foundBook));

        // When
        var result = bookService.findById(bookId);

        // Then
        verifyNoInteractions(categoryRepository);
        assertTrue(() -> {
            if(result.isEmpty()) {
                return false;
            }

            var _book = result.get();
            return Objects.equals(_book.edition(), "I") &&
                   Objects.equals(_book.pictures().size(), pictures.size() + 1);
        });
    }

    @Test
    void shouldSaveNewBook() {
        // Given
        var authors = List.of(
                new NewAuthorRequest("any author name", "any description", "anyPicture"),
                new NewAuthorRequest("any author name2", "any description2", "anyPicture2")
        );

        var category = "1;2";
        var publisher = "O’Reilly Media";
        var pictures = List.of("anyPicture.png", "anyPicture2.png");

        var request = new NewBookRequest("anyTitle", 15.50d, 5, authors, publisher, 550, 1,
                                         2010, "anyDescription", category, "mainPicture.png", pictures, BookCover.HARD);

        given(categoryRepository.findByIndex(eq(category)))
                .willReturn(Optional.of(Category.builder()
                                                .categoryId(1L)
                                                .name("any category name")
                                                .index(category)
                                                .build()));

        // When
        bookService.save(request);

        // Then
        verify(bookRepository).save(any());
        verify(publisherRepository).findByName(eq(publisher));
        verify(categoryRepository).findByIndex(eq(category));
        verify(authorRepository, times(2)).findByName(any());
    }
}
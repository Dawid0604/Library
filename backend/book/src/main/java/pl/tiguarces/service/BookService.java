package pl.tiguarces.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.tiguarces.book.dto.request.NewAuthorRequest;
import pl.tiguarces.book.dto.request.NewBookRequest;
import pl.tiguarces.book.dto.request.SearchBookRequest;
import pl.tiguarces.book.dto.response.BookDetailsResponse;
import pl.tiguarces.book.dto.response.BookResponse;
import pl.tiguarces.book.entity.Author;
import pl.tiguarces.book.entity.Book;
import pl.tiguarces.book.entity.Picture;
import pl.tiguarces.book.entity.Publisher;
import pl.tiguarces.book.repository.AuthorRepository;
import pl.tiguarces.book.repository.BookRepository;
import pl.tiguarces.book.repository.CategoryRepository;
import pl.tiguarces.book.repository.PublisherRepository;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static java.util.Collections.emptyList;
import static org.springframework.util.CollectionUtils.isEmpty;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final PublisherRepository publisherRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;

    private static final String SEMICOLON = ";";
    private static final Pattern SEMICOLON_PATTERN = Pattern.compile(SEMICOLON);

    public Page<BookResponse> findAll(final SearchBookRequest request) {
        return bookRepository.findAllByRequest(request.getCategory(), request.getPriceFrom(), request.getPriceTo(),
                                               request.getNumberOfPagesFrom(), request.getNumberOfPagesTo(), request.getPublicationYearFrom(),
                                               request.getPublicationYearTo(), request.getCover(), request.getTitle(), PageRequest.of(request.getPage(), request.getSize()))
                             .map(BookResponse::map);
    }

    @Transactional(readOnly = true)
    public Optional<BookDetailsResponse> findById(final long bookId) {
        var possibleBook = bookRepository.findById(bookId);

        if(possibleBook.isPresent()) {
            var book = possibleBook.get();
            var authors = book.getAuthors()
                              .stream()
                              .map(author -> new BookDetailsResponse.Author(author.getAuthorId(), author.getName(),
                                                                            author.getDescription(), author.getPicture()))
                              .toList();

            var pictures = new LinkedList<String>() {{
                add(book.getMainPicture());
                addAll(book.getPictures()
                           .stream()
                           .map(Picture::getPath)
                           .toList());
            }};

            var bookCategory = book.getCategory();
            String categoryIndex = bookCategory.getIndex();
            String category;
            String subCategory = null;

            if(!categoryIndex.endsWith(SEMICOLON)) {
                String[] splitCategoryIndex = SEMICOLON_PATTERN.split(categoryIndex);

                category = categoryRepository.findNameByIndex(splitCategoryIndex[0] + SEMICOLON);
                subCategory = categoryRepository.findNameByIndex(categoryIndex);

            } else {
                category = bookCategory.getName();
            }

            return Optional.of(new BookDetailsResponse(bookId, book.getTitle(), book.getPrice(), book.getOriginalPrice(),
                                                               book.getQuantity(), book.getPublisher(), authors, book.getNumberOfPages(),
                                                               book.getEdition(), book.getPublicationYear(), book.getDescription(),
                                                               category, subCategory, pictures, book.getCover().toString()));

        } else return Optional.empty();
    }

    public void save(final NewBookRequest request) {
        bookRepository.save(mapToNewBook(request));
    }

    private Book mapToNewBook(final NewBookRequest request) {
        Book book = new Book();
             book.setTitle(request.title());
             book.setPrice(request.price());
             book.setNumberOfPages(request.numberOfPages());
             book.setEdition(request.edition());
             book.setPublicationYear(request.publicationYear());
             book.setDescription(request.description());
             book.setMainPicture(request.mainPicture());
             book.setCover(request.cover());
             book.setQuantity(request.quantity());

        // TODO: set name as unique fields
        publisherRepository.findByName(request.publisher())
                           .ifPresentOrElse(book::setPublisher, () -> book.setPublisher(new Publisher(request.publisher(), book)));

        book.setAuthors(request.authors()
                               .stream()
                               .map(authorRequest -> mapToAuthor(authorRequest, book))
                               .toList());

        book.setPictures(request.pictures()
                                .stream()
                                .map(picture -> new Picture(picture, book))
                                .toList());

        book.setCategory(categoryRepository.findByIndex(request.category())
                                           .orElseThrow());

        return book;
    }

    private Author mapToAuthor(final NewAuthorRequest request, final Book book) {
        return authorRepository.findByName(request.name())
                               .orElse(new Author(request, book));
    }

    public List<Book> collect(final List<Long> booksIds) {
        return (!isEmpty(booksIds)) ? bookRepository.findBooksById(booksIds)
                                    : emptyList();
    }
}

package pl.tiguarces.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.tiguarces.model.Author;
import pl.tiguarces.model.Book;
import pl.tiguarces.repository.AuthorRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorService {
    private final AuthorRepository authorRepository;

    @Transactional(readOnly = true)
    public Optional<AuthorBooksResponse> getAuthorBooks(final long authorId) {
        return authorRepository.findByAuthorId(authorId)
                               .map(AuthorService::map);
    }

    private static AuthorBooksResponse map(final Author author) {
        var books = author.getBooks()
                          .stream()
                          .map(_book -> new Book(_book.getBookId(), _book.getTitle(), _book.getPrice(),
                                                 _book.getOriginalPrice(), _book.getNumberOfStars(), _book.getMainPicture()))
                          .toList();

        return new AuthorBooksResponse(author.getName(), author.getDescription(), author.getPicture(), books);
    }

    public record AuthorBooksResponse(String name, String description, String picture, List<Book> books) {}
}

package pl.tiguarces.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.tiguarces.book.dto.response.BookResponse;
import pl.tiguarces.book.entity.Author;
import pl.tiguarces.book.repository.AuthorRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorService {
    private final AuthorRepository authorRepository;

    @Transactional(readOnly = true)
    public Optional<AuthorBooksResponse> getAuthorBooks(final long authorId) {
        return authorRepository.findByAuthorId(authorId)
                               .map(this::map);
    }

    private AuthorBooksResponse map(final Author author) {
        var books = author.getBooks()
                          .stream()
                          .map(_book -> new BookResponse(_book.getBookId(), _book.getTitle(), _book.getPrice(),
                                                         _book.getOriginalPrice(), _book.getMainPicture()))
                          .toList();

        return new AuthorBooksResponse(author.getName(), author.getDescription(), author.getPicture(), books);
    }

    public List<Author> getAll() {
        return authorRepository.findAllAuthors();
    }

    public record AuthorBooksResponse(String name, String description, String picture, List<BookResponse> books) {}
}

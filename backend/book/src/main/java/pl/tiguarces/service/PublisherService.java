package pl.tiguarces.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.tiguarces.book.dto.response.BookResponse;
import pl.tiguarces.book.entity.Publisher;
import pl.tiguarces.book.repository.PublisherRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PublisherService {
    private final PublisherRepository publisherRepository;

    @Transactional(readOnly = true)
    public Optional<PublisherService.PublisherBooksResponse> findPublisherBooks(final long publisherId) {
        return publisherRepository.findByPublisherId(publisherId)
                                  .map(this::map);
    }

    private PublisherBooksResponse map(final Publisher publisher) {
        var books = publisher.getBooks()
                             .stream()
                             .map(_book -> new BookResponse(_book.getBookId(), _book.getTitle(), _book.getPrice(),
                                                            _book.getOriginalPrice(), _book.getMainPicture()))
                             .toList();

        return new PublisherBooksResponse(publisher.getName(), books);
    }

    public record PublisherBooksResponse(String name, List<BookResponse> books) {}
}

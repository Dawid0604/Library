package pl.tiguarces.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.tiguarces.model.Book;
import pl.tiguarces.model.Publisher;
import pl.tiguarces.repository.PublisherRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PublisherService {
    private final PublisherRepository publisherRepository;

    @Transactional(readOnly = true)
    public Optional<PublisherService.PublisherBooksResponse> getPublisherBooks(final long publisherId) {
        return publisherRepository.findByPublisherId(publisherId)
                                  .map(PublisherService::map);
    }

    private static PublisherBooksResponse map(final Publisher publisher) {
        var books = publisher.getBooks()
                             .stream()
                             .map(_book -> new Book(_book.getBookId(), _book.getTitle(), _book.getPrice(),
                                                    _book.getOriginalPrice(), _book.getNumberOfStars(), _book.getMainPicture()))
                             .toList();

        return new PublisherBooksResponse(publisher.getName(), books);
    }

    public record PublisherBooksResponse(String name, List<Book> books) {}
}

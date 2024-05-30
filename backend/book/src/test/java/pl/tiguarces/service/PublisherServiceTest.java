package pl.tiguarces.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.tiguarces.book.entity.Publisher;
import pl.tiguarces.book.repository.PublisherRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class PublisherServiceTest {
    @Mock private PublisherRepository publisherRepository;
    @InjectMocks private PublisherService publisherService;

    @Test
    void shouldFindPublisherBooks() {
        // Given
        long publisherId = 2;

        given(publisherRepository.findByPublisherId(eq(publisherId)))
                .willReturn(Optional.of(mock(Publisher.class)));

        // When
        var result = publisherService.findPublisherBooks(publisherId);

        // Then
        assertTrue(result.isPresent());
    }
}
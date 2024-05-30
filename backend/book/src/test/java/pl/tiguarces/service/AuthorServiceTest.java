package pl.tiguarces.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.tiguarces.book.entity.Author;
import pl.tiguarces.book.repository.AuthorRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {
    @Mock private AuthorRepository authorRepository;
    @InjectMocks private AuthorService authorService;

    @Test
    void shouldFindAuthorBooks() {
        // Given
        long authorId = 3;

        given(authorRepository.findByAuthorId(eq(authorId)))
                .willReturn(Optional.of(mock(Author.class)));

        // When
        // Then
        assertTrue(authorService.findAuthorBooks(authorId).isPresent());
    }

    @Test
    void shouldFindAll() {
        // Given
        given(authorRepository.findAllAuthors())
                .willReturn(List.of(mock(Author.class)));

        // When
        var result = authorService.findAll();

        // Then
        assertEquals(1, result.size());
    }
}
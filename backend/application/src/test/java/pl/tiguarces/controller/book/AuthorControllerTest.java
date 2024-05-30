package pl.tiguarces.controller.book;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.tiguarces.book.entity.Author;
import pl.tiguarces.service.AuthorService;

import java.util.List;
import java.util.Optional;

import static java.lang.String.valueOf;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("unused")
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class AuthorControllerTest {
    @MockBean private AuthorService authorService;
    @Autowired private MockMvc mockMvc;

    @Test
    void shouldFindAuthorBooks() throws Exception {
        // Given
        long authorId = 5;

        given(authorService.findAuthorBooks(eq(authorId)))
                .willReturn(Optional.of(new AuthorService.AuthorBooksResponse(null, null, null, null)));

        // When
        // Then
        mockMvc.perform(MockMvcRequestBuilders.get(getFullEndpoint("books"))
                                              .queryParam("authorId", valueOf(authorId)))
                .andDo(print())
                .andExpectAll(status().isOk(),
                              jsonPath("$").exists());
    }

    @Test
    void shouldNotFindAuthorBooks() throws Exception {
        // Given
        long authorId = 5;

        // When
        // Then
        mockMvc.perform(MockMvcRequestBuilders.get(getFullEndpoint("books"))
                                              .queryParam("authorId", valueOf(authorId)))
                .andDo(print())
                .andExpectAll(status().isNoContent(),
                              jsonPath("$").doesNotExist());

        verify(authorService).findAuthorBooks(eq(authorId));
    }

    @Test
    void shouldFindAll() throws Exception {
        // Given
        given(authorService.findAll())
                .willReturn(List.of(mock(Author.class)));

        // When
        // Then
        mockMvc.perform(MockMvcRequestBuilders.get(getFullEndpoint("all")))
                .andDo(print())
                .andExpectAll(status().isOk(),
                              jsonPath("$").exists());
    }

    @Test
    void shouldNotFindAll() throws Exception {
        // Given
        // When
        // Then
        mockMvc.perform(MockMvcRequestBuilders.get(getFullEndpoint("all")))
                .andDo(print())
                .andExpectAll(status().isNoContent(),
                              jsonPath("$").doesNotExist());

        verify(authorService).findAll();
    }

    private static String getFullEndpoint(final String endpoint) {
        return "/api/author/" + endpoint;
    }
}
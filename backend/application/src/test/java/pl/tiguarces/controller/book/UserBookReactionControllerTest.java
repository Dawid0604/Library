package pl.tiguarces.controller.book;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.tiguarces.book.dto.request.UserBookReactionRequest;
import pl.tiguarces.book.dto.response.BookReactionsResponse;
import pl.tiguarces.book.dto.response.UserBookReactionResponse;
import pl.tiguarces.book.entity.UserBookReaction;
import pl.tiguarces.service.UserBookReactionService;

import java.util.List;
import java.util.Optional;

import static java.lang.String.valueOf;
import static org.mockito.BDDMockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("unused")
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {
        "LIBRARY_DATABASE=localhost:52000",
        "LIBRARY_USERNAME=suse",
        "LIBRARY_PASSWORD=suse"
})
class UserBookReactionControllerTest {
    @MockBean private UserBookReactionService userBookReactionService;
    @Autowired private MockMvc mockMvc;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldAddNewReaction() throws Exception {
        // Given
        var request = new UserBookReactionRequest(1L, null, "Any note content");

        // When
        // Then
        mockMvc.perform(MockMvcRequestBuilders.post(getFullEndpoint("add"))
                                              .contentType(APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpectAll(status().isCreated(),
                              jsonPath("$").doesNotExist());

        verify(userBookReactionService).addBookReaction(eq(request));
    }

    @Test
    void shouldEditNewReaction() throws Exception {
        // Given
        var request = new UserBookReactionRequest(1L, null, "Any note content");

        // When
        // Then
        mockMvc.perform(MockMvcRequestBuilders.put(getFullEndpoint("edit"))
                                              .contentType(APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpectAll(status().isOk(),
                              jsonPath("$").doesNotExist());

        verify(userBookReactionService).editBookReaction(eq(request));
    }

    @Test
    void shouldDeleteReaction() throws Exception {
        // Given
        long reactionId = 2L;

        // When
        // Then
        mockMvc.perform(MockMvcRequestBuilders.delete(getFullEndpoint("delete"))
                                              .queryParam("id", valueOf(reactionId)))
                .andDo(print())
                .andExpectAll(status().isOk(),
                              jsonPath("$").doesNotExist());
    }

    @Test
    void shouldFindUserReaction() throws Exception {
        // Given
        long bookId = 2;

        given(userBookReactionService.findUserBookReaction(eq(bookId)))
                .willReturn(Optional.of(mock(UserBookReaction.class)));

        // When
        // Then
        mockMvc.perform(MockMvcRequestBuilders.get(getFullEndpoint("get"))
                                              .queryParam("bookId", valueOf(bookId)))
                .andDo(print())
                .andExpectAll(status().isOk(),
                              jsonPath("$").isNotEmpty());
    }

    @Test
    void shouldNotFindUserReaction() throws Exception {
        // Given
        long bookId = 2;

        // When
        // Then
        mockMvc.perform(MockMvcRequestBuilders.get(getFullEndpoint("get"))
                                              .queryParam("bookId", valueOf(bookId)))
                .andDo(print())
                .andExpectAll(status().isNoContent(),
                              jsonPath("$").doesNotExist());

        verify(userBookReactionService).findUserBookReaction(eq(bookId));
    }

    @Test
    void shouldFindAllBookReactions() throws Exception {
        // Given
        long bookId = 2;
        boolean comments = true;
        String sort = "DESC";
        int page = 1,
            size = 25;

        given(userBookReactionService.findBookReactions(eq(page), eq(size), eq(comments), eq(sort), eq(bookId)))
                .willReturn(new BookReactionsResponse(null, null, null));

        // When
        // Then
        mockMvc.perform(MockMvcRequestBuilders.get(getFullEndpoint("get-all"))
                                              .queryParam("bookId", valueOf(bookId))
                                              .queryParam("comments", valueOf(comments))
                                              .queryParam("sort", sort)
                                              .queryParam("page", valueOf(page))
                                              .queryParam("size", valueOf(size)))
                .andDo(print())
                .andExpectAll(status().isOk(),
                              jsonPath("$").exists());
    }

    @Test
    void shouldFindAllBookReactionsWithDefaultValues() throws Exception {
        // Given
        long bookId = 2;
        int page = 1,
            size = 25;

        given(userBookReactionService.findBookReactions(eq(page), eq(size), eq(true), eq("DESC"), eq(bookId)))
                .willReturn(new BookReactionsResponse(null, null, null));

        // When
        // Then
        mockMvc.perform(MockMvcRequestBuilders.get(getFullEndpoint("get-all"))
                                              .queryParam("bookId", valueOf(bookId))
                                              .queryParam("page", valueOf(page))
                                              .queryParam("size", valueOf(size)))
                .andDo(print())
                .andExpectAll(status().isOk(),
                              jsonPath("$").exists());
    }

    @Test
    void shouldFindUserReactions() throws Exception {
        // Given
        given(userBookReactionService.findUserReactions())
                .willReturn(List.of(new UserBookReactionResponse(1L, null, 1, null, null)));

        // When
        // Then
        mockMvc.perform(MockMvcRequestBuilders.get(getFullEndpoint("/user/get-all")))
               .andDo(print())
               .andExpectAll(status().isOk(),
                             jsonPath("$").isNotEmpty());
    }

    @Test
    void shouldNotFindUserReactions() throws Exception {
        // Given
        // When
        // Then
        mockMvc.perform(MockMvcRequestBuilders.get(getFullEndpoint("/user/get-all")))
               .andDo(print())
               .andExpectAll(status().isNoContent(),
                             jsonPath("$").isEmpty());

        verify(userBookReactionService).findUserReactions();
    }

    private static String getFullEndpoint(final String endpoint) {
        return "/api/reaction/" + endpoint;
    }
}
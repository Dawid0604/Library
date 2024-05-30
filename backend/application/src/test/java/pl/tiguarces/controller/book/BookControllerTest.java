package pl.tiguarces.controller.book;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.tiguarces.book.dto.request.NewBookRequest;
import pl.tiguarces.book.dto.request.SearchBookRequest;
import pl.tiguarces.book.dto.response.BookDetailsResponse;
import pl.tiguarces.book.entity.BookCover;
import pl.tiguarces.service.AppUserService;
import pl.tiguarces.service.BookService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
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
class BookControllerTest {
    @MockBean private AppUserService appUserService;
    @MockBean private BookService bookService;
    @Autowired private MockMvc mockMvc;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldFindAll() throws Exception {
        // Given
        var request = new SearchBookRequest(1, "anyCategory", 55.5, null, null, 1500,
                                            2011, 2024, BookCover.SOFT, "anyTitle");

        given(bookService.findAll(eq(request)))
                .willReturn(new PageImpl<>(List.of()));

        // When
        // Then
        mockMvc.perform(MockMvcRequestBuilders.post(getFullEndpoint("fetch/all"))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpectAll(status().isOk(),
                              jsonPath("$").exists());

        verify(bookService).findAll(eq(request));
    }

    @Test
    void shouldFindBookDetails() throws Exception {
        // Given
        long bookId = 15;

        given(bookService.findById(eq(bookId)))
                .willReturn(Optional.of(BookDetailsResponse.getEmptyObject()));

        // When
        // Then
        mockMvc.perform(MockMvcRequestBuilders.get(getFullEndpoint("fetch/{bookId}"), bookId))
                .andDo(print())
                .andExpectAll(status().isOk(),
                              jsonPath("$").exists());
    }

    @Test
    void shouldNotFindBookDetails() throws Exception {
        // Given
        long bookId = 15;

        // When
        // Then
        mockMvc.perform(MockMvcRequestBuilders.get(getFullEndpoint("fetch/{bookId}"), bookId))
                .andDo(print())
                .andExpectAll(status().isNoContent(),
                              jsonPath("$").doesNotExist());

        verify(bookService).findById(eq(bookId));
    }

    @Test
    void shouldSaveBook() throws Exception {
        // given
        // When
        // Then
        mockMvc.perform(MockMvcRequestBuilders.post(getFullEndpoint("create"))
                                              .contentType(APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsString(NewBookRequest.getEmptyOpject())))
                .andDo(print())
                .andExpectAll(status().isCreated());
    }

    private static String getFullEndpoint(final String endpoint) {
        return "/api/book/" + endpoint;
    }
}
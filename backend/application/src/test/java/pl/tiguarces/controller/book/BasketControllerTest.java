package pl.tiguarces.controller.book;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.tiguarces.book.entity.Book;
import pl.tiguarces.service.BookService;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("unused")
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class BasketControllerTest {
    @MockBean private BookService bookService;
    @Autowired private MockMvc mockMvc;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldFetchBasket() throws Exception {
        // Given
        var ids = List.of(1L, 2L, 3L);

        given(bookService.findAllByIds(eq(ids)))
                .willReturn(List.of(mock(Book.class)));

        // When
        // Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/basket/fetch")
                                              .contentType(APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsString(ids)))
                .andDo(print())
                .andExpectAll(status().isOk(),
                              jsonPath("$").exists());
    }

    @Test
    void shouldNotFetchBasket() throws Exception {
        // Given
        var ids = List.of(1L, 2L, 3L);

        // When
        // Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/basket/fetch")
                                              .contentType(APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsString(ids)))
                .andDo(print())
                .andExpectAll(status().isNoContent(),
                              jsonPath("$").doesNotExist());

        verify(bookService).findAllByIds(eq(ids));
    }
}
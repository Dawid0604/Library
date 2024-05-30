package pl.tiguarces.controller.book;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.tiguarces.service.PublisherService;
import pl.tiguarces.service.PublisherService.PublisherBooksResponse;

import java.util.Optional;

import static java.lang.String.valueOf;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("unused")
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class PublisherControllerTest {
    @MockBean private PublisherService publisherService;
    @Autowired private MockMvc mockMvc;

    @Test
    void shouldFindPublisherBooks() throws Exception {
        // Given
        long publisherId = 2;

        given(publisherService.findPublisherBooks(eq(publisherId)))
                .willReturn(Optional.of(new PublisherBooksResponse(null, null)));

        // When
        // Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/publisher/books")
                                              .queryParam("publisherId", valueOf(publisherId)))
                .andDo(print())
                .andExpectAll(status().isOk(),
                              jsonPath("$").isNotEmpty());
    }

    @Test
    void shouldNotFindPublisherBooks() throws Exception {
        // Given
        long publisherId = 2;

        // When
        // Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/publisher/books")
                                              .queryParam("publisherId", valueOf(publisherId)))
                .andDo(print())
                .andExpectAll(status().isNoContent(),
                              jsonPath("$").doesNotExist());

        verify(publisherService).findPublisherBooks(eq(publisherId));
    }
}
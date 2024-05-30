package pl.tiguarces.controller.book;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.tiguarces.book.entity.Category;
import pl.tiguarces.service.CategoryService;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
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
class CategoryControllerTest {
    @MockBean private CategoryService categoryService;
    @Autowired private MockMvc mockMvc;

    @Test
    void shouldFetchCategories() throws Exception {
        // Given
        given(categoryService.findCategories())
                .willReturn(List.of(mock(Category.class)));

        // When
        // Then
        mockMvc.perform(MockMvcRequestBuilders.get(getFullEndpoint("fetch")))
                .andDo(print())
                .andExpectAll(status().isOk(),
                              jsonPath("$").exists());
    }

    @Test
    void shouldNotFetchCategories() throws Exception {
        // Given
        // When
        // Then
        mockMvc.perform(MockMvcRequestBuilders.get(getFullEndpoint("fetch")))
                .andDo(print())
                .andExpectAll(status().isNoContent(),
                              jsonPath("$").isEmpty());

        verify(categoryService).findCategories();
    }

    @Test
    void shouldFetchSubCategories() throws Exception {
        // Given
        String parentCategory = "1;";

        given(categoryService.findSubCategories(eq(parentCategory)))
                .willReturn(List.of(mock(Category.class)));

        // When
        // Then
        mockMvc.perform(MockMvcRequestBuilders.get(getFullEndpoint("fetch/sub"))
                                              .queryParam("parent", parentCategory))
                .andDo(print())
                .andExpectAll(status().isOk(),
                              jsonPath("$").exists());
    }

    @Test
    void shouldNotFetchSubCategories() throws Exception {
        // Given
        String parentCategory = "1;";

        // When
        // Then
        mockMvc.perform(MockMvcRequestBuilders.get(getFullEndpoint("fetch/sub"))
                                              .queryParam("parent", parentCategory))
                .andDo(print())
                .andExpectAll(status().isNoContent(),
                              jsonPath("$").isEmpty());

        verify(categoryService).findSubCategories(eq(parentCategory));
    }

    private static String getFullEndpoint(final String endpoint) {
        return "/api/category/" + endpoint;
    }
}
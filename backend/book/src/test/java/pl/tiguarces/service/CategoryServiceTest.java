package pl.tiguarces.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.tiguarces.book.entity.Category;
import pl.tiguarces.book.repository.CategoryRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    @Mock private CategoryRepository categoryRepository;
    @InjectMocks private CategoryService categoryService;

    @Test
    void shouldFindCategories() {
        // Given
        given(categoryRepository.findAllParentsWithoutBooks())
                .willReturn(List.of(mock(Category.class)));

        // When
        var result = categoryService.findCategories();

        // Then
        assertEquals(1, result.size());
    }

    @Test
    void shouldFindSubCategories() {
        // Given
        String parentCategory = "1;";

        given(categoryRepository.findAllSubCategoriesWithoutBooks(eq(parentCategory)))
                .willReturn(List.of(mock(Category.class)));

        // When
        var result = categoryService.findSubCategories(parentCategory);

        // Then
        assertEquals(1, result.size());
    }
}
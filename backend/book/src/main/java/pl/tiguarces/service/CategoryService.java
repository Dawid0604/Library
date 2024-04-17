package pl.tiguarces.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.tiguarces.model.Category;
import pl.tiguarces.repository.CategoryRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<Category> getCategories() {
        return categoryRepository.findAllParentsWithoutBooks();
    }

    public List<Category> getSubCategories(final String parentCategory) {
        return categoryRepository.findAllSubCategoriesWithoutBooks(parentCategory);
    }
}

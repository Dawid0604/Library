package pl.tiguarces.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.tiguarces.book.entity.Category;
import pl.tiguarces.book.repository.CategoryRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<Category> findCategories() {
        return categoryRepository.findAllParentsWithoutBooks();
    }

    @Transactional(readOnly = true)
    public List<Category> findSubCategories(final String parentCategory) {
        return categoryRepository.findAllSubCategoriesWithoutBooks(parentCategory);
    }
}

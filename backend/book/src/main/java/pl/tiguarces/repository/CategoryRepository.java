package pl.tiguarces.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.tiguarces.model.Category;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Transactional(readOnly = true)
    @Query("SELECT new pl.tiguarces.model.Category(c.categoryId, c.name, c.index) FROM Category c WHERE c.index LIKE CONCAT(:parentCategory, '%') AND c.index NOT LIKE '%;'")
    List<Category> findAllSubCategoriesWithoutBooks(@Param("parentCategory") String parentCategory);

    @Transactional(readOnly = true)
    @Query("SELECT new pl.tiguarces.model.Category(c.categoryId, c.name, c.index) FROM Category c WHERE c.index LIKE '%;'")
    List<Category> findAllParentsWithoutBooks();

    @Transactional(readOnly = true)
    @Query("SELECT c.name FROM Category c WHERE c.index = :index")
    String findNameByIndex(String index);

    Optional<Category> findByIndex(String category);
}

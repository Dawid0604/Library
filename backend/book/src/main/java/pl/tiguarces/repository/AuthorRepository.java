package pl.tiguarces.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.tiguarces.model.Author;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    @Transactional(readOnly = true)
    Optional<Author> findByName(String name);

    Optional<Author> findByAuthorId(long authorId);

    @Transactional(readOnly = true)
    @Query("SELECT new pl.tiguarces.model.Author(a.authorId, a.name) FROM Author a")
    List<Author> findAllAuthors();
}

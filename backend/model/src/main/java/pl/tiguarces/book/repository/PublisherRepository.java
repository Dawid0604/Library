package pl.tiguarces.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.tiguarces.book.entity.Publisher;

import java.util.Optional;

@Repository
public interface PublisherRepository extends JpaRepository<Publisher, Long> {
    @Transactional(readOnly = true)
    Optional<Publisher> findByName(String name);

    Optional<Publisher> findByPublisherId(long publisherId);
}

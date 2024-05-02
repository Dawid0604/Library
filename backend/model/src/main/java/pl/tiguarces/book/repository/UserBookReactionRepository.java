package pl.tiguarces.book.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.tiguarces.book.entity.UserBookReaction;

import java.util.Optional;

@Repository
public interface UserBookReactionRepository extends JpaRepository<UserBookReaction, Long> {
    Optional<UserBookReaction> findByUserUserIdAndBookBookId(long userId, long bookId);

    @Query("""
        SELECT new pl.tiguarces.book.entity.UserBookReaction(r.reactionId, r.numberOfStars, r.comment, r.date, r.user)
        FROM UserBookReaction r WHERE r.book.bookId = :bookId AND (:comments = TRUE OR r.comment IS NULL)
    """)
    Page<UserBookReaction> findBookReactions(long bookId, boolean comments, Pageable pageable);

    @Query("SELECT COUNT(b) FROM UserBookReaction b WHERE b.book.bookId = :bookId AND b.numberOfStars = :numberOfStars")
    int countNumberOfStars(long bookId, int numberOfStars);

    @Query("SELECT COUNT(b) FROM UserBookReaction b WHERE b.book.bookId = :bookId AND b.comment IS NOT NULL")
    int countComments(long bookId);
}

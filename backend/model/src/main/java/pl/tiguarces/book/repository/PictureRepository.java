package pl.tiguarces.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.tiguarces.book.entity.Picture;

@Repository
public interface PictureRepository extends JpaRepository<Picture, Long> { }

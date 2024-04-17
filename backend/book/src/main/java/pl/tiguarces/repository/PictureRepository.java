package pl.tiguarces.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.tiguarces.model.Picture;

@Repository
public interface PictureRepository extends JpaRepository<Picture, Long> { }

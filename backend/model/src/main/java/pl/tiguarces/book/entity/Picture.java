package pl.tiguarces.book.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Pictures")
@EqualsAndHashCode(exclude = "book")
public class Picture {

    @Id
    @Column(name = "PictureId")
    @GeneratedValue(strategy = IDENTITY)
    private Long pictureId;

    @Column(name = "Path")
    private String path;

    @JsonIgnore
    @JoinColumn(name = "BookId")
    @ManyToOne(fetch = LAZY)
    private Book book;

    public Picture(final String path, final Book book) {
        this.path = path;
        this.book = book;
    }
}

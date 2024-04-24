package pl.tiguarces.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import pl.tiguarces.controller.request.NewAuthorRequest;

import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Authors")
@EqualsAndHashCode(exclude = "books")
public class Author {

    @Id
    @Column(name = "AuthorId")
    @GeneratedValue(strategy = IDENTITY)
    private Long authorId;

    @Column(name = "Name")
    private String name;

    @Column(name = "Description")
    private String description;

    @Column(name = "Picture")
    private String picture;

    @JsonIgnore
    @ManyToMany(mappedBy = "authors")
    private List<Book> books;

    public Author(final NewAuthorRequest authorRequest, final Book book) {
        this.name = authorRequest.name();
        this.description = authorRequest.description();
        this.picture = authorRequest.picture();
        this.books = List.of(book);
    }

    @SuppressWarnings("unused")
    public Author(final Long authorId, final String name, final String description, final String picture) {
        this.authorId = authorId;
        this.name = name;
        this.description = description;
        this.picture = picture;
    }

    @SuppressWarnings("unused")
    public Author(final Long authorId, final String name) {
        this.authorId = authorId;
        this.name = name;
    }
}

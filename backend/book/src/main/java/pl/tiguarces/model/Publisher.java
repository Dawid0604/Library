package pl.tiguarces.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Publishers")
@EqualsAndHashCode(exclude = "books")
public class Publisher {

    @Id
    @Column(name = "PublisherId")
    @GeneratedValue(strategy = IDENTITY)
    private Long publisherId;

    @Column(name = "Name")
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "publisher", orphanRemoval = true)
    private List<Book> books;

    public Publisher(final String name, final Book book) {
        this.name = name;
        this.books = List.of(book);
    }
}

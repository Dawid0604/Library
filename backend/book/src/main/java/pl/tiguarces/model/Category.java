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
@Table(name = "Categories")
@EqualsAndHashCode(exclude = "books")
public class Category {

    @Id
    @Column(name = "CategoryId")
    @GeneratedValue(strategy = IDENTITY)
    private Long categoryId;

    @Column(name = "Name", nullable = false)
    private String name;

    @Column(name = "CategoryIndex", nullable = false)
    private String index;

    @JsonIgnore
    @OneToMany(orphanRemoval = true, mappedBy = "category")
    private List<Book> books;

    @SuppressWarnings("unused")
    public Category(final Long categoryId, final String name, final String index) {
        this.categoryId = categoryId;
        this.name = name;
        this.index = index;
    }
}

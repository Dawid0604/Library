package pl.tiguarces.book.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Books")
@EqualsAndHashCode(exclude = { "authors", "publisher", "category", "pictures" })
public class Book {

    @Id
    @Column(name = "BookId")
    @GeneratedValue(strategy = IDENTITY)
    private Long bookId;

    @Column(name = "Title")
    private String title;

    @Column(name = "Price")
    private Double price;

    @Column(name = "OriginalPrice")
    private Double originalPrice;

    @Column(name = "Quantity")
    private int quantity;

    @ManyToMany(cascade = { MERGE, PERSIST })
    @JoinTable(
            name = "AuthorsBooks",
            joinColumns = @JoinColumn(name = "BookId"),
            inverseJoinColumns = @JoinColumn(name = "AuthorId"))
    private List<Author> authors;

    @JoinColumn(name = "PublisherId")
    @ManyToOne(cascade = { MERGE, PERSIST })
    private Publisher publisher;

    @Column(name = "NumberOfPages")
    private int numberOfPages;

    @Column(name = "Edition")
    private int edition;

    @Column(name = "PublicationYear")
    private Integer publicationYear;

    @Column(name = "Description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "CategoryId")
    private Category category;

    @Column(name = "MainPicture")
    private String mainPicture;

    @OneToMany(mappedBy = "book", cascade = { MERGE, PERSIST }, orphanRemoval = true)
    private List<Picture> pictures;

    @Enumerated(STRING)
    @Column(name = "BookCover")
    private BookCover cover;

    @OneToMany(mappedBy = "book", orphanRemoval = true)
    private List<UserBookReaction> reactions;

    @SuppressWarnings("unused")
    public Book(final Long bookId, final String title, final Double price,
                final Double originalPrice, final String mainPicture) {

        this.bookId = bookId;
        this.title = title;
        this.price = price;
        this.originalPrice = originalPrice;
        this.mainPicture = mainPicture;
    }
}

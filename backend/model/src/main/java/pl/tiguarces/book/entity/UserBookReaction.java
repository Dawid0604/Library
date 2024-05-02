package pl.tiguarces.book.entity;

import jakarta.persistence.*;
import lombok.*;
import pl.tiguarces.user.entity.AppUser;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "UserReactions")
@EqualsAndHashCode(exclude = { "book", "user" })
public class UserBookReaction {

    @Id
    @Column(name = "ReactionId")
    @GeneratedValue(strategy = IDENTITY)
    private Long reactionId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "BookId")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "UserId")
    private AppUser user;

    @Column(name = "NumberOfStars")
    private int numberOfStars;

    @Column(name = "Comment")
    private String comment;

    @Column(name = "DateAdded")
    private LocalDateTime date;

    @SuppressWarnings("unused")
    public UserBookReaction(final long reactionId, final int numberOfStars,
                            final String comment, final LocalDateTime date,
                            final AppUser user) {

        this.reactionId = reactionId;
        this.numberOfStars = numberOfStars;
        this.comment = comment;
        this.date = date;
        this.user = user;
    }
}

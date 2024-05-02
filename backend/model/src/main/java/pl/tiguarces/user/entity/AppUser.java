package pl.tiguarces.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import pl.tiguarces.book.entity.UserBookReaction;

import java.util.Collection;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Users")
@EqualsAndHashCode(exclude = "bookReactions")
public class AppUser {

    @Id
    @Column(name = "UserId")
    @GeneratedValue(strategy = IDENTITY)
    private Long userId;

    @Column(name = "Username")
    private String username;

    @Column(name = "Password")
    private String password;

    @Column(name = "Roles")
    private String roles;

    @Column(name = "Avatar")
    private String avatar;

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<UserBookReaction> bookReactions;

    public AppUser(final String username, final String password) {
        this.username = username;
        this.password = password;
        this.roles = "ROLE_USER";
    }

    public AppUser(final String username, final String password, final Collection<? extends GrantedAuthority> roles) {
        this.username = username;
        this.password = password;
        this.roles = String.join(", ", roles.stream()
                                                    .map(GrantedAuthority::getAuthority)
                                                    .toList());
    }
}

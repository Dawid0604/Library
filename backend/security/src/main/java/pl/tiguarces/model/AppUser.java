package pl.tiguarces.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "Users")
public class AppUser {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long userId;

    @Column(name = "Username", nullable = false)
    private String username;

    @Column(name = "Password", nullable = false)
    private String password;

    @Column(name = "Roles", nullable = false)
    private String roles;

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

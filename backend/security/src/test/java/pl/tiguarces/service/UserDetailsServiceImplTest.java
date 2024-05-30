package pl.tiguarces.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pl.tiguarces.user.entity.AppUser;
import pl.tiguarces.user.repository.AppUserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {
    @Mock private AppUserRepository appUserRepository;
    @InjectMocks private UserDetailsServiceImpl userDetailsService;

    @Test
    void shouldLoadByUsername() {
        // Given
        String username = "xyz";
        String role = "ROLE_USER";

        AppUser user = AppUser.builder()
                              .username(username)
                              .avatar("xyz.png")
                              .roles(role)
                              .password("zyx")
                              .build();

        given(appUserRepository.findByUsername(eq(username)))
                .willReturn(Optional.of(user));

        // When
        var result = userDetailsService.loadUserByUsername(username);

        // Then
        assertEquals(username, result.getUsername());
        assertTrue(result.getAuthorities().size() == 1 && result.getAuthorities().contains(new SimpleGrantedAuthority(role)));
        assertTrue(new BCryptPasswordEncoder().matches(user.getPassword(), result.getPassword()));
    }

    @Test
    void shouldNotLoadByUsernameAndThrowExceptionWhenUserNotFound() {
        // Given
        String username = "xyz";

        // When
        // Then
        assertThat(catchThrowable(() -> userDetailsService.loadUserByUsername(username)))
                                                          .isInstanceOf(UsernameNotFoundException.class)
                                                          .hasMessage("User with username '%s' not found".formatted(username));
    }

    @Test
    void shouldNotLoadByUsernameAndThrowExceptionWhenUserRolesAreNotPresent() {
        // Given
        String username = "xyz";

        AppUser user = AppUser.builder()
                              .username(username)
                              .avatar("xyz.png")
                              .password("zyx")
                              .build();

        given(appUserRepository.findByUsername(eq(username)))
                .willReturn(Optional.of(user));

        // When
        // Then
        assertThat(catchThrowable(() -> userDetailsService.loadUserByUsername(username)))
                                                          .isInstanceOf(IllegalArgumentException.class)
                                                          .hasMessage("User roles cannot be empty!");
    }
}
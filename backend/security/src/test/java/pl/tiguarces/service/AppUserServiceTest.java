package pl.tiguarces.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.tiguarces.JwtUtils;
import pl.tiguarces.book.dto.request.UpdateUserRequest;
import pl.tiguarces.user.dto.UserPayload;
import pl.tiguarces.user.entity.AppUser;
import pl.tiguarces.user.repository.AppUserRepository;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;

@ExtendWith(MockitoExtension.class)
class AppUserServiceTest {
    @Mock private AppUserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtUtils jwtUtils;
    @InjectMocks private AppUserService appUserService;

    @Test
    void shouldGetLoggedUser() {
        // Given
        var securityContext = mock(SecurityContext.class);
        var authentication = mock(Authentication.class);
        var loggedUser = new User("xyz", "yxz", List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.setContext(securityContext);

        given(securityContext.getAuthentication())
                .willReturn(authentication);

        given(authentication.getPrincipal())
                .willReturn(loggedUser);

        // When
        var result = appUserService.getLoggedUser();

        // Then
        assertEquals(loggedUser.getUsername(), result.getUsername());
        assertEquals(loggedUser.getPassword(), result.getPassword());
        assertEquals(loggedUser.getAuthorities().toArray(GrantedAuthority[]::new)[0].getAuthority(), result.getRoles());
    }

    @Test
    void shouldGetLoggedUserFromDb() {
        // Given
        var securityContext = mock(SecurityContext.class);
        var authentication = mock(Authentication.class);
        var loggedUser = new User("xyz", "yxz", List.of(new SimpleGrantedAuthority("ROLE_USER")));
        var userFromDb = AppUser.builder()
                                .userId(1L)
                                .roles("ROLE_USER")
                                .username(loggedUser.getUsername())
                                .password(loggedUser.getPassword())
                                .build();

        SecurityContextHolder.setContext(securityContext);

        given(securityContext.getAuthentication())
                .willReturn(authentication);

        given(authentication.getPrincipal())
                .willReturn(loggedUser);

        given(userRepository.findByUsername(eq(loggedUser.getUsername())))
                .willReturn(Optional.of(userFromDb));

        // When
        var result = appUserService.getLoggedUserFromDb();

        // Then
        assertEquals(userFromDb, result);
    }

    @Test
    void shouldNotGetLoggedUserFromDbAndThrowException() {
        // Given
        var securityContext = mock(SecurityContext.class);
        var authentication = mock(Authentication.class);
        var loggedUser = new User("xyz", "yxz", List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.setContext(securityContext);

        given(securityContext.getAuthentication())
                .willReturn(authentication);

        given(authentication.getPrincipal())
                .willReturn(loggedUser);

        // When
        // Then
        assertThat(catchThrowable(() -> appUserService.getLoggedUserFromDb()))
                                                      .isInstanceOf(NoSuchElementException.class);

        verify(userRepository).findByUsername(eq(loggedUser.getUsername()));
    }

    @Test
    void shouldRegisterUser() {
        // Given
        ArgumentCaptor<AppUser> argumentCaptor = ArgumentCaptor.forClass(AppUser.class);
        String encodedPassword = "encodedPassword";
        var payload = new UserPayload("xyz", "yxz");

        given(passwordEncoder.encode(eq(payload.password())))
                .willReturn(encodedPassword);

        // When
        var result = appUserService.register(payload);

        // Then
        verify(userRepository).existsByUsername(eq(payload.username()));
        verify(userRepository).save(argumentCaptor.capture());
        assertEquals(OK, result.getStatusCode());
        assertTrue(() -> {
            var _savedUser = argumentCaptor.getValue();
            return Objects.equals(_savedUser.getUsername(), payload.username()) &&
                   Objects.equals(_savedUser.getPassword(), encodedPassword);
        });
    }

    @Test
    void shouldNotRegisterWhenUsernameExists() {
        // Given
        var payload = new UserPayload("xyz", "yxz");

        given(userRepository.existsByUsername(eq(payload.username())))
                .willReturn(true);

        // When
        var result = appUserService.register(payload);

        // Then
        verify(userRepository).existsByUsername(eq(payload.username()));
        verify(userRepository, never()).save(any());
        assertEquals(BAD_REQUEST, result.getStatusCode());
    }

    @Test
    void shouldGenerateTokens() {
        // Given
        var payload = new UserPayload("syz", "yxs");
        var generatedTokens = Map.of("x", "y", "z", "d");
        var userFromDb = AppUser.builder()
                                .userId(1L)
                                .roles("ROLE_USER")
                                .username(payload.username())
                                .password(payload.password())
                                .build();

        given(userRepository.findByUsername(eq(payload.username())))
                .willReturn(Optional.of(userFromDb));

        given(passwordEncoder.matches(eq(payload.password()), eq(userFromDb.getPassword())))
                .willReturn(true);

        given(jwtUtils.generateTokens(payload.username()))
                .willReturn(generatedTokens);

        // When
        var result = appUserService.generateTokens(payload);

        // Then
        verify(authenticationManager).authenticate(eq(new UsernamePasswordAuthenticationToken(payload.username(), payload.password())));
        assertEquals(generatedTokens, result.getBody());
        assertEquals(OK, result.getStatusCode());
    }

    @Test
    void shouldNotGenerateTokensWhenPasswordNotMatches() {
        // Given
        var payload = new UserPayload("syz", "yxs");
        var userFromDb = AppUser.builder()
                                .userId(1L)
                                .roles("ROLE_USER")
                                .username(payload.username())
                                .password(payload.password())
                                .build();

        given(userRepository.findByUsername(eq(payload.username())))
                .willReturn(Optional.of(userFromDb));

        // When
        // Then
        verifyNoInteractions(authenticationManager, jwtUtils);
        assertThat(catchThrowable(() -> appUserService.generateTokens(payload)))
                                                      .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void shouldNotGenerateTokensWhenUserNotFound() {
        // Given
        var payload = new UserPayload("syz", "yxs");

        // When
        var result = appUserService.generateTokens(payload);

        // Then
        verifyNoInteractions(authenticationManager, passwordEncoder, jwtUtils);
        assertEquals(NOT_FOUND, result.getStatusCode());
    }

    @Test
    void shouldRefreshTokens() {
        // Given
        String refreshToken = "xyz";
        var refreshedTokens = Map.of("x", "y", "z", "d");

        given(jwtUtils.refreshTokens(eq(refreshToken)))
                .willReturn(refreshedTokens);

        // When
        var result = appUserService.refreshToken(refreshToken);

        // Then
        assertEquals(refreshedTokens, result);
    }

    @Test
    void shouldFindById() {
        // Given
        var userFromDb = AppUser.builder()
                                .userId(1L)
                                .roles("ROLE_USER")
                                .username("xyz")
                                .password("xyz")
                                .build();

        given(userRepository.findById(eq(userFromDb.getUserId())))
                .willReturn(Optional.of(userFromDb));

        // When
        var result = appUserService.findById(userFromDb.getUserId());

        // Then
        assertEquals(userFromDb, result);
    }

    @Test
    void shouldNotFindByIdAndThrowException() {
        // Given
        long userId = 1;

        // When
        // Then
        assertThat(catchThrowable(() -> appUserService.findById(userId)))
                                                      .isInstanceOf(NoSuchElementException.class);
        verify(userRepository).findById(eq(userId));
    }

    @Test
    void shouldDeleteAccount() {
        // Given
        String username = "Xyz";
        var securityContext = mock(SecurityContext.class);
        var authentication = mock(Authentication.class);

        var loggedUser = new User(username, "yxz", List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.setContext(securityContext);

        given(securityContext.getAuthentication())
                .willReturn(authentication);

        given(authentication.getPrincipal())
                .willReturn(loggedUser);

        // When
        appUserService.deleteAccount();

        // Then
        verify(userRepository).deleteByUsername(eq(username));
    }

    @Test
    void shouldUpdate() {
        // Given
        var request = new UpdateUserRequest("newXyz", "newImage.png");
        var securityContext = mock(SecurityContext.class);
        var authentication = mock(Authentication.class);
        var loggedUser = new User("xyz", "yxz", List.of(new SimpleGrantedAuthority("ROLE_USER")));
        var userFromDb = AppUser.builder()
                                .userId(1L)
                                .roles("ROLE_USER")
                                .username(loggedUser.getUsername())
                                .password(loggedUser.getPassword())
                                .build();

        SecurityContextHolder.setContext(securityContext);

        given(securityContext.getAuthentication())
                .willReturn(authentication);

        given(authentication.getPrincipal())
                .willReturn(loggedUser);

        given(userRepository.findByUsername(eq(loggedUser.getUsername())))
                .willReturn(Optional.of(userFromDb));

        given(passwordEncoder.encode(eq(request.password())))
                .willReturn(request.password());

        // When
        appUserService.update(request);

        // Then
        assertEquals(request.password(), userFromDb.getPassword());
        assertEquals(request.avatar(), userFromDb.getAvatar());
        verify(userRepository).save(eq(userFromDb));
    }

    @Test
    void shouldNotUpdate() {
        // Given
        String samePassword = "XYZ";
        String sameAvatar = "XZZ";

        var request = new UpdateUserRequest(samePassword, sameAvatar);
        var securityContext = mock(SecurityContext.class);
        var authentication = mock(Authentication.class);
        var loggedUser = new User("xyz", samePassword, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        var userFromDb = AppUser.builder()
                                .userId(1L)
                                .roles("ROLE_USER")
                                .username(loggedUser.getUsername())
                                .password(loggedUser.getPassword())
                                .avatar(sameAvatar)
                                .build();

        SecurityContextHolder.setContext(securityContext);

        given(securityContext.getAuthentication())
                .willReturn(authentication);

        given(authentication.getPrincipal())
                .willReturn(loggedUser);

        given(userRepository.findByUsername(eq(loggedUser.getUsername())))
                .willReturn(Optional.of(userFromDb));

        given(passwordEncoder.matches(eq(loggedUser.getPassword()), eq(request.password())))
                .willReturn(true);

        // When
        appUserService.update(request);

        // Then
        assertEquals(samePassword, userFromDb.getPassword());
        assertEquals(sameAvatar, userFromDb.getAvatar());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any());
    }
}
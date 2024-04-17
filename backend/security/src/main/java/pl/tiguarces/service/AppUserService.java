package pl.tiguarces.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.tiguarces.JwtUtils;
import pl.tiguarces.controller.model.UserPayload;
import pl.tiguarces.model.AppUser;
import pl.tiguarces.repository.AppUserRepository;

import java.util.Map;

import static org.springframework.http.HttpStatus.*;
import static pl.tiguarces.Constants.MESSAGE_KEY;

@Service
@RequiredArgsConstructor
public class AppUserService {
    private final AppUserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AppUser getLoggedUser() {
        Object userDetails = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return (userDetails instanceof final User user)
                 ? new AppUser(user.getUsername(), user.getPassword(), user.getAuthorities())
                 : null;
    }

    public ResponseEntity<?> register(final UserPayload payload) {
        if(repository.existsByUsername(payload.username())) {
            return new ResponseEntity<>("User with username '%s' exists".formatted(payload.username()), BAD_REQUEST);
        }

        repository.save(new AppUser(payload.username(), passwordEncoder.encode(payload.password())));
        return new ResponseEntity<>(OK);
    }

    public ResponseEntity<?> generateTokens(final UserPayload payload) {
        var user = repository.findByUsername(payload.username());

        if(user.isEmpty()) {
            return new ResponseEntity<>(Map.of(MESSAGE_KEY, "User not found"), NOT_FOUND);
        }

        String userPassword = user.get()
                                  .getPassword();

        if(!passwordEncoder.matches(payload.password(), userPassword)) {
            throw new BadCredentialsException("Invalid credentials");
        }

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(payload.username(), userPassword));
        return new ResponseEntity<>(jwtUtils.generateTokens(payload.username()), OK);
    }

    public Map<String, String> refreshToken(final String refreshToken) {
        return jwtUtils.refreshTokens(refreshToken);
    }
}

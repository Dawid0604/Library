package pl.tiguarces.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.tiguarces.service.AppUserService;
import pl.tiguarces.user.dto.UserPayload;

import java.util.Map;

import static java.util.Objects.requireNonNull;
import static pl.tiguarces.JwtUtils.REFRESH_TOKEN;

@RestController
@RequiredArgsConstructor
@SuppressWarnings("unused")
@RequestMapping("/api/auth")
public class AuthorizationController {
    private final AppUserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody final UserPayload payload) {
        return userService.generateTokens(payload);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody final UserPayload payload) {
        return userService.register(payload);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshTokens(@RequestBody final Map<String, String> refreshToken) {
        return ResponseEntity.ok(userService.refreshToken(requireNonNull(refreshToken.get(REFRESH_TOKEN))));
    }
}

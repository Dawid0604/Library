package pl.tiguarces.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.tiguarces.service.AppUserService;
import pl.tiguarces.user.dto.UserPayload;

import java.util.Map;

import static java.util.Objects.requireNonNull;
import static org.springframework.http.HttpStatus.OK;
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

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete() {
        userService.deleteAccount();
        return new ResponseEntity<>(OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshTokens(@RequestBody final Map<String, String> refreshToken) {
        return ResponseEntity.ok(userService.refreshToken(requireNonNull(refreshToken.get(REFRESH_TOKEN))));
    }
}

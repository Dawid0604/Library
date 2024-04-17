package pl.tiguarces.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.tiguarces.service.AppUserService;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@SuppressWarnings("unused")
@RequestMapping("/api/user")
public class UserDetailsController {
    private final AppUserService appUserService;

    @GetMapping("/details")
    public ResponseEntity<?> getUserDetails() {
        return new ResponseEntity<>(appUserService.getLoggedUser(), OK);
    }

    @GetMapping("/roles")
    public ResponseEntity<?> getRoles() {
        return new ResponseEntity<>(appUserService.getLoggedUser()
                                                  .getRoles(), OK);
    }
}

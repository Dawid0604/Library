package pl.tiguarces.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.tiguarces.book.dto.request.UpdateUserRequest;
import pl.tiguarces.book.dto.response.UserDetailsResponse;
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
        var result = appUserService.getLoggedUserFromDb()
                                   .map(UserDetailsResponse::map)
                                   .orElseThrow();

        return new ResponseEntity<>(result, OK);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody final UpdateUserRequest request) {
        appUserService.update(request);
        return new ResponseEntity<>(OK);
    }

    @GetMapping("/roles")
    public ResponseEntity<?> getRoles() {
        return new ResponseEntity<>(appUserService.getLoggedUser()
                                                  .getRoles(), OK);
    }
}

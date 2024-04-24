package pl.tiguarces.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.tiguarces.service.AuthorService;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@SuppressWarnings("unused")
@RequestMapping("/api/author")
public class AuthorController {
    private final AuthorService authorService;

    @GetMapping("/books")
    public ResponseEntity<?> getAuthorBooks(@RequestParam("authorId") final long authorId) {
        var response = authorService.getAuthorBooks(authorId);
        return (response.isPresent()) ? new ResponseEntity<>(response, OK)
                                      : new ResponseEntity<>(NO_CONTENT);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAll() {
        var response = authorService.getAll();
        return (!response.isEmpty()) ? new ResponseEntity<>(response, OK)
                                     : new ResponseEntity<>(NO_CONTENT);
    }
}

package pl.tiguarces.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.tiguarces.service.PublisherService;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@SuppressWarnings("unused")
@RequestMapping("/api/publisher")
public class PublisherController {
    private final PublisherService publisherService;

    @GetMapping("/books")
    public ResponseEntity<?> getPublisherBooks(@RequestParam("publisherId") final long publisherId) {
        var response = publisherService.getPublisherBooks(publisherId);
        return (response.isPresent()) ? new ResponseEntity<>(response, OK)
                                      : new ResponseEntity<>(NO_CONTENT);
    }
}

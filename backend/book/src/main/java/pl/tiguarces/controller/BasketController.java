package pl.tiguarces.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.tiguarces.service.BookService;

import java.util.List;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.util.CollectionUtils.isEmpty;

@RestController
@RequiredArgsConstructor
@SuppressWarnings("unused")
@RequestMapping("/api/basket")
public class BasketController {
    private final BookService bookService;

    @PostMapping("/fetch")
    public ResponseEntity<?> fetchBasket(@RequestBody final List<Long> books) {
        var result = bookService.collect(books);
        return (!isEmpty(result)) ? new ResponseEntity<>(result, OK)
                                  : new ResponseEntity<>(NO_CONTENT);
    }
}

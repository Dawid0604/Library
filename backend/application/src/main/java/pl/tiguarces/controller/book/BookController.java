package pl.tiguarces.controller.book;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.tiguarces.book.dto.request.NewBookRequest;
import pl.tiguarces.book.dto.request.SearchBookRequest;
import pl.tiguarces.service.AppUserService;
import pl.tiguarces.service.BookService;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@SuppressWarnings("unused")
@RequestMapping("/api/book")
public class BookController {
    private final AppUserService appUserService;
    private final BookService bookService;

    @PostMapping("/fetch/all")
    public ResponseEntity<?> findAll(@RequestBody final SearchBookRequest request) {
        var result = bookService.findAll(request);
        return new ResponseEntity<>(result, OK);
    }

    @GetMapping("/fetch/{bookId}")
    public ResponseEntity<?> findBookDetails(@PathVariable("bookId") final long bookId) {
        var result = bookService.findById(bookId);

        return result.isPresent() ? new ResponseEntity<>(result.get(), OK)
                                  : new ResponseEntity<>(NO_CONTENT);
    }

    @PostMapping("/create")
    public ResponseEntity<?> save(@RequestBody final NewBookRequest request) {
        bookService.save(request);
        return new ResponseEntity<>(CREATED);
    }
}

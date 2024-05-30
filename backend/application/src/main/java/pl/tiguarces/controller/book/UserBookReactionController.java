package pl.tiguarces.controller.book;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.tiguarces.book.dto.request.UserBookReactionRequest;
import pl.tiguarces.service.UserBookReactionService;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@SuppressWarnings("unused")
@RequestMapping("/api/reaction")
public class UserBookReactionController {
    private final UserBookReactionService userBookReactionService;

    @PostMapping("/add")
    public ResponseEntity<?> addNewReaction(@RequestBody final UserBookReactionRequest request) {
        userBookReactionService.addBookReaction(request);
        return new ResponseEntity<>(CREATED);
    }

    @PutMapping("/edit")
    public ResponseEntity<?> editReaction(@RequestBody final UserBookReactionRequest request) {
        userBookReactionService.editBookReaction(request);
        return new ResponseEntity<>(OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteReaction(@RequestParam("id") final long reactionId) {
        userBookReactionService.deleteReaction(reactionId);
        return new ResponseEntity<>(OK);
    }

    @GetMapping("/get")
    public ResponseEntity<?> findUserReaction(@RequestParam("bookId") final long bookId) {
        var result = userBookReactionService.findUserBookReaction(bookId);

        return (result.isPresent()) ? new ResponseEntity<>(result.get(), OK)
                                    : new ResponseEntity<>(NO_CONTENT);
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> findAllBookReactions(@RequestParam("bookId") final long bookId,
                                                  @RequestParam(value = "comments", defaultValue = "true", required = false) final boolean comments,
                                                  @RequestParam(value = "sort", defaultValue = "DESC", required = false) final String sort,
                                                  @RequestParam("page") final int page,
                                                  @RequestParam("size") final int size) {

        var result = userBookReactionService.findBookReactions(page, size, comments, sort, bookId);
        return new ResponseEntity<>(result, OK);
    }

    @GetMapping("/user/get-all")
    public ResponseEntity<?> findUserReactions() {
        var result = userBookReactionService.findUserReactions();
        return new ResponseEntity<>(result, result.isEmpty() ? NO_CONTENT : OK);
    }
}

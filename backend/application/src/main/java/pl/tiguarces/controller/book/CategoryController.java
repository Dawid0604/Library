package pl.tiguarces.controller.book;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.tiguarces.service.CategoryService;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@SuppressWarnings("unused")
@RequestMapping("/api/category")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/fetch")
    public ResponseEntity<?> fetchCategories() {
        var result = categoryService.findCategories();
        return new ResponseEntity<>(result, result.isEmpty() ? NO_CONTENT : OK);
    }

    @GetMapping("/fetch/sub")
    public ResponseEntity<?> fetchSubCategories(@RequestParam(name = "parent") final String parentCategory) {
        var result = categoryService.findSubCategories(parentCategory);
        return new ResponseEntity<>(result, result.isEmpty() ? NO_CONTENT : OK);
    }
}

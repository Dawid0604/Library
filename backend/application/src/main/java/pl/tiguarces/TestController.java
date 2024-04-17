package pl.tiguarces;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/sayHello")
    public ResponseEntity<?> sayProtectedHello() {
        return ResponseEntity.ok("Protected Hello!");
    }
}

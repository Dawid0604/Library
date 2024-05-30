package pl.tiguarces.controller.user;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static pl.tiguarces.Constants.MESSAGE_KEY;

@RestControllerAdvice
@SuppressWarnings("unused")
public class ControllerExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialsException(final BadCredentialsException exception) {
        return new ResponseEntity<>(Map.of(MESSAGE_KEY, exception.getMessage()), BAD_REQUEST);
    }
}

package com.kennedy.demo_park_api.web.exception;

import com.kennedy.demo_park_api.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> methodArgumentNotValidException(MethodArgumentNotValidException exc,
                                                                        HttpServletRequest request,
                                                                        BindingResult result){

        log.error("API error - ", exc);

        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        new ErrorMessage(
                                request,
                                HttpStatus.UNPROCESSABLE_ENTITY,
                                "Invalid field(s)",
                                result)
                );
    }


    @ExceptionHandler({
            UsernameUniqueViolationException.class,
            CpfUniqueViolationException.class,
            CodeUniqueViolationException.class
    })
    public ResponseEntity<ErrorMessage> UniqueViolationException(RuntimeException exc,
                                                                        HttpServletRequest request){

        log.error("API error - ", exc);

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        new ErrorMessage(
                                request,
                                HttpStatus.CONFLICT,
                                exc.getMessage())
                );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorMessage> entityNotFoundException(EntityNotFoundException exc,
                                                                         HttpServletRequest request){

        log.error("API error - ", exc);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        new ErrorMessage(
                                request,
                                HttpStatus.NOT_FOUND,
                                exc.getMessage())
                );
    }

    @ExceptionHandler(PasswordInvalidException.class)
    public ResponseEntity<ErrorMessage> passwordInvalidException(PasswordInvalidException exc,
                                                                         HttpServletRequest request){

        log.error("API error - ", exc);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        new ErrorMessage(
                                request,
                                HttpStatus.BAD_REQUEST,
                                exc.getMessage())
                );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorMessage> accessDeniedException(AccessDeniedException exc,
                                                                 HttpServletRequest request){

        log.error("API error - ", exc);

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        new ErrorMessage(
                                request,
                                HttpStatus.FORBIDDEN,
                                exc.getMessage())
                );
    }

}

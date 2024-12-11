package com.kennedy.demo_park_api.web.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Getter
@ToString
public class ErrorMessage {

    private String path;
    private String method;
    private int status;
    private String statusText;
    private String message;
    private Map<String, String> errors;

    public ErrorMessage() {
    }

    public ErrorMessage(HttpServletRequest request, HttpStatus status, String message) {
        path = request.getRequestURI();
        method = request.getMethod();
        statusText = status.getReasonPhrase();
        this.status = status.value();
        this.message = message;

    }

    public ErrorMessage(HttpServletRequest request, HttpStatus status, String message, BindingResult result) {
        path = request.getRequestURI();
        method = request.getMethod();
        statusText = status.getReasonPhrase();
        this.status = status.value();
        this.message = message;
        addErrors(result);

    }

    private void addErrors(BindingResult result) {
        this.errors = new HashMap<>();

        for(FieldError fieldError : result.getFieldErrors()){
            this.errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
    }

}

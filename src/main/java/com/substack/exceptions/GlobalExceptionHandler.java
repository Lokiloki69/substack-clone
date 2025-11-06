package com.substack.exceptions;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFound(ResourceNotFoundException e, Model model) {
        model.addAttribute("error", e.getMessage());
        return "error/404";
    }

    @ExceptionHandler(UnauthorizedException.class)
    public String handleUnauthorized(UnauthorizedException e, Model model) {
        model.addAttribute("error", e.getMessage());
        return "error/403";
    }

    @ExceptionHandler(ValidationException.class)
    public String handleValidation(ValidationException e, Model model) {
        model.addAttribute("error", e.getMessage());
        return "error/400";
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception e, Model model) {
        model.addAttribute("error", "An unexpected error occurred");
        e.printStackTrace();
        return "error/500";
    }
}

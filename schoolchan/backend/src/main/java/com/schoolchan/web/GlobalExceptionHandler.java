package com.schoolchan.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
<<<<<<< HEAD
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

=======
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * One friendly error page for anything that goes wrong. The status code is
 * kept honest so a missing page is a 404 and not a 200.
 */
>>>>>>> d5851d3 (Initial commit)
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

<<<<<<< HEAD
    @ExceptionHandler(Exception.class)
=======
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String notFound(NoResourceFoundException ex, Model model) {
        model.addAttribute("message", "That page does not exist.");
        return "error";
    }

    @ExceptionHandler({IllegalArgumentException.class,
            org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String badRequest(Exception ex, Model model) {
        log.warn("Bad request: {}", ex.getMessage());
        String message = ex.getMessage() == null ? "That request was not valid." : ex.getMessage();
        if (ex instanceof MethodArgumentTypeMismatchException) {
            message = "That link is not valid.";
        }
        model.addAttribute("message", message);
        return "error";
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String conflict(IllegalStateException ex, Model model) {
        log.warn("Conflict: {}", ex.getMessage());
        model.addAttribute("message", ex.getMessage() == null ? "That did not work." : ex.getMessage());
        return "error";
    }

    /**
     * A write endpoint opened with GET (for example, a sign-in redirect that
     * pointed at a reply endpoint). This is a normal dead end, not a failure,
     * so it must not be reported as a server error.
     */
    @ExceptionHandler(org.springframework.web.HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public String wrongMethod(org.springframework.web.HttpRequestMethodNotSupportedException ex, Model model) {
        log.info("Wrong method for {}: {}", ex.getMethod(), ex.getMessage());
        model.addAttribute("message",
                "That link does not open a page by itself. Go back to the board and try again from the button.");
        return "error";
    }

    /**
     * A deliberate status, such as a post number that does not exist, must keep
     * its code instead of being flattened into a 500 by the catch-all below.
     */
    @ExceptionHandler(org.springframework.web.server.ResponseStatusException.class)
    public String status(org.springframework.web.server.ResponseStatusException ex,
                         jakarta.servlet.http.HttpServletResponse response, Model model) {
        int code = ex.getStatusCode().value();
        response.setStatus(code);
        String message = code == 404
                ? "That page does not exist."
                : ex.getReason() == null ? "That request did not work." : ex.getReason();
        model.addAttribute("message", message);
        return "error";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
>>>>>>> d5851d3 (Initial commit)
    public String handle(Exception ex, Model model) {
        log.error("Request failed", ex);
        model.addAttribute("message", "An error occurred. Please try again.");
        return "error";
    }
}

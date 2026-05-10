package com.charite.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public String handleTypeMismatch(MethodArgumentTypeMismatchException ex, Model model) {
        model.addAttribute("status", 400);
        model.addAttribute("error", "Format de paramètre invalide");
        model.addAttribute("message", "La valeur '" + ex.getValue() + "' n'est pas valide pour le paramètre '" + ex.getName() + "'.");
        return "error";
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNotFound(NoResourceFoundException ex, Model model) {
        model.addAttribute("status", 404);
        model.addAttribute("error", "Page Non Trouvée");
        model.addAttribute("message", "La ressource demandée n'a pas été trouvée.");
        return "error/404";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex, Model model) {
        // Log the actual exception to console for debugging
        System.err.println("Uncaught Exception: " + ex.getClass().getName() + " - " + ex.getMessage());
        ex.printStackTrace();

        model.addAttribute("status", 500);
        model.addAttribute("error", "Erreur Interne du Serveur");
        model.addAttribute("message", ex.getMessage());
        return "error";
    }
}

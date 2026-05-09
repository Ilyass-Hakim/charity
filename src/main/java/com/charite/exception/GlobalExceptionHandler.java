package com.charite.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public String handleTypeMismatch(MethodArgumentTypeMismatchException ex, Model model) {
        model.addAttribute("status", 400);
        model.addAttribute("error", "Format de paramètre invalide");
        model.addAttribute("message", "La valeur '" + ex.getValue() + "' n'est pas valide pour le paramètre '" + ex.getName() + "'.");
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex, Model model) {
        model.addAttribute("status", 500);
        model.addAttribute("error", "Erreur Interne du Serveur");
        model.addAttribute("message", ex.getMessage());
        return "error";
    }
}

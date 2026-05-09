package com.charite.controller;

import com.charite.entity.Utilisateur;
import com.charite.service.UtilisateurService;
import com.charite.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final UtilisateurService utilisateurService;

    @ModelAttribute("currentUser")
    public Utilisateur addCurrentUserToModel(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getPrincipal().equals("anonymousUser")) {
            return null;
        }
        String email = SecurityUtils.getEmail(authentication);
        return utilisateurService.getByEmail(email);
    }
}

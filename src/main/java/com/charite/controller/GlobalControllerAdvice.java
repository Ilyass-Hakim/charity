package com.charite.controller;

import com.charite.entity.Utilisateur;
import com.charite.repository.UtilisateurRepository;
import com.charite.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final UtilisateurRepository utilisateurRepository;

    @ModelAttribute
    public void addAttributes(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            try {
                // Try to get user by email first
                String email = SecurityUtils.getEmail(authentication);
                Utilisateur user = null;
                
                if (email != null) {
                    user = utilisateurRepository.findByEmail(email).orElse(null);
                }
                
                // Fallback to authentication name if email failed
                if (user == null) {
                    user = utilisateurRepository.findByEmail(authentication.getName()).orElse(null);
                }

                if (user != null) {
                    model.addAttribute("currentUser", user);
                    if (user.getMemberships() != null && !user.getMemberships().isEmpty()) {
                        model.addAttribute("currentOrg", user.getMemberships().get(0).getOrganisation());
                    }
                    // Debug log
                    System.out.println("DEBUG: GlobalControllerAdvice injected user: " + user.getEmail());
                } else {
                    System.out.println("DEBUG: GlobalControllerAdvice could not find user for: " + authentication.getName());
                }
            } catch (Exception e) {
                System.err.println("ERROR: GlobalControllerAdvice failed: " + e.getMessage());
            }
        }
    }
}

package com.charite.controller;

import com.charite.entity.ActionCharite;
import com.charite.entity.Don;
import com.charite.entity.Utilisateur;
import com.charite.service.ActionService;
import com.charite.service.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ActionService actionService;
    private final UtilisateurService utilisateurService;

    @GetMapping("/")
    public String home(Model model, Authentication authentication) {
        // Actions à la une
        List<ActionCharite> featuredActions = actionService.explorer(null, null, 0, 3).getContent();
        model.addAttribute("featuredActions", featuredActions);

        // Appel urgent (la dernière action)
        if (!featuredActions.isEmpty()) {
            model.addAttribute("urgentAction", featuredActions.get(0));
        }

        // Global Statistics
        BigDecimal totalGlobalDonations = actionService.getTotalGlobalDonations();
        long totalCauses = actionService.countAllActiveActions();
        model.addAttribute("totalDonated", totalGlobalDonations);
        model.addAttribute("causesSupported", totalCauses);
        model.addAttribute("livesTouched", totalCauses * 12); // Simulation global

        // Statistiques personnelles (si connecté)
        if (authentication != null && authentication.isAuthenticated()) {
            try {
                Utilisateur user = utilisateurService.getByEmail(authentication.getName());
                // On peut garder d'autres infos personnelles ici si besoin
            } catch (Exception e) {
                // Silently fail
            }
        }

        return "index";
    }

    @GetMapping("/explore")
    public String explore() {
        return "redirect:/actions";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/watch")
    public String watch(Model model) {
        List<ActionCharite> featuredActions = actionService.explorer(null, null, 0, 1).getContent();
        if (!featuredActions.isEmpty()) {
            model.addAttribute("action", featuredActions.get(0));
        }
        return "watch";
    }

    @GetMapping("/dashboard")
    public String dashboardRouter(Authentication authentication) {
        if (authentication == null) {
            return "redirect:/auth/login";
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // Redirection dynamique basee sur le role
        if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"))) {
            return "redirect:/admin/dashboard";
        } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ORG_ADMIN"))) {
            return "redirect:/organisation/dashboard";
        } else {
            // Utilisateur standard redirige vers son profil (ou vers actions)
            return "redirect:/utilisateur/profil";
        }
    }
}

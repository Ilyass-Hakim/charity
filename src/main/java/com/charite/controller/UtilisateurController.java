package com.charite.controller;

import com.charite.dto.UtilisateurDto;
import com.charite.entity.Utilisateur;
import com.charite.repository.ContributionRepository;
import com.charite.service.UtilisateurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/utilisateur")
@RequiredArgsConstructor
public class UtilisateurController {
    private final UtilisateurService utilisateurService;
    private final ContributionRepository contributionRepository;

    @GetMapping("/profil")
    public String profil(@AuthenticationPrincipal UserDetails principal, Model model) {
        Utilisateur u = utilisateurService.getByEmail(principal.getUsername());
        model.addAttribute("utilisateur", u);
        // Utilisation du repo Spring Data si existant, sinon appel .getContributions()
        model.addAttribute("contributions", u.getContributions());
        return "utilisateur/profil";
    }

    @GetMapping("/profil/modifier")
    public String modifierForm(@AuthenticationPrincipal UserDetails principal, Model model) {
        Utilisateur u = utilisateurService.getByEmail(principal.getUsername());
        model.addAttribute("dto", new UtilisateurDto(u));
        return "utilisateur/modifier";
    }

    @PostMapping("/profil/modifier")
    public String modifier(@AuthenticationPrincipal UserDetails principal,
                           @Valid @ModelAttribute("dto") UtilisateurDto dto,
                           BindingResult result, RedirectAttributes ra) {
        if (result.hasErrors()) return "utilisateur/modifier";
        
        Utilisateur u = utilisateurService.getByEmail(principal.getUsername());
        utilisateurService.mettreAJour(u.getId(), dto);
        ra.addFlashAttribute("success", "Profil mis a jour.");
        return "redirect:/utilisateur/profil";
    }
}

package com.charite.controller;

import com.charite.service.FileService;

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
    private final FileService fileService;

    @GetMapping("/profil")
    public String profil(org.springframework.security.core.Authentication authentication, Model model) {
        String email = com.charite.security.SecurityUtils.getEmail(authentication);
        Utilisateur u = utilisateurService.getByEmail(email);
        model.addAttribute("utilisateur", u);
        model.addAttribute("contributions", u.getContributions());
        
        java.math.BigDecimal totalDonations = u.getContributions().stream()
                .filter(c -> c instanceof com.charite.entity.Don)
                .map(c -> ((com.charite.entity.Don) c).getMontant())
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        
        model.addAttribute("totalMAD", totalDonations);
        model.addAttribute("totalActions", u.getContributions().size());
        
        return "utilisateur/profil";
    }

    @GetMapping("/profil/modifier")
    public String modifierForm(org.springframework.security.core.Authentication authentication, Model model) {
        String email = com.charite.security.SecurityUtils.getEmail(authentication);
        Utilisateur u = utilisateurService.getByEmail(email);
        model.addAttribute("dto", new UtilisateurDto(u));
        model.addAttribute("utilisateur", u); // To show current photo
        return "utilisateur/modifier";
    }

    @PostMapping("/profil/modifier")
    public String modifier(org.springframework.security.core.Authentication authentication,
                           @Valid @ModelAttribute("dto") UtilisateurDto dto,
                           BindingResult result, 
                           @org.springframework.web.bind.annotation.RequestParam(value = "photoFile", required = false) org.springframework.web.multipart.MultipartFile photoFile,
                           RedirectAttributes ra,
                           Model model) {
        if (result.hasErrors()) return "utilisateur/modifier";
        
        String email = com.charite.security.SecurityUtils.getEmail(authentication);
        Utilisateur u = utilisateurService.getByEmail(email);

        if (photoFile != null && !photoFile.isEmpty()) {
            String photoPath = fileService.saveFile(photoFile);
            u.setPhotoProfil(photoPath);
        }

        utilisateurService.mettreAJour(u.getId(), dto);
        ra.addFlashAttribute("success", "Profil mis a jour.");
        return "redirect:/utilisateur/profil";
    }
}

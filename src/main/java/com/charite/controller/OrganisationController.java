package com.charite.controller;

import com.charite.dto.OrganisationDto;
import com.charite.entity.MembreOrganisation;
import com.charite.entity.Utilisateur;
import com.charite.repository.MembreOrganisationRepository;
import com.charite.service.ActionService;
import com.charite.service.OrganisationService;
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
@RequestMapping("/organisation")
@RequiredArgsConstructor
public class OrganisationController {
    private final OrganisationService organisationService;
    private final ActionService actionService;
    private final UtilisateurService utilisateurService;
    private final MembreOrganisationRepository membreRepository;

    @GetMapping("/inscription")
    public String inscriptionForm(Model model) {
        model.addAttribute("dto", new OrganisationDto());
        return "organisation/inscription";
    }

    @PostMapping("/inscription")
    public String inscrire(@AuthenticationPrincipal UserDetails principal,
                           @Valid @ModelAttribute("dto") OrganisationDto dto,
                           BindingResult result, RedirectAttributes ra) {
        if (result.hasErrors()) return "organisation/inscription";
        organisationService.inscrire(dto, principal.getUsername());
        ra.addFlashAttribute("success", "Demande soumise. En attente de validation.");
        return "redirect:/";
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails principal, Model model) {
        Utilisateur u = utilisateurService.getByEmail(principal.getUsername());
        
        if (u.getMemberships().isEmpty()) {
            throw new RuntimeException("Aucune organisation associee");
        }
        
        Long orgId = u.getMemberships().get(0).getOrganisation().getId();
        MembreOrganisation membre = membreRepository
                .findByUtilisateurIdAndOrganisationId(u.getId(), orgId)
                .orElseThrow(() -> new RuntimeException("Membre introuvable"));
                
        model.addAttribute("organisation", membre.getOrganisation());
        model.addAttribute("actions", actionService.findByOrganisation(orgId));
        return "organisation/dashboard";
    }
}

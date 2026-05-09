package com.charite.controller;

import com.charite.service.FileService;

import com.charite.dto.OrganisationDto;
import com.charite.entity.MembreOrganisation;
import com.charite.entity.Organisation;
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
    private final FileService fileService;

    @GetMapping("/inscription")
    public String inscriptionForm(Model model) {
        model.addAttribute("dto", new OrganisationDto());
        return "organisation/inscription";
    }

    @PostMapping("/inscription")
    public String inscrire(org.springframework.security.core.Authentication authentication,
                           @Valid @ModelAttribute("dto") OrganisationDto dto,
                           BindingResult result, RedirectAttributes ra) {
        if (result.hasErrors()) return "organisation/inscription";
        String email = com.charite.security.SecurityUtils.getEmail(authentication);
        organisationService.inscrire(dto, email);
        ra.addFlashAttribute("success", "Demande soumise. En attente de validation.");
        return "redirect:/";
    }

    @GetMapping("/dashboard")
    public String dashboard(org.springframework.security.core.Authentication authentication, Model model) {
        String email = com.charite.security.SecurityUtils.getEmail(authentication);
        Utilisateur u = utilisateurService.getByEmail(email);
        
        if (u.getMemberships() == null || u.getMemberships().isEmpty()) {
            throw new RuntimeException("Aucune organisation associee");
        }
        
        Organisation org = u.getMemberships().get(0).getOrganisation();
        java.util.List<com.charite.entity.ActionCharite> actions = actionService.findByOrganisation(org.getId());
        
        // Stats logic
        java.math.BigDecimal totalDonated = actions.stream()
                .flatMap(a -> a.getContributions().stream())
                .filter(c -> c instanceof com.charite.entity.Don)
                .map(c -> ((com.charite.entity.Don) c).getMontant())
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
                
        long activeCampaignsCount = actions.stream()
                .filter(a -> "ACTIVE".equals(a.getStatutAction()))
                .count();
                
        long totalDonors = actions.stream()
                .flatMap(a -> a.getContributions().stream())
                .map(c -> c.getUtilisateur().getId())
                .distinct()
                .count();
                
        java.util.List<com.charite.entity.Contribution> recentContributions = actions.stream()
                .flatMap(a -> a.getContributions().stream())
                .sorted((c1, c2) -> c2.getDate().compareTo(c1.getDate()))
                .limit(5)
                .toList();

        model.addAttribute("organisation", org);
        model.addAttribute("totalDonated", totalDonated);
        model.addAttribute("activeCampaignsCount", activeCampaignsCount);
        model.addAttribute("uniqueDonorsCount", totalDonors);
        model.addAttribute("recentContributions", recentContributions);
        
        return "organisation/dashboard";
    }

    @GetMapping("/campaigns")
    public String campaigns(org.springframework.security.core.Authentication authentication, Model model) {
        String email = com.charite.security.SecurityUtils.getEmail(authentication);
        Utilisateur u = utilisateurService.getByEmail(email);
        
        if (u.getMemberships() == null || u.getMemberships().isEmpty()) {
            throw new RuntimeException("Aucune organisation associee");
        }
        
        Long orgId = u.getMemberships().get(0).getOrganisation().getId();
        model.addAttribute("organisation", u.getMemberships().get(0).getOrganisation());
        model.addAttribute("actions", actionService.findByOrganisation(orgId));
        return "organisation/campaigns";
    }

    @GetMapping("/donations")
    public String donations(org.springframework.security.core.Authentication authentication, Model model) {
        String email = com.charite.security.SecurityUtils.getEmail(authentication);
        Utilisateur u = utilisateurService.getByEmail(email);
        
        if (u.getMemberships() == null || u.getMemberships().isEmpty()) {
            throw new RuntimeException("Aucune organisation associee");
        }
        
        Organisation org = u.getMemberships().get(0).getOrganisation();
        
        // Fetch all contributions for this organisation's actions
        java.util.List<com.charite.entity.ActionCharite> actions = actionService.findByOrganisation(org.getId());
        java.util.List<com.charite.entity.Contribution> contributions = actions.stream()
                .flatMap(a -> a.getContributions().stream())
                .sorted((c1, c2) -> c2.getDate().compareTo(c1.getDate()))
                .toList();

        model.addAttribute("organisation", org);
        model.addAttribute("contributions", contributions);
        return "organisation/donations";
    }

    @GetMapping("/settings")
    public String settings(org.springframework.security.core.Authentication authentication, Model model) {
        String email = com.charite.security.SecurityUtils.getEmail(authentication);
        Utilisateur u = utilisateurService.getByEmail(email);
        Organisation org = u.getMemberships().get(0).getOrganisation();
        
        model.addAttribute("organisation", org);
        model.addAttribute("utilisateur", u);
        return "organisation/settings";
    }

    @PostMapping("/settings")
    public String updateSettings(org.springframework.security.core.Authentication authentication,
                                 @ModelAttribute Organisation organisation,
                                 @org.springframework.web.bind.annotation.RequestParam(value = "logoFile", required = false) org.springframework.web.multipart.MultipartFile logoFile,
                                 RedirectAttributes ra) {
        String email = com.charite.security.SecurityUtils.getEmail(authentication);
        Utilisateur u = utilisateurService.getByEmail(email);
        Organisation existingOrg = u.getMemberships().get(0).getOrganisation();
        
        // Update logo if provided
        if (logoFile != null && !logoFile.isEmpty()) {
            String logoPath = fileService.saveFile(logoFile);
            existingOrg.setLogo(logoPath);
        }

        // Update fields
        existingOrg.setNom(organisation.getNom());
        existingOrg.setDescription(organisation.getDescription());
        existingOrg.setAdresseLegale(organisation.getAdresseLegale());
        existingOrg.setMatriculeFiscal(organisation.getMatriculeFiscal());
        existingOrg.setEmailContact(organisation.getEmailContact());
        existingOrg.setTelephone(organisation.getTelephone());
        
        organisationService.enregistrer(existingOrg);
        
        ra.addFlashAttribute("success", "Settings updated successfully!");
        return "redirect:/organisation/settings";
    }
}

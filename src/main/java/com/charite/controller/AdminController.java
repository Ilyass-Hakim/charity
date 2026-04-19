package com.charite.controller;

import com.charite.repository.ActionChariteRepository;
import com.charite.repository.DonRepository;
import com.charite.repository.MembreOrganisationRepository;
import com.charite.repository.OrganisationRepository;
import com.charite.repository.UtilisateurRepository;
import com.charite.service.OrganisationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final OrganisationRepository organisationRepository;
    private final OrganisationService organisationService;
    private final UtilisateurRepository utilisateurRepository;
    private final ActionChariteRepository actionRepository;
    private final DonRepository donRepository;
    private final MembreOrganisationRepository membreRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("orgsEnAttente", organisationRepository.findByStatutOrganisation("EN_ATTENTE"));
        model.addAttribute("orgsApprouvees", organisationRepository.findByStatutOrganisation("APPROUVEE"));
        
        // Statistiques
        model.addAttribute("nbUtilisateurs", utilisateurRepository.count());
        model.addAttribute("nbActions", actionRepository.count());
        model.addAttribute("nbDons", donRepository.count());
        
        return "admin/dashboard";
    }

    @PostMapping("/organisations/{id}/valider")
    public String valider(@PathVariable Long id, @RequestParam boolean approuver, RedirectAttributes ra) {
        organisationService.valider(id, approuver);
        ra.addFlashAttribute("success", approuver ? "Organisation approuvee." : "Organisation rejetee.");
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/organisations/{id}/membres")
    public String membres(@PathVariable Long id, Model model) {
        model.addAttribute("membres", membreRepository.findByOrganisationId(id));
        return "admin/membres";
    }
}

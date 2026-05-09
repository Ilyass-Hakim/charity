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
        
        model.addAttribute("nbUtilisateurs", utilisateurRepository.count());
        model.addAttribute("nbActions", actionRepository.count());
        model.addAttribute("nbOrgs", organisationRepository.count());
        
        java.math.BigDecimal totalGlobalDonations = donRepository.findAll().stream()
                .map(com.charite.entity.Don::getMontant)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        model.addAttribute("totalGlobalDonations", totalGlobalDonations);

        // Recent Activity (Derniers dons)
        model.addAttribute("recentDonations", donRepository.findAll().stream()
                .sorted((d1, d2) -> d2.getDate().compareTo(d1.getDate()))
                .limit(5)
                .collect(java.util.stream.Collectors.toList()));

        // Chart Data (Simulation par mois pour l'instant avec les vrais dons si possible)
        // Pour faire simple, on va juste envoyer une liste de montants par mois
        java.util.Map<Integer, java.math.BigDecimal> monthlyStats = new java.util.HashMap<>();
        donRepository.findAll().forEach(don -> {
            int month = don.getDate().getMonthValue();
            monthlyStats.merge(month, don.getMontant(), java.math.BigDecimal::add);
        });
        model.addAttribute("monthlyStats", monthlyStats);
        
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

    @GetMapping("/utilisateurs")
    public String utilisateurs(Model model) {
        java.util.List<com.charite.entity.Utilisateur> users = utilisateurRepository.findAll();
        model.addAttribute("utilisateurs", users);
        
        // Total Donated mapping
        java.util.Map<Long, java.math.BigDecimal> userDonations = new java.util.HashMap<>();
        for (com.charite.entity.Utilisateur u : users) {
            java.math.BigDecimal total = donRepository.findAll().stream()
                    .filter(d -> d.getUtilisateur() != null && d.getUtilisateur().getId().equals(u.getId()))
                    .map(com.charite.entity.Don::getMontant)
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
            userDonations.put(u.getId(), total);
        }
        model.addAttribute("userDonations", userDonations);
        
        // Stats
        model.addAttribute("totalUsersCount", utilisateurRepository.count());
        long activeDonors = donRepository.findAll().stream()
                .map(d -> d.getUtilisateur())
                .filter(java.util.Objects::nonNull)
                .map(u -> u.getId())
                .distinct()
                .count();
        model.addAttribute("activeDonorsCount", activeDonors);
        model.addAttribute("pendingApprovalsCount", organisationRepository.countByStatutOrganisation("EN_ATTENTE"));
        
        return "admin/utilisateurs";
    }

    @GetMapping("/dons")
    public String dons(Model model) {
        model.addAttribute("dons", donRepository.findAll());
        return "admin/dons";
    }
}

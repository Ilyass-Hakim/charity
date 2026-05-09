package com.charite.controller;

import com.charite.entity.Utilisateur;
import com.charite.repository.ActionChariteRepository;
import com.charite.repository.DonRepository;
import com.charite.repository.MembreOrganisationRepository;
import com.charite.repository.OrganisationRepository;
import com.charite.repository.UtilisateurRepository;
import com.charite.service.OrganisationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    private void addCurrentUser(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            utilisateurRepository.findByEmail(auth.getName()).ifPresent(u -> model.addAttribute("currentUser", u));
        }
    }

    @GetMapping("")
    public String index() {
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        addCurrentUser(model);
        
        model.addAttribute("orgsEnAttente", organisationRepository.findByStatutOrganisation("EN_ATTENTE"));
        model.addAttribute("orgsApprouvees", organisationRepository.findByStatutOrganisation("APPROUVEE"));
        
        model.addAttribute("nbUtilisateurs", utilisateurRepository.count());
        model.addAttribute("nbActions", actionRepository.count());
        model.addAttribute("nbOrgs", organisationRepository.count());
        
        BigDecimal totalGlobalDonations = donRepository.findAll().stream()
                .filter(don -> don.getMontant() != null)
                .map(com.charite.entity.Don::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("totalGlobalDonations", totalGlobalDonations);

        model.addAttribute("recentDonations", donRepository.findAll().stream()
                .filter(don -> don.getDate() != null && don.getMontant() != null && don.getActionCharite() != null && don.getUtilisateur() != null)
                .sorted((d1, d2) -> d2.getDate().compareTo(d1.getDate()))
                .limit(5)
                .collect(Collectors.toList()));

        Map<Integer, BigDecimal> monthlyStats = new HashMap<>();
        for (int i = 1; i <= 12; i++) {
            monthlyStats.put(i, BigDecimal.ZERO);
        }

        donRepository.findAll().stream()
                .filter(don -> don.getDate() != null && don.getMontant() != null)
                .forEach(don -> {
                    int month = don.getDate().getMonthValue();
                    monthlyStats.merge(month, don.getMontant(), BigDecimal::add);
                });
        
        // Calculate heights for the chart in Java to avoid Thymeleaf errors
        List<Double> chartHeights = new java.util.ArrayList<>();
        double total = totalGlobalDonations.doubleValue();
        for (int i = 1; i <= 12; i++) {
            double monthlyTotal = monthlyStats.get(i).doubleValue();
            chartHeights.add(total > 0 ? (monthlyTotal * 100 / total) : 0);
        }
        model.addAttribute("monthlyStats", monthlyStats);
        model.addAttribute("chartHeights", chartHeights);
        
        return "admin/dashboard";
    }

    @PostMapping("/organisations/{id}/valider")
    public String valider(@PathVariable Long id, @RequestParam boolean approuver, RedirectAttributes ra) {
        organisationService.valider(id, approuver);
        ra.addFlashAttribute("success", approuver ? "Organisation approuvee." : "Organisation rejetee.");
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/utilisateurs")
    public String utilisateurs(Model model) {
        addCurrentUser(model);
        List<Utilisateur> users = utilisateurRepository.findAll();
        model.addAttribute("utilisateurs", users);
        
        Map<Long, BigDecimal> userDonations = new HashMap<>();
        for (Utilisateur u : users) {
            BigDecimal total = donRepository.findAll().stream()
                    .filter(d -> d.getUtilisateur() != null && d.getUtilisateur().getId().equals(u.getId()))
                    .map(com.charite.entity.Don::getMontant)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            userDonations.put(u.getId(), total);
        }
        model.addAttribute("userDonations", userDonations);
        
        model.addAttribute("totalUsersCount", utilisateurRepository.count());
        long activeDonors = donRepository.findAll().stream()
                .filter(d -> d.getUtilisateur() != null)
                .map(d -> d.getUtilisateur().getId())
                .distinct()
                .count();
        model.addAttribute("activeDonorsCount", activeDonors);
        model.addAttribute("pendingApprovalsCount", organisationRepository.countByStatutOrganisation("EN_ATTENTE"));
        
        return "admin/utilisateurs";
    }

    @GetMapping("/dons")
    public String dons(Model model) {
        addCurrentUser(model);
        model.addAttribute("dons", donRepository.findAll());
        return "admin/dons";
    }
}

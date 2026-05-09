package com.charite.controller;

import com.charite.dto.ActionDto;
import com.charite.entity.ActionCharite;
import com.charite.entity.Utilisateur;
import com.charite.repository.CategorieRepository;
import com.charite.service.ActionService;
import com.charite.service.MediaService;
import com.charite.service.UtilisateurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/actions")
@RequiredArgsConstructor
public class ActionController {
    private final ActionService actionService;
    private final CategorieRepository categorieRepository;
    private final MediaService mediaService;
    private final UtilisateurService utilisateurService;

    @GetMapping
    public String explorer(@RequestParam(required = false) String categorie,
                           @RequestParam(required = false) String keyword,
                           @RequestParam(defaultValue = "0") int page,
                           Model model) {
        model.addAttribute("actions", actionService.explorer(categorie, keyword, page, 9));
        model.addAttribute("categories", categorieRepository.findAll());
        model.addAttribute("categorieSelectionnee", categorie);
        model.addAttribute("keyword", keyword);
        return "actions/liste";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("action", actionService.findById(id));
        return "actions/detail";
    }

    @GetMapping("/creer")
    public String creerForm(org.springframework.security.core.Authentication authentication, Model model) {
        String email = com.charite.security.SecurityUtils.getEmail(authentication);
        Utilisateur u = utilisateurService.getByEmail(email);
        if (u.getMemberships() != null && !u.getMemberships().isEmpty()) {
            model.addAttribute("organisation", u.getMemberships().get(0).getOrganisation());
        }
        model.addAttribute("dto", new ActionDto());
        model.addAttribute("categories", categorieRepository.findAll());
        return "actions/formulaire";
    }

    @PostMapping("/creer")
    public String creer(org.springframework.security.core.Authentication authentication,
                        @Valid @ModelAttribute("dto") ActionDto dto,
                        @RequestParam(required = false) List<MultipartFile> medias,
                        BindingResult result, RedirectAttributes ra) {
        if (result.hasErrors()) return "actions/formulaire";
        
        String email = com.charite.security.SecurityUtils.getEmail(authentication);
        Utilisateur u = utilisateurService.getByEmail(email);
        if (u.getMemberships() == null || u.getMemberships().isEmpty()) {
            throw new RuntimeException("Aucune organisation associee");
        }
        
        Long orgId = u.getMemberships().get(0).getOrganisation().getId();
        ActionCharite action = actionService.creer(dto, orgId);
        
        if (medias != null) {
            medias.stream()
                  .filter(f -> !f.isEmpty())
                  .forEach(f -> mediaService.sauvegarder(f, action));
        }
        
        ra.addFlashAttribute("success", "Action creee.");
        return "redirect:/actions/" + action.getId();
    }

    @PostMapping("/{id}/archiver")
    public String archiver(@PathVariable Long id, RedirectAttributes ra) {
        actionService.archiver(id);
        ra.addFlashAttribute("success", "Action archivee.");
        return "redirect:/organisation/dashboard";
    }
}

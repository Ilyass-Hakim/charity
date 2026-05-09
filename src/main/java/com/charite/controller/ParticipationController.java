package com.charite.controller;

import com.charite.service.ParticipationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/participations")
@RequiredArgsConstructor
public class ParticipationController {
    private final ParticipationService participationService;

    @PostMapping("/inscrire/{actionId}")
    public String inscrire(@PathVariable Long actionId,
                           org.springframework.security.core.Authentication authentication,
                           RedirectAttributes ra) {
        try {
            String email = com.charite.security.SecurityUtils.getEmail(authentication);
            participationService.inscrire(actionId, email);
            ra.addFlashAttribute("success", "Votre inscription à l'action a été confirmée ! Merci pour votre engagement.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/actions/" + actionId;
    }
}

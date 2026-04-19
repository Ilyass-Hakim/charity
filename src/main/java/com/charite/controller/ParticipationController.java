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
                           @AuthenticationPrincipal UserDetails principal,
                           RedirectAttributes ra) {
        participationService.inscrire(actionId, principal.getUsername());
        ra.addFlashAttribute("success", "Inscription confirmee !");
        return "redirect:/actions/" + actionId;
    }
}

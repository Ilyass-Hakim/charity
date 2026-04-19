package com.charite.controller;

import com.charite.dto.DonDto;
import com.charite.service.ActionService;
import com.charite.service.DonService;
import com.charite.service.StripeService;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@Controller
@RequestMapping("/dons")
@RequiredArgsConstructor
public class DonController {

    private final StripeService stripeService;
    private final DonService donService;
    private final ActionService actionService;

    // Affiche le formulaire de don
    @GetMapping("/initier/{actionId}")
    public String formulaireDon(@PathVariable Long actionId, Model model) {
        model.addAttribute("action", actionService.findById(actionId));
        model.addAttribute("stripePublicKey", "${stripe.publishable.key}");
        return "dons/formulaire";
    }

    // Cree l'intention de paiement et retourne le clientSecret a Stripe.js
    @PostMapping("/creer-intent")
    @ResponseBody
    public Map<String, String> creerIntent(@RequestBody DonDto dto) {
        PaymentIntent intent = stripeService.creerPaymentIntent(
                dto.getMontant(), "mad");
        return Map.of("clientSecret", intent.getClientSecret());
    }

    // Confirme le don apres paiement Stripe reussi
    @PostMapping("/confirmer")
    public String confirmerDon(@RequestParam String paymentIntentId,
                               @RequestParam Long actionId,
                               @RequestParam BigDecimal montant,
                               @AuthenticationPrincipal UserDetails userDetails) {
        if (stripeService.confirmerPaiement(paymentIntentId)) {
            donService.enregistrerDon(actionId, userDetails.getUsername(),
                    montant, paymentIntentId);
            return "redirect:/actions/" + actionId + "?don=succes";
        }
        return "redirect:/actions/" + actionId + "?don=echec";
    }
}

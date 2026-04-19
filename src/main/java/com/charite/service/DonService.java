package com.charite.service;

import com.charite.entity.ActionCharite;
import com.charite.entity.Don;
import com.charite.entity.Utilisateur;
import com.charite.repository.ActionChariteRepository;
import com.charite.repository.DonRepository;
import com.charite.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class DonService {

    private final DonRepository donRepository;
    private final ActionChariteRepository actionRepository;
    private final UtilisateurRepository utilisateurRepository;

    public void enregistrerDon(Long actionId, String emailUser, BigDecimal montant, String paymentIntentId) {
        ActionCharite action = actionRepository.findById(actionId)
                .orElseThrow(() -> new RuntimeException("Action non trouvee"));

        Utilisateur user = utilisateurRepository.findByEmail(emailUser)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouve"));

        Don don = Don.builder()
                .montant(montant)
                .methodePaiement("STRIPE")
                .statutPaiement("CONFIRME")
                .identifiantTransaction(paymentIntentId)
                .build();
                
        // Setting Contribution properties
        don.setActionCharite(action);
        don.setUtilisateur(user);

        donRepository.save(don);

        // Mettre a jour le montant actuel de l'action
        action.setMontantActuel(action.getMontantActuel().add(montant));
        actionRepository.save(action);
    }
}

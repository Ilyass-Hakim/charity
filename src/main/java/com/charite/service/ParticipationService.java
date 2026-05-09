package com.charite.service;

import com.charite.entity.ActionCharite;
import com.charite.entity.Participation;
import com.charite.entity.Utilisateur;
import com.charite.repository.ActionChariteRepository;
import com.charite.repository.ParticipationRepository;
import com.charite.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ParticipationService {

    private final ParticipationRepository participationRepository;
    private final ActionChariteRepository actionRepository;
    private final UtilisateurRepository utilisateurRepository;

    public Participation inscrire(Long actionId, String email) {
        ActionCharite action = actionRepository.findById(actionId)
                .orElseThrow(() -> new RuntimeException("Action introuvable"));

        Utilisateur u = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // Verifier si deja inscrit
        boolean dejaInscrit = participationRepository.findAll().stream()
                .anyMatch(p -> p.getActionCharite().getId().equals(actionId) && 
                              p.getUtilisateur().getId().equals(u.getId()));
        
        if (dejaInscrit) {
            throw new RuntimeException("Vous êtes déjà inscrit à cette action.");
        }

        Participation p = Participation.builder()
                .statutParticipation("INSCRIT")
                .build();
                
        p.setActionCharite(action);
        p.setUtilisateur(u);
        return participationRepository.save(p);
    }
}

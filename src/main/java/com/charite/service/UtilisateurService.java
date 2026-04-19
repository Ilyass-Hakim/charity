package com.charite.service;

import com.charite.dto.UtilisateurDto;
import com.charite.entity.Contribution;
import com.charite.entity.Role;
import com.charite.entity.Utilisateur;
import com.charite.repository.RoleRepository;
import com.charite.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public Utilisateur inscrire(UtilisateurDto dto) {
        if (utilisateurRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email deja utilise");
        }

        Role userRole = roleRepository.findByNom("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Role ROLE_USER introuvable - verifie data.sql"));

        Utilisateur user = Utilisateur.builder()
                .nom(dto.getNom())
                .prenom(dto.getPrenom())
                .email(dto.getEmail())
                .motDePasse(passwordEncoder.encode(dto.getMotDePasse()))
                .telephone(dto.getTelephone())
                .role(userRole)
                .provider("local")
                .actif(true)
                .build();

        return utilisateurRepository.save(user);
    }

    public Utilisateur mettreAJour(Long id, UtilisateurDto dto) {
        Utilisateur u = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        u.setNom(dto.getNom());
        u.setPrenom(dto.getPrenom());
        u.setTelephone(dto.getTelephone());

        if (dto.getMotDePasse() != null && !dto.getMotDePasse().isBlank()) {
            u.setMotDePasse(passwordEncoder.encode(dto.getMotDePasse()));
        }

        return utilisateurRepository.save(u);
    }

    public Utilisateur getByEmail(String email) {
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }

    public List<Contribution> getHistoriqueDons(Long userId) {
        return utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouve"))
                .getContributions();
    }
}

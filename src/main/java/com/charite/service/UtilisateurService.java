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

import com.charite.entity.MembreOrganisation;
import com.charite.entity.Organisation;
import com.charite.entity.StatutOrganisation;
import com.charite.repository.MembreOrganisationRepository;
import com.charite.repository.OrganisationRepository;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final OrganisationRepository organisationRepository;

    public Utilisateur inscrire(UtilisateurDto dto) {
        if (utilisateurRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email deja utilise");
        }

        boolean isOrg = "ORG".equals(dto.getTypeCompte());
        String roleName = isOrg ? "ROLE_ORG_ADMIN" : "ROLE_USER";

        Role userRole = roleRepository.findByNom(roleName)
                .orElseThrow(() -> new RuntimeException("Role " + roleName + " introuvable - verifie data.sql"));

        Utilisateur user = Utilisateur.builder()
                .nom(isOrg ? dto.getNomOrganisation() : dto.getNom())
                .prenom(isOrg ? "" : dto.getPrenom())
                .email(dto.getEmail())
                .motDePasse(passwordEncoder.encode(dto.getMotDePasse()))
                .telephone(dto.getTelephone())
                .role(userRole)
                .provider("local")
                .actif(!isOrg) // L'orga est inactive (false) jusqu'a validation
                .build();

        Utilisateur savedUser = utilisateurRepository.save(user);

        if (isOrg) {
            Organisation org = Organisation.builder()
                    .nom(dto.getNomOrganisation())
                    .adresseLegale(dto.getAdresseLegale())
                    .matriculeFiscal(dto.getMatriculeFiscal())
                    .description(dto.getDescriptionMission())
                    .emailContact(dto.getEmail())
                    .contactPrincipal(dto.getNom() + " " + dto.getPrenom())
                    .telephone(dto.getTelephone())
                    .statutOrganisation(StatutOrganisation.EN_ATTENTE.name())
                    .build();

            Organisation savedOrg = organisationRepository.save(org);

            MembreOrganisation membre = MembreOrganisation.builder()
                    .utilisateur(savedUser)
                    .organisation(savedOrg)
                    .role("ADMIN")
                    .statutMembre("ACTIF")
                    .build();

            if (savedUser.getMemberships() == null) {
                savedUser.setMemberships(new ArrayList<>());
            }
            savedUser.getMemberships().add(membre);
            utilisateurRepository.save(savedUser);
        }

        return savedUser;
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

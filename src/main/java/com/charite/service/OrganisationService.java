package com.charite.service;

import com.charite.dto.OrganisationDto;
import com.charite.entity.MembreOrganisation;
import com.charite.entity.Organisation;
import com.charite.entity.Role;
import com.charite.entity.StatutOrganisation;
import com.charite.entity.Utilisateur;
import com.charite.repository.OrganisationRepository;
import com.charite.repository.RoleRepository;
import com.charite.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrganisationService {

    private final OrganisationRepository organisationRepository;
    private final UtilisateurRepository userRepository;
    private final RoleRepository roleRepository;
    private final EmailService emailService;

    public Organisation inscrire(OrganisationDto dto, String emailAdmin) {
        Organisation org = Organisation.builder()
                .nom(dto.getNom())
                .adresseLegale(dto.getAdresseLegale())
                .matriculeFiscal(dto.getMatriculeFiscal())
                .contactPrincipal(dto.getContactPrincipal())
                .emailContact(dto.getEmailContact())
                .telephone(dto.getTelephone())
                .description(dto.getDescriptionMission())
                .statutOrganisation(StatutOrganisation.EN_ATTENTE.name())
                .build();

        Organisation savedOrg = organisationRepository.save(org);

        Utilisateur admin = userRepository.findByEmail(emailAdmin)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouve"));

        // Ne PAS attribuer le role ORG_ADMIN ici. L'utilisateur reste ROLE_USER
        // jusqu'a ce que le Super Administrateur approuve l'organisation.

        MembreOrganisation membre = MembreOrganisation.builder()
                .utilisateur(admin)
                .organisation(savedOrg)
                .role("ADMIN")
                .statutMembre("ACTIF")
                .build();
                
        if (admin.getMemberships() == null) {
            admin.setMemberships(new java.util.ArrayList<>());
        }
        admin.getMemberships().add(membre);

        userRepository.save(admin);

        return savedOrg;
    }

    public void valider(Long orgId, boolean approuver) {
        Organisation org = organisationRepository.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organisation non trouvee"));

        if (approuver) {
            org.setStatutOrganisation(StatutOrganisation.APPROUVEE.name());
            
            // Si l'organisation est approuvee, on evalue l'utilisateur responsable pour lui donner les droits ORG_ADMIN
            if (org.getMembres() != null && !org.getMembres().isEmpty()) {
                Utilisateur admin = org.getMembres().get(0).getUtilisateur();
                Role orgAdminRole = roleRepository.findByNom("ROLE_ORG_ADMIN")
                        .orElseThrow(() -> new RuntimeException("Role ROLE_ORG_ADMIN non trouve"));
                admin.setRole(orgAdminRole);
                admin.setActif(true); // Activer l'utilisateur orga
                userRepository.save(admin);
            }
        } else {
            org.setStatutOrganisation(StatutOrganisation.REJETEE.name());
        }

        organisationRepository.save(org);

        try {
            emailService.envoyerEmailValidation(org.getEmailContact(), org.getNom(), approuver);
        } catch (Exception e) {
            System.err.println("Erreur email: " + e.getMessage());
        }
    }
    public Organisation enregistrer(Organisation org) {
        return organisationRepository.save(org);
    }
}

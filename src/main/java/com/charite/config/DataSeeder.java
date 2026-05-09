package com.charite.config;

import com.charite.entity.*;
import com.charite.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final CategorieRepository categorieRepository;
    private final ActionChariteRepository actionRepository;
    private final OrganisationRepository organisationRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        // 1. Creer le Super Admin seulement s'il n'existe pas deja
        Role adminRole = roleRepository.findByNom("ROLE_SUPER_ADMIN").orElse(null);
        if (adminRole != null && utilisateurRepository.findByEmail("admin@charity.com").isEmpty()) {
            Utilisateur superAdmin = Utilisateur.builder()
                    .nom("System")
                    .prenom("Admin")
                    .email("admin@charity.com")
                    .motDePasse(passwordEncoder.encode("admin123"))
                    .telephone("0000000000")
                    .role(adminRole)
                    .actif(true)
                    .provider("local")
                    .build();
            utilisateurRepository.save(superAdmin);
            System.out.println("============== SUPER ADMIN CREE ==============");
        }

        // 2. Créer une catégorie de test si besoin
        if (categorieRepository.count() == 0) {
            Categorie cat = new Categorie();
            cat.setNom("URGENCE");
            categorieRepository.save(cat);
        }

        // 3. Créer une organisation de test si besoin
        if (organisationRepository.count() == 0) {
            Organisation org = Organisation.builder()
                    .nom("Atlas Relief")
                    .description("Organisation dédiée au soutien des populations rurales.")
                    .emailContact("contact@atlasrelief.org")
                    .statutOrganisation("APPROUVEE")
                    .build();
            organisationRepository.save(org);
        }

        // 4. Créer une action de test s'il n'y en a aucune
        if (actionRepository.count() == 0) {
            Categorie cat = categorieRepository.findByNom("URGENCE").orElse(null);
            Organisation org = organisationRepository.findAll().stream().findFirst().orElse(null);

            if (cat != null && org != null) {
                ActionCharite testAction = ActionCharite.builder()
                        .titre("Urgence Atlas : Soutien aux villages isolés")
                        .description("Cette campagne vise à fournir des kits de première nécessité aux familles touchées par le froid dans les montagnes de l'Atlas.")
                        .montantObjectif(new BigDecimal("50000"))
                        .montantActuel(BigDecimal.ZERO)
                        .dateDebut(LocalDate.now())
                        .dateFin(LocalDate.now().plusMonths(1))
                        .lieu("Haut Atlas, Maroc")
                        .categorie(cat)
                        .organisation(org)
                        .archivee(false)
                        .statutAction("ACTIVE")
                        .build();
                actionRepository.save(testAction);
                System.out.println("============== ACTION DE TEST CREEE ==============");
            }
        }
    }
}

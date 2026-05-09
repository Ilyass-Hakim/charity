package com.charite.config;

import com.charite.entity.Role;
import com.charite.entity.Utilisateur;
import com.charite.repository.RoleRepository;
import com.charite.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        // Creer le Super Admin seulement s'il n'existe pas deja
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
            System.out.println("Email    : admin@charity.com");
            System.out.println("Password : admin123");
            System.out.println("==============================================");
        }
    }
}

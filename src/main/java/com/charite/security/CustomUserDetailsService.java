package com.charite.security;

import com.charite.entity.Utilisateur;
import com.charite.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UtilisateurRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Utilisateur user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException(
                "Utilisateur non trouve avec l'email : " + email));

        String roleName = user.getRole() != null ? user.getRole().getNom().replace("ROLE_", "") : "USER";

        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getEmail())
            .password(user.getMotDePasse() != null ? user.getMotDePasse() : "")
            .roles(roleName)
            .disabled(!user.isActif())
            .build();
    }
}

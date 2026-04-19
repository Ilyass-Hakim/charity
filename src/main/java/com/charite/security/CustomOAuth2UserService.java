package com.charite.security;

import com.charite.entity.Role;
import com.charite.entity.Utilisateur;
import com.charite.repository.RoleRepository;
import com.charite.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UtilisateurRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(request);

        String email = oAuth2User.getAttribute("email");
        String nom   = oAuth2User.getAttribute("family_name");
        String prenom = oAuth2User.getAttribute("given_name");
        String photo  = oAuth2User.getAttribute("picture");
        String providerId = oAuth2User.getAttribute("sub");

        // Cherche l'utilisateur existant ou en cree un nouveau
        Utilisateur user = userRepository.findByEmail(email)
            .orElseGet(() -> {
                // Fetch default role (e.g. ROLE_USER)
                Role userRole = roleRepository.findByNom("ROLE_USER").orElse(null);

                Utilisateur newUser = Utilisateur.builder()
                    .email(email)
                    .nom(nom != null ? nom : "")
                    .prenom(prenom != null ? prenom : "")
                    .photoProfil(photo)
                    .provider("google")
                    .providerId(providerId)
                    .role(userRole)
                    .actif(true)
                    .build();
                return userRepository.save(newUser);
            });

        // Met a jour la photo si elle a change
        if (photo != null && !photo.equals(user.getPhotoProfil())) {
            user.setPhotoProfil(photo);
            userRepository.save(user);
        }
        return oAuth2User;
    }
}

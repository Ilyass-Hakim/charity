package com.charite.dto;

import com.charite.entity.Utilisateur;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UtilisateurDto {
    private String typeCompte = "USER"; // "USER" ou "ORG"
    
    // Champs Utilisateur
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private String telephone;

    // Champs Organisation (si typeCompte == "ORG")
    private String nomOrganisation;
    private String adresseLegale;
    private String matriculeFiscal;
    private String descriptionMission;

    // Constructeur a partir de l'entite
    public UtilisateurDto(Utilisateur u) {
        this.nom = u.getNom();
        this.prenom = u.getPrenom();
        this.email = u.getEmail();
        this.telephone = u.getTelephone();
        // Le mot de passe n'est generalement pas renvoye pour des raisons de securite
    }
}

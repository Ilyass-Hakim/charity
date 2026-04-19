package com.charite.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity 
@Table(name = "utilisateurs") 
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder 
public class Utilisateur { 

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id; 

    @Column(nullable = false) 
    private String nom; 

    private String prenom;

    @Email 
    @Column(unique = true, nullable = false) 
    private String email; 

    private String motDePasse; // null si connexion OAuth 

    private String photoProfil;

    private boolean actif = true;

    @CreationTimestamp 
    @Column(updatable = false) 
    private LocalDateTime dateCreation; 

    private String provider;   // 'local' ou 'google' 
    private String providerId; // ID Google si OAuth 

    // Lien vers le role (entite JPA) 
    @ManyToOne(fetch = FetchType.EAGER) 
    @JoinColumn(name = "role_id") 
    private Role role; 

    // Memberships dans les organisations 
    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL) 
    private List<MembreOrganisation> memberships = new ArrayList<>(); 

    // Contributions effectuees (dons + participations) 
    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL) 
    private List<Contribution> contributions = new ArrayList<>(); 
}

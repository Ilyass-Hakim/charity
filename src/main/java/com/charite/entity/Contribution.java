package com.charite.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "contributions")
@Inheritance(strategy = InheritanceType.JOINED)
@Data @NoArgsConstructor @AllArgsConstructor
public abstract class Contribution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    private LocalDateTime date;

    // L'utilisateur auteur de la contribution 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    // Peut venir d'un membre d'organisation (relation englobe du diagramme) 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membre_organisation_id")
    private MembreOrganisation membreOrganisation;

    // L'action concernee 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_charite_id", nullable = false)
    private ActionCharite actionCharite;
}

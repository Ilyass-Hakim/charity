package com.charite.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "membres_organisation")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class MembreOrganisation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Role au sein de l'organisation : 'ADMIN', 'BENEVOLE', 'MEMBRE'
    @Column(nullable = false)
    private String role;

    @CreationTimestamp
    private LocalDateTime dateAdhesion;

    // 'ACTIF', 'INACTIF', 'SUSPENDU'
    private String statutMembre = "ACTIF";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id", nullable = false)
    private Organisation organisation;

    @OneToMany(mappedBy = "membreOrganisation", cascade = CascadeType.ALL)
    private List<Contribution> contributions = new ArrayList<>();
}

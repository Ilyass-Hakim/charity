package com.charite.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "actions_charite")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ActionCharite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String lieu;

    @Column(nullable = false)
    private BigDecimal montantObjectif;

    private BigDecimal montantActuel = BigDecimal.ZERO;

    private LocalDate dateDebut;
    private LocalDate dateFin;

    // 'ACTIVE', 'TERMINEE', 'ARCHIVEE'
    private String statutAction = "ACTIVE";

    // Lien vers Categorie (entite JPA)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categorie_id")
    private Categorie categorie;

    // Organisation qui a cree cette action
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id", nullable = false)
    private Organisation organisation;

    // Medias associes (images, videos)
    @OneToMany(mappedBy = "actionCharite", cascade = CascadeType.ALL)
    private List<Media> medias = new ArrayList<>();

    // Contributions recues (Dons + Participations)
    @OneToMany(mappedBy = "actionCharite", cascade = CascadeType.ALL)
    private List<Contribution> contributions = new ArrayList<>();
}
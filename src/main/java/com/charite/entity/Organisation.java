package com.charite.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "organisations")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Organisation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    private String adresseLegale;
    private String matriculeFiscal;
    private String emailContact;
    private String logo;
    private String description;

    // 'EN_ATTENTE', 'APPROUVEE', 'REJETEE'
    private String statutOrganisation = "EN_ATTENTE";

    @CreationTimestamp
    private LocalDateTime dateInscription;

    @OneToMany(mappedBy = "organisation", cascade = CascadeType.ALL)
    private List<MembreOrganisation> membres = new ArrayList<>();

    @OneToMany(mappedBy = "organisation", cascade = CascadeType.ALL)
    private List<ActionCharite> actions = new ArrayList<>();
}
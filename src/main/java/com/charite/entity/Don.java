package com.charite.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "dons")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
@PrimaryKeyJoinColumn(name = "contribution_id")
public class Don extends Contribution {

    @Column(nullable = false)
    private BigDecimal montant;

    // 'CARTE', 'VIREMENT', 'STRIPE'
    private String methodePaiement;

    // 'EN_ATTENTE', 'CONFIRME', 'ECHOUE', 'REMBOURSE'
    private String statutPaiement = "EN_ATTENTE";

    // ID retourne par Stripe apres le paiement
    private String identifiantTransaction;
}
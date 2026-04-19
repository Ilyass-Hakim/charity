package com.charite.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "participations")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
@PrimaryKeyJoinColumn(name = "contribution_id")
public class Participation extends Contribution {

    // 'INSCRIT', 'PRESENT', 'ABSENT', 'ANNULE'
    private String statutParticipation = "INSCRIT";
}
package com.charite.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "medias")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String url; // chemin local ou URL Cloudinary

    private String type; // 'image/jpeg', 'image/png', 'video/mp4'
    private String nom;  // nom original du fichier

    @CreationTimestamp
    private LocalDateTime dateUpload;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_charite_id", nullable = false)
    private ActionCharite actionCharite;
}

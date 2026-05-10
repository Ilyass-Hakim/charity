package com.charite.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "medias")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Media {

    @Id
    private String id;

    private String url; // chemin local ou URL Cloudinary

    private String type; // 'image/jpeg', 'image/png', 'video/mp4'
    private String nom;  // nom original du fichier

    @Builder.Default
    private LocalDateTime dateUpload = LocalDateTime.now();

    private Long actionChariteId; // Lien vers l'entite JPA ActionCharite
}

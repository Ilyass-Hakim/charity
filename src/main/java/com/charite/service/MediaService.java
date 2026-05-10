package com.charite.service;

import com.charite.entity.ActionCharite;
import com.charite.entity.Media;
import com.charite.repository.MediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaRepository mediaRepository;

    @Value("${file.upload-dir:uploads/}")
    private String uploadDir;

    public Media sauvegarder(MultipartFile fichier, ActionCharite action) {
        if (fichier.isEmpty()) return null;
        String type = fichier.getContentType();
        if (type == null || (!type.startsWith("image/") && !type.startsWith("video/"))) {
            throw new RuntimeException("Type non supporte : " + type);
        }

        String originalName = fichier.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }
        String nomFichier = UUID.randomUUID() + ext;

        try {
            Path dest = Paths.get(uploadDir).resolve(nomFichier);
            Files.createDirectories(dest.getParent());
            Files.copy(fichier.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Erreur upload", e);
        }

        return mediaRepository.save(Media.builder()
                .url("/uploads/" + nomFichier)
                .type(type)
                .nom(fichier.getOriginalFilename())
                .actionChariteId(action.getId())
                .build());
    }
}

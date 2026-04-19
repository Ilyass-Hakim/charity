package com.charite.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Component
public class FileStorageUtil {

    @Value("${file.upload-dir:uploads/}")
    private String uploadDir;

    public String sauvegarderFichier(MultipartFile fichier) {
        String contentType = fichier.getContentType();
        if (contentType == null ||
                (!contentType.startsWith("image/") &&
                        !contentType.startsWith("video/"))) {
            throw new RuntimeException("Type de fichier non supporte");
        }

        String extension = Objects.requireNonNull(
                fichier.getOriginalFilename()).split("\\.")[1];
        String nomFichier = UUID.randomUUID() + "." + extension;

        Path chemin = Paths.get(uploadDir).resolve(nomFichier);
        try {
            Files.createDirectories(chemin.getParent());
            Files.copy(fichier.getInputStream(), chemin,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la sauvegarde", e);
        }

        return "/uploads/" + nomFichier;
    }
}

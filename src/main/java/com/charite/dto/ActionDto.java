package com.charite.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ActionDto {
    private String titre;
    private String description;
    private String categorieNom;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String lieu;
    private BigDecimal montantObjectif;
}

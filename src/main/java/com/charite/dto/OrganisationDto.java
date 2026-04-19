package com.charite.dto;

import lombok.Data;

@Data
public class OrganisationDto {
    private String nom;
    private String adresseLegale;
    private String matriculeFiscal;
    private String contactPrincipal;
    private String emailContact;
    private String telephone;
    private String descriptionMission;
}

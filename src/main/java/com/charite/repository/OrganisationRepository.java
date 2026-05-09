package com.charite.repository;

import com.charite.entity.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganisationRepository extends JpaRepository<Organisation, Long> {
    List<Organisation> findByStatutOrganisation(String statut);
    long countByStatutOrganisation(String statut);
}

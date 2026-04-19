package com.charite.repository;

import com.charite.entity.MembreOrganisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembreOrganisationRepository extends JpaRepository<MembreOrganisation, Long> {
    List<MembreOrganisation> findByOrganisationId(Long orgId);
    Optional<MembreOrganisation> findByUtilisateurIdAndOrganisationId(Long utilisateurId, Long organisationId);
}

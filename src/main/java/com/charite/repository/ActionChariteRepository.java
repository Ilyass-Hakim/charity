package com.charite.repository;

import com.charite.entity.ActionCharite;
import com.charite.entity.Categorie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActionChariteRepository extends JpaRepository<ActionCharite, Long> {
    Page<ActionCharite> findByStatutActionNot(String statut, Pageable pageable);
    List<ActionCharite> findByCategorieAndStatutAction(Categorie cat, String statut);
    List<ActionCharite> findByOrganisationId(Long orgId);
    
    Page<ActionCharite> findByArchiveeFalse(Pageable pageable);
    Page<ActionCharite> findByCategorieAndArchiveeFalse(Categorie cat, Pageable pageable);

    @Query("SELECT a FROM ActionCharite a WHERE a.archivee = false AND "
         + "(LOWER(a.titre) LIKE LOWER(CONCAT('%',:kw,'%')) OR "
         + "LOWER(a.description) LIKE LOWER(CONCAT('%',:kw,'%')))")
    Page<ActionCharite> searchByKeyword(@Param("kw") String keyword, Pageable p);

    @Query("SELECT SUM(a.montantActuel) FROM ActionCharite a")
    java.math.BigDecimal getTotalGlobalDonations();

    @Query("SELECT COUNT(a) FROM ActionCharite a WHERE a.archivee = false")
    long countAllActiveActions();
}

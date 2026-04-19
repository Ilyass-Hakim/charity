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

    @Query("SELECT a FROM ActionCharite a WHERE a.statutAction = 'ACTIVE' AND "
         + "(LOWER(a.titre) LIKE LOWER(CONCAT('%',:kw,'%')) OR "
         + "LOWER(a.description) LIKE LOWER(CONCAT('%',:kw,'%')))")
    Page<ActionCharite> search(@Param("kw") String keyword, Pageable p);
}

package com.charite.repository;

import com.charite.entity.Don;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface DonRepository extends JpaRepository<Don, Long> {
    List<Don> findByUtilisateurId(Long userId);
    List<Don> findByActionChariteId(Long actionId);

    @Query("SELECT SUM(d.montant) FROM Don d WHERE d.actionCharite.id = :id"
         + " AND d.statutPaiement = 'CONFIRME'")
    BigDecimal sumMontantByActionId(@Param("id") Long actionId);
}

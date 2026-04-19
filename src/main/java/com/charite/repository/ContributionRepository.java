package com.charite.repository;

import com.charite.entity.Contribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContributionRepository extends JpaRepository<Contribution, Long> {
    List<Contribution> findByUtilisateurId(Long userId);
    List<Contribution> findByActionChariteId(Long actionId);
}

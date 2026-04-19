package com.charite.repository;

import com.charite.entity.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    List<Participation> findByUtilisateurId(Long userId);
    List<Participation> findByActionChariteId(Long actionId);
}

package com.charite.service;

import com.charite.dto.ActionDto;
import com.charite.entity.ActionCharite;
import com.charite.entity.Categorie;
import com.charite.entity.Organisation;
import com.charite.entity.StatutOrganisation;
import com.charite.repository.ActionChariteRepository;
import com.charite.repository.CategorieRepository;
import com.charite.repository.MediaRepository;
import com.charite.repository.OrganisationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class ActionService {

    private final ActionChariteRepository actionRepository;
    private final OrganisationRepository orgRepository;
    private final CategorieRepository categorieRepository;
    private final MediaRepository mediaRepository;

    public ActionCharite creer(ActionDto dto, Long organisationId) {
        Organisation org = orgRepository.findById(organisationId)
                .orElseThrow(() -> new RuntimeException("Organisation non trouvee"));

        if (!StatutOrganisation.APPROUVEE.name().equals(org.getStatutOrganisation())) {
            throw new RuntimeException("L'organisation doit etre approuvee");
        }
        
        Categorie cat = categorieRepository.findByNom(dto.getCategorieNom().toUpperCase())
                .orElseThrow(() -> new RuntimeException("Categorie non trouvee"));

        ActionCharite action = ActionCharite.builder()
                .titre(dto.getTitre())
                .description(dto.getDescription())
                .categorie(cat)
                .dateDebut(dto.getDateDebut())
                .dateFin(dto.getDateFin())
                .lieu(dto.getLieu())
                .montantObjectif(dto.getMontantObjectif())
                .montantActuel(BigDecimal.ZERO)
                .organisation(org)
                .archivee(false)
                .statutAction("ACTIVE")
                .build();

        return actionRepository.save(action);
    }

    public Page<ActionCharite> explorer(String categorie,
                                        String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("dateCreation").descending());

        Page<ActionCharite> actions = getActions(categorie, keyword, pageable);
        actions.getContent().forEach(this::populateMedias);
        return actions;
    }

    private Page<ActionCharite> getActions(String categorie, String keyword, Pageable pageable) {
        if (keyword != null && !keyword.isEmpty()) {
            return actionRepository.searchByKeyword(keyword, pageable);
        }
        if (categorie != null && !categorie.isEmpty()) {
            Categorie cat = categorieRepository.findByNom(categorie.toUpperCase())
                    .orElse(null);
            if (cat != null) {
                return actionRepository.findByCategorieAndArchiveeFalse(cat, pageable);
            }
        }
        return actionRepository.findByArchiveeFalse(pageable);
    }

    public java.util.List<ActionCharite> findByOrganisation(Long orgId) {
        java.util.List<ActionCharite> actions = actionRepository.findByOrganisationId(orgId);
        if (actions != null) {
            actions.forEach(this::populateMedias);
        }
        return actions;
    }

    public void archiver(Long actionId) {
        ActionCharite action = actionRepository.findById(actionId)
                .orElseThrow(() -> new RuntimeException("Action non trouvee"));

        action.setArchivee(true);
        action.setStatutAction("ARCHIVEE");
        actionRepository.save(action);
    }

    public ActionCharite findById(Long actionId) {
        ActionCharite action = actionRepository.findById(actionId)
                .orElseThrow(() -> new RuntimeException("Action non trouvee"));
        populateMedias(action);
        return action;
    }

    private void populateMedias(ActionCharite action) {
        if (action != null && action.getId() != null) {
            action.setMedias(mediaRepository.findByActionChariteId(action.getId()));
        }
    }

    public BigDecimal getTotalGlobalDonations() {
        BigDecimal total = actionRepository.getTotalGlobalDonations();
        return total != null ? total : BigDecimal.ZERO;
    }

    public long countAllActiveActions() {
        return actionRepository.countAllActiveActions();
    }
}

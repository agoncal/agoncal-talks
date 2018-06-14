package io.quantixx.sponsor.service;

import io.quantixx.sponsor.domain.Organisation;
import io.quantixx.sponsor.repository.OrganisationRepositoryExtended;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class OrganisationServiceExtended extends OrganisationService {

    private final Logger log = LoggerFactory.getLogger(OrganisationServiceExtended.class);

    private final OrganisationRepositoryExtended organisationRepository;

    public OrganisationServiceExtended(OrganisationRepositoryExtended organisationRepository) {
        super(organisationRepository);
        this.organisationRepository = organisationRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Organisation> findOneWithEagerRelationships(Long id) {
        log.debug("Request to get Organisation with Eager Relationship: {}", id);
        return organisationRepository.findOneWithEagerRelationships(id);
    }
}

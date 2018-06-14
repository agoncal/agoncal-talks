package io.quantixx.sponsor.service;

import io.quantixx.sponsor.domain.Organisation;
import io.quantixx.sponsor.repository.OrganisationRepositoryExtended;
import io.quantixx.sponsor.service.dto.OrganisationWithContactsDTO;
import io.quantixx.sponsor.service.mapper.OrganisationWithContactsMapper;
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

    private final OrganisationWithContactsMapper organisationMapper;

    public OrganisationServiceExtended(OrganisationRepositoryExtended organisationRepository, OrganisationWithContactsMapper organisationMapper) {
        super(organisationRepository, organisationMapper);
        this.organisationRepository = organisationRepository;
        this.organisationMapper = organisationMapper;
    }

    @Transactional(readOnly = true)
    public Optional<OrganisationWithContactsDTO> findOneWithEagerRelationships(Long id) {
        log.debug("Request to get Organisation with Eager Relationship: {}", id);
        return organisationRepository.findOneWithEagerRelationships(id)
            .map(organisationMapper::toDto);
    }

    public Organisation save(Organisation organisation) {
        log.debug("Request to save Organisation : {}", organisation);
        organisation = organisationRepository.save(organisation);
        return organisation;
    }
}

package io.quantixx.sponsor.service;

import io.quantixx.sponsor.domain.Address;
import io.quantixx.sponsor.domain.Organisation;
import io.quantixx.sponsor.repository.AddressRepository;
import io.quantixx.sponsor.repository.OrganisationExtendedRepository;
import io.quantixx.sponsor.repository.OrganisationRepository;
import io.quantixx.sponsor.service.dto.OrganisationDTO;
import io.quantixx.sponsor.service.mapper.OrganisationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@Transactional
public class OrganisationExtendedService extends OrganisationService {

    private final Logger log = LoggerFactory.getLogger(OrganisationExtendedService.class);

    private final OrganisationExtendedRepository organisationRepository;

    private final OrganisationMapper organisationMapper;

    public OrganisationExtendedService(OrganisationExtendedRepository organisationRepository, OrganisationMapper organisationMapper) {
        super(organisationRepository, organisationMapper);
        this.organisationRepository = organisationRepository;
        this.organisationMapper = organisationMapper;
    }

    /**
     * Get one organisation by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Optional<OrganisationDTO> findOne(Long id) {
        log.debug("Request to get Organisation with Eager Relationship: {}", id);
        return organisationRepository.findOneWithEagerRelationships(id)
            .map(organisationMapper::toDto);
    }
}

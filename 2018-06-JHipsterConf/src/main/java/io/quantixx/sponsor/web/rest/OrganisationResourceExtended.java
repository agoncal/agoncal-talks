package io.quantixx.sponsor.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.jhipster.web.util.ResponseUtil;
import io.quantixx.sponsor.domain.Organisation;
import io.quantixx.sponsor.service.OrganisationQueryService;
import io.quantixx.sponsor.service.OrganisationServiceExtended;
import io.quantixx.sponsor.service.dto.OrganisationCriteria;
import io.quantixx.sponsor.web.rest.errors.BadRequestAlertException;
import io.quantixx.sponsor.web.rest.util.HeaderUtil;
import io.quantixx.sponsor.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Organisation.
 */
@RestController
@RequestMapping("/api/extended")
public class OrganisationResourceExtended {

    private final Logger log = LoggerFactory.getLogger(OrganisationResourceExtended.class);

    private static final String ENTITY_NAME = "organisation";

    private final OrganisationServiceExtended organisationService;

    private final OrganisationQueryService organisationQueryService;

    public OrganisationResourceExtended(OrganisationServiceExtended organisationService, OrganisationQueryService organisationQueryService) {
        this.organisationService = organisationService;
        this.organisationQueryService = organisationQueryService;
    }

    /**
     * POST  /organisations : Create a new organisation.
     *
     * @param organisation the organisation to create
     * @return the ResponseEntity with status 201 (Created) and with body the new organisation, or with status 400 (Bad Request) if the organisation has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/organisations")
    @Timed
    public ResponseEntity<Organisation> createOrganisation(@Valid @RequestBody Organisation organisation) throws URISyntaxException {
        log.debug("REST request to save Organisation : {}", organisation);
        if (organisation.getId() != null) {
            throw new BadRequestAlertException("A new organisation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Organisation result = organisationService.save(organisation);
        return ResponseEntity.created(new URI("/api/organisations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /organisations : Updates an existing organisation.
     *
     * @param organisation the organisation to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated organisation,
     * or with status 400 (Bad Request) if the organisation is not valid,
     * or with status 500 (Internal Server Error) if the organisation couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/organisations")
    @Timed
    public ResponseEntity<Organisation> updateOrganisation(@Valid @RequestBody Organisation organisation) throws URISyntaxException {
        log.debug("REST request to update Organisation : {}", organisation);
        if (organisation.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Organisation result = organisationService.save(organisation);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, organisation.getId().toString()))
            .body(result);
    }

    /**
     * GET  /organisations : get all the organisations.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of organisations in body
     */
    @GetMapping("/organisations")
    @Timed
    public ResponseEntity<List<Organisation>> getAllOrganisations(OrganisationCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Organisations by criteria: {}", criteria);
        Page<Organisation> page = organisationQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/organisations");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /organisations/:id : get the "id" organisation.
     *
     * @param id the id of the organisation to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the organisation, or with status 404 (Not Found)
     */
    @GetMapping("/organisations/{id}")
    @Timed
    public ResponseEntity<Organisation> getOrganisation(@PathVariable Long id) {
        log.debug("REST request to get Organisation : {}", id);
        Optional<Organisation> organisation = organisationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(organisation);
    }

    /**
     * DELETE  /organisations/:id : delete the "id" organisation.
     *
     * @param id the id of the organisation to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/organisations/{id}")
    @Timed
    public ResponseEntity<Void> deleteOrganisation(@PathVariable Long id) {
        log.debug("REST request to delete Organisation : {}", id);
        organisationService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}

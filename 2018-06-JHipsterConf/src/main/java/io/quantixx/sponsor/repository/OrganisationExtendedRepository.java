package io.quantixx.sponsor.repository;

import io.quantixx.sponsor.domain.Organisation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the Organisation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrganisationExtendedRepository extends OrganisationRepository  {

    @Query("select spn_organisation from Organisation spn_organisation left join fetch spn_organisation.contacts where spn_organisation.id =:id")
    Optional<Organisation> findOneWithEagerRelationships(@Param("id") Long id);
}

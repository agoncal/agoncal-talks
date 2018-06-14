package io.quantixx.sponsor.service;

import io.quantixx.sponsor.SponsorApp;
import io.quantixx.sponsor.domain.Contact;
import io.quantixx.sponsor.domain.Organisation;
import io.quantixx.sponsor.service.dto.OrganisationWithContactsDTO;
import io.quantixx.sponsor.web.rest.ContactResourceIntTest;
import io.quantixx.sponsor.web.rest.OrganisationResourceIntTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SponsorApp.class)
public class OrganisationServiceExtendedIntTest {

    @Autowired
    private EntityManager em;

    private Organisation organisation;

    @Autowired
    private OrganisationServiceExtended organisationService;

    @Autowired
    private ContactServiceExtended contactService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @Transactional
    public void shouldGetAnOrgnisationWithContacts() throws Exception {
        organisation = OrganisationResourceIntTest.createEntity(em);
        Contact contact1 = ContactResourceIntTest.createEntity(em);
        contact1.setOrganisation(organisation);
        Contact contact2 = ContactResourceIntTest.createEntity(em);
        contact2.setOrganisation(organisation);
        Contact contact3 = ContactResourceIntTest.createEntity(em);
        contact3.setOrganisation(organisation);
        organisation.addContacts(contact1);
        organisation.addContacts(contact2);
        organisation.addContacts(contact3);

        assertThat(organisation.getId()).isNull();
        assertThat(organisation.getContacts().iterator().next().getId()).isNull();

        // Create organisation and contacts
        organisation = organisationService.save(organisation);
        contactService.save(contact1);
        contactService.save(contact2);
        contactService.save(contact3);
        assertThat(organisation.getId()).isNotNull();
        assertThat(organisation.getContacts().size()).isEqualTo(3);
        assertThat(organisation.getContacts().iterator().next().getId()).isNotNull();


        // Should find the organisation with contacts
        OrganisationWithContactsDTO organisationDTO = organisationService.findOneWithEagerRelationships(organisation.getId()).get();
        assertThat(organisationDTO.getId()).isNotNull();
        assertThat(organisationDTO.getContacts().size()).isEqualTo(3);
        assertThat(organisation.getContacts().iterator().next().getId()).isNotNull();
    }

}

package io.quantixx.sponsor.web.rest;

import io.quantixx.sponsor.SponsorApp;
import io.quantixx.sponsor.domain.Address;
import io.quantixx.sponsor.domain.Contact;
import io.quantixx.sponsor.domain.IndustrySector;
import io.quantixx.sponsor.domain.Organisation;
import io.quantixx.sponsor.repository.OrganisationRepositoryExtended;
import io.quantixx.sponsor.service.OrganisationQueryService;
import io.quantixx.sponsor.service.OrganisationServiceExtended;
import io.quantixx.sponsor.web.rest.errors.ExceptionTranslator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static io.quantixx.sponsor.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the OrganisationResource REST controller.
 *
 * @see OrganisationResourceExtended
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SponsorApp.class)
public class OrganisationResourceExtendedIntTest {

    private static final String DEFAULT_SLUG = "AAAAAAAAAA";
    private static final String UPDATED_SLUG = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_BUSINESS_IDENTIFICATION = "AAAAAAAAAA";
    private static final String UPDATED_BUSINESS_IDENTIFICATION = "BBBBBBBBBB";

    private static final String DEFAULT_CORPORATE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_CORPORATE_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DOMAIN_NAME = "AAAAAAAAAA";
    private static final String UPDATED_DOMAIN_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE_AREA = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_AREA = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_VAT_IDENTIFICATION = "AAAAAAAAAA";
    private static final String UPDATED_VAT_IDENTIFICATION = "BBBBBBBBBB";

    private static final String DEFAULT_WEB_SITE = "AAAAAAAAAA";
    private static final String UPDATED_WEB_SITE = "BBBBBBBBBB";

    private static final String DEFAULT_ADDITIONAL_INFORMATION = "AAAAAAAAAA";
    private static final String UPDATED_ADDITIONAL_INFORMATION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVATED = false;
    private static final Boolean UPDATED_ACTIVATED = true;

    private static final Instant DEFAULT_CREATED_ON = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_ON = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private OrganisationRepositoryExtended organisationRepository;


    @Autowired
    private OrganisationServiceExtended organisationService;

    @Autowired
    private OrganisationQueryService organisationQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restOrganisationMockMvc;

    private Organisation organisation;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final OrganisationResourceExtended organisationResource = new OrganisationResourceExtended(organisationService, organisationQueryService);
        this.restOrganisationMockMvc = MockMvcBuilders.standaloneSetup(organisationResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Organisation createEntity(EntityManager em) {
        Organisation organisation = new Organisation()
            .slug(DEFAULT_SLUG)
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .businessIdentification(DEFAULT_BUSINESS_IDENTIFICATION)
            .corporateName(DEFAULT_CORPORATE_NAME)
            .domainName(DEFAULT_DOMAIN_NAME)
            .phoneArea(DEFAULT_PHONE_AREA)
            .phoneNumber(DEFAULT_PHONE_NUMBER)
            .vatIdentification(DEFAULT_VAT_IDENTIFICATION)
            .webSite(DEFAULT_WEB_SITE)
            .additionalInformation(DEFAULT_ADDITIONAL_INFORMATION)
            .activated(DEFAULT_ACTIVATED)
            .createdOn(DEFAULT_CREATED_ON);
        return organisation;
    }

    @Before
    public void initTest() {
        organisation = createEntity(em);
        Contact contact1 = ContactResourceIntTest.createEntity(em);
        contact1.setOrganisation(organisation);
        Contact contact2 = ContactResourceIntTest.createEntity(em);
        contact2.setOrganisation(organisation);
        Contact contact3 = ContactResourceIntTest.createEntity(em);
        contact3.setOrganisation(organisation);
        organisation.addContacts(contact1);
        organisation.addContacts(contact2);
        organisation.addContacts(contact3);
        em.flush();

    }

    @Test
    @Transactional
    public void createOrganisationWithContacts() throws Exception {
        int databaseSizeBeforeCreate = organisationRepository.findAll().size();

        // Create the Organisation
        restOrganisationMockMvc.perform(post("/api/extended/organisations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(organisation)))
            .andExpect(status().isCreated());

        // Validate the Organisation in the database
        List<Organisation> organisationList = organisationRepository.findAll();
        assertThat(organisationList).hasSize(databaseSizeBeforeCreate + 1);
        Organisation testOrganisation = organisationList.get(organisationList.size() - 1);
        assertThat(testOrganisation.getSlug()).isEqualTo(DEFAULT_SLUG);
        assertThat(testOrganisation.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testOrganisation.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testOrganisation.getBusinessIdentification()).isEqualTo(DEFAULT_BUSINESS_IDENTIFICATION);
        assertThat(testOrganisation.getCorporateName()).isEqualTo(DEFAULT_CORPORATE_NAME);
        assertThat(testOrganisation.getDomainName()).isEqualTo(DEFAULT_DOMAIN_NAME);
        assertThat(testOrganisation.getPhoneArea()).isEqualTo(DEFAULT_PHONE_AREA);
        assertThat(testOrganisation.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
        assertThat(testOrganisation.getVatIdentification()).isEqualTo(DEFAULT_VAT_IDENTIFICATION);
        assertThat(testOrganisation.getWebSite()).isEqualTo(DEFAULT_WEB_SITE);
        assertThat(testOrganisation.getAdditionalInformation()).isEqualTo(DEFAULT_ADDITIONAL_INFORMATION);
        assertThat(testOrganisation.isActivated()).isEqualTo(DEFAULT_ACTIVATED);
        assertThat(testOrganisation.getCreatedOn()).isEqualTo(DEFAULT_CREATED_ON);


        Organisation organisationWithContacts = organisationRepository.findOneWithEagerRelationships(testOrganisation.getId()).get();
        assertThat(organisationWithContacts.getContacts()).isEqualTo(3);

    }

    @Test
    @Transactional
    public void createOrganisationWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = organisationRepository.findAll().size();

        // Create the Organisation with an existing ID
        organisation.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrganisationMockMvc.perform(post("/api/extended/organisations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(organisation)))
            .andExpect(status().isBadRequest());

        // Validate the Organisation in the database
        List<Organisation> organisationList = organisationRepository.findAll();
        assertThat(organisationList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkSlugIsRequired() throws Exception {
        int databaseSizeBeforeTest = organisationRepository.findAll().size();
        // set the field null
        organisation.setSlug(null);

        // Create the Organisation, which fails.

        restOrganisationMockMvc.perform(post("/api/extended/organisations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(organisation)))
            .andExpect(status().isBadRequest());

        List<Organisation> organisationList = organisationRepository.findAll();
        assertThat(organisationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = organisationRepository.findAll().size();
        // set the field null
        organisation.setName(null);

        // Create the Organisation, which fails.

        restOrganisationMockMvc.perform(post("/api/extended/organisations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(organisation)))
            .andExpect(status().isBadRequest());

        List<Organisation> organisationList = organisationRepository.findAll();
        assertThat(organisationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllOrganisations() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList
        restOrganisationMockMvc.perform(get("/api/extended/organisations?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(organisation.getId().intValue())))
            .andExpect(jsonPath("$.[*].slug").value(hasItem(DEFAULT_SLUG.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].businessIdentification").value(hasItem(DEFAULT_BUSINESS_IDENTIFICATION.toString())))
            .andExpect(jsonPath("$.[*].corporateName").value(hasItem(DEFAULT_CORPORATE_NAME.toString())))
            .andExpect(jsonPath("$.[*].domainName").value(hasItem(DEFAULT_DOMAIN_NAME.toString())))
            .andExpect(jsonPath("$.[*].phoneArea").value(hasItem(DEFAULT_PHONE_AREA.toString())))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER.toString())))
            .andExpect(jsonPath("$.[*].vatIdentification").value(hasItem(DEFAULT_VAT_IDENTIFICATION.toString())))
            .andExpect(jsonPath("$.[*].webSite").value(hasItem(DEFAULT_WEB_SITE.toString())))
            .andExpect(jsonPath("$.[*].additionalInformation").value(hasItem(DEFAULT_ADDITIONAL_INFORMATION.toString())))
            .andExpect(jsonPath("$.[*].activated").value(hasItem(DEFAULT_ACTIVATED.booleanValue())))
            .andExpect(jsonPath("$.[*].createdOn").value(hasItem(DEFAULT_CREATED_ON.toString())));
    }


    @Test
    @Transactional
    public void getOrganisation() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get the organisation
        restOrganisationMockMvc.perform(get("/api/extended/organisations/{id}", organisation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(organisation.getId().intValue()))
            .andExpect(jsonPath("$.slug").value(DEFAULT_SLUG.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.businessIdentification").value(DEFAULT_BUSINESS_IDENTIFICATION.toString()))
            .andExpect(jsonPath("$.corporateName").value(DEFAULT_CORPORATE_NAME.toString()))
            .andExpect(jsonPath("$.domainName").value(DEFAULT_DOMAIN_NAME.toString()))
            .andExpect(jsonPath("$.phoneArea").value(DEFAULT_PHONE_AREA.toString()))
            .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER.toString()))
            .andExpect(jsonPath("$.vatIdentification").value(DEFAULT_VAT_IDENTIFICATION.toString()))
            .andExpect(jsonPath("$.webSite").value(DEFAULT_WEB_SITE.toString()))
            .andExpect(jsonPath("$.additionalInformation").value(DEFAULT_ADDITIONAL_INFORMATION.toString()))
            .andExpect(jsonPath("$.activated").value(DEFAULT_ACTIVATED.booleanValue()))
            .andExpect(jsonPath("$.createdOn").value(DEFAULT_CREATED_ON.toString()));
    }

    @Test
    @Transactional
    public void getAllOrganisationsBySlugIsEqualToSomething() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where slug equals to DEFAULT_SLUG
        defaultOrganisationShouldBeFound("slug.equals=" + DEFAULT_SLUG);

        // Get all the organisationList where slug equals to UPDATED_SLUG
        defaultOrganisationShouldNotBeFound("slug.equals=" + UPDATED_SLUG);
    }

    @Test
    @Transactional
    public void getAllOrganisationsBySlugIsInShouldWork() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where slug in DEFAULT_SLUG or UPDATED_SLUG
        defaultOrganisationShouldBeFound("slug.in=" + DEFAULT_SLUG + "," + UPDATED_SLUG);

        // Get all the organisationList where slug equals to UPDATED_SLUG
        defaultOrganisationShouldNotBeFound("slug.in=" + UPDATED_SLUG);
    }

    @Test
    @Transactional
    public void getAllOrganisationsBySlugIsNullOrNotNull() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where slug is not null
        defaultOrganisationShouldBeFound("slug.specified=true");

        // Get all the organisationList where slug is null
        defaultOrganisationShouldNotBeFound("slug.specified=false");
    }

    @Test
    @Transactional
    public void getAllOrganisationsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where name equals to DEFAULT_NAME
        defaultOrganisationShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the organisationList where name equals to UPDATED_NAME
        defaultOrganisationShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllOrganisationsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where name in DEFAULT_NAME or UPDATED_NAME
        defaultOrganisationShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the organisationList where name equals to UPDATED_NAME
        defaultOrganisationShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllOrganisationsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where name is not null
        defaultOrganisationShouldBeFound("name.specified=true");

        // Get all the organisationList where name is null
        defaultOrganisationShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllOrganisationsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where description equals to DEFAULT_DESCRIPTION
        defaultOrganisationShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the organisationList where description equals to UPDATED_DESCRIPTION
        defaultOrganisationShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllOrganisationsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultOrganisationShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the organisationList where description equals to UPDATED_DESCRIPTION
        defaultOrganisationShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllOrganisationsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where description is not null
        defaultOrganisationShouldBeFound("description.specified=true");

        // Get all the organisationList where description is null
        defaultOrganisationShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    public void getAllOrganisationsByBusinessIdentificationIsEqualToSomething() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where businessIdentification equals to DEFAULT_BUSINESS_IDENTIFICATION
        defaultOrganisationShouldBeFound("businessIdentification.equals=" + DEFAULT_BUSINESS_IDENTIFICATION);

        // Get all the organisationList where businessIdentification equals to UPDATED_BUSINESS_IDENTIFICATION
        defaultOrganisationShouldNotBeFound("businessIdentification.equals=" + UPDATED_BUSINESS_IDENTIFICATION);
    }

    @Test
    @Transactional
    public void getAllOrganisationsByBusinessIdentificationIsInShouldWork() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where businessIdentification in DEFAULT_BUSINESS_IDENTIFICATION or UPDATED_BUSINESS_IDENTIFICATION
        defaultOrganisationShouldBeFound("businessIdentification.in=" + DEFAULT_BUSINESS_IDENTIFICATION + "," + UPDATED_BUSINESS_IDENTIFICATION);

        // Get all the organisationList where businessIdentification equals to UPDATED_BUSINESS_IDENTIFICATION
        defaultOrganisationShouldNotBeFound("businessIdentification.in=" + UPDATED_BUSINESS_IDENTIFICATION);
    }

    @Test
    @Transactional
    public void getAllOrganisationsByBusinessIdentificationIsNullOrNotNull() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where businessIdentification is not null
        defaultOrganisationShouldBeFound("businessIdentification.specified=true");

        // Get all the organisationList where businessIdentification is null
        defaultOrganisationShouldNotBeFound("businessIdentification.specified=false");
    }

    @Test
    @Transactional
    public void getAllOrganisationsByCorporateNameIsEqualToSomething() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where corporateName equals to DEFAULT_CORPORATE_NAME
        defaultOrganisationShouldBeFound("corporateName.equals=" + DEFAULT_CORPORATE_NAME);

        // Get all the organisationList where corporateName equals to UPDATED_CORPORATE_NAME
        defaultOrganisationShouldNotBeFound("corporateName.equals=" + UPDATED_CORPORATE_NAME);
    }

    @Test
    @Transactional
    public void getAllOrganisationsByCorporateNameIsInShouldWork() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where corporateName in DEFAULT_CORPORATE_NAME or UPDATED_CORPORATE_NAME
        defaultOrganisationShouldBeFound("corporateName.in=" + DEFAULT_CORPORATE_NAME + "," + UPDATED_CORPORATE_NAME);

        // Get all the organisationList where corporateName equals to UPDATED_CORPORATE_NAME
        defaultOrganisationShouldNotBeFound("corporateName.in=" + UPDATED_CORPORATE_NAME);
    }

    @Test
    @Transactional
    public void getAllOrganisationsByCorporateNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where corporateName is not null
        defaultOrganisationShouldBeFound("corporateName.specified=true");

        // Get all the organisationList where corporateName is null
        defaultOrganisationShouldNotBeFound("corporateName.specified=false");
    }

    @Test
    @Transactional
    public void getAllOrganisationsByDomainNameIsEqualToSomething() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where domainName equals to DEFAULT_DOMAIN_NAME
        defaultOrganisationShouldBeFound("domainName.equals=" + DEFAULT_DOMAIN_NAME);

        // Get all the organisationList where domainName equals to UPDATED_DOMAIN_NAME
        defaultOrganisationShouldNotBeFound("domainName.equals=" + UPDATED_DOMAIN_NAME);
    }

    @Test
    @Transactional
    public void getAllOrganisationsByDomainNameIsInShouldWork() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where domainName in DEFAULT_DOMAIN_NAME or UPDATED_DOMAIN_NAME
        defaultOrganisationShouldBeFound("domainName.in=" + DEFAULT_DOMAIN_NAME + "," + UPDATED_DOMAIN_NAME);

        // Get all the organisationList where domainName equals to UPDATED_DOMAIN_NAME
        defaultOrganisationShouldNotBeFound("domainName.in=" + UPDATED_DOMAIN_NAME);
    }

    @Test
    @Transactional
    public void getAllOrganisationsByDomainNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where domainName is not null
        defaultOrganisationShouldBeFound("domainName.specified=true");

        // Get all the organisationList where domainName is null
        defaultOrganisationShouldNotBeFound("domainName.specified=false");
    }

    @Test
    @Transactional
    public void getAllOrganisationsByPhoneAreaIsEqualToSomething() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where phoneArea equals to DEFAULT_PHONE_AREA
        defaultOrganisationShouldBeFound("phoneArea.equals=" + DEFAULT_PHONE_AREA);

        // Get all the organisationList where phoneArea equals to UPDATED_PHONE_AREA
        defaultOrganisationShouldNotBeFound("phoneArea.equals=" + UPDATED_PHONE_AREA);
    }

    @Test
    @Transactional
    public void getAllOrganisationsByPhoneAreaIsInShouldWork() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where phoneArea in DEFAULT_PHONE_AREA or UPDATED_PHONE_AREA
        defaultOrganisationShouldBeFound("phoneArea.in=" + DEFAULT_PHONE_AREA + "," + UPDATED_PHONE_AREA);

        // Get all the organisationList where phoneArea equals to UPDATED_PHONE_AREA
        defaultOrganisationShouldNotBeFound("phoneArea.in=" + UPDATED_PHONE_AREA);
    }

    @Test
    @Transactional
    public void getAllOrganisationsByPhoneAreaIsNullOrNotNull() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where phoneArea is not null
        defaultOrganisationShouldBeFound("phoneArea.specified=true");

        // Get all the organisationList where phoneArea is null
        defaultOrganisationShouldNotBeFound("phoneArea.specified=false");
    }

    @Test
    @Transactional
    public void getAllOrganisationsByPhoneNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where phoneNumber equals to DEFAULT_PHONE_NUMBER
        defaultOrganisationShouldBeFound("phoneNumber.equals=" + DEFAULT_PHONE_NUMBER);

        // Get all the organisationList where phoneNumber equals to UPDATED_PHONE_NUMBER
        defaultOrganisationShouldNotBeFound("phoneNumber.equals=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    public void getAllOrganisationsByPhoneNumberIsInShouldWork() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where phoneNumber in DEFAULT_PHONE_NUMBER or UPDATED_PHONE_NUMBER
        defaultOrganisationShouldBeFound("phoneNumber.in=" + DEFAULT_PHONE_NUMBER + "," + UPDATED_PHONE_NUMBER);

        // Get all the organisationList where phoneNumber equals to UPDATED_PHONE_NUMBER
        defaultOrganisationShouldNotBeFound("phoneNumber.in=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    public void getAllOrganisationsByPhoneNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where phoneNumber is not null
        defaultOrganisationShouldBeFound("phoneNumber.specified=true");

        // Get all the organisationList where phoneNumber is null
        defaultOrganisationShouldNotBeFound("phoneNumber.specified=false");
    }

    @Test
    @Transactional
    public void getAllOrganisationsByVatIdentificationIsEqualToSomething() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where vatIdentification equals to DEFAULT_VAT_IDENTIFICATION
        defaultOrganisationShouldBeFound("vatIdentification.equals=" + DEFAULT_VAT_IDENTIFICATION);

        // Get all the organisationList where vatIdentification equals to UPDATED_VAT_IDENTIFICATION
        defaultOrganisationShouldNotBeFound("vatIdentification.equals=" + UPDATED_VAT_IDENTIFICATION);
    }

    @Test
    @Transactional
    public void getAllOrganisationsByVatIdentificationIsInShouldWork() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where vatIdentification in DEFAULT_VAT_IDENTIFICATION or UPDATED_VAT_IDENTIFICATION
        defaultOrganisationShouldBeFound("vatIdentification.in=" + DEFAULT_VAT_IDENTIFICATION + "," + UPDATED_VAT_IDENTIFICATION);

        // Get all the organisationList where vatIdentification equals to UPDATED_VAT_IDENTIFICATION
        defaultOrganisationShouldNotBeFound("vatIdentification.in=" + UPDATED_VAT_IDENTIFICATION);
    }

    @Test
    @Transactional
    public void getAllOrganisationsByVatIdentificationIsNullOrNotNull() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where vatIdentification is not null
        defaultOrganisationShouldBeFound("vatIdentification.specified=true");

        // Get all the organisationList where vatIdentification is null
        defaultOrganisationShouldNotBeFound("vatIdentification.specified=false");
    }

    @Test
    @Transactional
    public void getAllOrganisationsByWebSiteIsEqualToSomething() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where webSite equals to DEFAULT_WEB_SITE
        defaultOrganisationShouldBeFound("webSite.equals=" + DEFAULT_WEB_SITE);

        // Get all the organisationList where webSite equals to UPDATED_WEB_SITE
        defaultOrganisationShouldNotBeFound("webSite.equals=" + UPDATED_WEB_SITE);
    }

    @Test
    @Transactional
    public void getAllOrganisationsByWebSiteIsInShouldWork() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where webSite in DEFAULT_WEB_SITE or UPDATED_WEB_SITE
        defaultOrganisationShouldBeFound("webSite.in=" + DEFAULT_WEB_SITE + "," + UPDATED_WEB_SITE);

        // Get all the organisationList where webSite equals to UPDATED_WEB_SITE
        defaultOrganisationShouldNotBeFound("webSite.in=" + UPDATED_WEB_SITE);
    }

    @Test
    @Transactional
    public void getAllOrganisationsByWebSiteIsNullOrNotNull() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where webSite is not null
        defaultOrganisationShouldBeFound("webSite.specified=true");

        // Get all the organisationList where webSite is null
        defaultOrganisationShouldNotBeFound("webSite.specified=false");
    }

    @Test
    @Transactional
    public void getAllOrganisationsByAdditionalInformationIsEqualToSomething() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where additionalInformation equals to DEFAULT_ADDITIONAL_INFORMATION
        defaultOrganisationShouldBeFound("additionalInformation.equals=" + DEFAULT_ADDITIONAL_INFORMATION);

        // Get all the organisationList where additionalInformation equals to UPDATED_ADDITIONAL_INFORMATION
        defaultOrganisationShouldNotBeFound("additionalInformation.equals=" + UPDATED_ADDITIONAL_INFORMATION);
    }

    @Test
    @Transactional
    public void getAllOrganisationsByAdditionalInformationIsInShouldWork() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where additionalInformation in DEFAULT_ADDITIONAL_INFORMATION or UPDATED_ADDITIONAL_INFORMATION
        defaultOrganisationShouldBeFound("additionalInformation.in=" + DEFAULT_ADDITIONAL_INFORMATION + "," + UPDATED_ADDITIONAL_INFORMATION);

        // Get all the organisationList where additionalInformation equals to UPDATED_ADDITIONAL_INFORMATION
        defaultOrganisationShouldNotBeFound("additionalInformation.in=" + UPDATED_ADDITIONAL_INFORMATION);
    }

    @Test
    @Transactional
    public void getAllOrganisationsByAdditionalInformationIsNullOrNotNull() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where additionalInformation is not null
        defaultOrganisationShouldBeFound("additionalInformation.specified=true");

        // Get all the organisationList where additionalInformation is null
        defaultOrganisationShouldNotBeFound("additionalInformation.specified=false");
    }

    @Test
    @Transactional
    public void getAllOrganisationsByActivatedIsEqualToSomething() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where activated equals to DEFAULT_ACTIVATED
        defaultOrganisationShouldBeFound("activated.equals=" + DEFAULT_ACTIVATED);

        // Get all the organisationList where activated equals to UPDATED_ACTIVATED
        defaultOrganisationShouldNotBeFound("activated.equals=" + UPDATED_ACTIVATED);
    }

    @Test
    @Transactional
    public void getAllOrganisationsByActivatedIsInShouldWork() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where activated in DEFAULT_ACTIVATED or UPDATED_ACTIVATED
        defaultOrganisationShouldBeFound("activated.in=" + DEFAULT_ACTIVATED + "," + UPDATED_ACTIVATED);

        // Get all the organisationList where activated equals to UPDATED_ACTIVATED
        defaultOrganisationShouldNotBeFound("activated.in=" + UPDATED_ACTIVATED);
    }

    @Test
    @Transactional
    public void getAllOrganisationsByActivatedIsNullOrNotNull() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where activated is not null
        defaultOrganisationShouldBeFound("activated.specified=true");

        // Get all the organisationList where activated is null
        defaultOrganisationShouldNotBeFound("activated.specified=false");
    }

    @Test
    @Transactional
    public void getAllOrganisationsByCreatedOnIsEqualToSomething() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where createdOn equals to DEFAULT_CREATED_ON
        defaultOrganisationShouldBeFound("createdOn.equals=" + DEFAULT_CREATED_ON);

        // Get all the organisationList where createdOn equals to UPDATED_CREATED_ON
        defaultOrganisationShouldNotBeFound("createdOn.equals=" + UPDATED_CREATED_ON);
    }

    @Test
    @Transactional
    public void getAllOrganisationsByCreatedOnIsInShouldWork() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where createdOn in DEFAULT_CREATED_ON or UPDATED_CREATED_ON
        defaultOrganisationShouldBeFound("createdOn.in=" + DEFAULT_CREATED_ON + "," + UPDATED_CREATED_ON);

        // Get all the organisationList where createdOn equals to UPDATED_CREATED_ON
        defaultOrganisationShouldNotBeFound("createdOn.in=" + UPDATED_CREATED_ON);
    }

    @Test
    @Transactional
    public void getAllOrganisationsByCreatedOnIsNullOrNotNull() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where createdOn is not null
        defaultOrganisationShouldBeFound("createdOn.specified=true");

        // Get all the organisationList where createdOn is null
        defaultOrganisationShouldNotBeFound("createdOn.specified=false");
    }

    @Test
    @Transactional
    public void getAllOrganisationsByAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        Address address = AddressResourceIntTest.createEntity(em);
        em.persist(address);
        em.flush();
        organisation.setAddress(address);
        organisationRepository.saveAndFlush(organisation);
        Long addressId = address.getId();

        // Get all the organisationList where address equals to addressId
        defaultOrganisationShouldBeFound("addressId.equals=" + addressId);

        // Get all the organisationList where address equals to addressId + 1
        defaultOrganisationShouldNotBeFound("addressId.equals=" + (addressId + 1));
    }


    @Test
    @Transactional
    public void getAllOrganisationsByContactsIsEqualToSomething() throws Exception {
        // Initialize the database
        Contact contacts = ContactResourceIntTest.createEntity(em);
        em.persist(contacts);
        em.flush();
        organisation.addContacts(contacts);
        organisationRepository.saveAndFlush(organisation);
        Long contactsId = contacts.getId();

        // Get all the organisationList where contacts equals to contactsId
        defaultOrganisationShouldBeFound("contactsId.equals=" + contactsId);

        // Get all the organisationList where contacts equals to contactsId + 1
        defaultOrganisationShouldNotBeFound("contactsId.equals=" + (contactsId + 1));
    }


    @Test
    @Transactional
    public void getAllOrganisationsByIndustrySectorIsEqualToSomething() throws Exception {
        // Initialize the database
        IndustrySector industrySector = IndustrySectorResourceIntTest.createEntity(em);
        em.persist(industrySector);
        em.flush();
        organisation.setIndustrySector(industrySector);
        organisationRepository.saveAndFlush(organisation);
        Long industrySectorId = industrySector.getId();

        // Get all the organisationList where industrySector equals to industrySectorId
        defaultOrganisationShouldBeFound("industrySectorId.equals=" + industrySectorId);

        // Get all the organisationList where industrySector equals to industrySectorId + 1
        defaultOrganisationShouldNotBeFound("industrySectorId.equals=" + (industrySectorId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultOrganisationShouldBeFound(String filter) throws Exception {
        restOrganisationMockMvc.perform(get("/api/extended/organisations?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(organisation.getId().intValue())))
            .andExpect(jsonPath("$.[*].slug").value(hasItem(DEFAULT_SLUG.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].businessIdentification").value(hasItem(DEFAULT_BUSINESS_IDENTIFICATION.toString())))
            .andExpect(jsonPath("$.[*].corporateName").value(hasItem(DEFAULT_CORPORATE_NAME.toString())))
            .andExpect(jsonPath("$.[*].domainName").value(hasItem(DEFAULT_DOMAIN_NAME.toString())))
            .andExpect(jsonPath("$.[*].phoneArea").value(hasItem(DEFAULT_PHONE_AREA.toString())))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER.toString())))
            .andExpect(jsonPath("$.[*].vatIdentification").value(hasItem(DEFAULT_VAT_IDENTIFICATION.toString())))
            .andExpect(jsonPath("$.[*].webSite").value(hasItem(DEFAULT_WEB_SITE.toString())))
            .andExpect(jsonPath("$.[*].additionalInformation").value(hasItem(DEFAULT_ADDITIONAL_INFORMATION.toString())))
            .andExpect(jsonPath("$.[*].activated").value(hasItem(DEFAULT_ACTIVATED.booleanValue())))
            .andExpect(jsonPath("$.[*].createdOn").value(hasItem(DEFAULT_CREATED_ON.toString())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultOrganisationShouldNotBeFound(String filter) throws Exception {
        restOrganisationMockMvc.perform(get("/api/extended/organisations?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @Transactional
    public void getNonExistingOrganisation() throws Exception {
        // Get the organisation
        restOrganisationMockMvc.perform(get("/api/extended/organisations/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateOrganisation() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        int databaseSizeBeforeUpdate = organisationRepository.findAll().size();

        // Update the organisation
        Organisation updatedOrganisation = organisationRepository.findById(organisation.getId()).get();
        // Disconnect from session so that the updates on updatedOrganisation are not directly saved in db
        em.detach(updatedOrganisation);
        updatedOrganisation
            .slug(UPDATED_SLUG)
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .businessIdentification(UPDATED_BUSINESS_IDENTIFICATION)
            .corporateName(UPDATED_CORPORATE_NAME)
            .domainName(UPDATED_DOMAIN_NAME)
            .phoneArea(UPDATED_PHONE_AREA)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .vatIdentification(UPDATED_VAT_IDENTIFICATION)
            .webSite(UPDATED_WEB_SITE)
            .additionalInformation(UPDATED_ADDITIONAL_INFORMATION)
            .activated(UPDATED_ACTIVATED)
            .createdOn(UPDATED_CREATED_ON);

        restOrganisationMockMvc.perform(put("/api/extended/organisations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedOrganisation)))
            .andExpect(status().isOk());

        // Validate the Organisation in the database
        List<Organisation> organisationList = organisationRepository.findAll();
        assertThat(organisationList).hasSize(databaseSizeBeforeUpdate);
        Organisation testOrganisation = organisationList.get(organisationList.size() - 1);
        assertThat(testOrganisation.getSlug()).isEqualTo(UPDATED_SLUG);
        assertThat(testOrganisation.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testOrganisation.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testOrganisation.getBusinessIdentification()).isEqualTo(UPDATED_BUSINESS_IDENTIFICATION);
        assertThat(testOrganisation.getCorporateName()).isEqualTo(UPDATED_CORPORATE_NAME);
        assertThat(testOrganisation.getDomainName()).isEqualTo(UPDATED_DOMAIN_NAME);
        assertThat(testOrganisation.getPhoneArea()).isEqualTo(UPDATED_PHONE_AREA);
        assertThat(testOrganisation.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testOrganisation.getVatIdentification()).isEqualTo(UPDATED_VAT_IDENTIFICATION);
        assertThat(testOrganisation.getWebSite()).isEqualTo(UPDATED_WEB_SITE);
        assertThat(testOrganisation.getAdditionalInformation()).isEqualTo(UPDATED_ADDITIONAL_INFORMATION);
        assertThat(testOrganisation.isActivated()).isEqualTo(UPDATED_ACTIVATED);
        assertThat(testOrganisation.getCreatedOn()).isEqualTo(UPDATED_CREATED_ON);
    }

    @Test
    @Transactional
    public void updateNonExistingOrganisation() throws Exception {
        int databaseSizeBeforeUpdate = organisationRepository.findAll().size();

        // Create the Organisation

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restOrganisationMockMvc.perform(put("/api/extended/organisations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(organisation)))
            .andExpect(status().isBadRequest());

        // Validate the Organisation in the database
        List<Organisation> organisationList = organisationRepository.findAll();
        assertThat(organisationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteOrganisation() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        int databaseSizeBeforeDelete = organisationRepository.findAll().size();

        // Get the organisation
        restOrganisationMockMvc.perform(delete("/api/extended/organisations/{id}", organisation.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Organisation> organisationList = organisationRepository.findAll();
        assertThat(organisationList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Organisation.class);
        Organisation organisation1 = new Organisation();
        organisation1.setId(1L);
        Organisation organisation2 = new Organisation();
        organisation2.setId(organisation1.getId());
        assertThat(organisation1).isEqualTo(organisation2);
        organisation2.setId(2L);
        assertThat(organisation1).isNotEqualTo(organisation2);
        organisation1.setId(null);
        assertThat(organisation1).isNotEqualTo(organisation2);
    }
}

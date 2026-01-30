package com.med.slot.web.rest;

import static com.med.slot.domain.ClinicAsserts.*;
import static com.med.slot.web.rest.TestUtil.createUpdateProxyForBean;
import static com.med.slot.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.med.slot.IntegrationTest;
import com.med.slot.domain.Clinic;
import com.med.slot.repository.ClinicRepository;
import com.med.slot.service.dto.ClinicDTO;
import com.med.slot.service.mapper.ClinicMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ClinicResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ClinicResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CREATED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_LAST_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_LAST_MODIFIED_BY = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_LAST_MODIFIED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_LAST_MODIFIED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String ENTITY_API_URL = "/api/clinics";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ClinicRepository clinicRepository;

    @Autowired
    private ClinicMapper clinicMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restClinicMockMvc;

    private Clinic clinic;

    private Clinic insertedClinic;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Clinic createEntity() {
        return new Clinic()
            .name(DEFAULT_NAME)
            .phone(DEFAULT_PHONE)
            .address(DEFAULT_ADDRESS)
            .active(DEFAULT_ACTIVE)
            .createdBy(DEFAULT_CREATED_BY)
            .createdDate(DEFAULT_CREATED_DATE)
            .lastModifiedBy(DEFAULT_LAST_MODIFIED_BY)
            .lastModifiedDate(DEFAULT_LAST_MODIFIED_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Clinic createUpdatedEntity() {
        return new Clinic()
            .name(UPDATED_NAME)
            .phone(UPDATED_PHONE)
            .address(UPDATED_ADDRESS)
            .active(UPDATED_ACTIVE)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
    }

    @BeforeEach
    void initTest() {
        clinic = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedClinic != null) {
            clinicRepository.delete(insertedClinic);
            insertedClinic = null;
        }
    }

    @Test
    @Transactional
    void createClinic() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Clinic
        ClinicDTO clinicDTO = clinicMapper.toDto(clinic);
        var returnedClinicDTO = om.readValue(
            restClinicMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clinicDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ClinicDTO.class
        );

        // Validate the Clinic in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedClinic = clinicMapper.toEntity(returnedClinicDTO);
        assertClinicUpdatableFieldsEquals(returnedClinic, getPersistedClinic(returnedClinic));

        insertedClinic = returnedClinic;
    }

    @Test
    @Transactional
    void createClinicWithExistingId() throws Exception {
        // Create the Clinic with an existing ID
        clinic.setId(1L);
        ClinicDTO clinicDTO = clinicMapper.toDto(clinic);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restClinicMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clinicDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Clinic in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        clinic.setName(null);

        // Create the Clinic, which fails.
        ClinicDTO clinicDTO = clinicMapper.toDto(clinic);

        restClinicMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clinicDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        clinic.setActive(null);

        // Create the Clinic, which fails.
        ClinicDTO clinicDTO = clinicMapper.toDto(clinic);

        restClinicMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clinicDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedByIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        clinic.setCreatedBy(null);

        // Create the Clinic, which fails.
        ClinicDTO clinicDTO = clinicMapper.toDto(clinic);

        restClinicMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clinicDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllClinics() throws Exception {
        // Initialize the database
        insertedClinic = clinicRepository.saveAndFlush(clinic);

        // Get all the clinicList
        restClinicMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(clinic.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(sameInstant(DEFAULT_CREATED_DATE))))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(sameInstant(DEFAULT_LAST_MODIFIED_DATE))));
    }

    @Test
    @Transactional
    void getClinic() throws Exception {
        // Initialize the database
        insertedClinic = clinicRepository.saveAndFlush(clinic);

        // Get the clinic
        restClinicMockMvc
            .perform(get(ENTITY_API_URL_ID, clinic.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(clinic.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.createdDate").value(sameInstant(DEFAULT_CREATED_DATE)))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY))
            .andExpect(jsonPath("$.lastModifiedDate").value(sameInstant(DEFAULT_LAST_MODIFIED_DATE)));
    }

    @Test
    @Transactional
    void getNonExistingClinic() throws Exception {
        // Get the clinic
        restClinicMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingClinic() throws Exception {
        // Initialize the database
        insertedClinic = clinicRepository.saveAndFlush(clinic);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the clinic
        Clinic updatedClinic = clinicRepository.findById(clinic.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedClinic are not directly saved in db
        em.detach(updatedClinic);
        updatedClinic
            .name(UPDATED_NAME)
            .phone(UPDATED_PHONE)
            .address(UPDATED_ADDRESS)
            .active(UPDATED_ACTIVE)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        ClinicDTO clinicDTO = clinicMapper.toDto(updatedClinic);

        restClinicMockMvc
            .perform(
                put(ENTITY_API_URL_ID, clinicDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clinicDTO))
            )
            .andExpect(status().isOk());

        // Validate the Clinic in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedClinicToMatchAllProperties(updatedClinic);
    }

    @Test
    @Transactional
    void putNonExistingClinic() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clinic.setId(longCount.incrementAndGet());

        // Create the Clinic
        ClinicDTO clinicDTO = clinicMapper.toDto(clinic);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClinicMockMvc
            .perform(
                put(ENTITY_API_URL_ID, clinicDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clinicDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Clinic in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchClinic() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clinic.setId(longCount.incrementAndGet());

        // Create the Clinic
        ClinicDTO clinicDTO = clinicMapper.toDto(clinic);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClinicMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(clinicDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Clinic in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamClinic() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clinic.setId(longCount.incrementAndGet());

        // Create the Clinic
        ClinicDTO clinicDTO = clinicMapper.toDto(clinic);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClinicMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clinicDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Clinic in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateClinicWithPatch() throws Exception {
        // Initialize the database
        insertedClinic = clinicRepository.saveAndFlush(clinic);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the clinic using partial update
        Clinic partialUpdatedClinic = new Clinic();
        partialUpdatedClinic.setId(clinic.getId());

        partialUpdatedClinic
            .address(UPDATED_ADDRESS)
            .active(UPDATED_ACTIVE)
            .createdBy(UPDATED_CREATED_BY)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);

        restClinicMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClinic.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedClinic))
            )
            .andExpect(status().isOk());

        // Validate the Clinic in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClinicUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedClinic, clinic), getPersistedClinic(clinic));
    }

    @Test
    @Transactional
    void fullUpdateClinicWithPatch() throws Exception {
        // Initialize the database
        insertedClinic = clinicRepository.saveAndFlush(clinic);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the clinic using partial update
        Clinic partialUpdatedClinic = new Clinic();
        partialUpdatedClinic.setId(clinic.getId());

        partialUpdatedClinic
            .name(UPDATED_NAME)
            .phone(UPDATED_PHONE)
            .address(UPDATED_ADDRESS)
            .active(UPDATED_ACTIVE)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);

        restClinicMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClinic.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedClinic))
            )
            .andExpect(status().isOk());

        // Validate the Clinic in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClinicUpdatableFieldsEquals(partialUpdatedClinic, getPersistedClinic(partialUpdatedClinic));
    }

    @Test
    @Transactional
    void patchNonExistingClinic() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clinic.setId(longCount.incrementAndGet());

        // Create the Clinic
        ClinicDTO clinicDTO = clinicMapper.toDto(clinic);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClinicMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, clinicDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(clinicDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Clinic in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchClinic() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clinic.setId(longCount.incrementAndGet());

        // Create the Clinic
        ClinicDTO clinicDTO = clinicMapper.toDto(clinic);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClinicMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(clinicDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Clinic in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamClinic() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clinic.setId(longCount.incrementAndGet());

        // Create the Clinic
        ClinicDTO clinicDTO = clinicMapper.toDto(clinic);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClinicMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(clinicDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Clinic in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteClinic() throws Exception {
        // Initialize the database
        insertedClinic = clinicRepository.saveAndFlush(clinic);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the clinic
        restClinicMockMvc
            .perform(delete(ENTITY_API_URL_ID, clinic.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return clinicRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Clinic getPersistedClinic(Clinic clinic) {
        return clinicRepository.findById(clinic.getId()).orElseThrow();
    }

    protected void assertPersistedClinicToMatchAllProperties(Clinic expectedClinic) {
        assertClinicAllPropertiesEquals(expectedClinic, getPersistedClinic(expectedClinic));
    }

    protected void assertPersistedClinicToMatchUpdatableProperties(Clinic expectedClinic) {
        assertClinicAllUpdatablePropertiesEquals(expectedClinic, getPersistedClinic(expectedClinic));
    }
}

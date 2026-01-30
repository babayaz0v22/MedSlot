package com.med.slot.web.rest;

import static com.med.slot.domain.PatientAsserts.*;
import static com.med.slot.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.med.slot.IntegrationTest;
import com.med.slot.domain.Patient;
import com.med.slot.repository.PatientRepository;
import com.med.slot.service.dto.PatientDTO;
import com.med.slot.service.mapper.PatientMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link PatientResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PatientResourceIT {

    private static final String DEFAULT_FULL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FULL_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/patients";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPatientMockMvc;

    private Patient patient;

    private Patient insertedPatient;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Patient createEntity() {
        return new Patient().fullName(DEFAULT_FULL_NAME).phone(DEFAULT_PHONE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Patient createUpdatedEntity() {
        return new Patient().fullName(UPDATED_FULL_NAME).phone(UPDATED_PHONE);
    }

    @BeforeEach
    void initTest() {
        patient = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPatient != null) {
            patientRepository.delete(insertedPatient);
            insertedPatient = null;
        }
    }

    @Test
    @Transactional
    void createPatient() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Patient
        PatientDTO patientDTO = patientMapper.toDto(patient);
        var returnedPatientDTO = om.readValue(
            restPatientMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(patientDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PatientDTO.class
        );

        // Validate the Patient in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPatient = patientMapper.toEntity(returnedPatientDTO);
        assertPatientUpdatableFieldsEquals(returnedPatient, getPersistedPatient(returnedPatient));

        insertedPatient = returnedPatient;
    }

    @Test
    @Transactional
    void createPatientWithExistingId() throws Exception {
        // Create the Patient with an existing ID
        patient.setId(1L);
        PatientDTO patientDTO = patientMapper.toDto(patient);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPatientMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(patientDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Patient in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkFullNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        patient.setFullName(null);

        // Create the Patient, which fails.
        PatientDTO patientDTO = patientMapper.toDto(patient);

        restPatientMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(patientDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPhoneIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        patient.setPhone(null);

        // Create the Patient, which fails.
        PatientDTO patientDTO = patientMapper.toDto(patient);

        restPatientMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(patientDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPatients() throws Exception {
        // Initialize the database
        insertedPatient = patientRepository.saveAndFlush(patient);

        // Get all the patientList
        restPatientMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(patient.getId().intValue())))
            .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)));
    }

    @Test
    @Transactional
    void getPatient() throws Exception {
        // Initialize the database
        insertedPatient = patientRepository.saveAndFlush(patient);

        // Get the patient
        restPatientMockMvc
            .perform(get(ENTITY_API_URL_ID, patient.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(patient.getId().intValue()))
            .andExpect(jsonPath("$.fullName").value(DEFAULT_FULL_NAME))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE));
    }

    @Test
    @Transactional
    void getNonExistingPatient() throws Exception {
        // Get the patient
        restPatientMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPatient() throws Exception {
        // Initialize the database
        insertedPatient = patientRepository.saveAndFlush(patient);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the patient
        Patient updatedPatient = patientRepository.findById(patient.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPatient are not directly saved in db
        em.detach(updatedPatient);
        updatedPatient.fullName(UPDATED_FULL_NAME).phone(UPDATED_PHONE);
        PatientDTO patientDTO = patientMapper.toDto(updatedPatient);

        restPatientMockMvc
            .perform(
                put(ENTITY_API_URL_ID, patientDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(patientDTO))
            )
            .andExpect(status().isOk());

        // Validate the Patient in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPatientToMatchAllProperties(updatedPatient);
    }

    @Test
    @Transactional
    void putNonExistingPatient() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        patient.setId(longCount.incrementAndGet());

        // Create the Patient
        PatientDTO patientDTO = patientMapper.toDto(patient);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPatientMockMvc
            .perform(
                put(ENTITY_API_URL_ID, patientDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(patientDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Patient in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPatient() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        patient.setId(longCount.incrementAndGet());

        // Create the Patient
        PatientDTO patientDTO = patientMapper.toDto(patient);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPatientMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(patientDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Patient in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPatient() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        patient.setId(longCount.incrementAndGet());

        // Create the Patient
        PatientDTO patientDTO = patientMapper.toDto(patient);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPatientMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(patientDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Patient in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePatientWithPatch() throws Exception {
        // Initialize the database
        insertedPatient = patientRepository.saveAndFlush(patient);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the patient using partial update
        Patient partialUpdatedPatient = new Patient();
        partialUpdatedPatient.setId(patient.getId());

        partialUpdatedPatient.fullName(UPDATED_FULL_NAME);

        restPatientMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPatient.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPatient))
            )
            .andExpect(status().isOk());

        // Validate the Patient in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPatientUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedPatient, patient), getPersistedPatient(patient));
    }

    @Test
    @Transactional
    void fullUpdatePatientWithPatch() throws Exception {
        // Initialize the database
        insertedPatient = patientRepository.saveAndFlush(patient);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the patient using partial update
        Patient partialUpdatedPatient = new Patient();
        partialUpdatedPatient.setId(patient.getId());

        partialUpdatedPatient.fullName(UPDATED_FULL_NAME).phone(UPDATED_PHONE);

        restPatientMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPatient.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPatient))
            )
            .andExpect(status().isOk());

        // Validate the Patient in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPatientUpdatableFieldsEquals(partialUpdatedPatient, getPersistedPatient(partialUpdatedPatient));
    }

    @Test
    @Transactional
    void patchNonExistingPatient() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        patient.setId(longCount.incrementAndGet());

        // Create the Patient
        PatientDTO patientDTO = patientMapper.toDto(patient);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPatientMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, patientDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(patientDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Patient in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPatient() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        patient.setId(longCount.incrementAndGet());

        // Create the Patient
        PatientDTO patientDTO = patientMapper.toDto(patient);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPatientMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(patientDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Patient in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPatient() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        patient.setId(longCount.incrementAndGet());

        // Create the Patient
        PatientDTO patientDTO = patientMapper.toDto(patient);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPatientMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(patientDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Patient in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePatient() throws Exception {
        // Initialize the database
        insertedPatient = patientRepository.saveAndFlush(patient);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the patient
        restPatientMockMvc
            .perform(delete(ENTITY_API_URL_ID, patient.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return patientRepository.count();
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

    protected Patient getPersistedPatient(Patient patient) {
        return patientRepository.findById(patient.getId()).orElseThrow();
    }

    protected void assertPersistedPatientToMatchAllProperties(Patient expectedPatient) {
        assertPatientAllPropertiesEquals(expectedPatient, getPersistedPatient(expectedPatient));
    }

    protected void assertPersistedPatientToMatchUpdatableProperties(Patient expectedPatient) {
        assertPatientAllUpdatablePropertiesEquals(expectedPatient, getPersistedPatient(expectedPatient));
    }
}

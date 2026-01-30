package com.med.slot.web.rest;

import static com.med.slot.domain.DoctorAsserts.*;
import static com.med.slot.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.med.slot.IntegrationTest;
import com.med.slot.domain.Clinic;
import com.med.slot.domain.Doctor;
import com.med.slot.repository.DoctorRepository;
import com.med.slot.service.dto.DoctorDTO;
import com.med.slot.service.mapper.DoctorMapper;
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
 * Integration tests for the {@link DoctorResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DoctorResourceIT {

    private static final String DEFAULT_SPECIALTY = "AAAAAAAAAA";
    private static final String UPDATED_SPECIALTY = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/doctors";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDoctorMockMvc;

    private Doctor doctor;

    private Doctor insertedDoctor;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Doctor createEntity() {
        return new Doctor().specialty(DEFAULT_SPECIALTY).phone(DEFAULT_PHONE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Doctor createUpdatedEntity() {
        return new Doctor().specialty(UPDATED_SPECIALTY).phone(UPDATED_PHONE);
    }

    @BeforeEach
    void initTest() {
        doctor = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedDoctor != null) {
            doctorRepository.delete(insertedDoctor);
            insertedDoctor = null;
        }
    }

    @Test
    @Transactional
    void createDoctor() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Doctor
        DoctorDTO doctorDTO = doctorMapper.toDto(doctor);
        var returnedDoctorDTO = om.readValue(
            restDoctorMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(doctorDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            DoctorDTO.class
        );

        // Validate the Doctor in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDoctor = doctorMapper.toEntity(returnedDoctorDTO);
        assertDoctorUpdatableFieldsEquals(returnedDoctor, getPersistedDoctor(returnedDoctor));

        insertedDoctor = returnedDoctor;
    }

    @Test
    @Transactional
    void createDoctorWithExistingId() throws Exception {
        // Create the Doctor with an existing ID
        doctor.setId(1L);
        DoctorDTO doctorDTO = doctorMapper.toDto(doctor);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDoctorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(doctorDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Doctor in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkSpecialtyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        doctor.setSpecialty(null);

        // Create the Doctor, which fails.
        DoctorDTO doctorDTO = doctorMapper.toDto(doctor);

        restDoctorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(doctorDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDoctors() throws Exception {
        // Initialize the database
        insertedDoctor = doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList
        restDoctorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(doctor.getId().intValue())))
            .andExpect(jsonPath("$.[*].specialty").value(hasItem(DEFAULT_SPECIALTY)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)));
    }

    @Test
    @Transactional
    void getDoctor() throws Exception {
        // Initialize the database
        insertedDoctor = doctorRepository.saveAndFlush(doctor);

        // Get the doctor
        restDoctorMockMvc
            .perform(get(ENTITY_API_URL_ID, doctor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(doctor.getId().intValue()))
            .andExpect(jsonPath("$.specialty").value(DEFAULT_SPECIALTY))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE));
    }

    @Test
    @Transactional
    void getDoctorsByIdFiltering() throws Exception {
        // Initialize the database
        insertedDoctor = doctorRepository.saveAndFlush(doctor);

        Long id = doctor.getId();

        defaultDoctorFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultDoctorFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultDoctorFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllDoctorsBySpecialtyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDoctor = doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where specialty equals to
        defaultDoctorFiltering("specialty.equals=" + DEFAULT_SPECIALTY, "specialty.equals=" + UPDATED_SPECIALTY);
    }

    @Test
    @Transactional
    void getAllDoctorsBySpecialtyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDoctor = doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where specialty in
        defaultDoctorFiltering("specialty.in=" + DEFAULT_SPECIALTY + "," + UPDATED_SPECIALTY, "specialty.in=" + UPDATED_SPECIALTY);
    }

    @Test
    @Transactional
    void getAllDoctorsBySpecialtyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDoctor = doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where specialty is not null
        defaultDoctorFiltering("specialty.specified=true", "specialty.specified=false");
    }

    @Test
    @Transactional
    void getAllDoctorsBySpecialtyContainsSomething() throws Exception {
        // Initialize the database
        insertedDoctor = doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where specialty contains
        defaultDoctorFiltering("specialty.contains=" + DEFAULT_SPECIALTY, "specialty.contains=" + UPDATED_SPECIALTY);
    }

    @Test
    @Transactional
    void getAllDoctorsBySpecialtyNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDoctor = doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where specialty does not contain
        defaultDoctorFiltering("specialty.doesNotContain=" + UPDATED_SPECIALTY, "specialty.doesNotContain=" + DEFAULT_SPECIALTY);
    }

    @Test
    @Transactional
    void getAllDoctorsByPhoneIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDoctor = doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where phone equals to
        defaultDoctorFiltering("phone.equals=" + DEFAULT_PHONE, "phone.equals=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllDoctorsByPhoneIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDoctor = doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where phone in
        defaultDoctorFiltering("phone.in=" + DEFAULT_PHONE + "," + UPDATED_PHONE, "phone.in=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllDoctorsByPhoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDoctor = doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where phone is not null
        defaultDoctorFiltering("phone.specified=true", "phone.specified=false");
    }

    @Test
    @Transactional
    void getAllDoctorsByPhoneContainsSomething() throws Exception {
        // Initialize the database
        insertedDoctor = doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where phone contains
        defaultDoctorFiltering("phone.contains=" + DEFAULT_PHONE, "phone.contains=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllDoctorsByPhoneNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDoctor = doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where phone does not contain
        defaultDoctorFiltering("phone.doesNotContain=" + UPDATED_PHONE, "phone.doesNotContain=" + DEFAULT_PHONE);
    }

    @Test
    @Transactional
    void getAllDoctorsByClinicIsEqualToSomething() throws Exception {
        Clinic clinic;
        if (TestUtil.findAll(em, Clinic.class).isEmpty()) {
            doctorRepository.saveAndFlush(doctor);
            clinic = ClinicResourceIT.createEntity();
        } else {
            clinic = TestUtil.findAll(em, Clinic.class).get(0);
        }
        em.persist(clinic);
        em.flush();
        doctor.setClinic(clinic);
        doctorRepository.saveAndFlush(doctor);
        Long clinicId = clinic.getId();
        // Get all the doctorList where clinic equals to clinicId
        defaultDoctorShouldBeFound("clinicId.equals=" + clinicId);

        // Get all the doctorList where clinic equals to (clinicId + 1)
        defaultDoctorShouldNotBeFound("clinicId.equals=" + (clinicId + 1));
    }

    private void defaultDoctorFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultDoctorShouldBeFound(shouldBeFound);
        defaultDoctorShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDoctorShouldBeFound(String filter) throws Exception {
        restDoctorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(doctor.getId().intValue())))
            .andExpect(jsonPath("$.[*].specialty").value(hasItem(DEFAULT_SPECIALTY)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)));

        // Check, that the count call also returns 1
        restDoctorMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDoctorShouldNotBeFound(String filter) throws Exception {
        restDoctorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDoctorMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDoctor() throws Exception {
        // Get the doctor
        restDoctorMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDoctor() throws Exception {
        // Initialize the database
        insertedDoctor = doctorRepository.saveAndFlush(doctor);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the doctor
        Doctor updatedDoctor = doctorRepository.findById(doctor.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDoctor are not directly saved in db
        em.detach(updatedDoctor);
        updatedDoctor.specialty(UPDATED_SPECIALTY).phone(UPDATED_PHONE);
        DoctorDTO doctorDTO = doctorMapper.toDto(updatedDoctor);

        restDoctorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, doctorDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(doctorDTO))
            )
            .andExpect(status().isOk());

        // Validate the Doctor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDoctorToMatchAllProperties(updatedDoctor);
    }

    @Test
    @Transactional
    void putNonExistingDoctor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        doctor.setId(longCount.incrementAndGet());

        // Create the Doctor
        DoctorDTO doctorDTO = doctorMapper.toDto(doctor);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDoctorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, doctorDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(doctorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Doctor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDoctor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        doctor.setId(longCount.incrementAndGet());

        // Create the Doctor
        DoctorDTO doctorDTO = doctorMapper.toDto(doctor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDoctorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(doctorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Doctor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDoctor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        doctor.setId(longCount.incrementAndGet());

        // Create the Doctor
        DoctorDTO doctorDTO = doctorMapper.toDto(doctor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDoctorMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(doctorDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Doctor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDoctorWithPatch() throws Exception {
        // Initialize the database
        insertedDoctor = doctorRepository.saveAndFlush(doctor);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the doctor using partial update
        Doctor partialUpdatedDoctor = new Doctor();
        partialUpdatedDoctor.setId(doctor.getId());

        partialUpdatedDoctor.phone(UPDATED_PHONE);

        restDoctorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDoctor.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDoctor))
            )
            .andExpect(status().isOk());

        // Validate the Doctor in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDoctorUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedDoctor, doctor), getPersistedDoctor(doctor));
    }

    @Test
    @Transactional
    void fullUpdateDoctorWithPatch() throws Exception {
        // Initialize the database
        insertedDoctor = doctorRepository.saveAndFlush(doctor);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the doctor using partial update
        Doctor partialUpdatedDoctor = new Doctor();
        partialUpdatedDoctor.setId(doctor.getId());

        partialUpdatedDoctor.specialty(UPDATED_SPECIALTY).phone(UPDATED_PHONE);

        restDoctorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDoctor.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDoctor))
            )
            .andExpect(status().isOk());

        // Validate the Doctor in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDoctorUpdatableFieldsEquals(partialUpdatedDoctor, getPersistedDoctor(partialUpdatedDoctor));
    }

    @Test
    @Transactional
    void patchNonExistingDoctor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        doctor.setId(longCount.incrementAndGet());

        // Create the Doctor
        DoctorDTO doctorDTO = doctorMapper.toDto(doctor);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDoctorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, doctorDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(doctorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Doctor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDoctor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        doctor.setId(longCount.incrementAndGet());

        // Create the Doctor
        DoctorDTO doctorDTO = doctorMapper.toDto(doctor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDoctorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(doctorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Doctor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDoctor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        doctor.setId(longCount.incrementAndGet());

        // Create the Doctor
        DoctorDTO doctorDTO = doctorMapper.toDto(doctor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDoctorMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(doctorDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Doctor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDoctor() throws Exception {
        // Initialize the database
        insertedDoctor = doctorRepository.saveAndFlush(doctor);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the doctor
        restDoctorMockMvc
            .perform(delete(ENTITY_API_URL_ID, doctor.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return doctorRepository.count();
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

    protected Doctor getPersistedDoctor(Doctor doctor) {
        return doctorRepository.findById(doctor.getId()).orElseThrow();
    }

    protected void assertPersistedDoctorToMatchAllProperties(Doctor expectedDoctor) {
        assertDoctorAllPropertiesEquals(expectedDoctor, getPersistedDoctor(expectedDoctor));
    }

    protected void assertPersistedDoctorToMatchUpdatableProperties(Doctor expectedDoctor) {
        assertDoctorAllUpdatablePropertiesEquals(expectedDoctor, getPersistedDoctor(expectedDoctor));
    }
}

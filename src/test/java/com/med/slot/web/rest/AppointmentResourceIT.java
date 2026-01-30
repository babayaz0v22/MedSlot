package com.med.slot.web.rest;

import static com.med.slot.domain.AppointmentAsserts.*;
import static com.med.slot.web.rest.TestUtil.createUpdateProxyForBean;
import static com.med.slot.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.med.slot.IntegrationTest;
import com.med.slot.domain.Appointment;
import com.med.slot.domain.Clinic;
import com.med.slot.domain.Doctor;
import com.med.slot.domain.Patient;
import com.med.slot.domain.enumeration.AppointmentStatus;
import com.med.slot.repository.AppointmentRepository;
import com.med.slot.service.dto.AppointmentDTO;
import com.med.slot.service.mapper.AppointmentMapper;
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
 * Integration tests for the {@link AppointmentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AppointmentResourceIT {

    private static final ZonedDateTime DEFAULT_APPOINTMENT_DATE_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_APPOINTMENT_DATE_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_APPOINTMENT_DATE_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final AppointmentStatus DEFAULT_STATUS = AppointmentStatus.BOOKED;
    private static final AppointmentStatus UPDATED_STATUS = AppointmentStatus.CANCELED;

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/appointments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAppointmentMockMvc;

    private Appointment appointment;

    private Appointment insertedAppointment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Appointment createEntity() {
        return new Appointment().appointmentDateTime(DEFAULT_APPOINTMENT_DATE_TIME).status(DEFAULT_STATUS).note(DEFAULT_NOTE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Appointment createUpdatedEntity() {
        return new Appointment().appointmentDateTime(UPDATED_APPOINTMENT_DATE_TIME).status(UPDATED_STATUS).note(UPDATED_NOTE);
    }

    @BeforeEach
    void initTest() {
        appointment = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedAppointment != null) {
            appointmentRepository.delete(insertedAppointment);
            insertedAppointment = null;
        }
    }

    @Test
    @Transactional
    void createAppointment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Appointment
        AppointmentDTO appointmentDTO = appointmentMapper.toDto(appointment);
        var returnedAppointmentDTO = om.readValue(
            restAppointmentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(appointmentDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AppointmentDTO.class
        );

        // Validate the Appointment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAppointment = appointmentMapper.toEntity(returnedAppointmentDTO);
        assertAppointmentUpdatableFieldsEquals(returnedAppointment, getPersistedAppointment(returnedAppointment));

        insertedAppointment = returnedAppointment;
    }

    @Test
    @Transactional
    void createAppointmentWithExistingId() throws Exception {
        // Create the Appointment with an existing ID
        appointment.setId(1L);
        AppointmentDTO appointmentDTO = appointmentMapper.toDto(appointment);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAppointmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(appointmentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Appointment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkAppointmentDateTimeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        appointment.setAppointmentDateTime(null);

        // Create the Appointment, which fails.
        AppointmentDTO appointmentDTO = appointmentMapper.toDto(appointment);

        restAppointmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(appointmentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        appointment.setStatus(null);

        // Create the Appointment, which fails.
        AppointmentDTO appointmentDTO = appointmentMapper.toDto(appointment);

        restAppointmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(appointmentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAppointments() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList
        restAppointmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(appointment.getId().intValue())))
            .andExpect(jsonPath("$.[*].appointmentDateTime").value(hasItem(sameInstant(DEFAULT_APPOINTMENT_DATE_TIME))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));
    }

    @Test
    @Transactional
    void getAppointment() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get the appointment
        restAppointmentMockMvc
            .perform(get(ENTITY_API_URL_ID, appointment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(appointment.getId().intValue()))
            .andExpect(jsonPath("$.appointmentDateTime").value(sameInstant(DEFAULT_APPOINTMENT_DATE_TIME)))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE));
    }

    @Test
    @Transactional
    void getAppointmentsByIdFiltering() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        Long id = appointment.getId();

        defaultAppointmentFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultAppointmentFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultAppointmentFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAppointmentsByAppointmentDateTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where appointmentDateTime equals to
        defaultAppointmentFiltering(
            "appointmentDateTime.equals=" + DEFAULT_APPOINTMENT_DATE_TIME,
            "appointmentDateTime.equals=" + UPDATED_APPOINTMENT_DATE_TIME
        );
    }

    @Test
    @Transactional
    void getAllAppointmentsByAppointmentDateTimeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where appointmentDateTime in
        defaultAppointmentFiltering(
            "appointmentDateTime.in=" + DEFAULT_APPOINTMENT_DATE_TIME + "," + UPDATED_APPOINTMENT_DATE_TIME,
            "appointmentDateTime.in=" + UPDATED_APPOINTMENT_DATE_TIME
        );
    }

    @Test
    @Transactional
    void getAllAppointmentsByAppointmentDateTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where appointmentDateTime is not null
        defaultAppointmentFiltering("appointmentDateTime.specified=true", "appointmentDateTime.specified=false");
    }

    @Test
    @Transactional
    void getAllAppointmentsByAppointmentDateTimeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where appointmentDateTime is greater than or equal to
        defaultAppointmentFiltering(
            "appointmentDateTime.greaterThanOrEqual=" + DEFAULT_APPOINTMENT_DATE_TIME,
            "appointmentDateTime.greaterThanOrEqual=" + UPDATED_APPOINTMENT_DATE_TIME
        );
    }

    @Test
    @Transactional
    void getAllAppointmentsByAppointmentDateTimeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where appointmentDateTime is less than or equal to
        defaultAppointmentFiltering(
            "appointmentDateTime.lessThanOrEqual=" + DEFAULT_APPOINTMENT_DATE_TIME,
            "appointmentDateTime.lessThanOrEqual=" + SMALLER_APPOINTMENT_DATE_TIME
        );
    }

    @Test
    @Transactional
    void getAllAppointmentsByAppointmentDateTimeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where appointmentDateTime is less than
        defaultAppointmentFiltering(
            "appointmentDateTime.lessThan=" + UPDATED_APPOINTMENT_DATE_TIME,
            "appointmentDateTime.lessThan=" + DEFAULT_APPOINTMENT_DATE_TIME
        );
    }

    @Test
    @Transactional
    void getAllAppointmentsByAppointmentDateTimeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where appointmentDateTime is greater than
        defaultAppointmentFiltering(
            "appointmentDateTime.greaterThan=" + SMALLER_APPOINTMENT_DATE_TIME,
            "appointmentDateTime.greaterThan=" + DEFAULT_APPOINTMENT_DATE_TIME
        );
    }

    @Test
    @Transactional
    void getAllAppointmentsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where status equals to
        defaultAppointmentFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllAppointmentsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where status in
        defaultAppointmentFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllAppointmentsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where status is not null
        defaultAppointmentFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllAppointmentsByNoteIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where note equals to
        defaultAppointmentFiltering("note.equals=" + DEFAULT_NOTE, "note.equals=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllAppointmentsByNoteIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where note in
        defaultAppointmentFiltering("note.in=" + DEFAULT_NOTE + "," + UPDATED_NOTE, "note.in=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllAppointmentsByNoteIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where note is not null
        defaultAppointmentFiltering("note.specified=true", "note.specified=false");
    }

    @Test
    @Transactional
    void getAllAppointmentsByNoteContainsSomething() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where note contains
        defaultAppointmentFiltering("note.contains=" + DEFAULT_NOTE, "note.contains=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllAppointmentsByNoteNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where note does not contain
        defaultAppointmentFiltering("note.doesNotContain=" + UPDATED_NOTE, "note.doesNotContain=" + DEFAULT_NOTE);
    }

    @Test
    @Transactional
    void getAllAppointmentsByClinicIsEqualToSomething() throws Exception {
        Clinic clinic;
        if (TestUtil.findAll(em, Clinic.class).isEmpty()) {
            appointmentRepository.saveAndFlush(appointment);
            clinic = ClinicResourceIT.createEntity();
        } else {
            clinic = TestUtil.findAll(em, Clinic.class).get(0);
        }
        em.persist(clinic);
        em.flush();
        appointment.setClinic(clinic);
        appointmentRepository.saveAndFlush(appointment);
        Long clinicId = clinic.getId();
        // Get all the appointmentList where clinic equals to clinicId
        defaultAppointmentShouldBeFound("clinicId.equals=" + clinicId);

        // Get all the appointmentList where clinic equals to (clinicId + 1)
        defaultAppointmentShouldNotBeFound("clinicId.equals=" + (clinicId + 1));
    }

    @Test
    @Transactional
    void getAllAppointmentsByDoctorIsEqualToSomething() throws Exception {
        Doctor doctor;
        if (TestUtil.findAll(em, Doctor.class).isEmpty()) {
            appointmentRepository.saveAndFlush(appointment);
            doctor = DoctorResourceIT.createEntity();
        } else {
            doctor = TestUtil.findAll(em, Doctor.class).get(0);
        }
        em.persist(doctor);
        em.flush();
        appointment.setDoctor(doctor);
        appointmentRepository.saveAndFlush(appointment);
        Long doctorId = doctor.getId();
        // Get all the appointmentList where doctor equals to doctorId
        defaultAppointmentShouldBeFound("doctorId.equals=" + doctorId);

        // Get all the appointmentList where doctor equals to (doctorId + 1)
        defaultAppointmentShouldNotBeFound("doctorId.equals=" + (doctorId + 1));
    }

    @Test
    @Transactional
    void getAllAppointmentsByPatientIsEqualToSomething() throws Exception {
        Patient patient;
        if (TestUtil.findAll(em, Patient.class).isEmpty()) {
            appointmentRepository.saveAndFlush(appointment);
            patient = PatientResourceIT.createEntity();
        } else {
            patient = TestUtil.findAll(em, Patient.class).get(0);
        }
        em.persist(patient);
        em.flush();
        appointment.setPatient(patient);
        appointmentRepository.saveAndFlush(appointment);
        Long patientId = patient.getId();
        // Get all the appointmentList where patient equals to patientId
        defaultAppointmentShouldBeFound("patientId.equals=" + patientId);

        // Get all the appointmentList where patient equals to (patientId + 1)
        defaultAppointmentShouldNotBeFound("patientId.equals=" + (patientId + 1));
    }

    private void defaultAppointmentFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultAppointmentShouldBeFound(shouldBeFound);
        defaultAppointmentShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAppointmentShouldBeFound(String filter) throws Exception {
        restAppointmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(appointment.getId().intValue())))
            .andExpect(jsonPath("$.[*].appointmentDateTime").value(hasItem(sameInstant(DEFAULT_APPOINTMENT_DATE_TIME))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));

        // Check, that the count call also returns 1
        restAppointmentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAppointmentShouldNotBeFound(String filter) throws Exception {
        restAppointmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAppointmentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAppointment() throws Exception {
        // Get the appointment
        restAppointmentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAppointment() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the appointment
        Appointment updatedAppointment = appointmentRepository.findById(appointment.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAppointment are not directly saved in db
        em.detach(updatedAppointment);
        updatedAppointment.appointmentDateTime(UPDATED_APPOINTMENT_DATE_TIME).status(UPDATED_STATUS).note(UPDATED_NOTE);
        AppointmentDTO appointmentDTO = appointmentMapper.toDto(updatedAppointment);

        restAppointmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, appointmentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(appointmentDTO))
            )
            .andExpect(status().isOk());

        // Validate the Appointment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAppointmentToMatchAllProperties(updatedAppointment);
    }

    @Test
    @Transactional
    void putNonExistingAppointment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appointment.setId(longCount.incrementAndGet());

        // Create the Appointment
        AppointmentDTO appointmentDTO = appointmentMapper.toDto(appointment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAppointmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, appointmentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(appointmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Appointment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAppointment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appointment.setId(longCount.incrementAndGet());

        // Create the Appointment
        AppointmentDTO appointmentDTO = appointmentMapper.toDto(appointment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppointmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(appointmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Appointment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAppointment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appointment.setId(longCount.incrementAndGet());

        // Create the Appointment
        AppointmentDTO appointmentDTO = appointmentMapper.toDto(appointment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppointmentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(appointmentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Appointment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAppointmentWithPatch() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the appointment using partial update
        Appointment partialUpdatedAppointment = new Appointment();
        partialUpdatedAppointment.setId(appointment.getId());

        partialUpdatedAppointment.appointmentDateTime(UPDATED_APPOINTMENT_DATE_TIME);

        restAppointmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAppointment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAppointment))
            )
            .andExpect(status().isOk());

        // Validate the Appointment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAppointmentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAppointment, appointment),
            getPersistedAppointment(appointment)
        );
    }

    @Test
    @Transactional
    void fullUpdateAppointmentWithPatch() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the appointment using partial update
        Appointment partialUpdatedAppointment = new Appointment();
        partialUpdatedAppointment.setId(appointment.getId());

        partialUpdatedAppointment.appointmentDateTime(UPDATED_APPOINTMENT_DATE_TIME).status(UPDATED_STATUS).note(UPDATED_NOTE);

        restAppointmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAppointment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAppointment))
            )
            .andExpect(status().isOk());

        // Validate the Appointment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAppointmentUpdatableFieldsEquals(partialUpdatedAppointment, getPersistedAppointment(partialUpdatedAppointment));
    }

    @Test
    @Transactional
    void patchNonExistingAppointment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appointment.setId(longCount.incrementAndGet());

        // Create the Appointment
        AppointmentDTO appointmentDTO = appointmentMapper.toDto(appointment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAppointmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, appointmentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(appointmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Appointment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAppointment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appointment.setId(longCount.incrementAndGet());

        // Create the Appointment
        AppointmentDTO appointmentDTO = appointmentMapper.toDto(appointment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppointmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(appointmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Appointment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAppointment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appointment.setId(longCount.incrementAndGet());

        // Create the Appointment
        AppointmentDTO appointmentDTO = appointmentMapper.toDto(appointment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppointmentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(appointmentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Appointment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAppointment() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the appointment
        restAppointmentMockMvc
            .perform(delete(ENTITY_API_URL_ID, appointment.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return appointmentRepository.count();
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

    protected Appointment getPersistedAppointment(Appointment appointment) {
        return appointmentRepository.findById(appointment.getId()).orElseThrow();
    }

    protected void assertPersistedAppointmentToMatchAllProperties(Appointment expectedAppointment) {
        assertAppointmentAllPropertiesEquals(expectedAppointment, getPersistedAppointment(expectedAppointment));
    }

    protected void assertPersistedAppointmentToMatchUpdatableProperties(Appointment expectedAppointment) {
        assertAppointmentAllUpdatablePropertiesEquals(expectedAppointment, getPersistedAppointment(expectedAppointment));
    }
}

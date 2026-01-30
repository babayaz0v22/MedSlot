package com.med.slot.web.rest;

import static com.med.slot.domain.DoctorScheduleAsserts.*;
import static com.med.slot.web.rest.TestUtil.createUpdateProxyForBean;
import static com.med.slot.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.med.slot.IntegrationTest;
import com.med.slot.domain.DoctorSchedule;
import com.med.slot.repository.DoctorScheduleRepository;
import com.med.slot.service.dto.DoctorScheduleDTO;
import com.med.slot.service.mapper.DoctorScheduleMapper;
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
 * Integration tests for the {@link DoctorScheduleResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DoctorScheduleResourceIT {

    private static final Integer DEFAULT_DAY_OF_WEEK = 1;
    private static final Integer UPDATED_DAY_OF_WEEK = 2;

    private static final ZonedDateTime DEFAULT_START_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_START_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_END_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_END_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String ENTITY_API_URL = "/api/doctor-schedules";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DoctorScheduleRepository doctorScheduleRepository;

    @Autowired
    private DoctorScheduleMapper doctorScheduleMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDoctorScheduleMockMvc;

    private DoctorSchedule doctorSchedule;

    private DoctorSchedule insertedDoctorSchedule;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DoctorSchedule createEntity() {
        return new DoctorSchedule().dayOfWeek(DEFAULT_DAY_OF_WEEK).startTime(DEFAULT_START_TIME).endTime(DEFAULT_END_TIME);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DoctorSchedule createUpdatedEntity() {
        return new DoctorSchedule().dayOfWeek(UPDATED_DAY_OF_WEEK).startTime(UPDATED_START_TIME).endTime(UPDATED_END_TIME);
    }

    @BeforeEach
    void initTest() {
        doctorSchedule = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedDoctorSchedule != null) {
            doctorScheduleRepository.delete(insertedDoctorSchedule);
            insertedDoctorSchedule = null;
        }
    }

    @Test
    @Transactional
    void createDoctorSchedule() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the DoctorSchedule
        DoctorScheduleDTO doctorScheduleDTO = doctorScheduleMapper.toDto(doctorSchedule);
        var returnedDoctorScheduleDTO = om.readValue(
            restDoctorScheduleMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(doctorScheduleDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            DoctorScheduleDTO.class
        );

        // Validate the DoctorSchedule in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDoctorSchedule = doctorScheduleMapper.toEntity(returnedDoctorScheduleDTO);
        assertDoctorScheduleUpdatableFieldsEquals(returnedDoctorSchedule, getPersistedDoctorSchedule(returnedDoctorSchedule));

        insertedDoctorSchedule = returnedDoctorSchedule;
    }

    @Test
    @Transactional
    void createDoctorScheduleWithExistingId() throws Exception {
        // Create the DoctorSchedule with an existing ID
        doctorSchedule.setId(1L);
        DoctorScheduleDTO doctorScheduleDTO = doctorScheduleMapper.toDto(doctorSchedule);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDoctorScheduleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(doctorScheduleDTO)))
            .andExpect(status().isBadRequest());

        // Validate the DoctorSchedule in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDayOfWeekIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        doctorSchedule.setDayOfWeek(null);

        // Create the DoctorSchedule, which fails.
        DoctorScheduleDTO doctorScheduleDTO = doctorScheduleMapper.toDto(doctorSchedule);

        restDoctorScheduleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(doctorScheduleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStartTimeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        doctorSchedule.setStartTime(null);

        // Create the DoctorSchedule, which fails.
        DoctorScheduleDTO doctorScheduleDTO = doctorScheduleMapper.toDto(doctorSchedule);

        restDoctorScheduleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(doctorScheduleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEndTimeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        doctorSchedule.setEndTime(null);

        // Create the DoctorSchedule, which fails.
        DoctorScheduleDTO doctorScheduleDTO = doctorScheduleMapper.toDto(doctorSchedule);

        restDoctorScheduleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(doctorScheduleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDoctorSchedules() throws Exception {
        // Initialize the database
        insertedDoctorSchedule = doctorScheduleRepository.saveAndFlush(doctorSchedule);

        // Get all the doctorScheduleList
        restDoctorScheduleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(doctorSchedule.getId().intValue())))
            .andExpect(jsonPath("$.[*].dayOfWeek").value(hasItem(DEFAULT_DAY_OF_WEEK)))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(sameInstant(DEFAULT_START_TIME))))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(sameInstant(DEFAULT_END_TIME))));
    }

    @Test
    @Transactional
    void getDoctorSchedule() throws Exception {
        // Initialize the database
        insertedDoctorSchedule = doctorScheduleRepository.saveAndFlush(doctorSchedule);

        // Get the doctorSchedule
        restDoctorScheduleMockMvc
            .perform(get(ENTITY_API_URL_ID, doctorSchedule.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(doctorSchedule.getId().intValue()))
            .andExpect(jsonPath("$.dayOfWeek").value(DEFAULT_DAY_OF_WEEK))
            .andExpect(jsonPath("$.startTime").value(sameInstant(DEFAULT_START_TIME)))
            .andExpect(jsonPath("$.endTime").value(sameInstant(DEFAULT_END_TIME)));
    }

    @Test
    @Transactional
    void getNonExistingDoctorSchedule() throws Exception {
        // Get the doctorSchedule
        restDoctorScheduleMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDoctorSchedule() throws Exception {
        // Initialize the database
        insertedDoctorSchedule = doctorScheduleRepository.saveAndFlush(doctorSchedule);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the doctorSchedule
        DoctorSchedule updatedDoctorSchedule = doctorScheduleRepository.findById(doctorSchedule.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDoctorSchedule are not directly saved in db
        em.detach(updatedDoctorSchedule);
        updatedDoctorSchedule.dayOfWeek(UPDATED_DAY_OF_WEEK).startTime(UPDATED_START_TIME).endTime(UPDATED_END_TIME);
        DoctorScheduleDTO doctorScheduleDTO = doctorScheduleMapper.toDto(updatedDoctorSchedule);

        restDoctorScheduleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, doctorScheduleDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(doctorScheduleDTO))
            )
            .andExpect(status().isOk());

        // Validate the DoctorSchedule in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDoctorScheduleToMatchAllProperties(updatedDoctorSchedule);
    }

    @Test
    @Transactional
    void putNonExistingDoctorSchedule() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        doctorSchedule.setId(longCount.incrementAndGet());

        // Create the DoctorSchedule
        DoctorScheduleDTO doctorScheduleDTO = doctorScheduleMapper.toDto(doctorSchedule);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDoctorScheduleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, doctorScheduleDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(doctorScheduleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DoctorSchedule in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDoctorSchedule() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        doctorSchedule.setId(longCount.incrementAndGet());

        // Create the DoctorSchedule
        DoctorScheduleDTO doctorScheduleDTO = doctorScheduleMapper.toDto(doctorSchedule);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDoctorScheduleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(doctorScheduleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DoctorSchedule in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDoctorSchedule() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        doctorSchedule.setId(longCount.incrementAndGet());

        // Create the DoctorSchedule
        DoctorScheduleDTO doctorScheduleDTO = doctorScheduleMapper.toDto(doctorSchedule);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDoctorScheduleMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(doctorScheduleDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DoctorSchedule in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDoctorScheduleWithPatch() throws Exception {
        // Initialize the database
        insertedDoctorSchedule = doctorScheduleRepository.saveAndFlush(doctorSchedule);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the doctorSchedule using partial update
        DoctorSchedule partialUpdatedDoctorSchedule = new DoctorSchedule();
        partialUpdatedDoctorSchedule.setId(doctorSchedule.getId());

        partialUpdatedDoctorSchedule.dayOfWeek(UPDATED_DAY_OF_WEEK).startTime(UPDATED_START_TIME);

        restDoctorScheduleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDoctorSchedule.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDoctorSchedule))
            )
            .andExpect(status().isOk());

        // Validate the DoctorSchedule in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDoctorScheduleUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedDoctorSchedule, doctorSchedule),
            getPersistedDoctorSchedule(doctorSchedule)
        );
    }

    @Test
    @Transactional
    void fullUpdateDoctorScheduleWithPatch() throws Exception {
        // Initialize the database
        insertedDoctorSchedule = doctorScheduleRepository.saveAndFlush(doctorSchedule);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the doctorSchedule using partial update
        DoctorSchedule partialUpdatedDoctorSchedule = new DoctorSchedule();
        partialUpdatedDoctorSchedule.setId(doctorSchedule.getId());

        partialUpdatedDoctorSchedule.dayOfWeek(UPDATED_DAY_OF_WEEK).startTime(UPDATED_START_TIME).endTime(UPDATED_END_TIME);

        restDoctorScheduleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDoctorSchedule.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDoctorSchedule))
            )
            .andExpect(status().isOk());

        // Validate the DoctorSchedule in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDoctorScheduleUpdatableFieldsEquals(partialUpdatedDoctorSchedule, getPersistedDoctorSchedule(partialUpdatedDoctorSchedule));
    }

    @Test
    @Transactional
    void patchNonExistingDoctorSchedule() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        doctorSchedule.setId(longCount.incrementAndGet());

        // Create the DoctorSchedule
        DoctorScheduleDTO doctorScheduleDTO = doctorScheduleMapper.toDto(doctorSchedule);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDoctorScheduleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, doctorScheduleDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(doctorScheduleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DoctorSchedule in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDoctorSchedule() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        doctorSchedule.setId(longCount.incrementAndGet());

        // Create the DoctorSchedule
        DoctorScheduleDTO doctorScheduleDTO = doctorScheduleMapper.toDto(doctorSchedule);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDoctorScheduleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(doctorScheduleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DoctorSchedule in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDoctorSchedule() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        doctorSchedule.setId(longCount.incrementAndGet());

        // Create the DoctorSchedule
        DoctorScheduleDTO doctorScheduleDTO = doctorScheduleMapper.toDto(doctorSchedule);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDoctorScheduleMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(doctorScheduleDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DoctorSchedule in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDoctorSchedule() throws Exception {
        // Initialize the database
        insertedDoctorSchedule = doctorScheduleRepository.saveAndFlush(doctorSchedule);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the doctorSchedule
        restDoctorScheduleMockMvc
            .perform(delete(ENTITY_API_URL_ID, doctorSchedule.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return doctorScheduleRepository.count();
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

    protected DoctorSchedule getPersistedDoctorSchedule(DoctorSchedule doctorSchedule) {
        return doctorScheduleRepository.findById(doctorSchedule.getId()).orElseThrow();
    }

    protected void assertPersistedDoctorScheduleToMatchAllProperties(DoctorSchedule expectedDoctorSchedule) {
        assertDoctorScheduleAllPropertiesEquals(expectedDoctorSchedule, getPersistedDoctorSchedule(expectedDoctorSchedule));
    }

    protected void assertPersistedDoctorScheduleToMatchUpdatableProperties(DoctorSchedule expectedDoctorSchedule) {
        assertDoctorScheduleAllUpdatablePropertiesEquals(expectedDoctorSchedule, getPersistedDoctorSchedule(expectedDoctorSchedule));
    }
}

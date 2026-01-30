package com.med.slot.web.rest;

import static com.med.slot.domain.SmsNotificationAsserts.*;
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
import com.med.slot.domain.SmsNotification;
import com.med.slot.domain.enumeration.SmsStatus;
import com.med.slot.repository.SmsNotificationRepository;
import com.med.slot.service.dto.SmsNotificationDTO;
import com.med.slot.service.mapper.SmsNotificationMapper;
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
 * Integration tests for the {@link SmsNotificationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SmsNotificationResourceIT {

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_MESSAGE = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_SEND_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_SEND_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_SEND_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_SENT_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_SENT_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_SENT_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final SmsStatus DEFAULT_STATUS = SmsStatus.PENDING;
    private static final SmsStatus UPDATED_STATUS = SmsStatus.SENT;

    private static final String DEFAULT_ERROR_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_ERROR_MESSAGE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/sms-notifications";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SmsNotificationRepository smsNotificationRepository;

    @Autowired
    private SmsNotificationMapper smsNotificationMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSmsNotificationMockMvc;

    private SmsNotification smsNotification;

    private SmsNotification insertedSmsNotification;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SmsNotification createEntity() {
        return new SmsNotification()
            .phone(DEFAULT_PHONE)
            .message(DEFAULT_MESSAGE)
            .sendAt(DEFAULT_SEND_AT)
            .sentAt(DEFAULT_SENT_AT)
            .status(DEFAULT_STATUS)
            .errorMessage(DEFAULT_ERROR_MESSAGE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SmsNotification createUpdatedEntity() {
        return new SmsNotification()
            .phone(UPDATED_PHONE)
            .message(UPDATED_MESSAGE)
            .sendAt(UPDATED_SEND_AT)
            .sentAt(UPDATED_SENT_AT)
            .status(UPDATED_STATUS)
            .errorMessage(UPDATED_ERROR_MESSAGE);
    }

    @BeforeEach
    void initTest() {
        smsNotification = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSmsNotification != null) {
            smsNotificationRepository.delete(insertedSmsNotification);
            insertedSmsNotification = null;
        }
    }

    @Test
    @Transactional
    void createSmsNotification() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SmsNotification
        SmsNotificationDTO smsNotificationDTO = smsNotificationMapper.toDto(smsNotification);
        var returnedSmsNotificationDTO = om.readValue(
            restSmsNotificationMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(smsNotificationDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SmsNotificationDTO.class
        );

        // Validate the SmsNotification in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSmsNotification = smsNotificationMapper.toEntity(returnedSmsNotificationDTO);
        assertSmsNotificationUpdatableFieldsEquals(returnedSmsNotification, getPersistedSmsNotification(returnedSmsNotification));

        insertedSmsNotification = returnedSmsNotification;
    }

    @Test
    @Transactional
    void createSmsNotificationWithExistingId() throws Exception {
        // Create the SmsNotification with an existing ID
        smsNotification.setId(1L);
        SmsNotificationDTO smsNotificationDTO = smsNotificationMapper.toDto(smsNotification);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSmsNotificationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(smsNotificationDTO)))
            .andExpect(status().isBadRequest());

        // Validate the SmsNotification in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkPhoneIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        smsNotification.setPhone(null);

        // Create the SmsNotification, which fails.
        SmsNotificationDTO smsNotificationDTO = smsNotificationMapper.toDto(smsNotification);

        restSmsNotificationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(smsNotificationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMessageIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        smsNotification.setMessage(null);

        // Create the SmsNotification, which fails.
        SmsNotificationDTO smsNotificationDTO = smsNotificationMapper.toDto(smsNotification);

        restSmsNotificationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(smsNotificationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSendAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        smsNotification.setSendAt(null);

        // Create the SmsNotification, which fails.
        SmsNotificationDTO smsNotificationDTO = smsNotificationMapper.toDto(smsNotification);

        restSmsNotificationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(smsNotificationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        smsNotification.setStatus(null);

        // Create the SmsNotification, which fails.
        SmsNotificationDTO smsNotificationDTO = smsNotificationMapper.toDto(smsNotification);

        restSmsNotificationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(smsNotificationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSmsNotifications() throws Exception {
        // Initialize the database
        insertedSmsNotification = smsNotificationRepository.saveAndFlush(smsNotification);

        // Get all the smsNotificationList
        restSmsNotificationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(smsNotification.getId().intValue())))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)))
            .andExpect(jsonPath("$.[*].sendAt").value(hasItem(sameInstant(DEFAULT_SEND_AT))))
            .andExpect(jsonPath("$.[*].sentAt").value(hasItem(sameInstant(DEFAULT_SENT_AT))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].errorMessage").value(hasItem(DEFAULT_ERROR_MESSAGE)));
    }

    @Test
    @Transactional
    void getSmsNotification() throws Exception {
        // Initialize the database
        insertedSmsNotification = smsNotificationRepository.saveAndFlush(smsNotification);

        // Get the smsNotification
        restSmsNotificationMockMvc
            .perform(get(ENTITY_API_URL_ID, smsNotification.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(smsNotification.getId().intValue()))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.message").value(DEFAULT_MESSAGE))
            .andExpect(jsonPath("$.sendAt").value(sameInstant(DEFAULT_SEND_AT)))
            .andExpect(jsonPath("$.sentAt").value(sameInstant(DEFAULT_SENT_AT)))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.errorMessage").value(DEFAULT_ERROR_MESSAGE));
    }

    @Test
    @Transactional
    void getSmsNotificationsByIdFiltering() throws Exception {
        // Initialize the database
        insertedSmsNotification = smsNotificationRepository.saveAndFlush(smsNotification);

        Long id = smsNotification.getId();

        defaultSmsNotificationFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultSmsNotificationFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultSmsNotificationFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSmsNotificationsByPhoneIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSmsNotification = smsNotificationRepository.saveAndFlush(smsNotification);

        // Get all the smsNotificationList where phone equals to
        defaultSmsNotificationFiltering("phone.equals=" + DEFAULT_PHONE, "phone.equals=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllSmsNotificationsByPhoneIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSmsNotification = smsNotificationRepository.saveAndFlush(smsNotification);

        // Get all the smsNotificationList where phone in
        defaultSmsNotificationFiltering("phone.in=" + DEFAULT_PHONE + "," + UPDATED_PHONE, "phone.in=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllSmsNotificationsByPhoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSmsNotification = smsNotificationRepository.saveAndFlush(smsNotification);

        // Get all the smsNotificationList where phone is not null
        defaultSmsNotificationFiltering("phone.specified=true", "phone.specified=false");
    }

    @Test
    @Transactional
    void getAllSmsNotificationsByPhoneContainsSomething() throws Exception {
        // Initialize the database
        insertedSmsNotification = smsNotificationRepository.saveAndFlush(smsNotification);

        // Get all the smsNotificationList where phone contains
        defaultSmsNotificationFiltering("phone.contains=" + DEFAULT_PHONE, "phone.contains=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllSmsNotificationsByPhoneNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSmsNotification = smsNotificationRepository.saveAndFlush(smsNotification);

        // Get all the smsNotificationList where phone does not contain
        defaultSmsNotificationFiltering("phone.doesNotContain=" + UPDATED_PHONE, "phone.doesNotContain=" + DEFAULT_PHONE);
    }

    @Test
    @Transactional
    void getAllSmsNotificationsByMessageIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSmsNotification = smsNotificationRepository.saveAndFlush(smsNotification);

        // Get all the smsNotificationList where message equals to
        defaultSmsNotificationFiltering("message.equals=" + DEFAULT_MESSAGE, "message.equals=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllSmsNotificationsByMessageIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSmsNotification = smsNotificationRepository.saveAndFlush(smsNotification);

        // Get all the smsNotificationList where message in
        defaultSmsNotificationFiltering("message.in=" + DEFAULT_MESSAGE + "," + UPDATED_MESSAGE, "message.in=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllSmsNotificationsByMessageIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSmsNotification = smsNotificationRepository.saveAndFlush(smsNotification);

        // Get all the smsNotificationList where message is not null
        defaultSmsNotificationFiltering("message.specified=true", "message.specified=false");
    }

    @Test
    @Transactional
    void getAllSmsNotificationsByMessageContainsSomething() throws Exception {
        // Initialize the database
        insertedSmsNotification = smsNotificationRepository.saveAndFlush(smsNotification);

        // Get all the smsNotificationList where message contains
        defaultSmsNotificationFiltering("message.contains=" + DEFAULT_MESSAGE, "message.contains=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllSmsNotificationsByMessageNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSmsNotification = smsNotificationRepository.saveAndFlush(smsNotification);

        // Get all the smsNotificationList where message does not contain
        defaultSmsNotificationFiltering("message.doesNotContain=" + UPDATED_MESSAGE, "message.doesNotContain=" + DEFAULT_MESSAGE);
    }

    @Test
    @Transactional
    void getAllSmsNotificationsBySendAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSmsNotification = smsNotificationRepository.saveAndFlush(smsNotification);

        // Get all the smsNotificationList where sendAt equals to
        defaultSmsNotificationFiltering("sendAt.equals=" + DEFAULT_SEND_AT, "sendAt.equals=" + UPDATED_SEND_AT);
    }

    @Test
    @Transactional
    void getAllSmsNotificationsBySendAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSmsNotification = smsNotificationRepository.saveAndFlush(smsNotification);

        // Get all the smsNotificationList where sendAt in
        defaultSmsNotificationFiltering("sendAt.in=" + DEFAULT_SEND_AT + "," + UPDATED_SEND_AT, "sendAt.in=" + UPDATED_SEND_AT);
    }

    @Test
    @Transactional
    void getAllSmsNotificationsBySendAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSmsNotification = smsNotificationRepository.saveAndFlush(smsNotification);

        // Get all the smsNotificationList where sendAt is not null
        defaultSmsNotificationFiltering("sendAt.specified=true", "sendAt.specified=false");
    }

    @Test
    @Transactional
    void getAllSmsNotificationsBySendAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSmsNotification = smsNotificationRepository.saveAndFlush(smsNotification);

        // Get all the smsNotificationList where sendAt is greater than or equal to
        defaultSmsNotificationFiltering("sendAt.greaterThanOrEqual=" + DEFAULT_SEND_AT, "sendAt.greaterThanOrEqual=" + UPDATED_SEND_AT);
    }

    @Test
    @Transactional
    void getAllSmsNotificationsBySendAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSmsNotification = smsNotificationRepository.saveAndFlush(smsNotification);

        // Get all the smsNotificationList where sendAt is less than or equal to
        defaultSmsNotificationFiltering("sendAt.lessThanOrEqual=" + DEFAULT_SEND_AT, "sendAt.lessThanOrEqual=" + SMALLER_SEND_AT);
    }

    @Test
    @Transactional
    void getAllSmsNotificationsBySendAtIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSmsNotification = smsNotificationRepository.saveAndFlush(smsNotification);

        // Get all the smsNotificationList where sendAt is less than
        defaultSmsNotificationFiltering("sendAt.lessThan=" + UPDATED_SEND_AT, "sendAt.lessThan=" + DEFAULT_SEND_AT);
    }

    @Test
    @Transactional
    void getAllSmsNotificationsBySendAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSmsNotification = smsNotificationRepository.saveAndFlush(smsNotification);

        // Get all the smsNotificationList where sendAt is greater than
        defaultSmsNotificationFiltering("sendAt.greaterThan=" + SMALLER_SEND_AT, "sendAt.greaterThan=" + DEFAULT_SEND_AT);
    }

    @Test
    @Transactional
    void getAllSmsNotificationsBySentAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSmsNotification = smsNotificationRepository.saveAndFlush(smsNotification);

        // Get all the smsNotificationList where sentAt equals to
        defaultSmsNotificationFiltering("sentAt.equals=" + DEFAULT_SENT_AT, "sentAt.equals=" + UPDATED_SENT_AT);
    }

    @Test
    @Transactional
    void getAllSmsNotificationsBySentAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSmsNotification = smsNotificationRepository.saveAndFlush(smsNotification);

        // Get all the smsNotificationList where sentAt in
        defaultSmsNotificationFiltering("sentAt.in=" + DEFAULT_SENT_AT + "," + UPDATED_SENT_AT, "sentAt.in=" + UPDATED_SENT_AT);
    }

    @Test
    @Transactional
    void getAllSmsNotificationsBySentAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSmsNotification = smsNotificationRepository.saveAndFlush(smsNotification);

        // Get all the smsNotificationList where sentAt is not null
        defaultSmsNotificationFiltering("sentAt.specified=true", "sentAt.specified=false");
    }

    @Test
    @Transactional
    void getAllSmsNotificationsBySentAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSmsNotification = smsNotificationRepository.saveAndFlush(smsNotification);

        // Get all the smsNotificationList where sentAt is greater than or equal to
        defaultSmsNotificationFiltering("sentAt.greaterThanOrEqual=" + DEFAULT_SENT_AT, "sentAt.greaterThanOrEqual=" + UPDATED_SENT_AT);
    }

    @Test
    @Transactional
    void getAllSmsNotificationsBySentAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSmsNotification = smsNotificationRepository.saveAndFlush(smsNotification);

        // Get all the smsNotificationList where sentAt is less than or equal to
        defaultSmsNotificationFiltering("sentAt.lessThanOrEqual=" + DEFAULT_SENT_AT, "sentAt.lessThanOrEqual=" + SMALLER_SENT_AT);
    }

    @Test
    @Transactional
    void getAllSmsNotificationsBySentAtIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSmsNotification = smsNotificationRepository.saveAndFlush(smsNotification);

        // Get all the smsNotificationList where sentAt is less than
        defaultSmsNotificationFiltering("sentAt.lessThan=" + UPDATED_SENT_AT, "sentAt.lessThan=" + DEFAULT_SENT_AT);
    }

    @Test
    @Transactional
    void getAllSmsNotificationsBySentAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSmsNotification = smsNotificationRepository.saveAndFlush(smsNotification);

        // Get all the smsNotificationList where sentAt is greater than
        defaultSmsNotificationFiltering("sentAt.greaterThan=" + SMALLER_SENT_AT, "sentAt.greaterThan=" + DEFAULT_SENT_AT);
    }

    @Test
    @Transactional
    void getAllSmsNotificationsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSmsNotification = smsNotificationRepository.saveAndFlush(smsNotification);

        // Get all the smsNotificationList where status equals to
        defaultSmsNotificationFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllSmsNotificationsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSmsNotification = smsNotificationRepository.saveAndFlush(smsNotification);

        // Get all the smsNotificationList where status in
        defaultSmsNotificationFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllSmsNotificationsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSmsNotification = smsNotificationRepository.saveAndFlush(smsNotification);

        // Get all the smsNotificationList where status is not null
        defaultSmsNotificationFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllSmsNotificationsByErrorMessageIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSmsNotification = smsNotificationRepository.saveAndFlush(smsNotification);

        // Get all the smsNotificationList where errorMessage equals to
        defaultSmsNotificationFiltering("errorMessage.equals=" + DEFAULT_ERROR_MESSAGE, "errorMessage.equals=" + UPDATED_ERROR_MESSAGE);
    }

    @Test
    @Transactional
    void getAllSmsNotificationsByErrorMessageIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSmsNotification = smsNotificationRepository.saveAndFlush(smsNotification);

        // Get all the smsNotificationList where errorMessage in
        defaultSmsNotificationFiltering(
            "errorMessage.in=" + DEFAULT_ERROR_MESSAGE + "," + UPDATED_ERROR_MESSAGE,
            "errorMessage.in=" + UPDATED_ERROR_MESSAGE
        );
    }

    @Test
    @Transactional
    void getAllSmsNotificationsByErrorMessageIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSmsNotification = smsNotificationRepository.saveAndFlush(smsNotification);

        // Get all the smsNotificationList where errorMessage is not null
        defaultSmsNotificationFiltering("errorMessage.specified=true", "errorMessage.specified=false");
    }

    @Test
    @Transactional
    void getAllSmsNotificationsByErrorMessageContainsSomething() throws Exception {
        // Initialize the database
        insertedSmsNotification = smsNotificationRepository.saveAndFlush(smsNotification);

        // Get all the smsNotificationList where errorMessage contains
        defaultSmsNotificationFiltering("errorMessage.contains=" + DEFAULT_ERROR_MESSAGE, "errorMessage.contains=" + UPDATED_ERROR_MESSAGE);
    }

    @Test
    @Transactional
    void getAllSmsNotificationsByErrorMessageNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSmsNotification = smsNotificationRepository.saveAndFlush(smsNotification);

        // Get all the smsNotificationList where errorMessage does not contain
        defaultSmsNotificationFiltering(
            "errorMessage.doesNotContain=" + UPDATED_ERROR_MESSAGE,
            "errorMessage.doesNotContain=" + DEFAULT_ERROR_MESSAGE
        );
    }

    @Test
    @Transactional
    void getAllSmsNotificationsByClinicIsEqualToSomething() throws Exception {
        Clinic clinic;
        if (TestUtil.findAll(em, Clinic.class).isEmpty()) {
            smsNotificationRepository.saveAndFlush(smsNotification);
            clinic = ClinicResourceIT.createEntity();
        } else {
            clinic = TestUtil.findAll(em, Clinic.class).get(0);
        }
        em.persist(clinic);
        em.flush();
        smsNotification.setClinic(clinic);
        smsNotificationRepository.saveAndFlush(smsNotification);
        Long clinicId = clinic.getId();
        // Get all the smsNotificationList where clinic equals to clinicId
        defaultSmsNotificationShouldBeFound("clinicId.equals=" + clinicId);

        // Get all the smsNotificationList where clinic equals to (clinicId + 1)
        defaultSmsNotificationShouldNotBeFound("clinicId.equals=" + (clinicId + 1));
    }

    @Test
    @Transactional
    void getAllSmsNotificationsByAppointmentIsEqualToSomething() throws Exception {
        Appointment appointment;
        if (TestUtil.findAll(em, Appointment.class).isEmpty()) {
            smsNotificationRepository.saveAndFlush(smsNotification);
            appointment = AppointmentResourceIT.createEntity();
        } else {
            appointment = TestUtil.findAll(em, Appointment.class).get(0);
        }
        em.persist(appointment);
        em.flush();
        smsNotification.setAppointment(appointment);
        smsNotificationRepository.saveAndFlush(smsNotification);
        Long appointmentId = appointment.getId();
        // Get all the smsNotificationList where appointment equals to appointmentId
        defaultSmsNotificationShouldBeFound("appointmentId.equals=" + appointmentId);

        // Get all the smsNotificationList where appointment equals to (appointmentId + 1)
        defaultSmsNotificationShouldNotBeFound("appointmentId.equals=" + (appointmentId + 1));
    }

    private void defaultSmsNotificationFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultSmsNotificationShouldBeFound(shouldBeFound);
        defaultSmsNotificationShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSmsNotificationShouldBeFound(String filter) throws Exception {
        restSmsNotificationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(smsNotification.getId().intValue())))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)))
            .andExpect(jsonPath("$.[*].sendAt").value(hasItem(sameInstant(DEFAULT_SEND_AT))))
            .andExpect(jsonPath("$.[*].sentAt").value(hasItem(sameInstant(DEFAULT_SENT_AT))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].errorMessage").value(hasItem(DEFAULT_ERROR_MESSAGE)));

        // Check, that the count call also returns 1
        restSmsNotificationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSmsNotificationShouldNotBeFound(String filter) throws Exception {
        restSmsNotificationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSmsNotificationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSmsNotification() throws Exception {
        // Get the smsNotification
        restSmsNotificationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSmsNotification() throws Exception {
        // Initialize the database
        insertedSmsNotification = smsNotificationRepository.saveAndFlush(smsNotification);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the smsNotification
        SmsNotification updatedSmsNotification = smsNotificationRepository.findById(smsNotification.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSmsNotification are not directly saved in db
        em.detach(updatedSmsNotification);
        updatedSmsNotification
            .phone(UPDATED_PHONE)
            .message(UPDATED_MESSAGE)
            .sendAt(UPDATED_SEND_AT)
            .sentAt(UPDATED_SENT_AT)
            .status(UPDATED_STATUS)
            .errorMessage(UPDATED_ERROR_MESSAGE);
        SmsNotificationDTO smsNotificationDTO = smsNotificationMapper.toDto(updatedSmsNotification);

        restSmsNotificationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, smsNotificationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(smsNotificationDTO))
            )
            .andExpect(status().isOk());

        // Validate the SmsNotification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSmsNotificationToMatchAllProperties(updatedSmsNotification);
    }

    @Test
    @Transactional
    void putNonExistingSmsNotification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        smsNotification.setId(longCount.incrementAndGet());

        // Create the SmsNotification
        SmsNotificationDTO smsNotificationDTO = smsNotificationMapper.toDto(smsNotification);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSmsNotificationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, smsNotificationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(smsNotificationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SmsNotification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSmsNotification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        smsNotification.setId(longCount.incrementAndGet());

        // Create the SmsNotification
        SmsNotificationDTO smsNotificationDTO = smsNotificationMapper.toDto(smsNotification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSmsNotificationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(smsNotificationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SmsNotification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSmsNotification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        smsNotification.setId(longCount.incrementAndGet());

        // Create the SmsNotification
        SmsNotificationDTO smsNotificationDTO = smsNotificationMapper.toDto(smsNotification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSmsNotificationMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(smsNotificationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SmsNotification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSmsNotificationWithPatch() throws Exception {
        // Initialize the database
        insertedSmsNotification = smsNotificationRepository.saveAndFlush(smsNotification);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the smsNotification using partial update
        SmsNotification partialUpdatedSmsNotification = new SmsNotification();
        partialUpdatedSmsNotification.setId(smsNotification.getId());

        partialUpdatedSmsNotification.message(UPDATED_MESSAGE).sendAt(UPDATED_SEND_AT).sentAt(UPDATED_SENT_AT).status(UPDATED_STATUS);

        restSmsNotificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSmsNotification.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSmsNotification))
            )
            .andExpect(status().isOk());

        // Validate the SmsNotification in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSmsNotificationUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSmsNotification, smsNotification),
            getPersistedSmsNotification(smsNotification)
        );
    }

    @Test
    @Transactional
    void fullUpdateSmsNotificationWithPatch() throws Exception {
        // Initialize the database
        insertedSmsNotification = smsNotificationRepository.saveAndFlush(smsNotification);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the smsNotification using partial update
        SmsNotification partialUpdatedSmsNotification = new SmsNotification();
        partialUpdatedSmsNotification.setId(smsNotification.getId());

        partialUpdatedSmsNotification
            .phone(UPDATED_PHONE)
            .message(UPDATED_MESSAGE)
            .sendAt(UPDATED_SEND_AT)
            .sentAt(UPDATED_SENT_AT)
            .status(UPDATED_STATUS)
            .errorMessage(UPDATED_ERROR_MESSAGE);

        restSmsNotificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSmsNotification.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSmsNotification))
            )
            .andExpect(status().isOk());

        // Validate the SmsNotification in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSmsNotificationUpdatableFieldsEquals(
            partialUpdatedSmsNotification,
            getPersistedSmsNotification(partialUpdatedSmsNotification)
        );
    }

    @Test
    @Transactional
    void patchNonExistingSmsNotification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        smsNotification.setId(longCount.incrementAndGet());

        // Create the SmsNotification
        SmsNotificationDTO smsNotificationDTO = smsNotificationMapper.toDto(smsNotification);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSmsNotificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, smsNotificationDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(smsNotificationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SmsNotification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSmsNotification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        smsNotification.setId(longCount.incrementAndGet());

        // Create the SmsNotification
        SmsNotificationDTO smsNotificationDTO = smsNotificationMapper.toDto(smsNotification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSmsNotificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(smsNotificationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SmsNotification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSmsNotification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        smsNotification.setId(longCount.incrementAndGet());

        // Create the SmsNotification
        SmsNotificationDTO smsNotificationDTO = smsNotificationMapper.toDto(smsNotification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSmsNotificationMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(smsNotificationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SmsNotification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSmsNotification() throws Exception {
        // Initialize the database
        insertedSmsNotification = smsNotificationRepository.saveAndFlush(smsNotification);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the smsNotification
        restSmsNotificationMockMvc
            .perform(delete(ENTITY_API_URL_ID, smsNotification.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return smsNotificationRepository.count();
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

    protected SmsNotification getPersistedSmsNotification(SmsNotification smsNotification) {
        return smsNotificationRepository.findById(smsNotification.getId()).orElseThrow();
    }

    protected void assertPersistedSmsNotificationToMatchAllProperties(SmsNotification expectedSmsNotification) {
        assertSmsNotificationAllPropertiesEquals(expectedSmsNotification, getPersistedSmsNotification(expectedSmsNotification));
    }

    protected void assertPersistedSmsNotificationToMatchUpdatableProperties(SmsNotification expectedSmsNotification) {
        assertSmsNotificationAllUpdatablePropertiesEquals(expectedSmsNotification, getPersistedSmsNotification(expectedSmsNotification));
    }
}

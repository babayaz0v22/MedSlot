package com.med.slot.web.rest;

import com.med.slot.repository.SmsNotificationRepository;
import com.med.slot.service.SmsNotificationQueryService;
import com.med.slot.service.SmsNotificationService;
import com.med.slot.service.criteria.SmsNotificationCriteria;
import com.med.slot.service.dto.SmsNotificationDTO;
import com.med.slot.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.med.slot.domain.SmsNotification}.
 */
@RestController
@RequestMapping("/api/sms-notifications")
public class SmsNotificationResource {

    private static final Logger LOG = LoggerFactory.getLogger(SmsNotificationResource.class);

    private static final String ENTITY_NAME = "smsNotification";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SmsNotificationService smsNotificationService;

    private final SmsNotificationRepository smsNotificationRepository;

    private final SmsNotificationQueryService smsNotificationQueryService;

    public SmsNotificationResource(
        SmsNotificationService smsNotificationService,
        SmsNotificationRepository smsNotificationRepository,
        SmsNotificationQueryService smsNotificationQueryService
    ) {
        this.smsNotificationService = smsNotificationService;
        this.smsNotificationRepository = smsNotificationRepository;
        this.smsNotificationQueryService = smsNotificationQueryService;
    }

    /**
     * {@code POST  /sms-notifications} : Create a new smsNotification.
     *
     * @param smsNotificationDTO the smsNotificationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new smsNotificationDTO, or with status {@code 400 (Bad Request)} if the smsNotification has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SmsNotificationDTO> createSmsNotification(@Valid @RequestBody SmsNotificationDTO smsNotificationDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save SmsNotification : {}", smsNotificationDTO);
        if (smsNotificationDTO.getId() != null) {
            throw new BadRequestAlertException("A new smsNotification cannot already have an ID", ENTITY_NAME, "idexists");
        }
        smsNotificationDTO = smsNotificationService.save(smsNotificationDTO);
        return ResponseEntity.created(new URI("/api/sms-notifications/" + smsNotificationDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, smsNotificationDTO.getId().toString()))
            .body(smsNotificationDTO);
    }

    /**
     * {@code PUT  /sms-notifications/:id} : Updates an existing smsNotification.
     *
     * @param id the id of the smsNotificationDTO to save.
     * @param smsNotificationDTO the smsNotificationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated smsNotificationDTO,
     * or with status {@code 400 (Bad Request)} if the smsNotificationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the smsNotificationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SmsNotificationDTO> updateSmsNotification(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SmsNotificationDTO smsNotificationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update SmsNotification : {}, {}", id, smsNotificationDTO);
        if (smsNotificationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, smsNotificationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!smsNotificationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        smsNotificationDTO = smsNotificationService.update(smsNotificationDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, smsNotificationDTO.getId().toString()))
            .body(smsNotificationDTO);
    }

    /**
     * {@code PATCH  /sms-notifications/:id} : Partial updates given fields of an existing smsNotification, field will ignore if it is null
     *
     * @param id the id of the smsNotificationDTO to save.
     * @param smsNotificationDTO the smsNotificationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated smsNotificationDTO,
     * or with status {@code 400 (Bad Request)} if the smsNotificationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the smsNotificationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the smsNotificationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SmsNotificationDTO> partialUpdateSmsNotification(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SmsNotificationDTO smsNotificationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update SmsNotification partially : {}, {}", id, smsNotificationDTO);
        if (smsNotificationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, smsNotificationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!smsNotificationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SmsNotificationDTO> result = smsNotificationService.partialUpdate(smsNotificationDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, smsNotificationDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /sms-notifications} : get all the smsNotifications.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of smsNotifications in body.
     */
    @GetMapping("")
    public ResponseEntity<List<SmsNotificationDTO>> getAllSmsNotifications(
        SmsNotificationCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get SmsNotifications by criteria: {}", criteria);

        Page<SmsNotificationDTO> page = smsNotificationQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /sms-notifications/count} : count all the smsNotifications.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countSmsNotifications(SmsNotificationCriteria criteria) {
        LOG.debug("REST request to count SmsNotifications by criteria: {}", criteria);
        return ResponseEntity.ok().body(smsNotificationQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /sms-notifications/:id} : get the "id" smsNotification.
     *
     * @param id the id of the smsNotificationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the smsNotificationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SmsNotificationDTO> getSmsNotification(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SmsNotification : {}", id);
        Optional<SmsNotificationDTO> smsNotificationDTO = smsNotificationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(smsNotificationDTO);
    }

    /**
     * {@code DELETE  /sms-notifications/:id} : delete the "id" smsNotification.
     *
     * @param id the id of the smsNotificationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSmsNotification(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SmsNotification : {}", id);
        smsNotificationService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}

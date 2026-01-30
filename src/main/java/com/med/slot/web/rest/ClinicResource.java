package com.med.slot.web.rest;

import com.med.slot.repository.ClinicRepository;
import com.med.slot.service.ClinicService;
import com.med.slot.service.dto.ClinicDTO;
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
 * REST controller for managing {@link com.med.slot.domain.Clinic}.
 */
@RestController
@RequestMapping("/api/clinics")
public class ClinicResource {

    private static final Logger LOG = LoggerFactory.getLogger(ClinicResource.class);

    private static final String ENTITY_NAME = "clinic";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ClinicService clinicService;

    private final ClinicRepository clinicRepository;

    public ClinicResource(ClinicService clinicService, ClinicRepository clinicRepository) {
        this.clinicService = clinicService;
        this.clinicRepository = clinicRepository;
    }

    /**
     * {@code POST  /clinics} : Create a new clinic.
     *
     * @param clinicDTO the clinicDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new clinicDTO, or with status {@code 400 (Bad Request)} if the clinic has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ClinicDTO> createClinic(@Valid @RequestBody ClinicDTO clinicDTO) throws URISyntaxException {
        LOG.debug("REST request to save Clinic : {}", clinicDTO);
        if (clinicDTO.getId() != null) {
            throw new BadRequestAlertException("A new clinic cannot already have an ID", ENTITY_NAME, "idexists");
        }
        clinicDTO = clinicService.save(clinicDTO);
        return ResponseEntity.created(new URI("/api/clinics/" + clinicDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, clinicDTO.getId().toString()))
            .body(clinicDTO);
    }

    /**
     * {@code PUT  /clinics/:id} : Updates an existing clinic.
     *
     * @param id the id of the clinicDTO to save.
     * @param clinicDTO the clinicDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clinicDTO,
     * or with status {@code 400 (Bad Request)} if the clinicDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the clinicDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClinicDTO> updateClinic(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ClinicDTO clinicDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Clinic : {}, {}", id, clinicDTO);
        if (clinicDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clinicDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clinicRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        clinicDTO = clinicService.update(clinicDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, clinicDTO.getId().toString()))
            .body(clinicDTO);
    }

    /**
     * {@code PATCH  /clinics/:id} : Partial updates given fields of an existing clinic, field will ignore if it is null
     *
     * @param id the id of the clinicDTO to save.
     * @param clinicDTO the clinicDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clinicDTO,
     * or with status {@code 400 (Bad Request)} if the clinicDTO is not valid,
     * or with status {@code 404 (Not Found)} if the clinicDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the clinicDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ClinicDTO> partialUpdateClinic(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ClinicDTO clinicDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Clinic partially : {}, {}", id, clinicDTO);
        if (clinicDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clinicDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clinicRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ClinicDTO> result = clinicService.partialUpdate(clinicDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, clinicDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /clinics} : get all the clinics.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of clinics in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ClinicDTO>> getAllClinics(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Clinics");
        Page<ClinicDTO> page = clinicService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /clinics/:id} : get the "id" clinic.
     *
     * @param id the id of the clinicDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the clinicDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClinicDTO> getClinic(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Clinic : {}", id);
        Optional<ClinicDTO> clinicDTO = clinicService.findOne(id);
        return ResponseUtil.wrapOrNotFound(clinicDTO);
    }

    /**
     * {@code DELETE  /clinics/:id} : delete the "id" clinic.
     *
     * @param id the id of the clinicDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClinic(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Clinic : {}", id);
        clinicService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}

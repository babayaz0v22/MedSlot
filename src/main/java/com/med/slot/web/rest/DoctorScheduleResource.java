package com.med.slot.web.rest;

import com.med.slot.repository.DoctorScheduleRepository;
import com.med.slot.service.DoctorScheduleService;
import com.med.slot.service.dto.DoctorScheduleDTO;
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
 * REST controller for managing {@link com.med.slot.domain.DoctorSchedule}.
 */
@RestController
@RequestMapping("/api/doctor-schedules")
public class DoctorScheduleResource {

    private static final Logger LOG = LoggerFactory.getLogger(DoctorScheduleResource.class);

    private static final String ENTITY_NAME = "doctorSchedule";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DoctorScheduleService doctorScheduleService;

    private final DoctorScheduleRepository doctorScheduleRepository;

    public DoctorScheduleResource(DoctorScheduleService doctorScheduleService, DoctorScheduleRepository doctorScheduleRepository) {
        this.doctorScheduleService = doctorScheduleService;
        this.doctorScheduleRepository = doctorScheduleRepository;
    }

    /**
     * {@code POST  /doctor-schedules} : Create a new doctorSchedule.
     *
     * @param doctorScheduleDTO the doctorScheduleDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new doctorScheduleDTO, or with status {@code 400 (Bad Request)} if the doctorSchedule has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<DoctorScheduleDTO> createDoctorSchedule(@Valid @RequestBody DoctorScheduleDTO doctorScheduleDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save DoctorSchedule : {}", doctorScheduleDTO);
        if (doctorScheduleDTO.getId() != null) {
            throw new BadRequestAlertException("A new doctorSchedule cannot already have an ID", ENTITY_NAME, "idexists");
        }
        doctorScheduleDTO = doctorScheduleService.save(doctorScheduleDTO);
        return ResponseEntity.created(new URI("/api/doctor-schedules/" + doctorScheduleDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, doctorScheduleDTO.getId().toString()))
            .body(doctorScheduleDTO);
    }

    /**
     * {@code PUT  /doctor-schedules/:id} : Updates an existing doctorSchedule.
     *
     * @param id the id of the doctorScheduleDTO to save.
     * @param doctorScheduleDTO the doctorScheduleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated doctorScheduleDTO,
     * or with status {@code 400 (Bad Request)} if the doctorScheduleDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the doctorScheduleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DoctorScheduleDTO> updateDoctorSchedule(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody DoctorScheduleDTO doctorScheduleDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update DoctorSchedule : {}, {}", id, doctorScheduleDTO);
        if (doctorScheduleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, doctorScheduleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!doctorScheduleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        doctorScheduleDTO = doctorScheduleService.update(doctorScheduleDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, doctorScheduleDTO.getId().toString()))
            .body(doctorScheduleDTO);
    }

    /**
     * {@code PATCH  /doctor-schedules/:id} : Partial updates given fields of an existing doctorSchedule, field will ignore if it is null
     *
     * @param id the id of the doctorScheduleDTO to save.
     * @param doctorScheduleDTO the doctorScheduleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated doctorScheduleDTO,
     * or with status {@code 400 (Bad Request)} if the doctorScheduleDTO is not valid,
     * or with status {@code 404 (Not Found)} if the doctorScheduleDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the doctorScheduleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DoctorScheduleDTO> partialUpdateDoctorSchedule(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody DoctorScheduleDTO doctorScheduleDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update DoctorSchedule partially : {}, {}", id, doctorScheduleDTO);
        if (doctorScheduleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, doctorScheduleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!doctorScheduleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DoctorScheduleDTO> result = doctorScheduleService.partialUpdate(doctorScheduleDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, doctorScheduleDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /doctor-schedules} : get all the doctorSchedules.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of doctorSchedules in body.
     */
    @GetMapping("")
    public ResponseEntity<List<DoctorScheduleDTO>> getAllDoctorSchedules(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of DoctorSchedules");
        Page<DoctorScheduleDTO> page = doctorScheduleService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /doctor-schedules/:id} : get the "id" doctorSchedule.
     *
     * @param id the id of the doctorScheduleDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the doctorScheduleDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DoctorScheduleDTO> getDoctorSchedule(@PathVariable("id") Long id) {
        LOG.debug("REST request to get DoctorSchedule : {}", id);
        Optional<DoctorScheduleDTO> doctorScheduleDTO = doctorScheduleService.findOne(id);
        return ResponseUtil.wrapOrNotFound(doctorScheduleDTO);
    }

    /**
     * {@code DELETE  /doctor-schedules/:id} : delete the "id" doctorSchedule.
     *
     * @param id the id of the doctorScheduleDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctorSchedule(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete DoctorSchedule : {}", id);
        doctorScheduleService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}

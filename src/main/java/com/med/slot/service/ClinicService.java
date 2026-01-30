package com.med.slot.service;

import com.med.slot.domain.Clinic;
import com.med.slot.repository.ClinicRepository;
import com.med.slot.service.dto.ClinicDTO;
import com.med.slot.service.mapper.ClinicMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.med.slot.domain.Clinic}.
 */
@Service
@Transactional
public class ClinicService {

    private static final Logger LOG = LoggerFactory.getLogger(ClinicService.class);

    private final ClinicRepository clinicRepository;

    private final ClinicMapper clinicMapper;

    public ClinicService(ClinicRepository clinicRepository, ClinicMapper clinicMapper) {
        this.clinicRepository = clinicRepository;
        this.clinicMapper = clinicMapper;
    }

    /**
     * Save a clinic.
     *
     * @param clinicDTO the entity to save.
     * @return the persisted entity.
     */
    public ClinicDTO save(ClinicDTO clinicDTO) {
        LOG.debug("Request to save Clinic : {}", clinicDTO);
        Clinic clinic = clinicMapper.toEntity(clinicDTO);
        clinic = clinicRepository.save(clinic);
        return clinicMapper.toDto(clinic);
    }

    /**
     * Update a clinic.
     *
     * @param clinicDTO the entity to save.
     * @return the persisted entity.
     */
    public ClinicDTO update(ClinicDTO clinicDTO) {
        LOG.debug("Request to update Clinic : {}", clinicDTO);
        Clinic clinic = clinicMapper.toEntity(clinicDTO);
        clinic = clinicRepository.save(clinic);
        return clinicMapper.toDto(clinic);
    }

    /**
     * Partially update a clinic.
     *
     * @param clinicDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ClinicDTO> partialUpdate(ClinicDTO clinicDTO) {
        LOG.debug("Request to partially update Clinic : {}", clinicDTO);

        return clinicRepository
            .findById(clinicDTO.getId())
            .map(existingClinic -> {
                clinicMapper.partialUpdate(existingClinic, clinicDTO);

                return existingClinic;
            })
            .map(clinicRepository::save)
            .map(clinicMapper::toDto);
    }

    /**
     * Get all the clinics.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ClinicDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Clinics");
        return clinicRepository.findAll(pageable).map(clinicMapper::toDto);
    }

    /**
     * Get one clinic by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ClinicDTO> findOne(Long id) {
        LOG.debug("Request to get Clinic : {}", id);
        return clinicRepository.findById(id).map(clinicMapper::toDto);
    }

    /**
     * Delete the clinic by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Clinic : {}", id);
        clinicRepository.deleteById(id);
    }
}

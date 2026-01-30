package com.med.slot.service;

import com.med.slot.domain.DoctorSchedule;
import com.med.slot.repository.DoctorScheduleRepository;
import com.med.slot.service.dto.DoctorScheduleDTO;
import com.med.slot.service.mapper.DoctorScheduleMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.med.slot.domain.DoctorSchedule}.
 */
@Service
@Transactional
public class DoctorScheduleService {

    private static final Logger LOG = LoggerFactory.getLogger(DoctorScheduleService.class);

    private final DoctorScheduleRepository doctorScheduleRepository;

    private final DoctorScheduleMapper doctorScheduleMapper;

    public DoctorScheduleService(DoctorScheduleRepository doctorScheduleRepository, DoctorScheduleMapper doctorScheduleMapper) {
        this.doctorScheduleRepository = doctorScheduleRepository;
        this.doctorScheduleMapper = doctorScheduleMapper;
    }

    /**
     * Save a doctorSchedule.
     *
     * @param doctorScheduleDTO the entity to save.
     * @return the persisted entity.
     */
    public DoctorScheduleDTO save(DoctorScheduleDTO doctorScheduleDTO) {
        LOG.debug("Request to save DoctorSchedule : {}", doctorScheduleDTO);
        DoctorSchedule doctorSchedule = doctorScheduleMapper.toEntity(doctorScheduleDTO);
        doctorSchedule = doctorScheduleRepository.save(doctorSchedule);
        return doctorScheduleMapper.toDto(doctorSchedule);
    }

    /**
     * Update a doctorSchedule.
     *
     * @param doctorScheduleDTO the entity to save.
     * @return the persisted entity.
     */
    public DoctorScheduleDTO update(DoctorScheduleDTO doctorScheduleDTO) {
        LOG.debug("Request to update DoctorSchedule : {}", doctorScheduleDTO);
        DoctorSchedule doctorSchedule = doctorScheduleMapper.toEntity(doctorScheduleDTO);
        doctorSchedule = doctorScheduleRepository.save(doctorSchedule);
        return doctorScheduleMapper.toDto(doctorSchedule);
    }

    /**
     * Partially update a doctorSchedule.
     *
     * @param doctorScheduleDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<DoctorScheduleDTO> partialUpdate(DoctorScheduleDTO doctorScheduleDTO) {
        LOG.debug("Request to partially update DoctorSchedule : {}", doctorScheduleDTO);

        return doctorScheduleRepository
            .findById(doctorScheduleDTO.getId())
            .map(existingDoctorSchedule -> {
                doctorScheduleMapper.partialUpdate(existingDoctorSchedule, doctorScheduleDTO);

                return existingDoctorSchedule;
            })
            .map(doctorScheduleRepository::save)
            .map(doctorScheduleMapper::toDto);
    }

    /**
     * Get all the doctorSchedules.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<DoctorScheduleDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all DoctorSchedules");
        return doctorScheduleRepository.findAll(pageable).map(doctorScheduleMapper::toDto);
    }

    /**
     * Get one doctorSchedule by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<DoctorScheduleDTO> findOne(Long id) {
        LOG.debug("Request to get DoctorSchedule : {}", id);
        return doctorScheduleRepository.findById(id).map(doctorScheduleMapper::toDto);
    }

    /**
     * Delete the doctorSchedule by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete DoctorSchedule : {}", id);
        doctorScheduleRepository.deleteById(id);
    }
}

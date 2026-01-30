package com.med.slot.service;

import com.med.slot.domain.*; // for static metamodels
import com.med.slot.domain.Doctor;
import com.med.slot.repository.DoctorRepository;
import com.med.slot.service.criteria.DoctorCriteria;
import com.med.slot.service.dto.DoctorDTO;
import com.med.slot.service.mapper.DoctorMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Doctor} entities in the database.
 * The main input is a {@link DoctorCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link DoctorDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class DoctorQueryService extends QueryService<Doctor> {

    private static final Logger LOG = LoggerFactory.getLogger(DoctorQueryService.class);

    private final DoctorRepository doctorRepository;

    private final DoctorMapper doctorMapper;

    public DoctorQueryService(DoctorRepository doctorRepository, DoctorMapper doctorMapper) {
        this.doctorRepository = doctorRepository;
        this.doctorMapper = doctorMapper;
    }

    /**
     * Return a {@link Page} of {@link DoctorDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<DoctorDTO> findByCriteria(DoctorCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Doctor> specification = createSpecification(criteria);
        return doctorRepository.findAll(specification, page).map(doctorMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(DoctorCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Doctor> specification = createSpecification(criteria);
        return doctorRepository.count(specification);
    }

    /**
     * Function to convert {@link DoctorCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Doctor> createSpecification(DoctorCriteria criteria) {
        Specification<Doctor> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Doctor_.id),
                buildStringSpecification(criteria.getSpecialty(), Doctor_.specialty),
                buildStringSpecification(criteria.getPhone(), Doctor_.phone),
                buildSpecification(criteria.getDoctorScheduleId(), root ->
                    root.join(Doctor_.doctorSchedules, JoinType.LEFT).get(DoctorSchedule_.id)
                ),
                buildSpecification(criteria.getAppointmentId(), root -> root.join(Doctor_.appointments, JoinType.LEFT).get(Appointment_.id)
                ),
                buildSpecification(criteria.getClinicId(), root -> root.join(Doctor_.clinic, JoinType.LEFT).get(Clinic_.id))
            );
        }
        return specification;
    }
}

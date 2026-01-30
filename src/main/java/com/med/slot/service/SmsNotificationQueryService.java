package com.med.slot.service;

import com.med.slot.domain.*; // for static metamodels
import com.med.slot.domain.SmsNotification;
import com.med.slot.repository.SmsNotificationRepository;
import com.med.slot.service.criteria.SmsNotificationCriteria;
import com.med.slot.service.dto.SmsNotificationDTO;
import com.med.slot.service.mapper.SmsNotificationMapper;
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
 * Service for executing complex queries for {@link SmsNotification} entities in the database.
 * The main input is a {@link SmsNotificationCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link SmsNotificationDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SmsNotificationQueryService extends QueryService<SmsNotification> {

    private static final Logger LOG = LoggerFactory.getLogger(SmsNotificationQueryService.class);

    private final SmsNotificationRepository smsNotificationRepository;

    private final SmsNotificationMapper smsNotificationMapper;

    public SmsNotificationQueryService(SmsNotificationRepository smsNotificationRepository, SmsNotificationMapper smsNotificationMapper) {
        this.smsNotificationRepository = smsNotificationRepository;
        this.smsNotificationMapper = smsNotificationMapper;
    }

    /**
     * Return a {@link Page} of {@link SmsNotificationDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SmsNotificationDTO> findByCriteria(SmsNotificationCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<SmsNotification> specification = createSpecification(criteria);
        return smsNotificationRepository.findAll(specification, page).map(smsNotificationMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SmsNotificationCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<SmsNotification> specification = createSpecification(criteria);
        return smsNotificationRepository.count(specification);
    }

    /**
     * Function to convert {@link SmsNotificationCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<SmsNotification> createSpecification(SmsNotificationCriteria criteria) {
        Specification<SmsNotification> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), SmsNotification_.id),
                buildStringSpecification(criteria.getPhone(), SmsNotification_.phone),
                buildStringSpecification(criteria.getMessage(), SmsNotification_.message),
                buildRangeSpecification(criteria.getSendAt(), SmsNotification_.sendAt),
                buildRangeSpecification(criteria.getSentAt(), SmsNotification_.sentAt),
                buildSpecification(criteria.getStatus(), SmsNotification_.status),
                buildStringSpecification(criteria.getErrorMessage(), SmsNotification_.errorMessage),
                buildSpecification(criteria.getClinicId(), root -> root.join(SmsNotification_.clinic, JoinType.LEFT).get(Clinic_.id)),
                buildSpecification(criteria.getAppointmentId(), root ->
                    root.join(SmsNotification_.appointment, JoinType.LEFT).get(Appointment_.id)
                )
            );
        }
        return specification;
    }
}

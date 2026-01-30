package com.med.slot.service;

import com.med.slot.domain.SmsNotification;
import com.med.slot.repository.SmsNotificationRepository;
import com.med.slot.service.dto.SmsNotificationDTO;
import com.med.slot.service.mapper.SmsNotificationMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.med.slot.domain.SmsNotification}.
 */
@Service
@Transactional
public class SmsNotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(SmsNotificationService.class);

    private final SmsNotificationRepository smsNotificationRepository;

    private final SmsNotificationMapper smsNotificationMapper;

    public SmsNotificationService(SmsNotificationRepository smsNotificationRepository, SmsNotificationMapper smsNotificationMapper) {
        this.smsNotificationRepository = smsNotificationRepository;
        this.smsNotificationMapper = smsNotificationMapper;
    }

    /**
     * Save a smsNotification.
     *
     * @param smsNotificationDTO the entity to save.
     * @return the persisted entity.
     */
    public SmsNotificationDTO save(SmsNotificationDTO smsNotificationDTO) {
        LOG.debug("Request to save SmsNotification : {}", smsNotificationDTO);
        SmsNotification smsNotification = smsNotificationMapper.toEntity(smsNotificationDTO);
        smsNotification = smsNotificationRepository.save(smsNotification);
        return smsNotificationMapper.toDto(smsNotification);
    }

    /**
     * Update a smsNotification.
     *
     * @param smsNotificationDTO the entity to save.
     * @return the persisted entity.
     */
    public SmsNotificationDTO update(SmsNotificationDTO smsNotificationDTO) {
        LOG.debug("Request to update SmsNotification : {}", smsNotificationDTO);
        SmsNotification smsNotification = smsNotificationMapper.toEntity(smsNotificationDTO);
        smsNotification = smsNotificationRepository.save(smsNotification);
        return smsNotificationMapper.toDto(smsNotification);
    }

    /**
     * Partially update a smsNotification.
     *
     * @param smsNotificationDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<SmsNotificationDTO> partialUpdate(SmsNotificationDTO smsNotificationDTO) {
        LOG.debug("Request to partially update SmsNotification : {}", smsNotificationDTO);

        return smsNotificationRepository
            .findById(smsNotificationDTO.getId())
            .map(existingSmsNotification -> {
                smsNotificationMapper.partialUpdate(existingSmsNotification, smsNotificationDTO);

                return existingSmsNotification;
            })
            .map(smsNotificationRepository::save)
            .map(smsNotificationMapper::toDto);
    }

    /**
     * Get one smsNotification by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SmsNotificationDTO> findOne(Long id) {
        LOG.debug("Request to get SmsNotification : {}", id);
        return smsNotificationRepository.findById(id).map(smsNotificationMapper::toDto);
    }

    /**
     * Delete the smsNotification by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete SmsNotification : {}", id);
        smsNotificationRepository.deleteById(id);
    }
}

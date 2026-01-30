package com.med.slot.repository;

import com.med.slot.domain.SmsNotification;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SmsNotification entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SmsNotificationRepository extends JpaRepository<SmsNotification, Long>, JpaSpecificationExecutor<SmsNotification> {}

package com.med.slot.repository;

import com.med.slot.domain.DoctorSchedule;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the DoctorSchedule entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long> {}

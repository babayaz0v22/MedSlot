package com.med.slot.repository;

import com.med.slot.domain.Clinic;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Clinic entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClinicRepository extends JpaRepository<Clinic, Long> {}

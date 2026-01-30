package com.med.slot.service.mapper;

import com.med.slot.domain.Clinic;
import com.med.slot.service.dto.ClinicDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Clinic} and its DTO {@link ClinicDTO}.
 */
@Mapper(componentModel = "spring")
public interface ClinicMapper extends EntityMapper<ClinicDTO, Clinic> {}

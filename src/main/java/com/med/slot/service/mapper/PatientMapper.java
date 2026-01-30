package com.med.slot.service.mapper;

import com.med.slot.domain.Clinic;
import com.med.slot.domain.Patient;
import com.med.slot.service.dto.ClinicDTO;
import com.med.slot.service.dto.PatientDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Patient} and its DTO {@link PatientDTO}.
 */
@Mapper(componentModel = "spring")
public interface PatientMapper extends EntityMapper<PatientDTO, Patient> {
    @Mapping(target = "clinic", source = "clinic", qualifiedByName = "clinicId")
    PatientDTO toDto(Patient s);

    @Named("clinicId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ClinicDTO toDtoClinicId(Clinic clinic);
}

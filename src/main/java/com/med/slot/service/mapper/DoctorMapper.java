package com.med.slot.service.mapper;

import com.med.slot.domain.Clinic;
import com.med.slot.domain.Doctor;
import com.med.slot.service.dto.ClinicDTO;
import com.med.slot.service.dto.DoctorDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Doctor} and its DTO {@link DoctorDTO}.
 */
@Mapper(componentModel = "spring")
public interface DoctorMapper extends EntityMapper<DoctorDTO, Doctor> {
    @Mapping(target = "clinic", source = "clinic", qualifiedByName = "clinicId")
    DoctorDTO toDto(Doctor s);

    @Named("clinicId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ClinicDTO toDtoClinicId(Clinic clinic);
}

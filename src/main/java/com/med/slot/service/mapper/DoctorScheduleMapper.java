package com.med.slot.service.mapper;

import com.med.slot.domain.Doctor;
import com.med.slot.domain.DoctorSchedule;
import com.med.slot.service.dto.DoctorDTO;
import com.med.slot.service.dto.DoctorScheduleDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link DoctorSchedule} and its DTO {@link DoctorScheduleDTO}.
 */
@Mapper(componentModel = "spring")
public interface DoctorScheduleMapper extends EntityMapper<DoctorScheduleDTO, DoctorSchedule> {
    @Mapping(target = "doctor", source = "doctor", qualifiedByName = "doctorId")
    DoctorScheduleDTO toDto(DoctorSchedule s);

    @Named("doctorId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    DoctorDTO toDtoDoctorId(Doctor doctor);
}

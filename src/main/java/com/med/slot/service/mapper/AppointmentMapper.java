package com.med.slot.service.mapper;

import com.med.slot.domain.Appointment;
import com.med.slot.domain.Clinic;
import com.med.slot.domain.Doctor;
import com.med.slot.domain.Patient;
import com.med.slot.service.dto.AppointmentDTO;
import com.med.slot.service.dto.ClinicDTO;
import com.med.slot.service.dto.DoctorDTO;
import com.med.slot.service.dto.PatientDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Appointment} and its DTO {@link AppointmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface AppointmentMapper extends EntityMapper<AppointmentDTO, Appointment> {
    @Mapping(target = "clinic", source = "clinic", qualifiedByName = "clinicId")
    @Mapping(target = "doctor", source = "doctor", qualifiedByName = "doctorId")
    @Mapping(target = "patient", source = "patient", qualifiedByName = "patientId")
    AppointmentDTO toDto(Appointment s);

    @Named("clinicId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ClinicDTO toDtoClinicId(Clinic clinic);

    @Named("doctorId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    DoctorDTO toDtoDoctorId(Doctor doctor);

    @Named("patientId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PatientDTO toDtoPatientId(Patient patient);
}

package com.med.slot.service.mapper;

import com.med.slot.domain.Appointment;
import com.med.slot.domain.Clinic;
import com.med.slot.domain.SmsNotification;
import com.med.slot.service.dto.AppointmentDTO;
import com.med.slot.service.dto.ClinicDTO;
import com.med.slot.service.dto.SmsNotificationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SmsNotification} and its DTO {@link SmsNotificationDTO}.
 */
@Mapper(componentModel = "spring")
public interface SmsNotificationMapper extends EntityMapper<SmsNotificationDTO, SmsNotification> {
    @Mapping(target = "clinic", source = "clinic", qualifiedByName = "clinicId")
    @Mapping(target = "appointment", source = "appointment", qualifiedByName = "appointmentId")
    SmsNotificationDTO toDto(SmsNotification s);

    @Named("clinicId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ClinicDTO toDtoClinicId(Clinic clinic);

    @Named("appointmentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AppointmentDTO toDtoAppointmentId(Appointment appointment);
}

package com.med.slot.service.dto;

import com.med.slot.domain.enumeration.SmsStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A DTO for the {@link com.med.slot.domain.SmsNotification} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SmsNotificationDTO implements Serializable {

    private Long id;

    @NotNull
    private String phone;

    @NotNull
    private String message;

    @NotNull
    private ZonedDateTime sendAt;

    private ZonedDateTime sentAt;

    @NotNull
    private SmsStatus status;

    private String errorMessage;

    private ClinicDTO clinic;

    private AppointmentDTO appointment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ZonedDateTime getSendAt() {
        return sendAt;
    }

    public void setSendAt(ZonedDateTime sendAt) {
        this.sendAt = sendAt;
    }

    public ZonedDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(ZonedDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public SmsStatus getStatus() {
        return status;
    }

    public void setStatus(SmsStatus status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ClinicDTO getClinic() {
        return clinic;
    }

    public void setClinic(ClinicDTO clinic) {
        this.clinic = clinic;
    }

    public AppointmentDTO getAppointment() {
        return appointment;
    }

    public void setAppointment(AppointmentDTO appointment) {
        this.appointment = appointment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SmsNotificationDTO)) {
            return false;
        }

        SmsNotificationDTO smsNotificationDTO = (SmsNotificationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, smsNotificationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SmsNotificationDTO{" +
            "id=" + getId() +
            ", phone='" + getPhone() + "'" +
            ", message='" + getMessage() + "'" +
            ", sendAt='" + getSendAt() + "'" +
            ", sentAt='" + getSentAt() + "'" +
            ", status='" + getStatus() + "'" +
            ", errorMessage='" + getErrorMessage() + "'" +
            ", clinic=" + getClinic() +
            ", appointment=" + getAppointment() +
            "}";
    }
}

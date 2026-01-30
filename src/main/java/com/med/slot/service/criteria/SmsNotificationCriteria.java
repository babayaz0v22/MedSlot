package com.med.slot.service.criteria;

import com.med.slot.domain.enumeration.SmsStatus;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.med.slot.domain.SmsNotification} entity. This class is used
 * in {@link com.med.slot.web.rest.SmsNotificationResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /sms-notifications?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SmsNotificationCriteria implements Serializable, Criteria {

    /**
     * Class for filtering SmsStatus
     */
    public static class SmsStatusFilter extends Filter<SmsStatus> {

        public SmsStatusFilter() {}

        public SmsStatusFilter(SmsStatusFilter filter) {
            super(filter);
        }

        @Override
        public SmsStatusFilter copy() {
            return new SmsStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter phone;

    private StringFilter message;

    private ZonedDateTimeFilter sendAt;

    private ZonedDateTimeFilter sentAt;

    private SmsStatusFilter status;

    private StringFilter errorMessage;

    private LongFilter clinicId;

    private LongFilter appointmentId;

    private Boolean distinct;

    public SmsNotificationCriteria() {}

    public SmsNotificationCriteria(SmsNotificationCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.phone = other.optionalPhone().map(StringFilter::copy).orElse(null);
        this.message = other.optionalMessage().map(StringFilter::copy).orElse(null);
        this.sendAt = other.optionalSendAt().map(ZonedDateTimeFilter::copy).orElse(null);
        this.sentAt = other.optionalSentAt().map(ZonedDateTimeFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(SmsStatusFilter::copy).orElse(null);
        this.errorMessage = other.optionalErrorMessage().map(StringFilter::copy).orElse(null);
        this.clinicId = other.optionalClinicId().map(LongFilter::copy).orElse(null);
        this.appointmentId = other.optionalAppointmentId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public SmsNotificationCriteria copy() {
        return new SmsNotificationCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getPhone() {
        return phone;
    }

    public Optional<StringFilter> optionalPhone() {
        return Optional.ofNullable(phone);
    }

    public StringFilter phone() {
        if (phone == null) {
            setPhone(new StringFilter());
        }
        return phone;
    }

    public void setPhone(StringFilter phone) {
        this.phone = phone;
    }

    public StringFilter getMessage() {
        return message;
    }

    public Optional<StringFilter> optionalMessage() {
        return Optional.ofNullable(message);
    }

    public StringFilter message() {
        if (message == null) {
            setMessage(new StringFilter());
        }
        return message;
    }

    public void setMessage(StringFilter message) {
        this.message = message;
    }

    public ZonedDateTimeFilter getSendAt() {
        return sendAt;
    }

    public Optional<ZonedDateTimeFilter> optionalSendAt() {
        return Optional.ofNullable(sendAt);
    }

    public ZonedDateTimeFilter sendAt() {
        if (sendAt == null) {
            setSendAt(new ZonedDateTimeFilter());
        }
        return sendAt;
    }

    public void setSendAt(ZonedDateTimeFilter sendAt) {
        this.sendAt = sendAt;
    }

    public ZonedDateTimeFilter getSentAt() {
        return sentAt;
    }

    public Optional<ZonedDateTimeFilter> optionalSentAt() {
        return Optional.ofNullable(sentAt);
    }

    public ZonedDateTimeFilter sentAt() {
        if (sentAt == null) {
            setSentAt(new ZonedDateTimeFilter());
        }
        return sentAt;
    }

    public void setSentAt(ZonedDateTimeFilter sentAt) {
        this.sentAt = sentAt;
    }

    public SmsStatusFilter getStatus() {
        return status;
    }

    public Optional<SmsStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public SmsStatusFilter status() {
        if (status == null) {
            setStatus(new SmsStatusFilter());
        }
        return status;
    }

    public void setStatus(SmsStatusFilter status) {
        this.status = status;
    }

    public StringFilter getErrorMessage() {
        return errorMessage;
    }

    public Optional<StringFilter> optionalErrorMessage() {
        return Optional.ofNullable(errorMessage);
    }

    public StringFilter errorMessage() {
        if (errorMessage == null) {
            setErrorMessage(new StringFilter());
        }
        return errorMessage;
    }

    public void setErrorMessage(StringFilter errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LongFilter getClinicId() {
        return clinicId;
    }

    public Optional<LongFilter> optionalClinicId() {
        return Optional.ofNullable(clinicId);
    }

    public LongFilter clinicId() {
        if (clinicId == null) {
            setClinicId(new LongFilter());
        }
        return clinicId;
    }

    public void setClinicId(LongFilter clinicId) {
        this.clinicId = clinicId;
    }

    public LongFilter getAppointmentId() {
        return appointmentId;
    }

    public Optional<LongFilter> optionalAppointmentId() {
        return Optional.ofNullable(appointmentId);
    }

    public LongFilter appointmentId() {
        if (appointmentId == null) {
            setAppointmentId(new LongFilter());
        }
        return appointmentId;
    }

    public void setAppointmentId(LongFilter appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SmsNotificationCriteria that = (SmsNotificationCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(phone, that.phone) &&
            Objects.equals(message, that.message) &&
            Objects.equals(sendAt, that.sendAt) &&
            Objects.equals(sentAt, that.sentAt) &&
            Objects.equals(status, that.status) &&
            Objects.equals(errorMessage, that.errorMessage) &&
            Objects.equals(clinicId, that.clinicId) &&
            Objects.equals(appointmentId, that.appointmentId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, phone, message, sendAt, sentAt, status, errorMessage, clinicId, appointmentId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SmsNotificationCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalPhone().map(f -> "phone=" + f + ", ").orElse("") +
            optionalMessage().map(f -> "message=" + f + ", ").orElse("") +
            optionalSendAt().map(f -> "sendAt=" + f + ", ").orElse("") +
            optionalSentAt().map(f -> "sentAt=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalErrorMessage().map(f -> "errorMessage=" + f + ", ").orElse("") +
            optionalClinicId().map(f -> "clinicId=" + f + ", ").orElse("") +
            optionalAppointmentId().map(f -> "appointmentId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}

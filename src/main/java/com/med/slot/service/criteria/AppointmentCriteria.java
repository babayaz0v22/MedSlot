package com.med.slot.service.criteria;

import com.med.slot.domain.enumeration.AppointmentStatus;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.med.slot.domain.Appointment} entity. This class is used
 * in {@link com.med.slot.web.rest.AppointmentResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /appointments?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AppointmentCriteria implements Serializable, Criteria {

    /**
     * Class for filtering AppointmentStatus
     */
    public static class AppointmentStatusFilter extends Filter<AppointmentStatus> {

        public AppointmentStatusFilter() {}

        public AppointmentStatusFilter(AppointmentStatusFilter filter) {
            super(filter);
        }

        @Override
        public AppointmentStatusFilter copy() {
            return new AppointmentStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private ZonedDateTimeFilter appointmentDateTime;

    private AppointmentStatusFilter status;

    private StringFilter note;

    private LongFilter smsNotificationId;

    private LongFilter clinicId;

    private LongFilter doctorId;

    private LongFilter patientId;

    private Boolean distinct;

    public AppointmentCriteria() {}

    public AppointmentCriteria(AppointmentCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.appointmentDateTime = other.optionalAppointmentDateTime().map(ZonedDateTimeFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(AppointmentStatusFilter::copy).orElse(null);
        this.note = other.optionalNote().map(StringFilter::copy).orElse(null);
        this.smsNotificationId = other.optionalSmsNotificationId().map(LongFilter::copy).orElse(null);
        this.clinicId = other.optionalClinicId().map(LongFilter::copy).orElse(null);
        this.doctorId = other.optionalDoctorId().map(LongFilter::copy).orElse(null);
        this.patientId = other.optionalPatientId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public AppointmentCriteria copy() {
        return new AppointmentCriteria(this);
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

    public ZonedDateTimeFilter getAppointmentDateTime() {
        return appointmentDateTime;
    }

    public Optional<ZonedDateTimeFilter> optionalAppointmentDateTime() {
        return Optional.ofNullable(appointmentDateTime);
    }

    public ZonedDateTimeFilter appointmentDateTime() {
        if (appointmentDateTime == null) {
            setAppointmentDateTime(new ZonedDateTimeFilter());
        }
        return appointmentDateTime;
    }

    public void setAppointmentDateTime(ZonedDateTimeFilter appointmentDateTime) {
        this.appointmentDateTime = appointmentDateTime;
    }

    public AppointmentStatusFilter getStatus() {
        return status;
    }

    public Optional<AppointmentStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public AppointmentStatusFilter status() {
        if (status == null) {
            setStatus(new AppointmentStatusFilter());
        }
        return status;
    }

    public void setStatus(AppointmentStatusFilter status) {
        this.status = status;
    }

    public StringFilter getNote() {
        return note;
    }

    public Optional<StringFilter> optionalNote() {
        return Optional.ofNullable(note);
    }

    public StringFilter note() {
        if (note == null) {
            setNote(new StringFilter());
        }
        return note;
    }

    public void setNote(StringFilter note) {
        this.note = note;
    }

    public LongFilter getSmsNotificationId() {
        return smsNotificationId;
    }

    public Optional<LongFilter> optionalSmsNotificationId() {
        return Optional.ofNullable(smsNotificationId);
    }

    public LongFilter smsNotificationId() {
        if (smsNotificationId == null) {
            setSmsNotificationId(new LongFilter());
        }
        return smsNotificationId;
    }

    public void setSmsNotificationId(LongFilter smsNotificationId) {
        this.smsNotificationId = smsNotificationId;
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

    public LongFilter getDoctorId() {
        return doctorId;
    }

    public Optional<LongFilter> optionalDoctorId() {
        return Optional.ofNullable(doctorId);
    }

    public LongFilter doctorId() {
        if (doctorId == null) {
            setDoctorId(new LongFilter());
        }
        return doctorId;
    }

    public void setDoctorId(LongFilter doctorId) {
        this.doctorId = doctorId;
    }

    public LongFilter getPatientId() {
        return patientId;
    }

    public Optional<LongFilter> optionalPatientId() {
        return Optional.ofNullable(patientId);
    }

    public LongFilter patientId() {
        if (patientId == null) {
            setPatientId(new LongFilter());
        }
        return patientId;
    }

    public void setPatientId(LongFilter patientId) {
        this.patientId = patientId;
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
        final AppointmentCriteria that = (AppointmentCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(appointmentDateTime, that.appointmentDateTime) &&
            Objects.equals(status, that.status) &&
            Objects.equals(note, that.note) &&
            Objects.equals(smsNotificationId, that.smsNotificationId) &&
            Objects.equals(clinicId, that.clinicId) &&
            Objects.equals(doctorId, that.doctorId) &&
            Objects.equals(patientId, that.patientId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, appointmentDateTime, status, note, smsNotificationId, clinicId, doctorId, patientId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AppointmentCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalAppointmentDateTime().map(f -> "appointmentDateTime=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalNote().map(f -> "note=" + f + ", ").orElse("") +
            optionalSmsNotificationId().map(f -> "smsNotificationId=" + f + ", ").orElse("") +
            optionalClinicId().map(f -> "clinicId=" + f + ", ").orElse("") +
            optionalDoctorId().map(f -> "doctorId=" + f + ", ").orElse("") +
            optionalPatientId().map(f -> "patientId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}

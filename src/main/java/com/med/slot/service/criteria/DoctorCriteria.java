package com.med.slot.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.med.slot.domain.Doctor} entity. This class is used
 * in {@link com.med.slot.web.rest.DoctorResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /doctors?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DoctorCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter specialty;

    private StringFilter phone;

    private LongFilter doctorScheduleId;

    private LongFilter appointmentId;

    private LongFilter clinicId;

    private Boolean distinct;

    public DoctorCriteria() {}

    public DoctorCriteria(DoctorCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.specialty = other.optionalSpecialty().map(StringFilter::copy).orElse(null);
        this.phone = other.optionalPhone().map(StringFilter::copy).orElse(null);
        this.doctorScheduleId = other.optionalDoctorScheduleId().map(LongFilter::copy).orElse(null);
        this.appointmentId = other.optionalAppointmentId().map(LongFilter::copy).orElse(null);
        this.clinicId = other.optionalClinicId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public DoctorCriteria copy() {
        return new DoctorCriteria(this);
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

    public StringFilter getSpecialty() {
        return specialty;
    }

    public Optional<StringFilter> optionalSpecialty() {
        return Optional.ofNullable(specialty);
    }

    public StringFilter specialty() {
        if (specialty == null) {
            setSpecialty(new StringFilter());
        }
        return specialty;
    }

    public void setSpecialty(StringFilter specialty) {
        this.specialty = specialty;
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

    public LongFilter getDoctorScheduleId() {
        return doctorScheduleId;
    }

    public Optional<LongFilter> optionalDoctorScheduleId() {
        return Optional.ofNullable(doctorScheduleId);
    }

    public LongFilter doctorScheduleId() {
        if (doctorScheduleId == null) {
            setDoctorScheduleId(new LongFilter());
        }
        return doctorScheduleId;
    }

    public void setDoctorScheduleId(LongFilter doctorScheduleId) {
        this.doctorScheduleId = doctorScheduleId;
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
        final DoctorCriteria that = (DoctorCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(specialty, that.specialty) &&
            Objects.equals(phone, that.phone) &&
            Objects.equals(doctorScheduleId, that.doctorScheduleId) &&
            Objects.equals(appointmentId, that.appointmentId) &&
            Objects.equals(clinicId, that.clinicId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, specialty, phone, doctorScheduleId, appointmentId, clinicId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DoctorCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalSpecialty().map(f -> "specialty=" + f + ", ").orElse("") +
            optionalPhone().map(f -> "phone=" + f + ", ").orElse("") +
            optionalDoctorScheduleId().map(f -> "doctorScheduleId=" + f + ", ").orElse("") +
            optionalAppointmentId().map(f -> "appointmentId=" + f + ", ").orElse("") +
            optionalClinicId().map(f -> "clinicId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}

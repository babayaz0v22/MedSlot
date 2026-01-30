package com.med.slot.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A DTO for the {@link com.med.slot.domain.DoctorSchedule} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DoctorScheduleDTO implements Serializable {

    private Long id;

    @NotNull
    @Min(value = 1)
    @Max(value = 7)
    private Integer dayOfWeek;

    @NotNull
    private ZonedDateTime startTime;

    @NotNull
    private ZonedDateTime endTime;

    private DoctorDTO doctor;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(ZonedDateTime startTime) {
        this.startTime = startTime;
    }

    public ZonedDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(ZonedDateTime endTime) {
        this.endTime = endTime;
    }

    public DoctorDTO getDoctor() {
        return doctor;
    }

    public void setDoctor(DoctorDTO doctor) {
        this.doctor = doctor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DoctorScheduleDTO)) {
            return false;
        }

        DoctorScheduleDTO doctorScheduleDTO = (DoctorScheduleDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, doctorScheduleDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DoctorScheduleDTO{" +
            "id=" + getId() +
            ", dayOfWeek=" + getDayOfWeek() +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            ", doctor=" + getDoctor() +
            "}";
    }
}

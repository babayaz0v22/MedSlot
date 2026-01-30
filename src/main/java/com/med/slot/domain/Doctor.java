package com.med.slot.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Doctor.
 */
@Entity
@Table(name = "main_doctor")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Doctor implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "specialty", nullable = false)
    private String specialty;

    @Column(name = "phone")
    private String phone;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "doctor")
    @JsonIgnoreProperties(value = { "doctor" }, allowSetters = true)
    private Set<DoctorSchedule> doctorSchedules = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "doctor")
    @JsonIgnoreProperties(value = { "smsNotifications", "clinic", "doctor", "patient" }, allowSetters = true)
    private Set<Appointment> appointments = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "doctors", "patients", "appointments", "smsNotifications" }, allowSetters = true)
    private Clinic clinic;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Doctor id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSpecialty() {
        return this.specialty;
    }

    public Doctor specialty(String specialty) {
        this.setSpecialty(specialty);
        return this;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getPhone() {
        return this.phone;
    }

    public Doctor phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Set<DoctorSchedule> getDoctorSchedules() {
        return this.doctorSchedules;
    }

    public void setDoctorSchedules(Set<DoctorSchedule> doctorSchedules) {
        if (this.doctorSchedules != null) {
            this.doctorSchedules.forEach(i -> i.setDoctor(null));
        }
        if (doctorSchedules != null) {
            doctorSchedules.forEach(i -> i.setDoctor(this));
        }
        this.doctorSchedules = doctorSchedules;
    }

    public Doctor doctorSchedules(Set<DoctorSchedule> doctorSchedules) {
        this.setDoctorSchedules(doctorSchedules);
        return this;
    }

    public Doctor addDoctorSchedule(DoctorSchedule doctorSchedule) {
        this.doctorSchedules.add(doctorSchedule);
        doctorSchedule.setDoctor(this);
        return this;
    }

    public Doctor removeDoctorSchedule(DoctorSchedule doctorSchedule) {
        this.doctorSchedules.remove(doctorSchedule);
        doctorSchedule.setDoctor(null);
        return this;
    }

    public Set<Appointment> getAppointments() {
        return this.appointments;
    }

    public void setAppointments(Set<Appointment> appointments) {
        if (this.appointments != null) {
            this.appointments.forEach(i -> i.setDoctor(null));
        }
        if (appointments != null) {
            appointments.forEach(i -> i.setDoctor(this));
        }
        this.appointments = appointments;
    }

    public Doctor appointments(Set<Appointment> appointments) {
        this.setAppointments(appointments);
        return this;
    }

    public Doctor addAppointment(Appointment appointment) {
        this.appointments.add(appointment);
        appointment.setDoctor(this);
        return this;
    }

    public Doctor removeAppointment(Appointment appointment) {
        this.appointments.remove(appointment);
        appointment.setDoctor(null);
        return this;
    }

    public Clinic getClinic() {
        return this.clinic;
    }

    public void setClinic(Clinic clinic) {
        this.clinic = clinic;
    }

    public Doctor clinic(Clinic clinic) {
        this.setClinic(clinic);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Doctor)) {
            return false;
        }
        return getId() != null && getId().equals(((Doctor) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Doctor{" +
            "id=" + getId() +
            ", specialty='" + getSpecialty() + "'" +
            ", phone='" + getPhone() + "'" +
            "}";
    }
}

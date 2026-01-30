package com.med.slot.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * A Clinic.
 */
@Entity
@Table(name = "main_clinic")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Clinic implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address")
    private String address;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active;

    @NotNull
    @Size(max = 50)
    @Column(name = "created_by", length = 50, nullable = false)
    private String createdBy;

    @Column(name = "created_date")
    private ZonedDateTime createdDate;

    @Size(max = 50)
    @Column(name = "last_modified_by", length = 50)
    private String lastModifiedBy;

    @Column(name = "last_modified_date")
    private ZonedDateTime lastModifiedDate;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "clinic")
    @JsonIgnoreProperties(value = { "doctorSchedules", "appointments", "clinic" }, allowSetters = true)
    private Set<Doctor> doctors = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "clinic")
    @JsonIgnoreProperties(value = { "appointments", "clinic" }, allowSetters = true)
    private Set<Patient> patients = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "clinic")
    @JsonIgnoreProperties(value = { "smsNotifications", "clinic", "doctor", "patient" }, allowSetters = true)
    private Set<Appointment> appointments = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "clinic")
    @JsonIgnoreProperties(value = { "clinic", "appointment" }, allowSetters = true)
    private Set<SmsNotification> smsNotifications = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Clinic id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Clinic name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return this.phone;
    }

    public Clinic phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return this.address;
    }

    public Clinic address(String address) {
        this.setAddress(address);
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getActive() {
        return this.active;
    }

    public Clinic active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public Clinic createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public ZonedDateTime getCreatedDate() {
        return this.createdDate;
    }

    public Clinic createdDate(ZonedDateTime createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    public void setCreatedDate(ZonedDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return this.lastModifiedBy;
    }

    public Clinic lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public ZonedDateTime getLastModifiedDate() {
        return this.lastModifiedDate;
    }

    public Clinic lastModifiedDate(ZonedDateTime lastModifiedDate) {
        this.setLastModifiedDate(lastModifiedDate);
        return this;
    }

    public void setLastModifiedDate(ZonedDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Set<Doctor> getDoctors() {
        return this.doctors;
    }

    public void setDoctors(Set<Doctor> doctors) {
        if (this.doctors != null) {
            this.doctors.forEach(i -> i.setClinic(null));
        }
        if (doctors != null) {
            doctors.forEach(i -> i.setClinic(this));
        }
        this.doctors = doctors;
    }

    public Clinic doctors(Set<Doctor> doctors) {
        this.setDoctors(doctors);
        return this;
    }

    public Clinic addDoctor(Doctor doctor) {
        this.doctors.add(doctor);
        doctor.setClinic(this);
        return this;
    }

    public Clinic removeDoctor(Doctor doctor) {
        this.doctors.remove(doctor);
        doctor.setClinic(null);
        return this;
    }

    public Set<Patient> getPatients() {
        return this.patients;
    }

    public void setPatients(Set<Patient> patients) {
        if (this.patients != null) {
            this.patients.forEach(i -> i.setClinic(null));
        }
        if (patients != null) {
            patients.forEach(i -> i.setClinic(this));
        }
        this.patients = patients;
    }

    public Clinic patients(Set<Patient> patients) {
        this.setPatients(patients);
        return this;
    }

    public Clinic addPatient(Patient patient) {
        this.patients.add(patient);
        patient.setClinic(this);
        return this;
    }

    public Clinic removePatient(Patient patient) {
        this.patients.remove(patient);
        patient.setClinic(null);
        return this;
    }

    public Set<Appointment> getAppointments() {
        return this.appointments;
    }

    public void setAppointments(Set<Appointment> appointments) {
        if (this.appointments != null) {
            this.appointments.forEach(i -> i.setClinic(null));
        }
        if (appointments != null) {
            appointments.forEach(i -> i.setClinic(this));
        }
        this.appointments = appointments;
    }

    public Clinic appointments(Set<Appointment> appointments) {
        this.setAppointments(appointments);
        return this;
    }

    public Clinic addAppointment(Appointment appointment) {
        this.appointments.add(appointment);
        appointment.setClinic(this);
        return this;
    }

    public Clinic removeAppointment(Appointment appointment) {
        this.appointments.remove(appointment);
        appointment.setClinic(null);
        return this;
    }

    public Set<SmsNotification> getSmsNotifications() {
        return this.smsNotifications;
    }

    public void setSmsNotifications(Set<SmsNotification> smsNotifications) {
        if (this.smsNotifications != null) {
            this.smsNotifications.forEach(i -> i.setClinic(null));
        }
        if (smsNotifications != null) {
            smsNotifications.forEach(i -> i.setClinic(this));
        }
        this.smsNotifications = smsNotifications;
    }

    public Clinic smsNotifications(Set<SmsNotification> smsNotifications) {
        this.setSmsNotifications(smsNotifications);
        return this;
    }

    public Clinic addSmsNotification(SmsNotification smsNotification) {
        this.smsNotifications.add(smsNotification);
        smsNotification.setClinic(this);
        return this;
    }

    public Clinic removeSmsNotification(SmsNotification smsNotification) {
        this.smsNotifications.remove(smsNotification);
        smsNotification.setClinic(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Clinic)) {
            return false;
        }
        return getId() != null && getId().equals(((Clinic) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Clinic{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", phone='" + getPhone() + "'" +
            ", address='" + getAddress() + "'" +
            ", active='" + getActive() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }
}

package com.med.slot.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.med.slot.domain.enumeration.AppointmentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * A Appointment.
 */
@Entity
@Table(name = "main_appointment")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Appointment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "appointment_date_time", nullable = false)
    private ZonedDateTime appointmentDateTime;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AppointmentStatus status;

    @Column(name = "note")
    private String note;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "appointment")
    @JsonIgnoreProperties(value = { "clinic", "appointment" }, allowSetters = true)
    private Set<SmsNotification> smsNotifications = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "doctors", "patients", "appointments", "smsNotifications" }, allowSetters = true)
    private Clinic clinic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "doctorSchedules", "appointments", "clinic" }, allowSetters = true)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "appointments", "clinic" }, allowSetters = true)
    private Patient patient;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Appointment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getAppointmentDateTime() {
        return this.appointmentDateTime;
    }

    public Appointment appointmentDateTime(ZonedDateTime appointmentDateTime) {
        this.setAppointmentDateTime(appointmentDateTime);
        return this;
    }

    public void setAppointmentDateTime(ZonedDateTime appointmentDateTime) {
        this.appointmentDateTime = appointmentDateTime;
    }

    public AppointmentStatus getStatus() {
        return this.status;
    }

    public Appointment status(AppointmentStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public String getNote() {
        return this.note;
    }

    public Appointment note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Set<SmsNotification> getSmsNotifications() {
        return this.smsNotifications;
    }

    public void setSmsNotifications(Set<SmsNotification> smsNotifications) {
        if (this.smsNotifications != null) {
            this.smsNotifications.forEach(i -> i.setAppointment(null));
        }
        if (smsNotifications != null) {
            smsNotifications.forEach(i -> i.setAppointment(this));
        }
        this.smsNotifications = smsNotifications;
    }

    public Appointment smsNotifications(Set<SmsNotification> smsNotifications) {
        this.setSmsNotifications(smsNotifications);
        return this;
    }

    public Appointment addSmsNotification(SmsNotification smsNotification) {
        this.smsNotifications.add(smsNotification);
        smsNotification.setAppointment(this);
        return this;
    }

    public Appointment removeSmsNotification(SmsNotification smsNotification) {
        this.smsNotifications.remove(smsNotification);
        smsNotification.setAppointment(null);
        return this;
    }

    public Clinic getClinic() {
        return this.clinic;
    }

    public void setClinic(Clinic clinic) {
        this.clinic = clinic;
    }

    public Appointment clinic(Clinic clinic) {
        this.setClinic(clinic);
        return this;
    }

    public Doctor getDoctor() {
        return this.doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Appointment doctor(Doctor doctor) {
        this.setDoctor(doctor);
        return this;
    }

    public Patient getPatient() {
        return this.patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Appointment patient(Patient patient) {
        this.setPatient(patient);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Appointment)) {
            return false;
        }
        return getId() != null && getId().equals(((Appointment) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Appointment{" +
            "id=" + getId() +
            ", appointmentDateTime='" + getAppointmentDateTime() + "'" +
            ", status='" + getStatus() + "'" +
            ", note='" + getNote() + "'" +
            "}";
    }
}

package com.med.slot.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.med.slot.domain.enumeration.SmsStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * A SmsNotification.
 */
@Entity
@Table(name = "main_sms_notification")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SmsNotification implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "phone", nullable = false)
    private String phone;

    @NotNull
    @Column(name = "message", nullable = false)
    private String message;

    @NotNull
    @Column(name = "send_at", nullable = false)
    private ZonedDateTime sendAt;

    @Column(name = "sent_at")
    private ZonedDateTime sentAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SmsStatus status;

    @Column(name = "error_message")
    private String errorMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "doctors", "patients", "appointments", "smsNotifications" }, allowSetters = true)
    private Clinic clinic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "smsNotifications", "clinic", "doctor", "patient" }, allowSetters = true)
    private Appointment appointment;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SmsNotification id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhone() {
        return this.phone;
    }

    public SmsNotification phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMessage() {
        return this.message;
    }

    public SmsNotification message(String message) {
        this.setMessage(message);
        return this;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ZonedDateTime getSendAt() {
        return this.sendAt;
    }

    public SmsNotification sendAt(ZonedDateTime sendAt) {
        this.setSendAt(sendAt);
        return this;
    }

    public void setSendAt(ZonedDateTime sendAt) {
        this.sendAt = sendAt;
    }

    public ZonedDateTime getSentAt() {
        return this.sentAt;
    }

    public SmsNotification sentAt(ZonedDateTime sentAt) {
        this.setSentAt(sentAt);
        return this;
    }

    public void setSentAt(ZonedDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public SmsStatus getStatus() {
        return this.status;
    }

    public SmsNotification status(SmsStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(SmsStatus status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public SmsNotification errorMessage(String errorMessage) {
        this.setErrorMessage(errorMessage);
        return this;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Clinic getClinic() {
        return this.clinic;
    }

    public void setClinic(Clinic clinic) {
        this.clinic = clinic;
    }

    public SmsNotification clinic(Clinic clinic) {
        this.setClinic(clinic);
        return this;
    }

    public Appointment getAppointment() {
        return this.appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public SmsNotification appointment(Appointment appointment) {
        this.setAppointment(appointment);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SmsNotification)) {
            return false;
        }
        return getId() != null && getId().equals(((SmsNotification) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SmsNotification{" +
            "id=" + getId() +
            ", phone='" + getPhone() + "'" +
            ", message='" + getMessage() + "'" +
            ", sendAt='" + getSendAt() + "'" +
            ", sentAt='" + getSentAt() + "'" +
            ", status='" + getStatus() + "'" +
            ", errorMessage='" + getErrorMessage() + "'" +
            "}";
    }
}

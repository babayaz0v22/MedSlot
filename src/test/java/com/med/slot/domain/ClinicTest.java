package com.med.slot.domain;

import static com.med.slot.domain.AppointmentTestSamples.*;
import static com.med.slot.domain.ClinicTestSamples.*;
import static com.med.slot.domain.DoctorTestSamples.*;
import static com.med.slot.domain.PatientTestSamples.*;
import static com.med.slot.domain.SmsNotificationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.med.slot.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ClinicTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Clinic.class);
        Clinic clinic1 = getClinicSample1();
        Clinic clinic2 = new Clinic();
        assertThat(clinic1).isNotEqualTo(clinic2);

        clinic2.setId(clinic1.getId());
        assertThat(clinic1).isEqualTo(clinic2);

        clinic2 = getClinicSample2();
        assertThat(clinic1).isNotEqualTo(clinic2);
    }

    @Test
    void doctorTest() {
        Clinic clinic = getClinicRandomSampleGenerator();
        Doctor doctorBack = getDoctorRandomSampleGenerator();

        clinic.addDoctor(doctorBack);
        assertThat(clinic.getDoctors()).containsOnly(doctorBack);
        assertThat(doctorBack.getClinic()).isEqualTo(clinic);

        clinic.removeDoctor(doctorBack);
        assertThat(clinic.getDoctors()).doesNotContain(doctorBack);
        assertThat(doctorBack.getClinic()).isNull();

        clinic.doctors(new HashSet<>(Set.of(doctorBack)));
        assertThat(clinic.getDoctors()).containsOnly(doctorBack);
        assertThat(doctorBack.getClinic()).isEqualTo(clinic);

        clinic.setDoctors(new HashSet<>());
        assertThat(clinic.getDoctors()).doesNotContain(doctorBack);
        assertThat(doctorBack.getClinic()).isNull();
    }

    @Test
    void patientTest() {
        Clinic clinic = getClinicRandomSampleGenerator();
        Patient patientBack = getPatientRandomSampleGenerator();

        clinic.addPatient(patientBack);
        assertThat(clinic.getPatients()).containsOnly(patientBack);
        assertThat(patientBack.getClinic()).isEqualTo(clinic);

        clinic.removePatient(patientBack);
        assertThat(clinic.getPatients()).doesNotContain(patientBack);
        assertThat(patientBack.getClinic()).isNull();

        clinic.patients(new HashSet<>(Set.of(patientBack)));
        assertThat(clinic.getPatients()).containsOnly(patientBack);
        assertThat(patientBack.getClinic()).isEqualTo(clinic);

        clinic.setPatients(new HashSet<>());
        assertThat(clinic.getPatients()).doesNotContain(patientBack);
        assertThat(patientBack.getClinic()).isNull();
    }

    @Test
    void appointmentTest() {
        Clinic clinic = getClinicRandomSampleGenerator();
        Appointment appointmentBack = getAppointmentRandomSampleGenerator();

        clinic.addAppointment(appointmentBack);
        assertThat(clinic.getAppointments()).containsOnly(appointmentBack);
        assertThat(appointmentBack.getClinic()).isEqualTo(clinic);

        clinic.removeAppointment(appointmentBack);
        assertThat(clinic.getAppointments()).doesNotContain(appointmentBack);
        assertThat(appointmentBack.getClinic()).isNull();

        clinic.appointments(new HashSet<>(Set.of(appointmentBack)));
        assertThat(clinic.getAppointments()).containsOnly(appointmentBack);
        assertThat(appointmentBack.getClinic()).isEqualTo(clinic);

        clinic.setAppointments(new HashSet<>());
        assertThat(clinic.getAppointments()).doesNotContain(appointmentBack);
        assertThat(appointmentBack.getClinic()).isNull();
    }

    @Test
    void smsNotificationTest() {
        Clinic clinic = getClinicRandomSampleGenerator();
        SmsNotification smsNotificationBack = getSmsNotificationRandomSampleGenerator();

        clinic.addSmsNotification(smsNotificationBack);
        assertThat(clinic.getSmsNotifications()).containsOnly(smsNotificationBack);
        assertThat(smsNotificationBack.getClinic()).isEqualTo(clinic);

        clinic.removeSmsNotification(smsNotificationBack);
        assertThat(clinic.getSmsNotifications()).doesNotContain(smsNotificationBack);
        assertThat(smsNotificationBack.getClinic()).isNull();

        clinic.smsNotifications(new HashSet<>(Set.of(smsNotificationBack)));
        assertThat(clinic.getSmsNotifications()).containsOnly(smsNotificationBack);
        assertThat(smsNotificationBack.getClinic()).isEqualTo(clinic);

        clinic.setSmsNotifications(new HashSet<>());
        assertThat(clinic.getSmsNotifications()).doesNotContain(smsNotificationBack);
        assertThat(smsNotificationBack.getClinic()).isNull();
    }
}

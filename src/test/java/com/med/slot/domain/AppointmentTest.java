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

class AppointmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Appointment.class);
        Appointment appointment1 = getAppointmentSample1();
        Appointment appointment2 = new Appointment();
        assertThat(appointment1).isNotEqualTo(appointment2);

        appointment2.setId(appointment1.getId());
        assertThat(appointment1).isEqualTo(appointment2);

        appointment2 = getAppointmentSample2();
        assertThat(appointment1).isNotEqualTo(appointment2);
    }

    @Test
    void smsNotificationTest() {
        Appointment appointment = getAppointmentRandomSampleGenerator();
        SmsNotification smsNotificationBack = getSmsNotificationRandomSampleGenerator();

        appointment.addSmsNotification(smsNotificationBack);
        assertThat(appointment.getSmsNotifications()).containsOnly(smsNotificationBack);
        assertThat(smsNotificationBack.getAppointment()).isEqualTo(appointment);

        appointment.removeSmsNotification(smsNotificationBack);
        assertThat(appointment.getSmsNotifications()).doesNotContain(smsNotificationBack);
        assertThat(smsNotificationBack.getAppointment()).isNull();

        appointment.smsNotifications(new HashSet<>(Set.of(smsNotificationBack)));
        assertThat(appointment.getSmsNotifications()).containsOnly(smsNotificationBack);
        assertThat(smsNotificationBack.getAppointment()).isEqualTo(appointment);

        appointment.setSmsNotifications(new HashSet<>());
        assertThat(appointment.getSmsNotifications()).doesNotContain(smsNotificationBack);
        assertThat(smsNotificationBack.getAppointment()).isNull();
    }

    @Test
    void clinicTest() {
        Appointment appointment = getAppointmentRandomSampleGenerator();
        Clinic clinicBack = getClinicRandomSampleGenerator();

        appointment.setClinic(clinicBack);
        assertThat(appointment.getClinic()).isEqualTo(clinicBack);

        appointment.clinic(null);
        assertThat(appointment.getClinic()).isNull();
    }

    @Test
    void doctorTest() {
        Appointment appointment = getAppointmentRandomSampleGenerator();
        Doctor doctorBack = getDoctorRandomSampleGenerator();

        appointment.setDoctor(doctorBack);
        assertThat(appointment.getDoctor()).isEqualTo(doctorBack);

        appointment.doctor(null);
        assertThat(appointment.getDoctor()).isNull();
    }

    @Test
    void patientTest() {
        Appointment appointment = getAppointmentRandomSampleGenerator();
        Patient patientBack = getPatientRandomSampleGenerator();

        appointment.setPatient(patientBack);
        assertThat(appointment.getPatient()).isEqualTo(patientBack);

        appointment.patient(null);
        assertThat(appointment.getPatient()).isNull();
    }
}

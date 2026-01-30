package com.med.slot.domain;

import static com.med.slot.domain.AppointmentTestSamples.*;
import static com.med.slot.domain.ClinicTestSamples.*;
import static com.med.slot.domain.PatientTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.med.slot.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PatientTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Patient.class);
        Patient patient1 = getPatientSample1();
        Patient patient2 = new Patient();
        assertThat(patient1).isNotEqualTo(patient2);

        patient2.setId(patient1.getId());
        assertThat(patient1).isEqualTo(patient2);

        patient2 = getPatientSample2();
        assertThat(patient1).isNotEqualTo(patient2);
    }

    @Test
    void appointmentTest() {
        Patient patient = getPatientRandomSampleGenerator();
        Appointment appointmentBack = getAppointmentRandomSampleGenerator();

        patient.addAppointment(appointmentBack);
        assertThat(patient.getAppointments()).containsOnly(appointmentBack);
        assertThat(appointmentBack.getPatient()).isEqualTo(patient);

        patient.removeAppointment(appointmentBack);
        assertThat(patient.getAppointments()).doesNotContain(appointmentBack);
        assertThat(appointmentBack.getPatient()).isNull();

        patient.appointments(new HashSet<>(Set.of(appointmentBack)));
        assertThat(patient.getAppointments()).containsOnly(appointmentBack);
        assertThat(appointmentBack.getPatient()).isEqualTo(patient);

        patient.setAppointments(new HashSet<>());
        assertThat(patient.getAppointments()).doesNotContain(appointmentBack);
        assertThat(appointmentBack.getPatient()).isNull();
    }

    @Test
    void clinicTest() {
        Patient patient = getPatientRandomSampleGenerator();
        Clinic clinicBack = getClinicRandomSampleGenerator();

        patient.setClinic(clinicBack);
        assertThat(patient.getClinic()).isEqualTo(clinicBack);

        patient.clinic(null);
        assertThat(patient.getClinic()).isNull();
    }
}

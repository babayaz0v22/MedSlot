package com.med.slot.domain;

import static com.med.slot.domain.AppointmentTestSamples.*;
import static com.med.slot.domain.ClinicTestSamples.*;
import static com.med.slot.domain.DoctorScheduleTestSamples.*;
import static com.med.slot.domain.DoctorTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.med.slot.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DoctorTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Doctor.class);
        Doctor doctor1 = getDoctorSample1();
        Doctor doctor2 = new Doctor();
        assertThat(doctor1).isNotEqualTo(doctor2);

        doctor2.setId(doctor1.getId());
        assertThat(doctor1).isEqualTo(doctor2);

        doctor2 = getDoctorSample2();
        assertThat(doctor1).isNotEqualTo(doctor2);
    }

    @Test
    void doctorScheduleTest() {
        Doctor doctor = getDoctorRandomSampleGenerator();
        DoctorSchedule doctorScheduleBack = getDoctorScheduleRandomSampleGenerator();

        doctor.addDoctorSchedule(doctorScheduleBack);
        assertThat(doctor.getDoctorSchedules()).containsOnly(doctorScheduleBack);
        assertThat(doctorScheduleBack.getDoctor()).isEqualTo(doctor);

        doctor.removeDoctorSchedule(doctorScheduleBack);
        assertThat(doctor.getDoctorSchedules()).doesNotContain(doctorScheduleBack);
        assertThat(doctorScheduleBack.getDoctor()).isNull();

        doctor.doctorSchedules(new HashSet<>(Set.of(doctorScheduleBack)));
        assertThat(doctor.getDoctorSchedules()).containsOnly(doctorScheduleBack);
        assertThat(doctorScheduleBack.getDoctor()).isEqualTo(doctor);

        doctor.setDoctorSchedules(new HashSet<>());
        assertThat(doctor.getDoctorSchedules()).doesNotContain(doctorScheduleBack);
        assertThat(doctorScheduleBack.getDoctor()).isNull();
    }

    @Test
    void appointmentTest() {
        Doctor doctor = getDoctorRandomSampleGenerator();
        Appointment appointmentBack = getAppointmentRandomSampleGenerator();

        doctor.addAppointment(appointmentBack);
        assertThat(doctor.getAppointments()).containsOnly(appointmentBack);
        assertThat(appointmentBack.getDoctor()).isEqualTo(doctor);

        doctor.removeAppointment(appointmentBack);
        assertThat(doctor.getAppointments()).doesNotContain(appointmentBack);
        assertThat(appointmentBack.getDoctor()).isNull();

        doctor.appointments(new HashSet<>(Set.of(appointmentBack)));
        assertThat(doctor.getAppointments()).containsOnly(appointmentBack);
        assertThat(appointmentBack.getDoctor()).isEqualTo(doctor);

        doctor.setAppointments(new HashSet<>());
        assertThat(doctor.getAppointments()).doesNotContain(appointmentBack);
        assertThat(appointmentBack.getDoctor()).isNull();
    }

    @Test
    void clinicTest() {
        Doctor doctor = getDoctorRandomSampleGenerator();
        Clinic clinicBack = getClinicRandomSampleGenerator();

        doctor.setClinic(clinicBack);
        assertThat(doctor.getClinic()).isEqualTo(clinicBack);

        doctor.clinic(null);
        assertThat(doctor.getClinic()).isNull();
    }
}

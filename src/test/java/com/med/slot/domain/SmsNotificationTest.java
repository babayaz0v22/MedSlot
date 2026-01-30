package com.med.slot.domain;

import static com.med.slot.domain.AppointmentTestSamples.*;
import static com.med.slot.domain.ClinicTestSamples.*;
import static com.med.slot.domain.SmsNotificationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.med.slot.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SmsNotificationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SmsNotification.class);
        SmsNotification smsNotification1 = getSmsNotificationSample1();
        SmsNotification smsNotification2 = new SmsNotification();
        assertThat(smsNotification1).isNotEqualTo(smsNotification2);

        smsNotification2.setId(smsNotification1.getId());
        assertThat(smsNotification1).isEqualTo(smsNotification2);

        smsNotification2 = getSmsNotificationSample2();
        assertThat(smsNotification1).isNotEqualTo(smsNotification2);
    }

    @Test
    void clinicTest() {
        SmsNotification smsNotification = getSmsNotificationRandomSampleGenerator();
        Clinic clinicBack = getClinicRandomSampleGenerator();

        smsNotification.setClinic(clinicBack);
        assertThat(smsNotification.getClinic()).isEqualTo(clinicBack);

        smsNotification.clinic(null);
        assertThat(smsNotification.getClinic()).isNull();
    }

    @Test
    void appointmentTest() {
        SmsNotification smsNotification = getSmsNotificationRandomSampleGenerator();
        Appointment appointmentBack = getAppointmentRandomSampleGenerator();

        smsNotification.setAppointment(appointmentBack);
        assertThat(smsNotification.getAppointment()).isEqualTo(appointmentBack);

        smsNotification.appointment(null);
        assertThat(smsNotification.getAppointment()).isNull();
    }
}

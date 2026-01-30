package com.med.slot.domain;

import static com.med.slot.domain.DoctorScheduleTestSamples.*;
import static com.med.slot.domain.DoctorTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.med.slot.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DoctorScheduleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DoctorSchedule.class);
        DoctorSchedule doctorSchedule1 = getDoctorScheduleSample1();
        DoctorSchedule doctorSchedule2 = new DoctorSchedule();
        assertThat(doctorSchedule1).isNotEqualTo(doctorSchedule2);

        doctorSchedule2.setId(doctorSchedule1.getId());
        assertThat(doctorSchedule1).isEqualTo(doctorSchedule2);

        doctorSchedule2 = getDoctorScheduleSample2();
        assertThat(doctorSchedule1).isNotEqualTo(doctorSchedule2);
    }

    @Test
    void doctorTest() {
        DoctorSchedule doctorSchedule = getDoctorScheduleRandomSampleGenerator();
        Doctor doctorBack = getDoctorRandomSampleGenerator();

        doctorSchedule.setDoctor(doctorBack);
        assertThat(doctorSchedule.getDoctor()).isEqualTo(doctorBack);

        doctorSchedule.doctor(null);
        assertThat(doctorSchedule.getDoctor()).isNull();
    }
}

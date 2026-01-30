package com.med.slot.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.med.slot.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DoctorScheduleDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DoctorScheduleDTO.class);
        DoctorScheduleDTO doctorScheduleDTO1 = new DoctorScheduleDTO();
        doctorScheduleDTO1.setId(1L);
        DoctorScheduleDTO doctorScheduleDTO2 = new DoctorScheduleDTO();
        assertThat(doctorScheduleDTO1).isNotEqualTo(doctorScheduleDTO2);
        doctorScheduleDTO2.setId(doctorScheduleDTO1.getId());
        assertThat(doctorScheduleDTO1).isEqualTo(doctorScheduleDTO2);
        doctorScheduleDTO2.setId(2L);
        assertThat(doctorScheduleDTO1).isNotEqualTo(doctorScheduleDTO2);
        doctorScheduleDTO1.setId(null);
        assertThat(doctorScheduleDTO1).isNotEqualTo(doctorScheduleDTO2);
    }
}

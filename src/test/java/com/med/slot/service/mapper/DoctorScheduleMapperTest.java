package com.med.slot.service.mapper;

import static com.med.slot.domain.DoctorScheduleAsserts.*;
import static com.med.slot.domain.DoctorScheduleTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DoctorScheduleMapperTest {

    private DoctorScheduleMapper doctorScheduleMapper;

    @BeforeEach
    void setUp() {
        doctorScheduleMapper = new DoctorScheduleMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getDoctorScheduleSample1();
        var actual = doctorScheduleMapper.toEntity(doctorScheduleMapper.toDto(expected));
        assertDoctorScheduleAllPropertiesEquals(expected, actual);
    }
}

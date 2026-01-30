package com.med.slot.service.mapper;

import static com.med.slot.domain.ClinicAsserts.*;
import static com.med.slot.domain.ClinicTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClinicMapperTest {

    private ClinicMapper clinicMapper;

    @BeforeEach
    void setUp() {
        clinicMapper = new ClinicMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getClinicSample1();
        var actual = clinicMapper.toEntity(clinicMapper.toDto(expected));
        assertClinicAllPropertiesEquals(expected, actual);
    }
}

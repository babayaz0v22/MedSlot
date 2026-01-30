package com.med.slot.service.mapper;

import static com.med.slot.domain.SmsNotificationAsserts.*;
import static com.med.slot.domain.SmsNotificationTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SmsNotificationMapperTest {

    private SmsNotificationMapper smsNotificationMapper;

    @BeforeEach
    void setUp() {
        smsNotificationMapper = new SmsNotificationMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSmsNotificationSample1();
        var actual = smsNotificationMapper.toEntity(smsNotificationMapper.toDto(expected));
        assertSmsNotificationAllPropertiesEquals(expected, actual);
    }
}

package com.med.slot.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.med.slot.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SmsNotificationDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SmsNotificationDTO.class);
        SmsNotificationDTO smsNotificationDTO1 = new SmsNotificationDTO();
        smsNotificationDTO1.setId(1L);
        SmsNotificationDTO smsNotificationDTO2 = new SmsNotificationDTO();
        assertThat(smsNotificationDTO1).isNotEqualTo(smsNotificationDTO2);
        smsNotificationDTO2.setId(smsNotificationDTO1.getId());
        assertThat(smsNotificationDTO1).isEqualTo(smsNotificationDTO2);
        smsNotificationDTO2.setId(2L);
        assertThat(smsNotificationDTO1).isNotEqualTo(smsNotificationDTO2);
        smsNotificationDTO1.setId(null);
        assertThat(smsNotificationDTO1).isNotEqualTo(smsNotificationDTO2);
    }
}

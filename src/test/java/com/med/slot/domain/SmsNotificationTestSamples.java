package com.med.slot.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SmsNotificationTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static SmsNotification getSmsNotificationSample1() {
        return new SmsNotification().id(1L).phone("phone1").message("message1").errorMessage("errorMessage1");
    }

    public static SmsNotification getSmsNotificationSample2() {
        return new SmsNotification().id(2L).phone("phone2").message("message2").errorMessage("errorMessage2");
    }

    public static SmsNotification getSmsNotificationRandomSampleGenerator() {
        return new SmsNotification()
            .id(longCount.incrementAndGet())
            .phone(UUID.randomUUID().toString())
            .message(UUID.randomUUID().toString())
            .errorMessage(UUID.randomUUID().toString());
    }
}

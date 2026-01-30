package com.med.slot.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PatientTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Patient getPatientSample1() {
        return new Patient().id(1L).fullName("fullName1").phone("phone1");
    }

    public static Patient getPatientSample2() {
        return new Patient().id(2L).fullName("fullName2").phone("phone2");
    }

    public static Patient getPatientRandomSampleGenerator() {
        return new Patient().id(longCount.incrementAndGet()).fullName(UUID.randomUUID().toString()).phone(UUID.randomUUID().toString());
    }
}

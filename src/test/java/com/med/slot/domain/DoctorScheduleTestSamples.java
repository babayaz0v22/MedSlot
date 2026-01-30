package com.med.slot.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class DoctorScheduleTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static DoctorSchedule getDoctorScheduleSample1() {
        return new DoctorSchedule().id(1L).dayOfWeek(1);
    }

    public static DoctorSchedule getDoctorScheduleSample2() {
        return new DoctorSchedule().id(2L).dayOfWeek(2);
    }

    public static DoctorSchedule getDoctorScheduleRandomSampleGenerator() {
        return new DoctorSchedule().id(longCount.incrementAndGet()).dayOfWeek(intCount.incrementAndGet());
    }
}

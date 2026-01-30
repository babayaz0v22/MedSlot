package com.med.slot.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class SmsNotificationCriteriaTest {

    @Test
    void newSmsNotificationCriteriaHasAllFiltersNullTest() {
        var smsNotificationCriteria = new SmsNotificationCriteria();
        assertThat(smsNotificationCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void smsNotificationCriteriaFluentMethodsCreatesFiltersTest() {
        var smsNotificationCriteria = new SmsNotificationCriteria();

        setAllFilters(smsNotificationCriteria);

        assertThat(smsNotificationCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void smsNotificationCriteriaCopyCreatesNullFilterTest() {
        var smsNotificationCriteria = new SmsNotificationCriteria();
        var copy = smsNotificationCriteria.copy();

        assertThat(smsNotificationCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(smsNotificationCriteria)
        );
    }

    @Test
    void smsNotificationCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var smsNotificationCriteria = new SmsNotificationCriteria();
        setAllFilters(smsNotificationCriteria);

        var copy = smsNotificationCriteria.copy();

        assertThat(smsNotificationCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(smsNotificationCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var smsNotificationCriteria = new SmsNotificationCriteria();

        assertThat(smsNotificationCriteria).hasToString("SmsNotificationCriteria{}");
    }

    private static void setAllFilters(SmsNotificationCriteria smsNotificationCriteria) {
        smsNotificationCriteria.id();
        smsNotificationCriteria.phone();
        smsNotificationCriteria.message();
        smsNotificationCriteria.sendAt();
        smsNotificationCriteria.sentAt();
        smsNotificationCriteria.status();
        smsNotificationCriteria.errorMessage();
        smsNotificationCriteria.clinicId();
        smsNotificationCriteria.appointmentId();
        smsNotificationCriteria.distinct();
    }

    private static Condition<SmsNotificationCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getPhone()) &&
                condition.apply(criteria.getMessage()) &&
                condition.apply(criteria.getSendAt()) &&
                condition.apply(criteria.getSentAt()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getErrorMessage()) &&
                condition.apply(criteria.getClinicId()) &&
                condition.apply(criteria.getAppointmentId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<SmsNotificationCriteria> copyFiltersAre(
        SmsNotificationCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getPhone(), copy.getPhone()) &&
                condition.apply(criteria.getMessage(), copy.getMessage()) &&
                condition.apply(criteria.getSendAt(), copy.getSendAt()) &&
                condition.apply(criteria.getSentAt(), copy.getSentAt()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getErrorMessage(), copy.getErrorMessage()) &&
                condition.apply(criteria.getClinicId(), copy.getClinicId()) &&
                condition.apply(criteria.getAppointmentId(), copy.getAppointmentId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}

package org.hisp.dhis.rules.models;

import com.google.auto.value.AutoValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Nonnull;

@AutoValue
public abstract class RuleEnrollment {

    @Nonnull
    public abstract String enrollment();

    @Nonnull
    public abstract Date incidentDate();

    @Nonnull
    public abstract Date enrollmentDate();

    @Nonnull
    public abstract Status status();

    @Nonnull
    public abstract List<RuleAttributeValue> attributeValues();

    public enum Status {
        ACTIVE, COMPLETED, CANCELLED
    }

    @Nonnull
    public static RuleEnrollment create(@Nonnull String enrollment, @Nonnull Date incidentDate,
            @Nonnull Date enrollmentDate, @Nonnull Status status,
            @Nonnull List<RuleAttributeValue> attributeValues) {
        return new AutoValue_RuleEnrollment(enrollment, incidentDate, enrollmentDate, status,
                Collections.unmodifiableList(new ArrayList<>(attributeValues)));
    }
}

package org.hisp.dhis.rules.models;

import com.google.auto.value.AutoValue;

import java.util.Date;

import javax.annotation.Nonnull;

@AutoValue
public abstract class RuleDataValue {

    @Nonnull
    public abstract Date eventDate();

    @Nonnull
    public abstract String programStage();

    @Nonnull
    public abstract String dataElement();

    @Nonnull
    public abstract String value();

    public static RuleDataValue create(@Nonnull Date eventDate, @Nonnull String programStage,
            @Nonnull String dataelement, @Nonnull String value) {
        return new AutoValue_RuleDataValue(eventDate, programStage, dataelement, value);
    }
}

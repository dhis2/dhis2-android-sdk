package org.hisp.dhis.android.rules;

import com.google.auto.value.AutoValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Nonnull;

@AutoValue
public abstract class Event {

    @Nonnull
    public abstract String event();

    @Nonnull
    public abstract EventStatus status();

    @Nonnull
    public abstract String programStage();

    @Nonnull
    public abstract Date eventDate();

    @Nonnull
    public abstract Date dueDate();

    @Nonnull
    public abstract List<DataValue> dataValues();

    @Nonnull
    public static Event create(
            @Nonnull String event,
            @Nonnull EventStatus status,
            @Nonnull String programStage,
            @Nonnull Date eventDate,
            @Nonnull Date dueDate,
            @Nonnull List<DataValue> dataValues) {
        return new AutoValue_Event(event, status, programStage, eventDate, dueDate,
                Collections.unmodifiableList(new ArrayList<>(dataValues)));
    }
}

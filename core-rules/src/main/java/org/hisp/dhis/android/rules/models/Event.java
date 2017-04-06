package org.hisp.dhis.android.rules.models;

import com.google.auto.value.AutoValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.Nonnull;

@AutoValue
public abstract class Event {
    public static final Comparator<Event> EVENT_DATE_COMPARATOR = new EventDateComparator();

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
    public abstract List<TrackedEntityDataValue> dataValues();

    @Nonnull
    public static Event create(
            @Nonnull String event,
            @Nonnull EventStatus status,
            @Nonnull String programStage,
            @Nonnull Date eventDate,
            @Nonnull Date dueDate,
            @Nonnull List<TrackedEntityDataValue> dataValues) {
        return new AutoValue_Event(event, status, programStage, eventDate, dueDate,
                Collections.unmodifiableList(new ArrayList<>(dataValues)));
    }

    private static class EventDateComparator implements Comparator<Event> {

        @Override
        public int compare(Event first, Event second) {
            return first.eventDate().compareTo(second.eventDate());
        }
    }
}

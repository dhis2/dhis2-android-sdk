package org.hisp.dhis.android.rules.models;

import com.google.auto.value.AutoValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.Nonnull;

@AutoValue
public abstract class RuleEvent {
    public static final Comparator<RuleEvent> EVENT_DATE_COMPARATOR = new EventDateComparator();

    @Nonnull
    public abstract String event();

    @Nonnull
    public abstract Status status();

    @Nonnull
    public abstract String programStage();

    @Nonnull
    public abstract Date eventDate();

    @Nonnull
    public abstract Date dueDate();

    @Nonnull
    public abstract List<RuleDataValue> dataValues();

    @Nonnull
    public static RuleEvent create(
            @Nonnull String event,
            @Nonnull Status status,
            @Nonnull String programStage,
            @Nonnull Date eventDate,
            @Nonnull Date dueDate,
            @Nonnull List<RuleDataValue> ruleDataValues) {
        return new AutoValue_RuleEvent(event, status, programStage, eventDate, dueDate,
                Collections.unmodifiableList(new ArrayList<>(ruleDataValues)));
    }

    public enum Status {
        ACTIVE, COMPLETED, SCHEDULE, SKIPPED
    }

    private static class EventDateComparator implements Comparator<RuleEvent> {

        @Override
        public int compare(RuleEvent first, RuleEvent second) {
            return second.eventDate().compareTo(first.eventDate());
        }
    }
}

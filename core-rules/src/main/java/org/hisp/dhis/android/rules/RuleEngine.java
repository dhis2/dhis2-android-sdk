package org.hisp.dhis.android.rules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public final class RuleEngine {

    @Nonnull
    private final RuleEngineContext ruleEngineContext;

    @Nonnull
    private final List<RuleEvent> ruleEvents;

    @Nullable
    private final RuleEnrollment ruleEnrollment;

    RuleEngine(@Nonnull RuleEngineContext ruleEngineContext,
            @Nonnull List<RuleEvent> ruleEvents,
            @Nullable RuleEnrollment ruleEnrollment) {
        this.ruleEngineContext = ruleEngineContext;
        this.ruleEvents = ruleEvents;
        this.ruleEnrollment = ruleEnrollment;
    }

    @Nonnull
    public List<RuleEvent> events() {
        return ruleEvents;
    }

    @Nullable
    public RuleEnrollment enrollment() {
        return ruleEnrollment;
    }

    @Nonnull
    public RuleEngineContext executionContext() {
        return ruleEngineContext;
    }

    @Nonnull
    public Callable<List<RuleEffect>> calculate(@Nonnull RuleEvent ruleEvent) {
        if (ruleEvent == null) {
            throw new IllegalArgumentException("ruleEvent == null");
        }

        for (RuleEvent contextualEvent : ruleEvents) {
            if (contextualEvent.event().equals(ruleEvent.event())) {
                throw new IllegalStateException(String.format(Locale.US, "Event '%s' is already " +
                        "set as a part of execution context.", contextualEvent.event()));
            }
        }

        throw new UnsupportedOperationException();
    }

    @Nonnull
    public Callable<List<RuleEffect>> calculate(@Nonnull RuleEnrollment ruleEnrollment) {
        if (ruleEnrollment == null) {
            throw new IllegalArgumentException("ruleEnrollment == null");
        }

        if (this.ruleEnrollment != null) {
            throw new IllegalStateException(String.format(Locale.US, "Enrollment '%s' is already " +
                    "set as a part of execution context.", this.ruleEnrollment.enrollment()));
        }

        throw new UnsupportedOperationException();
    }

    public static class Builder {

        @Nonnull
        private final RuleEngineContext ruleEngineContext;

        @Nullable
        private List<RuleEvent> ruleEvents;

        @Nullable
        private RuleEnrollment ruleEnrollment;

        Builder(@Nonnull RuleEngineContext ruleEngineContext) {
            this.ruleEngineContext = ruleEngineContext;
        }

        @Nonnull
        public Builder events(@Nonnull List<RuleEvent> ruleEvents) {
            if (ruleEvents != null) {
                this.ruleEvents = Collections.unmodifiableList(new ArrayList<>(ruleEvents));
            }

            return this;
        }

        @Nonnull
        public Builder enrollment(@Nonnull RuleEnrollment ruleEnrollment) {
            this.ruleEnrollment = ruleEnrollment;
            return this;
        }

        @Nonnull
        public RuleEngine build() {
            if (ruleEvents == null) {
                ruleEvents = Collections.unmodifiableList(new ArrayList<RuleEvent>());
            }

            return new RuleEngine(ruleEngineContext, ruleEvents, ruleEnrollment);
        }
    }
}

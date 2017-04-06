package org.hisp.dhis.android.rules;

import org.hisp.dhis.android.rules.models.Event;
import org.hisp.dhis.android.rules.models.ProgramRule;
import org.hisp.dhis.android.rules.models.variables.RuleVariable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import javax.annotation.Nonnull;

import static java.util.Collections.unmodifiableList;


public final class RuleEngine {
    private final ExpressionEvaluator evaluator;
    private final List<ProgramRule> programRules;
    private final List<RuleVariable> ruleVariables;
    private final List<Event> events;

    RuleEngine(ExpressionEvaluator evaluator,
            List<ProgramRule> programRules,
            List<RuleVariable> ruleVariables,
            List<Event> events) {
        this.evaluator = evaluator;
        this.programRules = programRules;
        this.ruleVariables = ruleVariables;
        this.events = events;
    }

    public static Builder builder(@Nonnull ExpressionEvaluator evaluator) {
        if (evaluator == null) {
            throw new IllegalArgumentException("evaluator == null");
        }

        return new Builder(evaluator);
    }

    @Nonnull
    public List<ProgramRule> programRules() {
        return programRules;
    }

    @Nonnull
    public List<RuleVariable> programRuleVariables() {
        return ruleVariables;
    }

    @Nonnull
    public List<Event> events() {
        return events;
    }

    @Nonnull
    public Callable<List<RuleEffect>> calculate(@Nonnull Event currentEvent) {
        if (currentEvent == null) {
            throw new IllegalArgumentException("currentEvent == null");
        }

        return new RuleExecution(new HashMap<String, ProgramRuleVariableValue>());
    }

    public static class Builder {
        private final ExpressionEvaluator evaluator;
        private List<ProgramRule> programRules;
        private List<RuleVariable> ruleVariables;
        private List<Event> events;

        Builder(@Nonnull ExpressionEvaluator evaluator) {
            this.evaluator = evaluator;
        }

        public Builder programRules(@Nonnull List<ProgramRule> programRules) {
            if (programRules == null) {
                throw new IllegalArgumentException("programRules == null");
            }

            this.programRules = unmodifiableList(new ArrayList<>(programRules));
            return this;
        }

        // ToDo: Is there a use case where program rules do not contain any variables?
        public Builder programRuleVariables(@Nonnull List<RuleVariable> ruleVariables) {
            if (ruleVariables == null) {
                throw new IllegalArgumentException("ruleVariables == null");
            }

            this.ruleVariables = unmodifiableList(new ArrayList<>(ruleVariables));
            return this;
        }

        public Builder events(@Nonnull List<Event> events) {
            if (events != null) {
                this.events = Collections.unmodifiableList(new ArrayList<>(events));
            }
            return this;
        }


        public RuleEngine build() {
            // avoiding null collections
            if (events == null) {
                events = Collections.unmodifiableList(new ArrayList<Event>());
            }

            return new RuleEngine(evaluator, programRules, ruleVariables, events);
        }
    }
}

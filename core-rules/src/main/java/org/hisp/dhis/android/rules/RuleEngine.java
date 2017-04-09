package org.hisp.dhis.android.rules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import javax.annotation.Nonnull;

import static java.util.Collections.unmodifiableList;


public final class RuleEngine {
    private final RuleExpressionEvaluator evaluator;
    private final List<Rule> rules;
    private final List<RuleVariable> ruleVariables;
    private final List<RuleEvent> ruleEvents;

    RuleEngine(RuleExpressionEvaluator evaluator,
            List<Rule> rules,
            List<RuleVariable> ruleVariables,
            List<RuleEvent> ruleEvents) {
        this.evaluator = evaluator;
        this.rules = rules;
        this.ruleVariables = ruleVariables;
        this.ruleEvents = ruleEvents;
    }

    public static Builder builder(@Nonnull RuleExpressionEvaluator evaluator) {
        if (evaluator == null) {
            throw new IllegalArgumentException("evaluator == null");
        }

        return new Builder(evaluator);
    }

    @Nonnull
    public List<Rule> programRules() {
        return rules;
    }

    @Nonnull
    public List<RuleVariable> programRuleVariables() {
        return ruleVariables;
    }

    @Nonnull
    public List<RuleEvent> events() {
        return ruleEvents;
    }

    @Nonnull
    public Callable<List<RuleEffect>> calculate(@Nonnull RuleEvent currentRuleEvent) {
        if (currentRuleEvent == null) {
            throw new IllegalArgumentException("currentRuleEvent == null");
        }

        return new RuleExecution(new HashMap<String, RuleVariableValue>());
    }

    public static class Builder {
        private final RuleExpressionEvaluator evaluator;
        private List<Rule> rules;
        private List<RuleVariable> ruleVariables;
        private List<RuleEvent> ruleEvents;

        Builder(@Nonnull RuleExpressionEvaluator evaluator) {
            this.evaluator = evaluator;
        }

        public Builder programRules(@Nonnull List<Rule> rules) {
            if (rules == null) {
                throw new IllegalArgumentException("rules == null");
            }

            this.rules = unmodifiableList(new ArrayList<>(rules));
            return this;
        }

        // ToDo: Is there a use case where program rules do not contain any ruleVariables?
        public Builder programRuleVariables(@Nonnull List<RuleVariable> ruleVariables) {
            if (ruleVariables == null) {
                throw new IllegalArgumentException("ruleVariables == null");
            }

            this.ruleVariables = unmodifiableList(new ArrayList<>(ruleVariables));
            return this;
        }

        public Builder events(@Nonnull List<RuleEvent> ruleEvents) {
            if (ruleEvents != null) {
                this.ruleEvents = Collections.unmodifiableList(new ArrayList<>(ruleEvents));
            }
            return this;
        }


        public RuleEngine build() {
            // avoiding null collections
            if (ruleEvents == null) {
                ruleEvents = Collections.unmodifiableList(new ArrayList<RuleEvent>());
            }

            return new RuleEngine(evaluator, rules, ruleVariables, ruleEvents);
        }
    }
}

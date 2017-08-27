package org.hisp.dhis.rules;

import org.hisp.dhis.rules.models.Rule;
import org.hisp.dhis.rules.models.RuleVariable;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static java.util.Collections.unmodifiableList;

public final class RuleEngineContext {

    @Nonnull
    private final RuleExpressionEvaluator expressionEvaluator;

    @Nonnull
    private final List<Rule> rules;

    @Nonnull
    private final List<RuleVariable> ruleVariables;

    RuleEngineContext(@Nonnull RuleExpressionEvaluator expressionEvaluator,
            @Nonnull List<Rule> rules, @Nonnull List<RuleVariable> ruleVariables) {
        this.expressionEvaluator = expressionEvaluator;
        this.rules = rules;
        this.ruleVariables = ruleVariables;
    }

    @Nonnull
    public List<Rule> rules() {
        return rules;
    }

    @Nonnull
    public List<RuleVariable> ruleVariables() {
        return ruleVariables;
    }

    @Nonnull
    public RuleExpressionEvaluator expressionEvaluator() {
        return expressionEvaluator;
    }

    @Nonnull
    public RuleEngine.Builder toEngineBuilder() {
        return new RuleEngine.Builder(this);
    }

    @Nonnull
    public static Builder builder(@Nonnull RuleExpressionEvaluator evaluator) {
        if (evaluator == null) {
            throw new IllegalArgumentException("evaluator == null");
        }

        return new Builder(evaluator);
    }

    public static class Builder {

        @Nonnull
        private final RuleExpressionEvaluator evaluator;

        @Nullable
        private List<Rule> rules;

        @Nullable
        private List<RuleVariable> ruleVariables;

        Builder(@Nonnull RuleExpressionEvaluator evaluator) {
            this.evaluator = evaluator;
        }

        @Nonnull
        public Builder rules(@Nonnull List<Rule> rules) {
            if (rules == null) {
                throw new IllegalArgumentException("rules == null");
            }

            this.rules = unmodifiableList(new ArrayList<>(rules));
            return this;
        }

        @Nonnull
        public Builder ruleVariables(@Nonnull List<RuleVariable> ruleVariables) {
            if (ruleVariables == null) {
                throw new IllegalArgumentException("ruleVariables == null");
            }

            this.ruleVariables = unmodifiableList(new ArrayList<>(ruleVariables));
            return this;
        }

        @Nonnull
        public RuleEngineContext build() {
            if (rules == null) {
                rules = unmodifiableList(new ArrayList<Rule>());
            }

            if (ruleVariables == null) {
                ruleVariables = unmodifiableList(new ArrayList<RuleVariable>());
            }

            return new RuleEngineContext(evaluator, rules, ruleVariables);
        }
    }
}

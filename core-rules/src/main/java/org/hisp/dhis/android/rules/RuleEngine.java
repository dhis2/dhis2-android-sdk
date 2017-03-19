package org.hisp.dhis.android.rules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;


public final class RuleEngine {
    private final ExpressionEvaluator evaluator;
    private final List<ProgramRule> programRules;
    private final List<ProgramRuleVariable> programRuleVariables;

    private RuleEngine(ExpressionEvaluator evaluator,
            List<ProgramRule> programRules,
            List<ProgramRuleVariable> programRuleVariables) {
        this.evaluator = evaluator;
        this.programRules = programRules;
        this.programRuleVariables = programRuleVariables;
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
    public List<ProgramRuleVariable> programRuleVariables() {
        return programRuleVariables;
    }

    public static class Builder {
        private final ExpressionEvaluator evaluator;
        private List<ProgramRule> programRules;
        private List<ProgramRuleVariable> programRuleVariables;

        Builder(@Nonnull ExpressionEvaluator evaluator) {
            this.evaluator = evaluator;
        }

        public Builder programRules(@Nonnull List<ProgramRule> programRules) {
            if (programRules == null) {
                throw new IllegalArgumentException("programRules == null");
            }

            this.programRules = Collections.unmodifiableList(new ArrayList<>(programRules));
            return this;
        }

        // ToDo: Is there a use case where program rules do not contain any variables?
        public Builder programRuleVariables(@Nonnull List<ProgramRuleVariable> programRuleVariables) {
            if (programRuleVariables == null) {
                throw new IllegalArgumentException("programRuleVariables == null");
            }

            this.programRuleVariables = Collections.unmodifiableList(new ArrayList<>(programRuleVariables));
            return this;
        }

        public RuleEngine build() {
            return new RuleEngine(evaluator, programRules, programRuleVariables);
        }
    }
}

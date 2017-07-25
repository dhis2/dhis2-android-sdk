package org.hisp.dhis.rules;

import javax.annotation.Nonnull;

public interface RuleExpressionEvaluator {

    @Nonnull
    String evaluate(@Nonnull String expression);
}

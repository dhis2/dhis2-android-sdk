package org.hisp.dhis.android.rules;

import javax.annotation.Nonnull;

public interface ExpressionEvaluator {

    @Nonnull
    String evaluate(@Nonnull String expression);
}

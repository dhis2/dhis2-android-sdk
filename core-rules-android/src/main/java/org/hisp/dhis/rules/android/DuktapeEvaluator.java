package org.hisp.dhis.rules.android;

import android.support.annotation.NonNull;

import com.squareup.duktape.Duktape;

import org.hisp.dhis.rules.RuleExpressionEvaluator;

import javax.annotation.Nonnull;

public final class DuktapeEvaluator implements RuleExpressionEvaluator {

    @NonNull
    private final Duktape duktape;

    public DuktapeEvaluator(@NonNull Duktape duktape) {
        if (duktape == null) {
            throw new NullPointerException("duktape == null");
        }

        this.duktape = duktape;
    }

    @Nonnull
    @Override
    public String evaluate(@Nonnull String expression) {
        if (expression == null) {
            throw new NullPointerException("expression == null");
        }

        return duktape.evaluate(expression).toString();
    }
}

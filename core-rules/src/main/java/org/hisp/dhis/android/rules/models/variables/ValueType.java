package org.hisp.dhis.android.rules.models.variables;

import javax.annotation.Nonnull;

public enum ValueType {
    TEXT("''"), NUMERIC("0"), BOOLEAN("false");

    private final String defaultValue;

    ValueType(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Nonnull
    public String defaultValue() {
        return defaultValue;
    }
}

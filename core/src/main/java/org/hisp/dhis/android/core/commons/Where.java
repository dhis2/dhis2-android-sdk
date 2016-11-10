package org.hisp.dhis.android.core.commons;

import android.support.annotation.Nullable;

import java.util.Arrays;

public final class Where {

    @Nullable
    private final String where;

    @Nullable
    private final String[] arguments;

    private Where(@Nullable String where, @Nullable String[] arguments) {
        this.where = where;
        this.arguments = arguments;
    }

    @Nullable
    public String where() {
        return where;
    }

    @Nullable
    public String[] arguments() {
        if (arguments != null) {
            return Arrays.copyOf(arguments, arguments.length);
        }

        return null;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        Where where1 = (Where) other;

        if (where != null ? !where.equals(where1.where) : where1.where != null) {
            return false;
        }

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(arguments, where1.arguments);

    }

    @Override
    public int hashCode() {
        int result = where != null ? where.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(arguments);
        return result;
    }

    public static class Builder {

        @Nullable
        private String where;

        @Nullable
        private String[] arguments;

        Builder() {
            // empty constructor
        }

        public Builder where(String where) {
            this.where = where;
            return this;
        }

        public Builder arguments(String... arguments) {
            this.arguments = arguments;
            return this;
        }

        public Where build() {
            return new Where(where, arguments);
        }
    }
}

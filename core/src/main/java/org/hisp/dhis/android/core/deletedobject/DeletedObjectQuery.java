package org.hisp.dhis.android.core.deletedobject;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.category.CategoryOption;
import org.hisp.dhis.android.core.resource.ResourceModel;

import java.util.HashSet;
import java.util.Set;

public class DeletedObjectQuery {
    private final String klass;
    private final String deletedAt;

    public DeletedObjectQuery(
            String klass, String deletedAt) {
        this.klass = klass;
        this.deletedAt = deletedAt;

    }

    public String getKlass() {
        return klass;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public static class Builder {
        private String klass;
        private String deletedAt;

        private Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        public Builder withKlass(String klass) {
            this.klass = klass;
            return this;
        }
        public Builder withDeletedAt(String deletedAt) {
            this.deletedAt = deletedAt;
            return this;
        }

        public DeletedObjectQuery build() {
            if (klass == null) {
                throw new IllegalArgumentException(
                        "klass can not be null");
            }
            if (deletedAt == null) {
                throw new IllegalArgumentException(
                        "deletedAt can not be null");
            }

            return new DeletedObjectQuery(klass, deletedAt);
        }
    }
}
package org.hisp.dhis.android.core.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.common.BaseQuery;
import org.hisp.dhis.android.core.data.api.OuMode;

import java.util.Collection;
import java.util.Collections;

@AutoValue
public abstract class EventQuery extends BaseQuery {

    @Nullable
    public abstract String orgUnit();

    @Nullable
    public abstract String program();

    @Nullable
    public abstract String trackedEntityInstance();

    @NonNull
    public abstract OuMode ouMode();

    @Nullable
    public abstract CategoryCombo categoryCombo();

    @Nullable
    public abstract Collection<String> uIds();

    public static Builder builder() {
        return new AutoValue_EventQuery.Builder()
                .page(1)
                .pageSize(DEFAULT_PAGE_SIZE)
                .paging(true)
                .ouMode(OuMode.SELECTED)
                .uIds(Collections.<String>emptyList());
    }

    @AutoValue.Builder
    public abstract static class Builder extends BaseQuery.Builder<Builder> {
        public abstract Builder orgUnit(String orgUnit);

        public abstract Builder program(String program);

        public abstract Builder trackedEntityInstance(String trackedEntityInstance);

        public abstract Builder ouMode(OuMode ouMode);

        public abstract Builder categoryCombo(CategoryCombo categoryCombo);

        public abstract Builder uIds(Collection<String> uIds);

        public abstract EventQuery build();
    }
}

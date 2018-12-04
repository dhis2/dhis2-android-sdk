package org.hisp.dhis.android.core.trackedentity;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseQuery;
import org.hisp.dhis.android.core.data.api.OuMode;

import java.util.Collection;
import java.util.Collections;

@AutoValue
public abstract class TeiQuery extends BaseQuery {

    @Nullable
    public abstract Collection<String> orgUnits();

    @NonNull
    public abstract OuMode ouMode();

    @Nullable
    public abstract String lastUpdatedStartDate();

    @Nullable
    public abstract Collection<String> uIds();

    public static Builder builder() {
        return new AutoValue_TeiQuery.Builder()
                .page(1)
                .pageSize(DEFAULT_PAGE_SIZE)
                .paging(true)
                .ouMode(OuMode.SELECTED)
                .uIds(Collections.<String>emptyList());
    }

    @AutoValue.Builder
    public abstract static class Builder extends BaseQuery.Builder<Builder> {
        public abstract Builder orgUnits(Collection<String> orgUnits);

        public abstract Builder ouMode(OuMode ouMode);

        public abstract Builder lastUpdatedStartDate(String lastUpdatedStartDate);

        public abstract Builder uIds(Collection<String> uIds);

        public abstract TeiQuery build();
    }
}

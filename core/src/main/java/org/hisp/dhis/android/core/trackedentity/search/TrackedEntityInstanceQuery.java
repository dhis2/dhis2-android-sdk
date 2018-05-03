package org.hisp.dhis.android.core.trackedentity.search;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseQuery;
import org.hisp.dhis.android.core.data.api.OuMode;

import javax.annotation.Nullable;

import io.reactivex.annotations.NonNull;

@AutoValue
public abstract class TrackedEntityInstanceQuery extends BaseQuery {


    @NonNull
    public abstract String orgUnit();

    @Nullable
    public abstract OuMode orgUnitMode();

    @Nullable
    public abstract String program();

    public static Builder builder() {
        return new AutoValue_TrackedEntityInstanceQuery.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder extends BaseQuery.Builder<Builder> {
        public abstract Builder orgUnit(String orgUnit);

        public abstract Builder orgUnitMode(OuMode orgUnitMode);

        public abstract Builder program(String program);

        public abstract TrackedEntityInstanceQuery build();
    }
}

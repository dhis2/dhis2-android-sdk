package org.hisp.dhis.android.core.trackedentity.search;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseQuery;
import org.hisp.dhis.android.core.data.api.OuMode;

import java.util.List;

import javax.annotation.Nullable;

import io.reactivex.annotations.NonNull;

@AutoValue
public abstract class TrackedEntityInstanceQuery extends BaseQuery {


    @NonNull
    public abstract List<String> orgUnits();

    @Nullable
    public abstract OuMode orgUnitMode();

    @Nullable
    public abstract String program();

    @Nullable
    public abstract String query();

    @Nullable
    public abstract List<String> attribute();

    @Nullable
    public abstract List<String> filter();

    public static Builder builder() {
        return new AutoValue_TrackedEntityInstanceQuery.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder extends BaseQuery.Builder<Builder> {
        public abstract Builder orgUnits(List<String> orgUnits);

        public abstract Builder orgUnitMode(OuMode orgUnitMode);

        public abstract Builder program(String program);

        public abstract Builder query(String query);

        public abstract Builder attribute(List<String> attribute);

        public abstract Builder filter(List<String> filter);

        public abstract TrackedEntityInstanceQuery build();
    }
}

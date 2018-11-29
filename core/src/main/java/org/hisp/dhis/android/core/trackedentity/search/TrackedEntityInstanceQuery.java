package org.hisp.dhis.android.core.trackedentity.search;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseQuery;
import org.hisp.dhis.android.core.common.SafeDateFormat;
import org.hisp.dhis.android.core.data.api.OuMode;

import java.util.Date;
import java.util.List;

@AutoValue
public abstract class TrackedEntityInstanceQuery extends BaseQuery {

    private static final SafeDateFormat QUERY_FORMAT = new SafeDateFormat("yyyy-MM-dd");

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

    @Nullable
    public abstract Date programStartDate();

    @Nullable
    public abstract Date programEndDate();

    public String formattedProgramStartDate() {
        return programStartDate() == null ? null : QUERY_FORMAT.format(programStartDate());
    }

    public String formattedProgramEndDate() {
        return programEndDate() == null ? null : QUERY_FORMAT.format(programEndDate());
    }

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

        public abstract Builder programStartDate(Date programStartDate);

        public abstract Builder programEndDate(Date programEndDate);

        public abstract TrackedEntityInstanceQuery build();
    }
}
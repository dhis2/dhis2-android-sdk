package org.hisp.dhis.android.core.settings;

import android.database.Cursor;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.CoreObject;

@AutoValue
@JsonDeserialize(builder = $$AutoValue_FilterConfig.Builder.class)
public abstract class FilterConfig implements CoreObject {

    @Nullable
    public abstract String scope();

    @Nullable
    public abstract String filterType();

    @Nullable
    public abstract String uid();

    @Nullable
    @JsonProperty()
    public abstract Boolean sort();

    @Nullable
    @JsonProperty()
    public abstract Boolean filter();

    public static FilterConfig create(Cursor cursor) {
        return $AutoValue_FilterConfig.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new $AutoValue_FilterConfig.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder {

        public abstract Builder id(Long id);

        public abstract Builder scope(String scope);

        public abstract Builder filterType(String filterType);

        public abstract Builder uid(String uid);

        public abstract Builder sort(Boolean sort);

        public abstract Builder filter(Boolean filter);

        public abstract FilterConfig build();
    }
}

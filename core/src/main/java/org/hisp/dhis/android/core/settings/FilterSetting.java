package org.hisp.dhis.android.core.settings;

import android.database.Cursor;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.CoreObject;

@AutoValue
@JsonDeserialize(builder = $$AutoValue_FilterSetting.Builder.class)
public abstract class FilterSetting implements CoreObject {

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

    public static FilterSetting create(Cursor cursor) {
        return $AutoValue_FilterSetting.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new $AutoValue_FilterSetting.Builder();
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

        public abstract FilterSetting build();
    }
}

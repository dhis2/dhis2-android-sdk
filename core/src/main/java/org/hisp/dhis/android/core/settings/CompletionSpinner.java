package org.hisp.dhis.android.core.settings;

import android.database.Cursor;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.CoreObject;

@AutoValue
@JsonDeserialize(builder = $AutoValue_CompletionSpinner.Builder.class)
public abstract class CompletionSpinner implements CoreObject {

    @Nullable
    public abstract String uid();

    public abstract Boolean visible();

    public static CompletionSpinner create(Cursor cursor) {
        return $AutoValue_CompletionSpinner.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new $AutoValue_CompletionSpinner.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder {

        public abstract Builder id(Long id);

        public abstract Builder uid(String uid);

        public abstract Builder visible(Boolean visible);

        public abstract CompletionSpinner build();
    }
}

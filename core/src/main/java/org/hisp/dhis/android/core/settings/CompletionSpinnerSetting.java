package org.hisp.dhis.android.core.settings;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;

import java.util.Map;

@AutoValue
@JsonDeserialize(builder = AutoValue_CompletionSpinnerSetting.Builder.class)
public abstract class CompletionSpinnerSetting {

    @Nullable
    @JsonProperty
    public abstract CompletionSpinner globalSettings();

    @Nullable
    @JsonProperty
    public abstract Map<String, CompletionSpinner> specificSettings();

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new AutoValue_CompletionSpinnerSetting.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder {
        public abstract Builder globalSettings(CompletionSpinner globalSettings);

        public abstract Builder specificSettings(Map<String, CompletionSpinner> specificSettings);

        public abstract CompletionSpinnerSetting build();
    }
}

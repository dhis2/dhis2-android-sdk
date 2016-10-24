package org.hisp.dhis.client.models.dataelement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.client.models.common.BaseNameableObject;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

// TODO: Tests
@AutoValue
@JsonDeserialize(builder = AutoValue_CategoryOption.Builder.class)
public abstract class CategoryOption extends BaseNameableObject {
    private static final String JSON_PROPERTY_CATEGORY_OPTION_COMBOS = "categoryOptionCombos";
    private static final String JSON_PROPERTY_START_DATE = "startDate";
    private static final String JSON_PROPERTY_END_DATE = "endDate";

    @Nullable
    @JsonProperty(JSON_PROPERTY_CATEGORY_OPTION_COMBOS)
    public abstract List<CategoryOptionCombo> categoryOptionCombos();

    @Nullable
    @JsonProperty(JSON_PROPERTY_START_DATE)
    public abstract Date startDate();

    @Nullable
    @JsonProperty(JSON_PROPERTY_END_DATE)
    public abstract Date endDate();

    public static Builder builder() {
        return new AutoValue_CategoryOption.Builder();
    }

    @AutoValue.Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static abstract class Builder extends BaseNameableObject.Builder<Builder> {

        @JsonProperty(JSON_PROPERTY_CATEGORY_OPTION_COMBOS)
        public abstract Builder categoryOptionCombos(
                @Nullable List<CategoryOptionCombo> categoryOptionCombos);

        @JsonProperty(JSON_PROPERTY_START_DATE)
        public abstract Builder startDate(@Nullable Date startDate);

        @JsonProperty(JSON_PROPERTY_END_DATE)
        public abstract Builder endDate(@Nullable Date endDate);

        // internal, not exposed
        abstract List<CategoryOptionCombo> categoryOptionCombos();

        abstract CategoryOption autoBuild();

        public CategoryOption build() {
            if (categoryOptionCombos() != null) {
                categoryOptionCombos(Collections.unmodifiableList(categoryOptionCombos()));
            }

            return autoBuild();
        }
    }
}

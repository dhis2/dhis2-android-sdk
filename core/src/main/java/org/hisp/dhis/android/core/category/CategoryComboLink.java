package org.hisp.dhis.android.core.category;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.data.api.Field;


@AutoValue
@JsonDeserialize(builder = AutoValue_CategoryComboLink.Builder.class)
public abstract class CategoryComboLink {
    public static final String CATEGORY = "category";
    public static final String COMBO = "combo";

    public static final Field<CategoryComboLink, String> category = Field.create(CATEGORY);
    public static final Field<CategoryComboLink, String> combo = Field.create(COMBO);

    public static Builder builder() {
        return new AutoValue_CategoryComboLink.Builder();
    }

    @JsonCreator
    public static CategoryComboLink create(
            @JsonProperty(CATEGORY) String category,
            @JsonProperty(COMBO) String combo
    ) {
        return builder().category(category).combo(combo).build();
    }

    @Nullable
    @JsonProperty(CATEGORY)
    public abstract String category();

    @Nullable
    @JsonProperty(COMBO)
    public abstract String combo();

    @AutoValue.Builder
    public static abstract class Builder {

        @JsonProperty(CATEGORY)
        public abstract Builder category(String category);

        @JsonProperty(COMBO)
        public abstract Builder combo(String combo);

        abstract CategoryComboLink autoBuild();

        public CategoryComboLink build() {
            return autoBuild();
        }
    }
}

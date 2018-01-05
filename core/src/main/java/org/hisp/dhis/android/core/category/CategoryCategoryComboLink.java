package org.hisp.dhis.android.core.category;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.data.api.Field;


@AutoValue
@JsonDeserialize(builder = AutoValue_CategoryCategoryComboLink.Builder.class)
public abstract class CategoryCategoryComboLink {
    public static final String CATEGORY = "category";
    public static final String CATEGORY_COMBO = "categoryCombo";

    public static final Field<CategoryCategoryComboLink, String> category = Field.create(CATEGORY);
    public static final Field<CategoryCategoryComboLink, String> categoryCombo = Field.create(CATEGORY_COMBO);

    public static Builder builder() {
        return new AutoValue_CategoryCategoryComboLink.Builder();
    }

    @JsonCreator
    public static CategoryCategoryComboLink create(
            @JsonProperty(CATEGORY) String category,
            @JsonProperty(CATEGORY_COMBO) String categoryCombo
    ) {
        return builder().category(category).categoryCombo(categoryCombo).build();
    }

    @Nullable
    @JsonProperty(CATEGORY)
    public abstract String category();

    @Nullable
    @JsonProperty(CATEGORY_COMBO)
    public abstract String categoryCombo();

    @AutoValue.Builder
    public static abstract class Builder {

        @JsonProperty(CATEGORY)
        public abstract Builder category(String category);

        @JsonProperty(CATEGORY_COMBO)
        public abstract Builder categoryCombo(String combo);

        abstract CategoryCategoryComboLink autoBuild();

        public CategoryCategoryComboLink build() {
            return autoBuild();
        }
    }
}

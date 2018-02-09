package org.hisp.dhis.android.core.category;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.data.api.Field;


@AutoValue
@JsonDeserialize(builder = AutoValue_CategoryCategoryOptionLink.Builder.class)
public abstract class CategoryCategoryOptionLink {
    public static final String CATEGORY = "category";
    public static final String CATEGORY_OPTION = "categoryOption";

    public static final Field<CategoryCategoryOptionLink, String> category = Field.create(CATEGORY);
    public static final Field<CategoryCategoryOptionLink, String> categoryOption = Field.create(CATEGORY_OPTION);

    public static Builder builder() {
        return new AutoValue_CategoryCategoryOptionLink.Builder();
    }

    @JsonCreator
    public static CategoryCategoryOptionLink create(
            @JsonProperty(CATEGORY) String category,
            @JsonProperty(CATEGORY_OPTION) String categoryOption
    ) {
        return builder().category(category).categoryOption(categoryOption).build();
    }

    @Nullable
    @JsonProperty(CATEGORY)
    public abstract String category();

    @Nullable
    @JsonProperty(CATEGORY_OPTION)
    public abstract String categoryOption();

    @AutoValue.Builder
    public static abstract class Builder {

        @JsonProperty(CATEGORY)
        public abstract Builder category(String category);

        @JsonProperty(CATEGORY_OPTION)
        public abstract Builder categoryOption(String categoryOption);

        abstract CategoryCategoryOptionLink autoBuild();

        public CategoryCategoryOptionLink build() {
            return autoBuild();
        }
    }
}

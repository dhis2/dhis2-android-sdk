/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.category;

import static org.hisp.dhis.android.core.utils.Utils.safeUnmodifiableList;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.api.Field;
import org.hisp.dhis.android.core.data.api.NestedField;

import java.util.List;

@AutoValue
@JsonDeserialize(builder = AutoValue_CategoryCombo.Builder.class)
public abstract class CategoryCombo extends BaseIdentifiableObject {
    private static final String JSON_PROPERTY_IS_DEFAULT = "isDefault";
    private static final String JSON_PROPERTY_CATEGORIES = "categories";
    private static final String CATEGORY_OPTION_COMBOS = "categoryOptionCombos";

    public static final Field<CategoryCombo, String> uid = Field.create(UID);
    public static final Field<CategoryCombo, String> code = Field.create(CODE);
    public static final Field<CategoryCombo, String> name = Field.create(NAME);
    public static final Field<CategoryCombo, String> displayName = Field.create(DISPLAY_NAME);
    public static final Field<CategoryCombo, String> created = Field.create(CREATED);
    public static final Field<CategoryCombo, String> lastUpdated = Field.create(LAST_UPDATED);
    public static final Field<CategoryCombo, Boolean> deleted = Field.create(DELETED);
    public static final Field<CategoryCombo, Boolean> isDefault = Field.create(
            JSON_PROPERTY_IS_DEFAULT);
    public static final Field<CategoryCombo, List<Category>> categories = Field.create(
            JSON_PROPERTY_CATEGORIES);

    public static final NestedField<CategoryCombo, CategoryOptionCombo> categoryOptionCombos =
            NestedField.create(CATEGORY_OPTION_COMBOS);

    @Nullable
    @JsonProperty(JSON_PROPERTY_IS_DEFAULT)
    public abstract Boolean isDefault();

    @Nullable
    @JsonProperty(JSON_PROPERTY_CATEGORIES)
    public abstract List<Category> categories();

    @Nullable
    @JsonProperty(CATEGORY_OPTION_COMBOS)
    public abstract List<CategoryOptionCombo> categoryOptionCombos();

    public static Builder builder() {
        return new AutoValue_CategoryCombo.Builder();
    }

    @AutoValue.Builder
    public static abstract class Builder extends BaseIdentifiableObject.Builder<Builder> {

        @JsonProperty(JSON_PROPERTY_IS_DEFAULT)
        public abstract Builder isDefault(@Nullable Boolean isDefault);

        @JsonProperty(JSON_PROPERTY_CATEGORIES)
        public abstract Builder categories(@Nullable List<Category> categories);

        @JsonProperty(CATEGORY_OPTION_COMBOS)
        public abstract Builder categoryOptionCombos(
                @Nullable List<CategoryOptionCombo> categoryOptionCombos);

        // internal, not exposed
        abstract List<Category> categories();

        abstract List<CategoryOptionCombo> categoryOptionCombos();

        abstract CategoryCombo autoBuild();

        public CategoryCombo build() {
            categories(safeUnmodifiableList(categories()));
            categoryOptionCombos(safeUnmodifiableList(categoryOptionCombos()));
            return autoBuild();
        }
    }
}

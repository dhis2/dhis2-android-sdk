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

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseNameableObject;
import org.hisp.dhis.android.core.data.api.Field;
import org.hisp.dhis.android.core.data.api.NestedField;

import java.util.Collections;
import java.util.List;

@AutoValue
@JsonDeserialize(builder = AutoValue_Category.Builder.class)
public abstract class Category extends BaseNameableObject {
    private static final String CATEGORY_OPTIONS = "categoryOptions";
    private static final String DATA_DIMENSION_TYPE = "dataDimensionType";

    public static final Field<Category, String> uid = Field.create(UID);
    public static final Field<Category, String> code = Field.create(CODE);
    public static final Field<Category, String> name = Field.create(NAME);
    public static final Field<Category, String> displayName = Field.create(DISPLAY_NAME);
    public static final Field<Category, String> created = Field.create(CREATED);
    public static final Field<Category, String> lastUpdated = Field.create(LAST_UPDATED);
    public static final Field<Category, Boolean> deleted = Field.create(DELETED);
    public static final Field<Category, String> shortName = Field.create(SHORT_NAME);
    public static final Field<Category, String> displayShortName = Field.create(DISPLAY_SHORT_NAME);
    public static final Field<Category, String> dataDimensionType = Field.create(
            DATA_DIMENSION_TYPE);

    public static final NestedField<Category, CategoryOption> categoryOptions = NestedField.create(
            CATEGORY_OPTIONS);


    @Nullable
    @JsonProperty(DATA_DIMENSION_TYPE)
    public abstract String dataDimensionType();

    @Nullable
    @JsonProperty(CATEGORY_OPTIONS)
    public abstract List<CategoryOption> categoryOptions();

    public static Builder builder() {
        return new AutoValue_Category.Builder();
    }


    @AutoValue.Builder
    public static abstract class Builder extends BaseNameableObject.Builder<Builder> {

        @JsonProperty(CATEGORY_OPTIONS)
        public abstract Builder categoryOptions(@Nullable List<CategoryOption> categoryOptions);

        @JsonProperty(DATA_DIMENSION_TYPE)
        public abstract Builder dataDimensionType(String dimensionType);

        // used only to support unmodifiable collections
        @Nullable
        abstract List<CategoryOption> categoryOptions();

        // used only to support unmodifiable collections
        abstract Category autoBuild();

        public Category build() {
            if (categoryOptions() != null) {
                //noinspection ConstantConditions
                categoryOptions(Collections.unmodifiableList(categoryOptions()));
            }

            return autoBuild();
        }
    }
}

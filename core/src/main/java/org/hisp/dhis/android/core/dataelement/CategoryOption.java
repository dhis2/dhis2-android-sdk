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

package org.hisp.dhis.android.core.dataelement;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseNameableObject;

import java.util.Date;
import java.util.List;

import static org.hisp.dhis.android.core.utils.Utils.safeUnmodifiableList;

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
            categoryOptionCombos(safeUnmodifiableList(categoryOptionCombos()));
            return autoBuild();
        }
    }
}

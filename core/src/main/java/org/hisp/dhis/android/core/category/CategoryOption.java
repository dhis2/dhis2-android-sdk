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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseNameableObject;
import org.hisp.dhis.android.core.data.api.Field;

import java.util.Date;
import java.util.List;

@AutoValue
@JsonDeserialize(builder = AutoValue_CategoryOption.Builder.class)
public abstract class CategoryOption extends BaseNameableObject {
    private static final String CATEGORY_OPTION_COMBOS = "categoryOptionCombos";
    private static final String START_DATE = "startDate";
    private static final String END_DATE = "endDate";

    public static final Field<CategoryOption, String> uid = Field.create(UID);
    public static final Field<CategoryOption, String> code = Field.create(CODE);
    public static final Field<CategoryOption, String> name = Field.create(NAME);
    public static final Field<CategoryOption, String> displayName = Field.create(DISPLAY_NAME);
    public static final Field<CategoryOption, String> created = Field.create(CREATED);
    public static final Field<CategoryOption, String> lastUpdated = Field.create(LAST_UPDATED);
    public static final Field<CategoryOption, Boolean> deleted = Field.create(DELETED);
    public static final Field<CategoryOption, String> shortName = Field.create(SHORT_NAME);
    public static final Field<CategoryOption, String> displayShortName = Field.create(DISPLAY_SHORT_NAME);


    @Nullable
    @JsonProperty(CATEGORY_OPTION_COMBOS)
    public abstract List<CategoryOptionCombo> categoryOptionCombos();

    @Nullable
    @JsonProperty(START_DATE)
    public abstract Date startDate();

    @Nullable
    @JsonProperty(END_DATE)
    public abstract Date endDate();

    public static Builder builder() {
        return new AutoValue_CategoryOption.Builder();
    }

    @JsonCreator
    public static CategoryOption create(
            @JsonProperty(UID) String uid,
            @JsonProperty(CODE) String code,
            @JsonProperty(NAME) String name,
            @JsonProperty(DISPLAY_NAME) String displayName,
            @JsonProperty(CREATED) Date created,
            @JsonProperty(LAST_UPDATED) Date lastUpdated
    ) {
        return builder().uid(uid).code(code).name(name).displayName(displayName).created(
                created).lastUpdated(lastUpdated).build();
    }

    @AutoValue.Builder
    public static abstract class Builder extends BaseNameableObject.Builder<Builder> {

        @JsonProperty(CATEGORY_OPTION_COMBOS)
        public abstract Builder categoryOptionCombos(
                @Nullable List<CategoryOptionCombo> categoryOptionCombos);

        @JsonProperty(START_DATE)
        public abstract Builder startDate(@Nullable Date startDate);

        @JsonProperty(END_DATE)
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

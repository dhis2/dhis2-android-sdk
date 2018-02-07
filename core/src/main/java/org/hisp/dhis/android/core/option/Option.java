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

package org.hisp.dhis.android.core.option;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.api.Field;
import org.hisp.dhis.android.core.data.api.NestedField;

@AutoValue
@JsonDeserialize(builder = AutoValue_Option.Builder.class)
public abstract class Option extends BaseIdentifiableObject {
    private static final String OPTION_SET = "optionSet";

    public static final Field<Option, String> uid = Field.create(UID);
    public static final Field<Option, String> code = Field.create(CODE);
    public static final Field<Option, String> name = Field.create(NAME);
    public static final Field<Option, String> displayName = Field.create(DISPLAY_NAME);
    public static final Field<Option, String> created = Field.create(CREATED);
    public static final Field<Option, String> lastUpdated = Field.create(LAST_UPDATED);
    public static final Field<Option, Boolean> deleted = Field.create(DELETED);
    public static final NestedField<Option, OptionSet> optionSet = NestedField.create(OPTION_SET);

    @Nullable
    @JsonProperty(OPTION_SET)
    public abstract OptionSet optionSet();

    abstract Option.Builder toBuilder();

    static Option.Builder builder() {
        return new AutoValue_Option.Builder();
    }

    @AutoValue.Builder
    public static abstract class Builder extends BaseIdentifiableObject.Builder<Option.Builder> {
        @JsonProperty(OPTION_SET)
        public abstract Option.Builder optionSet(OptionSet optionSet);

        public abstract Option build();
    }
}

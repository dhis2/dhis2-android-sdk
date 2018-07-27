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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.data.api.Field;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.api.NestedField;

import java.util.Date;

@AutoValue
public abstract class Option extends BaseIdentifiableObject {
    private final static String SORT_ORDER = "sortOrder";
    private static final String OPTION_SET = "optionSet";
    private final static String STYLE = "style";

    public static final Field<Option, String> uid = Field.create(UID);
    public static final Field<Option, String> code = Field.create(CODE);
    public static final Field<Option, String> name = Field.create(NAME);
    public static final Field<Option, String> displayName = Field.create(DISPLAY_NAME);
    public static final Field<Option, String> created = Field.create(CREATED);
    public static final Field<Option, String> lastUpdated = Field.create(LAST_UPDATED);
    public static final Field<Option, Boolean> deleted = Field.create(DELETED);
    public static final Field<Option, Integer> sortOrder = Field.create(SORT_ORDER);
    public static final NestedField<Option, OptionSet> optionSet = NestedField.create(OPTION_SET);
    public static final NestedField<Option, ObjectStyle> style = NestedField.create(STYLE);

    static final Fields<Option> allFields = Fields.<Option>builder().fields(
            uid, code, name, displayName, created, lastUpdated, deleted, sortOrder, optionSet.with(OptionSet.uid),
            style.with(ObjectStyle.allFields)).build();

    @Nullable
    @JsonProperty(SORT_ORDER)
    public abstract Integer sortOrder();

    @Nullable
    @JsonProperty(OPTION_SET)
    public abstract OptionSet optionSet();

    @Nullable
    @JsonProperty(STYLE)
    public abstract ObjectStyle style();

    @JsonCreator
    public static Option create(
            @JsonProperty(UID) String uid,
            @JsonProperty(CODE) String code,
            @JsonProperty(NAME) String name,
            @JsonProperty(DISPLAY_NAME) String displayName,
            @JsonProperty(CREATED) Date created,
            @JsonProperty(LAST_UPDATED) Date lastUpdated,
            @JsonProperty(SORT_ORDER) Integer sortOrder,
            @JsonProperty(OPTION_SET) OptionSet optionSet,
            @JsonProperty(STYLE) ObjectStyle style,
            @JsonProperty(DELETED) Boolean deleted) {
        return new AutoValue_Option(uid, code, name, displayName, created, lastUpdated, deleted, sortOrder, optionSet,
                style);
    }

}

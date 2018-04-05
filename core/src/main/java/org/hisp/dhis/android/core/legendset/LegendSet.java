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

package org.hisp.dhis.android.core.legendset;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.api.Field;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.api.NestedField;

import java.util.Date;
import java.util.List;

@AutoValue
public abstract class LegendSet extends BaseIdentifiableObject {
    private final static String SYMBOLIZER = "symbolizer";
    private final static String LEGENDS = "legends";

    private static final Field<LegendSet, String> uid = Field.create(UID);
    private static final Field<LegendSet, String> code = Field.create(CODE);
    private static final Field<LegendSet, String> name = Field.create(NAME);
    private static final Field<LegendSet, String> displayName = Field.create(DISPLAY_NAME);
    private static final Field<LegendSet, String> created = Field.create(CREATED);
    private static final Field<LegendSet, String> lastUpdated = Field.create(LAST_UPDATED);
    private static final Field<LegendSet, String> deleted = Field.create(DELETED);
    private static final Field<LegendSet, String> symbolizer = Field.create(SYMBOLIZER);
    private static final NestedField<LegendSet, Legend> legends = NestedField.create(LEGENDS);

    public static final Fields<LegendSet> allFields = Fields.<LegendSet>builder().fields(
            uid, code, name, displayName, created, lastUpdated, deleted, symbolizer, legends.with(Legend.allFields))
            .build();

    @Nullable
    @JsonProperty(SYMBOLIZER)
    public abstract String symbolizer();

    @Nullable
    @JsonProperty(LEGENDS)
    public abstract List<Legend> legends();

    @JsonCreator
    static LegendSet create(
            @JsonProperty(UID) String uid,
            @JsonProperty(CODE) String code,
            @JsonProperty(NAME) String name,
            @JsonProperty(DISPLAY_NAME) String displayName,
            @JsonProperty(CREATED) Date created,
            @JsonProperty(LAST_UPDATED) Date lastUpdated,
            @JsonProperty(DELETED) Boolean deleted,
            @JsonProperty(SYMBOLIZER) String symbolizer,
            @JsonProperty(LEGENDS) List<Legend> legends) {

        return new AutoValue_LegendSet(uid, code, name, displayName, created, lastUpdated, deleted, symbolizer,
                legends);
    }
}

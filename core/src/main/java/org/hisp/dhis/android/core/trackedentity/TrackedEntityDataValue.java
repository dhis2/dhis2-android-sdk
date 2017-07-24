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

package org.hisp.dhis.android.core.trackedentity;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.data.api.Field;

import java.util.Date;

@AutoValue
public abstract class TrackedEntityDataValue {
    private final static String DATA_ELEMENT = "dataElement";
    private final static String STORED_BY = "storedBy";
    private final static String VALUE = "value";
    private final static String CREATED = "created";
    private final static String LAST_UPDATED = "lastUpdated";
    private final static String PROVIDED_ELSEWHERE = "providedElsewhere";

    public static final Field<TrackedEntityDataValue, String> dataElement = Field.create(DATA_ELEMENT);
    public static final Field<TrackedEntityDataValue, String> storedBy = Field.create(STORED_BY);
    public static final Field<TrackedEntityDataValue, String> value = Field.create(VALUE);
    public static final Field<TrackedEntityDataValue, Date> created = Field.create(CREATED);
    public static final Field<TrackedEntityDataValue, Date> lastUpdated = Field.create(LAST_UPDATED);
    public static final Field<TrackedEntityDataValue, Boolean> providedElsewhere = Field.create(PROVIDED_ELSEWHERE);

    @Nullable
    @JsonProperty(CREATED)
    public abstract Date created();

    @Nullable
    @JsonProperty(LAST_UPDATED)
    public abstract Date lastUpdated();

    @Nullable
    @JsonProperty(DATA_ELEMENT)
    public abstract String dataElement();

    @Nullable
    @JsonProperty(STORED_BY)
    public abstract String storedBy();

    @Nullable
    @JsonProperty(VALUE)
    public abstract String value();

    @Nullable
    @JsonProperty(PROVIDED_ELSEWHERE)
    public abstract Boolean providedElsewhere();

    @JsonCreator
    public static TrackedEntityDataValue create(
            @JsonProperty(CREATED) Date created,
            @JsonProperty(LAST_UPDATED) Date lastUpdated,
            @JsonProperty(DATA_ELEMENT) String dataElement,
            @JsonProperty(STORED_BY) String storedBy,
            @JsonProperty(VALUE) String value,
            @JsonProperty(PROVIDED_ELSEWHERE) Boolean providedElsewhere
    ) {
        return new AutoValue_TrackedEntityDataValue(
                created, lastUpdated, dataElement, storedBy, value, providedElsewhere
        );
    }
}

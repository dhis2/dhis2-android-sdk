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
import org.hisp.dhis.android.core.data.api.Fields;

import java.util.Date;

@AutoValue
public abstract class TrackedEntityAttributeValue {
    private static final String ATTRIBUTE = "attribute";
    private static final String VALUE = "value";
    private final static String CREATED = "created";
    private final static String LAST_UPDATED = "lastUpdated";

    private static final Field<TrackedEntityAttributeValue, String> trackedEntityAttribute =
            Field.create(ATTRIBUTE);
    private static final Field<TrackedEntityAttributeValue, String> value = Field.create(VALUE);
    private static final Field<TrackedEntityAttributeValue, Date> created = Field.create(CREATED);
    private static final Field<TrackedEntityAttributeValue, Date> lastUpdated = Field.create(
            LAST_UPDATED);

    public static final Fields<TrackedEntityAttributeValue> allFields = Fields
            .<TrackedEntityAttributeValue>builder().fields(
            trackedEntityAttribute, value, created, lastUpdated
    ).build();

    @Nullable
    @JsonProperty(ATTRIBUTE)
    public abstract String trackedEntityAttribute();

    @Nullable
    @JsonProperty(VALUE)
    public abstract String value();

    @Nullable
    @JsonProperty(CREATED)
    public abstract Date created();

    @Nullable
    @JsonProperty(LAST_UPDATED)
    public abstract Date lastUpdated();

    @JsonCreator
    public static TrackedEntityAttributeValue create(
            @JsonProperty(ATTRIBUTE) String attribute,
            @JsonProperty(VALUE) String value,
            @JsonProperty(CREATED) Date created,
            @JsonProperty(LAST_UPDATED) Date lastUpdated) {
        return new AutoValue_TrackedEntityAttributeValue(attribute, value, created, lastUpdated);
    }
}

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
public abstract class TrackedEntityAttributeReservedValue {
    private final static String OWNER_OBJECT = "ownerObject";
    private final static String OWNER_UID = "ownerUid";
    private final static String KEY = "key";
    private final static String VALUE = "value";
    private final static String CREATED = "created";
    private final static String EXPIRY_DATE = "expiryDate";

    private static final Field<TrackedEntityAttributeReservedValue, String> ownerObject = Field.create(OWNER_OBJECT);
    private static final Field<TrackedEntityAttributeReservedValue, String> ownerUid = Field.create(OWNER_UID);
    private static final Field<TrackedEntityAttributeReservedValue, String> key = Field.create(KEY);
    private static final Field<TrackedEntityAttributeReservedValue, String> value = Field.create(VALUE);
    private static final Field<TrackedEntityAttributeReservedValue, String> created = Field.create(CREATED);
    private static final Field<TrackedEntityAttributeReservedValue, String> expiryDate = Field.create(EXPIRY_DATE);

    static final Fields<TrackedEntityAttributeReservedValue> allFields = Fields.<TrackedEntityAttributeReservedValue>builder().fields(
            ownerObject, ownerUid, key, value, created, expiryDate).build();

    @Nullable
    @JsonProperty(OWNER_OBJECT)
    public abstract String ownerObject();

    @Nullable
    @JsonProperty(OWNER_UID)
    public abstract String ownerUid();

    @Nullable
    @JsonProperty(KEY)
    public abstract String key();

    @Nullable
    @JsonProperty(VALUE)
    public abstract String value();

    @Nullable
    @JsonProperty(CREATED)
    public abstract Date created();

    @Nullable
    @JsonProperty(EXPIRY_DATE)
    public abstract Date expiryDate();

    @JsonCreator
    public static TrackedEntityAttributeReservedValue create(
            @JsonProperty(OWNER_OBJECT) String ownerObject,
            @JsonProperty(OWNER_UID) String ownerUid,
            @JsonProperty(KEY) String key,
            @JsonProperty(VALUE) String value,
            @JsonProperty(CREATED) Date created,
            @JsonProperty(EXPIRY_DATE) Date expiryDate) {

        return new AutoValue_ReservedValue(ownerObject, ownerUid, key, value, created, expiryDate);
    }
}

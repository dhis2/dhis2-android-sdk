/*
 * Copyright (c) 2016, University of Oslo
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

package org.hisp.dhis.client.models.common;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.annotation.Nullable;

public abstract class BaseIdentifiableObject extends BaseModel implements IdentifiableObject {
    /* date format which should be used for all Date instances
    within models which extend BaseIdentifiableObject */
    public static final DateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US);

    private static final int UID_LENGTH = 11;

    private static final String JSON_PROPERTY_UID = "id";
    private static final String JSON_PROPERTY_CODE = "code";
    private static final String JSON_PROPERTY_NAME = "name";
    private static final String JSON_PROPERTY_DISPLAY_NAME = "displayName";
    private static final String JSON_PROPERTY_CREATED = "created";
    private static final String JSON_PROPERTY_LAST_UPDATED = "lastUpdated";

    @Override
    @JsonProperty(JSON_PROPERTY_UID)
    public abstract String uid();

    @Override
    @Nullable
    @JsonProperty(JSON_PROPERTY_CODE)
    public abstract String code();

    @Override
    @Nullable
    @JsonProperty(JSON_PROPERTY_NAME)
    public abstract String name();

    @Override
    @Nullable
    @JsonProperty(JSON_PROPERTY_DISPLAY_NAME)
    public abstract String displayName();

    @Override
    @Nullable
    @JsonProperty(JSON_PROPERTY_CREATED)
    public abstract Date created();

    @Override
    @Nullable
    @JsonProperty(JSON_PROPERTY_LAST_UPDATED)
    public abstract Date lastUpdated();

    @Override
    public boolean isValid() {
        // check if properties are null or not
        if (created() == null || lastUpdated() == null) {
            return false;
        }

        // check uid length which must be 11 characters long
        if (uid().length() != UID_LENGTH) {
            return false;
        }

        return true;
    }

    public static abstract class Builder<T extends Builder> extends BaseModel.Builder<T> {

        @JsonProperty(JSON_PROPERTY_UID)
        public abstract T uid(String uid);

        @JsonProperty(JSON_PROPERTY_CODE)
        public abstract T code(@Nullable String code);

        @JsonProperty(JSON_PROPERTY_NAME)
        public abstract T name(@Nullable String name);

        @JsonProperty(JSON_PROPERTY_DISPLAY_NAME)
        public abstract T displayName(@Nullable String displayName);

        @JsonProperty(JSON_PROPERTY_CREATED)
        public abstract T created(@Nullable Date created);

        @JsonProperty(JSON_PROPERTY_LAST_UPDATED)
        public abstract T lastUpdated(@Nullable Date lastUpdated);
    }
}

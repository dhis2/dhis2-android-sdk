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

package org.hisp.dhis.android.core.common;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public abstract class BaseIdentifiableObject implements IdentifiableObject {
    /* date format which should be used for all Date instances
    within models which extend BaseIdentifiableObject */
    public static final SafeDateFormat DATE_FORMAT = new SafeDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

    protected static final String UID = "id";
    protected static final String CODE = "code";
    protected static final String NAME = "name";
    protected static final String DISPLAY_NAME = "displayName";
    protected static final String CREATED = "created";
    protected static final String LAST_UPDATED = "lastUpdated";
    protected static final String DELETED = "deleted";

    @Override
    @JsonProperty(UID)
    public abstract String uid();

    @Override
    @Nullable
    @JsonProperty(CODE)
    public abstract String code();

    @Override
    @Nullable
    @JsonProperty(NAME)
    public abstract String name();

    @Override
    @Nullable
    @JsonProperty(DISPLAY_NAME)
    public abstract String displayName();

    @Override
    @Nullable
    @JsonProperty(CREATED)
    public abstract Date created();

    @Override
    @Nullable
    @JsonProperty(LAST_UPDATED)
    public abstract Date lastUpdated();

    @Nullable
    @JsonProperty(DELETED)
    public abstract Boolean deleted();

    protected static abstract class Builder<T extends Builder> {

        @JsonProperty(UID)
        public abstract T uid(String uid);

        @JsonProperty(CODE)
        public abstract T code(@Nullable String code);

        @JsonProperty(NAME)
        public abstract T name(@Nullable String name);

        @JsonProperty(DISPLAY_NAME)
        public abstract T displayName(@Nullable String displayName);

        @JsonProperty(CREATED)
        public abstract T created(@Nullable Date created);

        @JsonProperty(LAST_UPDATED)
        public abstract T lastUpdated(@Nullable Date lastUpdated);

        @JsonProperty(DELETED)
        public abstract T deleted(@Nullable Boolean deleted);
    }
}

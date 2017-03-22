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

package org.hisp.dhis.android.core.systeminfo;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.data.api.Field;

import java.util.Date;

@AutoValue
@JsonDeserialize(builder = AutoValue_SystemInfo.Builder.class)
public abstract class SystemInfo {
    private static final String SERVER_DATE_TIME = "serverDate";
    private static final String DATE_FORMAT = "dateFormat";
    private static final String VERSION = "version";
    private static final String CONTEXT_PATH = "contextPath";

    public static final Field<SystemInfo, String> serverDateTime = Field.create(SERVER_DATE_TIME);
    public static final Field<SystemInfo, String> dateFormat = Field.create(DATE_FORMAT);
    public static final Field<SystemInfo, String> version = Field.create(VERSION);
    public static final Field<SystemInfo, String> contextPath = Field.create(CONTEXT_PATH);

    @Nullable
    @JsonProperty(SERVER_DATE_TIME)
    public abstract Date serverDate();

    @Nullable
    @JsonProperty(DATE_FORMAT)
    public abstract String dateFormat();

    @Nullable
    @JsonProperty(VERSION)
    public abstract String version();

    @Nullable
    @JsonProperty(CONTEXT_PATH)
    public abstract String contextPath();

    @AutoValue.Builder
    public static abstract class Builder {

        @JsonProperty(SERVER_DATE_TIME)
        public abstract Builder serverDate(@Nullable Date serverDate);

        @JsonProperty(DATE_FORMAT)
        public abstract Builder dateFormat(@Nullable String dateFormat);

        @JsonProperty(VERSION)
        public abstract Builder version(@Nullable String version);

        @JsonProperty(CONTEXT_PATH)
        public abstract Builder contextPath(@Nullable String contextPath);

        public abstract SystemInfo build();
    }
}

/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.sms.data.webapirepository.internal;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;

import java.util.Date;
import java.util.List;

@AutoValue
@JsonDeserialize(builder = AutoValue_MetadataResponse.Builder.class)
public abstract class MetadataResponse {
    public abstract MetadataSystemInfo system();

    @Nullable
    public abstract List<MetadataId> categoryOptionCombos();

    @Nullable
    public abstract List<MetadataId> organisationUnits();

    @Nullable
    public abstract List<MetadataId> dataElements();

    @Nullable
    public abstract List<MetadataId> users();

    @Nullable
    public abstract List<MetadataId> trackedEntityTypes();

    @Nullable
    public abstract List<MetadataId> trackedEntityAttributes();

    @Nullable
    public abstract List<MetadataId> programs();

    @AutoValue
    @JsonDeserialize(builder = AutoValue_MetadataResponse_MetadataSystemInfo.Builder.class)
    public abstract static class MetadataSystemInfo {
        public abstract Date date();

        @AutoValue.Builder
        @JsonPOJOBuilder(withPrefix = "")
        public static abstract class Builder {

            public abstract MetadataSystemInfo.Builder date(Date date);

            public abstract MetadataSystemInfo build();
        }
    }

    @AutoValue
    @JsonDeserialize(builder = AutoValue_MetadataResponse_MetadataId.Builder.class)
    public abstract static class MetadataId {
        public abstract String id();

        @AutoValue.Builder
        @JsonPOJOBuilder(withPrefix = "")
        public static abstract class Builder {

            public abstract MetadataId.Builder id(String id);

            public abstract MetadataId build();
        }
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public static abstract class Builder {

        public abstract MetadataResponse.Builder system(MetadataSystemInfo systemInfo);

        public abstract MetadataResponse.Builder categoryOptionCombos(List<MetadataId> ids);

        public abstract MetadataResponse.Builder organisationUnits(List<MetadataId> ids);

        public abstract MetadataResponse.Builder dataElements(List<MetadataId> ids);

        public abstract MetadataResponse.Builder users(List<MetadataId> ids);

        public abstract MetadataResponse.Builder trackedEntityTypes(List<MetadataId> ids);

        public abstract MetadataResponse.Builder trackedEntityAttributes(List<MetadataId> ids);

        public abstract MetadataResponse.Builder programs(List<MetadataId> ids);

        public abstract MetadataResponse build();
    }
}
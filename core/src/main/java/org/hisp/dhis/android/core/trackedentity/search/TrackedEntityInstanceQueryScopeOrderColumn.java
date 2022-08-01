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

package org.hisp.dhis.android.core.trackedentity.search;

import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class TrackedEntityInstanceQueryScopeOrderColumn {

    enum Type {
        CREATED, LAST_UPDATED, ATTRIBUTE, ORGUNIT_NAME,
        ENROLLMENT_DATE, INCIDENT_DATE, ENROLLMENT_STATUS,
        EVENT_DATE, COMPLETION_DATE
    }

    public static final TrackedEntityInstanceQueryScopeOrderColumn CREATED =
            builder().type(Type.CREATED).apiName("created").build();

    public static final TrackedEntityInstanceQueryScopeOrderColumn LAST_UPDATED =
            builder().type(Type.LAST_UPDATED).apiName("lastupdated").build();

    public static final TrackedEntityInstanceQueryScopeOrderColumn ORGUNIT_NAME =
            builder().type(Type.ORGUNIT_NAME).apiName("ouname").build();

    public static final TrackedEntityInstanceQueryScopeOrderColumn ENROLLMENT_DATE =
            builder().type(Type.ENROLLMENT_DATE).build();

    public static final TrackedEntityInstanceQueryScopeOrderColumn INCIDENT_DATE =
            builder().type(Type.INCIDENT_DATE).build();

    public static final TrackedEntityInstanceQueryScopeOrderColumn COMPLETION_DATE =
            builder().type(Type.COMPLETION_DATE).build();

    public static final TrackedEntityInstanceQueryScopeOrderColumn EVENT_DATE =
            builder().type(Type.EVENT_DATE).build();

    public static final TrackedEntityInstanceQueryScopeOrderColumn ENROLLMENT_STATUS =
            builder().type(Type.ENROLLMENT_STATUS).build();

    public static TrackedEntityInstanceQueryScopeOrderColumn attribute(String attributeId) {
        return builder().type(Type.ATTRIBUTE).apiName(attributeId).value(attributeId).build();
    }

    public abstract Type type();

    @Nullable
    public abstract String apiName();

    @Nullable
    public abstract String value();

    public boolean hasApiName() {
        return apiName() != null;
    }

    public static Builder builder() {
        return new AutoValue_TrackedEntityInstanceQueryScopeOrderColumn.Builder();
    }

    @AutoValue.Builder
    abstract static class Builder {

        abstract Builder type(Type column);

        abstract Builder apiName(String apiName);

        abstract Builder value(String value);

        abstract TrackedEntityInstanceQueryScopeOrderColumn build();
    }
}

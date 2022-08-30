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

package org.hisp.dhis.android.core.event.search;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;

import java.util.Arrays;
import java.util.List;

@AutoValue
abstract class EventQueryScopeOrderColumn {

    enum Type {
        EVENT, PROGRAM, PROGRAM_STAGE,
        ENROLLMENT, ENROLLMENT_STATUS,
        ORGUNIT, ORGUNIT_NAME, TRACKED_ENTITY_INSTANCE, FOLLOW_UP, STATUS,
        EVENT_DATE, DUE_DATE, STORED_BY,
        CREATED, LAST_UPDATED, COMPLETED_BY, COMPLETED_DATE,
        TIMELINE, DATA_ELEMENT
    }

    public static final EventQueryScopeOrderColumn EVENT =
            builder().type(Type.EVENT).apiName("event").build();

    public static final EventQueryScopeOrderColumn PROGRAM =
            builder().type(Type.PROGRAM).apiName("program").build();

    public static final EventQueryScopeOrderColumn PROGRAM_STAGE =
            builder().type(Type.PROGRAM_STAGE).apiName("programStage").build();

    public static final EventQueryScopeOrderColumn ENROLLMENT =
            builder().type(Type.ENROLLMENT).apiName("enrollment").build();

    public static final EventQueryScopeOrderColumn ENROLLMENT_STATUS =
            builder().type(Type.ENROLLMENT_STATUS).apiName("enrollmentStatus").build();

    public static final EventQueryScopeOrderColumn ORGUNIT =
            builder().type(Type.ORGUNIT).apiName("orgUnit").build();

    public static final EventQueryScopeOrderColumn ORGUNIT_NAME =
            builder().type(Type.ORGUNIT_NAME).apiName("orgUnitName").build();

    public static final EventQueryScopeOrderColumn TRACKED_ENTITY_INSTANCE =
            builder().type(Type.TRACKED_ENTITY_INSTANCE).apiName("trackedEntityInstance").build();

    public static final EventQueryScopeOrderColumn EVENT_DATE =
            builder().type(Type.EVENT_DATE).apiName("eventDate").build();

    public static final EventQueryScopeOrderColumn FOLLOW_UP =
            builder().type(Type.FOLLOW_UP).apiName("followup").build();

    public static final EventQueryScopeOrderColumn STATUS =
            builder().type(Type.STATUS).apiName("status").build();

    public static final EventQueryScopeOrderColumn DUE_DATE =
            builder().type(Type.DUE_DATE).apiName("dueDate").build();

    public static final EventQueryScopeOrderColumn STORED_BY =
            builder().type(Type.STORED_BY).apiName("storedBy").build();

    public static final EventQueryScopeOrderColumn CREATED =
            builder().type(Type.CREATED).apiName("created").build();

    public static final EventQueryScopeOrderColumn LAST_UPDATED =
            builder().type(Type.LAST_UPDATED).apiName("lastUpdated").build();

    public static final EventQueryScopeOrderColumn COMPLETED_BY =
            builder().type(Type.COMPLETED_BY).apiName("completedBy").build();

    public static final EventQueryScopeOrderColumn COMPLETED_DATE =
            builder().type(Type.COMPLETED_DATE).apiName("completedDate").build();

    public static final EventQueryScopeOrderColumn TIMELINE =
            builder().type(Type.TIMELINE).build();

    static List<EventQueryScopeOrderColumn> fixedOrderColumns = Arrays.asList(
            EVENT, PROGRAM, PROGRAM_STAGE,
            ENROLLMENT, ENROLLMENT_STATUS,
            ORGUNIT, ORGUNIT_NAME, TRACKED_ENTITY_INSTANCE, FOLLOW_UP, STATUS,
            EVENT_DATE, DUE_DATE, STORED_BY,
            CREATED, LAST_UPDATED, COMPLETED_BY, COMPLETED_DATE,
            TIMELINE);

    public static EventQueryScopeOrderColumn dataElement(String dataElementUid) {
        return builder().type(Type.DATA_ELEMENT).apiName(dataElementUid).value(dataElementUid).build();
    }

    @NonNull
    public abstract Type type();

    @Nullable
    public abstract String apiName();

    @Nullable
    public abstract String value();

    public boolean hasApiName() {
        return apiName() != null;
    }

    public static Builder builder() {
        return new AutoValue_EventQueryScopeOrderColumn.Builder();
    }

    @AutoValue.Builder
    abstract static class Builder {

        abstract Builder type(Type column);

        abstract Builder apiName(String apiName);

        abstract Builder value(String value);

        abstract EventQueryScopeOrderColumn build();
    }
}

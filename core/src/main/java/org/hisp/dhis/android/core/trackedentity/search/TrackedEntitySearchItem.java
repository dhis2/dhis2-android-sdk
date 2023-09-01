/*
 *  Copyright (c) 2004-2023, University of Oslo
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.Geometry;
import org.hisp.dhis.android.core.common.ObjectWithUidInterface;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;

import java.util.Date;
import java.util.List;

@AutoValue
public abstract class TrackedEntitySearchItem implements ObjectWithUidInterface {
    @Override
    public abstract String uid();

    @Nullable
    public abstract Date created();

    @Nullable
    public abstract Date lastUpdated();

    @Nullable
    public abstract Date createdAtClient();

    @Nullable
    public abstract Date lastUpdatedAtClient();

    @Nullable
    public abstract String organisationUnit();

    @Nullable
    public abstract String trackedEntityType();

    @Nullable
    public abstract Geometry geometry();

    @Nullable
    public abstract List<TrackedEntityAttributeValue> trackedEntityAttributeValues();

    @Nullable
    public abstract State syncState();

    @Nullable
    public abstract State aggregatedSyncState();

    @Nullable
    public abstract Boolean deleted();

    @Nullable
    public abstract String header();

    public static TrackedEntitySearchItem.Builder builder() {
        return new AutoValue_TrackedEntitySearchItem.Builder();
    }

    public abstract TrackedEntitySearchItem.Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder uid(String uid);

        public abstract Builder created(Date created);

        public abstract Builder lastUpdated(Date lastUpdated);

        public abstract Builder createdAtClient(Date createdAtClient);

        public abstract Builder lastUpdatedAtClient(Date lastUpdatedAtClient);

        public abstract Builder organisationUnit(String organisationUnit);

        public abstract Builder trackedEntityType(String trackedEntityType);

        public abstract Builder geometry(Geometry geometry);

        public abstract Builder trackedEntityAttributeValues(
                List<TrackedEntityAttributeValue> trackedEntityAttributeValues);

        public abstract Builder syncState(State state);

        public abstract Builder aggregatedSyncState(State state);

        public abstract Builder deleted(Boolean deleted);

        public abstract Builder header(String header);

        public abstract TrackedEntitySearchItem build();
    }
}

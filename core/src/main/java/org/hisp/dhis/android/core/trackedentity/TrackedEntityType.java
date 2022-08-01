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

package org.hisp.dhis.android.core.trackedentity;

import android.database.Cursor;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.AccessColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.FeatureTypeColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreTrackedEntityTypeAttributeListColumnAdapter;
import org.hisp.dhis.android.core.arch.helpers.AccessHelper;
import org.hisp.dhis.android.core.common.Access;
import org.hisp.dhis.android.core.common.BaseNameableObject;
import org.hisp.dhis.android.core.common.CoreObject;
import org.hisp.dhis.android.core.common.FeatureType;
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.common.ObjectWithStyle;

import java.util.List;

@AutoValue
@JsonDeserialize(builder = $$AutoValue_TrackedEntityType.Builder.class)
public abstract class TrackedEntityType extends BaseNameableObject implements CoreObject,
        ObjectWithStyle<TrackedEntityType, TrackedEntityType.Builder> {

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreTrackedEntityTypeAttributeListColumnAdapter.class)
    public abstract List<TrackedEntityTypeAttribute> trackedEntityTypeAttributes();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(FeatureTypeColumnAdapter.class)
    public abstract FeatureType featureType();

    @JsonProperty()
    @ColumnAdapter(AccessColumnAdapter.class)
    public abstract Access access();

    public static Builder builder() {
        return new $$AutoValue_TrackedEntityType.Builder();
    }

    public static TrackedEntityType create(Cursor cursor) {
        return $AutoValue_TrackedEntityType.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public static abstract class Builder extends BaseNameableObject.Builder<Builder>
            implements ObjectWithStyle.Builder<TrackedEntityType, Builder> {

        public abstract Builder id(Long id);

        public abstract Builder trackedEntityTypeAttributes(List<TrackedEntityTypeAttribute> trackedEntityAttributes);

        public abstract Builder featureType(FeatureType featureType);

        public abstract Builder access(Access access);

        abstract TrackedEntityType autoBuild();

        // Auxiliary fields
        abstract ObjectStyle style();
        abstract Access access();

        public TrackedEntityType build() {
            try {
                style();
            } catch (IllegalStateException e) {
                style(ObjectStyle.builder().build());
            }

            try {
                access();
            } catch (IllegalStateException e) {
                access(AccessHelper.defaultAccess());
            }

            return autoBuild();
        }
    }
}
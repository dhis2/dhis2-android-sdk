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

package org.hisp.dhis.android.core.relationship;

import android.database.Cursor;

import androidx.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.StringListColumnAdapter;
import org.hisp.dhis.android.core.common.BaseObject;

import java.util.Collections;
import java.util.List;

@AutoValue
public abstract class TrackerDataView extends BaseObject {

    @Nullable
    @ColumnName(RelationshipConstraintTableInfo.Columns.TRACKER_DATA_VIEW_ATTRIBUTES)
    @ColumnAdapter(StringListColumnAdapter.class)
    public abstract List<String> attributes();

    @Nullable
    @ColumnName(RelationshipConstraintTableInfo.Columns.TRACKER_DATA_VIEW_DATA_ELEMENTS)
    @ColumnAdapter(StringListColumnAdapter.class)
    public abstract List<String> dataElements();

    public static TrackerDataView create(Cursor cursor) {
        return $AutoValue_TrackerDataView.createFromCursor(cursor);
    }

    public static Builder builder() {
        return new AutoValue_TrackerDataView.Builder();
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder extends BaseObject.Builder<Builder> {

        public abstract Builder attributes(List<String> attributes);

        public abstract Builder dataElements(List<String> dataElements);

        abstract TrackerDataView autoBuild();

        //Auxiliary fields
        abstract List<String> attributes();

        abstract List<String> dataElements();

        public TrackerDataView build() {

            try {
                attributes();
            } catch (IllegalStateException e) {
                attributes(Collections.emptyList());
            }

            try {
                dataElements();
            } catch (IllegalStateException e) {
                dataElements(Collections.emptyList());
            }

            return autoBuild();
        }

    }
}

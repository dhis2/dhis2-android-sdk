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

package org.hisp.dhis.android.core.visualization;

import android.database.Cursor;

import androidx.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.TrackerVisualizationDimensionRepetitionColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.LayoutPositionColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.identifiable.internal.ObjectWithUidColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.identifiable.internal.ObjectWithUidListColumnAdapter;
import org.hisp.dhis.android.core.common.CoreObject;
import org.hisp.dhis.android.core.common.ObjectWithUid;

import java.util.List;

@AutoValue
public abstract class TrackerVisualizationDimension implements CoreObject {

    @Nullable
    public abstract String trackerVisualization();

    @Nullable
    @ColumnAdapter(LayoutPositionColumnAdapter.class)
    public abstract LayoutPosition position();

    @Nullable
    public abstract String dimension();

    @Nullable
    public abstract String dimensionType();

    @Nullable
    @ColumnAdapter(ObjectWithUidColumnAdapter.class)
    public abstract ObjectWithUid program();

    @Nullable
    @ColumnAdapter(ObjectWithUidColumnAdapter.class)
    public abstract ObjectWithUid programStage();

    @Nullable
    @ColumnAdapter(ObjectWithUidListColumnAdapter.class)
    public abstract List<ObjectWithUid> items();

    @Nullable
    public abstract String filter();

    @Nullable
    @ColumnAdapter(TrackerVisualizationDimensionRepetitionColumnAdapter.class)
    public abstract TrackerVisualizationDimensionRepetition repetition();

    @Nullable
    public abstract Integer sortOrder();

    public static Builder builder() {
        return new AutoValue_TrackerVisualizationDimension.Builder();
    }

    public static TrackerVisualizationDimension create(Cursor cursor) {
        return $AutoValue_TrackerVisualizationDimension.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder trackerVisualization(String trackerVisualization);

        public abstract Builder position(LayoutPosition position);

        public abstract Builder dimension(String dimension);

        public abstract Builder dimensionType(String dimensionType);

        public abstract Builder program(ObjectWithUid program);

        public abstract Builder programStage(ObjectWithUid programStage);

        public abstract Builder items(List<ObjectWithUid> items);

        public abstract Builder filter(String filter);

        public abstract Builder repetition(TrackerVisualizationDimensionRepetition repetition);

        public abstract Builder sortOrder(Integer sortOrder);

        public abstract TrackerVisualizationDimension build();
    }
}
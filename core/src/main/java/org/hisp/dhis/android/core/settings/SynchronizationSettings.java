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

package org.hisp.dhis.android.core.settings;

import android.database.Cursor;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.DataSyncPeriodColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.MetadataSyncPeriodColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.TrackerImporterVersionColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.DataSetSyncSettingsColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.ProgramSyncSettingsColumnAdapter;
import org.hisp.dhis.android.core.common.CoreObject;
import org.hisp.dhis.android.core.tracker.TrackerImporterVersion;

@AutoValue
@JsonDeserialize(builder = $$AutoValue_SynchronizationSettings.Builder.class)
public abstract class SynchronizationSettings implements CoreObject {

    @Nullable
    @ColumnAdapter(DataSyncPeriodColumnAdapter.class)
    public abstract DataSyncPeriod dataSync();

    @Nullable
    @ColumnAdapter(MetadataSyncPeriodColumnAdapter.class)
    public abstract MetadataSyncPeriod metadataSync();

    @Nullable
    @ColumnAdapter(TrackerImporterVersionColumnAdapter.class)
    public abstract TrackerImporterVersion trackerImporterVersion();

    @Nullable
    @ColumnAdapter(DataSetSyncSettingsColumnAdapter.class)
    public abstract DataSetSettings dataSetSettings();

    @Nullable
    @ColumnAdapter(ProgramSyncSettingsColumnAdapter.class)
    public abstract ProgramSettings programSettings();

    public static SynchronizationSettings create(Cursor cursor) {
        return $AutoValue_SynchronizationSettings.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new $AutoValue_SynchronizationSettings.Builder()
                .dataSetSettings(DataSetSettings.builder().build())
                .programSettings(ProgramSettings.builder().build());
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder {
        public abstract Builder id(Long id);

        public abstract Builder dataSync(DataSyncPeriod dataSync);

        public abstract Builder metadataSync(MetadataSyncPeriod metadataSync);

        public abstract Builder trackerImporterVersion(TrackerImporterVersion trackerImporterVersion);

        public abstract Builder dataSetSettings(DataSetSettings dataSetSettings);

        public abstract Builder programSettings(ProgramSettings programSettings);

        public abstract SynchronizationSettings build();
    }
}
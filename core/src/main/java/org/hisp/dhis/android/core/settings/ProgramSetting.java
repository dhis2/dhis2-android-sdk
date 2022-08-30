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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.DbDateColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.DownloadPeriodColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.EnrollmentScopeColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.LimitScopeColumnAdapter;
import org.hisp.dhis.android.core.common.CoreObject;

import java.util.Date;

import static org.hisp.dhis.android.core.common.BaseIdentifiableObject.UID;

@AutoValue
@JsonDeserialize(builder = $$AutoValue_ProgramSetting.Builder.class)
public abstract class ProgramSetting implements CoreObject {

    @Nullable
    @JsonProperty(UID)
    public abstract String uid();

    @Nullable
    @JsonProperty()
    public abstract String name();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date lastUpdated();

    @Nullable
    @JsonProperty()
    public abstract Integer teiDownload();

    @Nullable
    @JsonProperty()
    public abstract Integer teiDBTrimming();

    @Nullable
    @JsonProperty()
    public abstract Integer eventsDownload();

    @Nullable
    @JsonProperty()
    public abstract Integer eventsDBTrimming();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DownloadPeriodColumnAdapter.class)
    public abstract DownloadPeriod updateDownload();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DownloadPeriodColumnAdapter.class)
    public abstract DownloadPeriod updateDBTrimming();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(LimitScopeColumnAdapter.class)
    public abstract LimitScope settingDownload();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(LimitScopeColumnAdapter.class)
    public abstract LimitScope settingDBTrimming();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(EnrollmentScopeColumnAdapter.class)
    public abstract EnrollmentScope enrollmentDownload();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(EnrollmentScopeColumnAdapter.class)
    public abstract EnrollmentScope enrollmentDBTrimming();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DownloadPeriodColumnAdapter.class)
    public abstract DownloadPeriod eventDateDownload();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DownloadPeriodColumnAdapter.class)
    public abstract DownloadPeriod eventDateDBTrimming();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DownloadPeriodColumnAdapter.class)
    public abstract DownloadPeriod enrollmentDateDownload();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DownloadPeriodColumnAdapter.class)
    public abstract DownloadPeriod enrollmentDateDBTrimming();

    public static ProgramSetting create(Cursor cursor) {
        return $AutoValue_ProgramSetting.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new $AutoValue_ProgramSetting.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder {
        public abstract Builder id(Long id);

        @JsonProperty(UID)
        public abstract Builder uid(String uid);

        public abstract Builder name(String name);

        public abstract Builder lastUpdated(Date lastUpdated);

        public abstract Builder teiDownload(Integer teiDownload);

        public abstract Builder teiDBTrimming(Integer teiDBTrimming);

        public abstract Builder eventsDownload(Integer eventsDownload);

        public abstract Builder eventsDBTrimming(Integer eventsDBTrimming);

        public abstract Builder updateDownload(DownloadPeriod updateDownload);

        public abstract Builder updateDBTrimming(DownloadPeriod updateDBTrimming);

        public abstract Builder settingDownload(LimitScope settingDownload);

        public abstract Builder settingDBTrimming(LimitScope settingDBTrimming);

        public abstract Builder enrollmentDownload(EnrollmentScope enrollmentDownload);

        public abstract Builder enrollmentDBTrimming(EnrollmentScope enrollmentDBTrimming);

        public abstract Builder eventDateDownload(DownloadPeriod eventPeriodDownload);

        public abstract Builder eventDateDBTrimming(DownloadPeriod eventPeriodDBTrimming);

        public abstract Builder enrollmentDateDownload(DownloadPeriod enrollmentDateDownload);

        public abstract Builder enrollmentDateDBTrimming(DownloadPeriod enrollmentDateDBTrimming);

        public abstract ProgramSetting build();
    }
}
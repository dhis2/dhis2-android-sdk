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

package org.hisp.dhis.android.core.settings;

import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.CoreObject;
import org.hisp.dhis.android.core.common.ObjectWithUid;

import java.util.Date;
import java.util.List;

@AutoValue
public abstract class ProgramSetting implements CoreObject {

    @Nullable
    public abstract String uid();

    @Nullable
    public abstract String name();

    @Nullable
    public abstract List<ObjectWithUid> filters();

    @Nullable
    public abstract Date lastUpdated();

    @Nullable
    public abstract Integer teiDownload();

    @Nullable
    public abstract Integer teiDBTrimming();

    @Nullable
    public abstract Integer eventsDownload();

    @Nullable
    public abstract Integer eventsDBTrimming();

    @Nullable
    public abstract DownloadPeriod updateDownload();

    @Nullable
    public abstract DownloadPeriod updateDBTrimming();

    @Nullable
    public abstract LimitScope settingDownload();

    @Nullable
    public abstract LimitScope settingDBTrimming();

    @Nullable
    public abstract EnrollmentScope enrollmentDownload();

    @Nullable
    public abstract EnrollmentScope enrollmentDBTrimming();

    @Nullable
    public abstract DownloadPeriod eventDateDownload();

    @Nullable
    public abstract DownloadPeriod eventDateDBTrimming();

    @Nullable
    public abstract DownloadPeriod enrollmentDateDownload();

    @Nullable
    public abstract DownloadPeriod enrollmentDateDBTrimming();

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new AutoValue_ProgramSetting.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder uid(String uid);

        public abstract Builder name(String name);

        public abstract Builder filters(List<ObjectWithUid> filters);

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

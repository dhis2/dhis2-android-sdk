/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;

@AutoValue
@JsonDeserialize(builder = AutoValue_ProgramSettingsItem.Builder.class)
public abstract class ProgramSettingsItem {

    @JsonProperty
    public abstract String id();

    @JsonProperty()
    public abstract String lastUpdated();

    @JsonProperty()
    public abstract Integer teiDownload();

    @JsonProperty()
    public abstract Integer teiDBTrimming();

    @JsonProperty()
    public abstract Integer eventsDownload();

    @JsonProperty()
    public abstract Integer eventsDBTrimming();

    @JsonProperty()
    public abstract DownloadPeriod updateDownload();

    @JsonProperty()
    public abstract DownloadPeriod updateDBTrimming();

    @JsonProperty()
    public abstract LimitScope settingDownload();

    @JsonProperty()
    public abstract LimitScope settingDBTrimming();

    @JsonProperty()
    public abstract EnrollmentScope enrollmentDownload();

    @JsonProperty()
    public abstract EnrollmentScope enrollmentDBTrimming();

    @JsonProperty()
    public abstract Integer teReservedDownload();

    @JsonProperty()
    public abstract Integer teReservedDBTrimming();

    @JsonProperty()
    public abstract DownloadPeriod eventPeriodDownload();

    @JsonProperty()
    public abstract DownloadPeriod eventPeriodDBTrimming();

    @JsonProperty()
    public abstract DownloadPeriod enrollmentDateDownload();

    @JsonProperty()
    public abstract DownloadPeriod enrollmentDateDBTrimming();

    public static Builder builder() {
        return new AutoValue_ProgramSettingsItem.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder {
        public abstract Builder id(String id);

        public abstract Builder lastUpdated(String lastUpdated);

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

        public abstract Builder teReservedDownload(Integer teReservedDownload);

        public abstract Builder teReservedDBTrimming(Integer teReservedDBTrimming);

        public abstract Builder eventPeriodDownload(DownloadPeriod eventPeriodDownload);

        public abstract Builder eventPeriodDBTrimming(DownloadPeriod eventPeriodDBTrimming);

        public abstract Builder enrollmentDateDownload(DownloadPeriod enrollmentDateDownload);

        public abstract Builder enrollmentDateDBTrimming(DownloadPeriod enrollmentDateDBTrimming);

        public abstract ProgramSettingsItem build();
    }
}
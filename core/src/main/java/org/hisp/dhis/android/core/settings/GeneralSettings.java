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

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.DbDateColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreDataSyncPeriodColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreMetadataSyncPeriodColumnAdapter;
import org.hisp.dhis.android.core.common.CoreObject;

import java.util.Date;

@AutoValue
@JsonDeserialize(builder = $$AutoValue_GeneralSettings.Builder.class)
public abstract class GeneralSettings implements CoreObject {

    /**
     * @deprecated Use {@link SynchronizationSettings#dataSync()} instead.
     */
    @Deprecated
    @Nullable
    @ColumnAdapter(IgnoreDataSyncPeriodColumnAdapter.class)
    public abstract DataSyncPeriod dataSync();

    public abstract Boolean encryptDB();

    @Nullable
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date lastUpdated();

    /**
     * @deprecated Use {@link SynchronizationSettings#metadataSync()} instead.
     */
    @Deprecated
    @Nullable
    @ColumnAdapter(IgnoreMetadataSyncPeriodColumnAdapter.class)
    public abstract MetadataSyncPeriod metadataSync();

    @Nullable
    public abstract Integer reservedValues();

    /**
     * @deprecated Use {@link #smsGateway()} instead.
     */
    @Deprecated
    @Nullable
    public String numberSmsToSend() {
        return smsGateway();
    }

    /**
     * @deprecated Use {@link #smsResultSender()} instead.
     */
    @Deprecated
    @Nullable
    public String numberSmsConfirmation() {
        return smsResultSender();
    }

    @Nullable
    public abstract String smsGateway();

    @Nullable
    public abstract String smsResultSender();

    @Nullable
    public abstract Integer matomoID();

    @Nullable
    public abstract String matomoURL();

    @Nullable
    public abstract Boolean allowScreenCapture();

    @Nullable
    public abstract String messageOfTheDay();

    public static GeneralSettings create(Cursor cursor) {
        return $AutoValue_GeneralSettings.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new $AutoValue_GeneralSettings.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder {
        public abstract Builder id(Long id);

        @Deprecated
        public abstract Builder dataSync(DataSyncPeriod dataSync);

        public abstract Builder encryptDB(Boolean encryptDB);

        public abstract Builder lastUpdated(Date lastUpdated);

        @Deprecated
        public abstract Builder metadataSync(MetadataSyncPeriod metadataSync);

        public abstract Builder reservedValues(Integer reservedValues);

        @JsonAlias("numberSmsToSend")
        public abstract Builder smsGateway(String smsGateway);

        @JsonAlias("numberSmsConfirmation")
        public abstract Builder smsResultSender(String smsGateway);

        public abstract Builder matomoID(Integer matomoID);

        public abstract Builder matomoURL(String matomoURL);

        public abstract Builder allowScreenCapture(Boolean allowScreenCapture);

        public abstract Builder messageOfTheDay(String messageOfTheDay);

        public abstract GeneralSettings build();
    }
}
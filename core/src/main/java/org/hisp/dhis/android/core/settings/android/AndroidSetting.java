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

package org.hisp.dhis.android.core.settings.android;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.DataSyncPeriodColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.MetadataSyncPeriodColumnAdapter;
import org.hisp.dhis.android.core.common.BaseObject;

@AutoValue
@JsonDeserialize(builder = $AutoValue_AndroidSetting.Builder.class)
public abstract class AndroidSetting extends BaseObject {

    @JsonProperty()
    public abstract Boolean loading();

    @JsonProperty()
    @ColumnAdapter(DataSyncPeriodColumnAdapter.class)
    public abstract DataSyncPeriod dataSync();

    @JsonProperty()
    public abstract Boolean encryptDB();

    @JsonProperty()
    public abstract Boolean isUpdated();

    @JsonProperty()
    public abstract Integer valuesTEI();

    @JsonProperty()
    public abstract String lastUpdated();

    @JsonProperty()
    @ColumnAdapter(MetadataSyncPeriodColumnAdapter.class)
    public abstract MetadataSyncPeriod metadataSync();

    @JsonProperty()
    public abstract String numberSmsToSend();

    @JsonProperty()
    public abstract Boolean errorConfirmation();

    @JsonProperty()
    public abstract String numberSmsConfirmation();

    public static Builder builder() {
        return new $AutoValue_AndroidSetting.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder {
        public abstract Builder id(Long id);

        public abstract Builder loading(Boolean loading);

        public abstract Builder dataSync(DataSyncPeriod dataSync);

        public abstract Builder encryptDB(Boolean encryptDB);

        public abstract Builder isUpdated(Boolean isUpdated);

        public abstract Builder valuesTEI(Integer valuesTEI);

        public abstract Builder lastUpdated(String lastUpdated);

        public abstract Builder metadataSync(MetadataSyncPeriod metadataSync);

        public abstract Builder numberSmsToSend(String numberSmsToSend);

        public abstract Builder errorConfirmation(Boolean errorConfirmation);

        public abstract Builder numberSmsConfirmation(String numberSmsConfirmation);

        public abstract AndroidSetting build();
    }
}
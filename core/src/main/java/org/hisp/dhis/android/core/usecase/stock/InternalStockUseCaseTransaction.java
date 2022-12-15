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

package org.hisp.dhis.android.core.usecase.stock;

import static org.hisp.dhis.android.core.common.BaseIdentifiableObject.UID;

import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseObject;

@AutoValue
@JsonDeserialize(builder = $$AutoValue_InternalStockUseCaseTransaction.Builder.class)
public abstract class InternalStockUseCaseTransaction extends BaseObject {

    @Nullable
    public abstract String programUid();

    @NonNull
    @JsonProperty()
    public abstract Integer sortOrder();

    @NonNull
    @JsonProperty()
    public abstract String transactionType();

    @Nullable
    @JsonProperty()
    public abstract String distributedTo();

    @Nullable
    @JsonProperty()
    public abstract String stockDistributed();

    @Nullable
    @JsonProperty()
    public abstract String stockDiscarded();

    @Nullable
    @JsonProperty()
    public abstract String stockCorrected();

    public static InternalStockUseCaseTransaction create(Cursor cursor) {
        return AutoValue_InternalStockUseCaseTransaction.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new $$AutoValue_InternalStockUseCaseTransaction.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public static abstract class Builder extends BaseObject.Builder<Builder> {

        @JsonProperty(UID)
        public abstract Builder programUid(String programUid);

        public abstract Builder sortOrder(Integer sortOrder);

        public abstract Builder transactionType(String transactionType);

        public abstract Builder distributedTo(String distributedTo);

        public abstract Builder stockDistributed(String stockDistributed);

        public abstract Builder stockDiscarded(String stockDiscarded);

        public abstract Builder stockCorrected(String stockCorrected);

        public abstract InternalStockUseCaseTransaction build();
    }
}

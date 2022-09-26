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

package org.hisp.dhis.android.core.programtheme.stock;

import static org.hisp.dhis.android.core.common.BaseIdentifiableObject.UID;

import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreBooleanColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreInternalStockThemeTransactionListColumnAdapter;
import org.hisp.dhis.android.core.common.BaseObject;
import org.hisp.dhis.android.core.common.ObjectWithDeleteInterface;
import org.hisp.dhis.android.core.common.ObjectWithUidInterface;

import java.util.List;

@AutoValue
@JsonDeserialize(builder = $$AutoValue_InternalStockTheme.Builder.class)
public abstract class InternalStockTheme extends BaseObject
        implements ObjectWithUidInterface, ObjectWithDeleteInterface {

    public static final String TRANSACTIONS = "transactions";

    @Override
    @NonNull
    @JsonProperty("programUid")
    public abstract String uid();

    @NonNull
    @JsonProperty()
    public abstract String itemCode();

    @NonNull
    @JsonProperty()
    public abstract String itemDescription();

    @NonNull
    @JsonProperty()
    public abstract String programType();

    @NonNull
    @JsonProperty()
    public abstract String description();

    @NonNull
    @JsonProperty()
    public abstract String stockOnHand();

    @Nullable
    @JsonIgnore()
    @ColumnAdapter(IgnoreBooleanColumnAdapter.class)
    public abstract Boolean deleted();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreInternalStockThemeTransactionListColumnAdapter.class)
    public abstract List<InternalStockThemeTransaction> transactions();

    public static InternalStockTheme create(Cursor cursor) {
        return AutoValue_InternalStockTheme.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new $$AutoValue_InternalStockTheme.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public static abstract class Builder extends BaseObject.Builder<Builder> {

        @JsonProperty("programUid")
        public abstract Builder uid(String uid);

        public abstract Builder itemCode(String itemCode);

        public abstract Builder itemDescription(String itemDescription);

        public abstract Builder programType(String programType);

        public abstract Builder description(String description);

        public abstract Builder stockOnHand(String stockOnHand);

        public abstract Builder deleted(Boolean deleted);

        public abstract Builder transactions(List<InternalStockThemeTransaction> transactions);

        public abstract InternalStockTheme build();
    }
}
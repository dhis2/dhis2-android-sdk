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

package org.hisp.dhis.android.core.dataapproval;

import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.DataApprovalStateColumnAdapter;
import org.hisp.dhis.android.core.common.BaseObject;
import org.hisp.dhis.android.core.dataapproval.internal.DataApprovalFields;

@AutoValue
@JsonDeserialize(builder = $$AutoValue_DataApproval.Builder.class)
public abstract class DataApproval extends BaseObject {

    @JsonProperty(DataApprovalFields.WORKFLOW)
    public abstract String workflow();

    @JsonProperty(DataApprovalFields.ORGANISATION_UNIT)
    public abstract String organisationUnit();

    @JsonProperty(DataApprovalFields.PERIOD)
    public abstract String period();

    @JsonProperty(DataApprovalFields.ATTRIBUTE_OPTION_COMBO)
    public abstract String attributeOptionCombo();

    @Nullable
    @JsonProperty
    @ColumnAdapter(DataApprovalStateColumnAdapter.class)
    public abstract DataApprovalState state();

    @NonNull
    public static DataApproval create(Cursor cursor) {
        return AutoValue_DataApproval.createFromCursor(cursor);
    }

    public static DataApproval.Builder builder() {
        return new $$AutoValue_DataApproval.Builder();
    }

    public abstract DataApproval.Builder toBuilder();


    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder extends BaseObject.Builder<DataApproval.Builder> {

        @JsonProperty(DataApprovalFields.WORKFLOW)
        public abstract Builder workflow(@NonNull String workflow);

        @JsonProperty(DataApprovalFields.ORGANISATION_UNIT)
        public abstract Builder organisationUnit(@NonNull String organisationUnit);

        @JsonProperty(DataApprovalFields.PERIOD)
        public abstract Builder period(@NonNull String period);

        @JsonProperty(DataApprovalFields.ATTRIBUTE_OPTION_COMBO)
        public abstract Builder attributeOptionCombo(@NonNull String attributeOptionCombo);

        public abstract Builder state(@NonNull DataApprovalState state);

        public abstract DataApproval build();
    }



}

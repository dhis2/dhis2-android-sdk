/*
 * Copyright (c) 2004-2018, University of Oslo
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

package org.hisp.dhis.android.core.dataset;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.data.api.Field;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.api.NestedField;
import org.hisp.dhis.android.core.data.database.DbDateColumnAdapter;

import java.util.Date;

@AutoValue
public abstract class DataInputPeriod {

    private static final String PERIOD = "period";
    private static final String OPENING_DATE = "openingDate";
    private static final String CLOSING_DATE = "closingDate";

    public static final NestedField<DataInputPeriod, ObjectWithUid> period = NestedField.create(PERIOD);
    public static final Field<DataInputPeriod, String> openingDate = Field.create(OPENING_DATE);
    public static final Field<DataInputPeriod, String> closingDate = Field.create(CLOSING_DATE);

    public static final Fields<DataInputPeriod> allFields = Fields.<DataInputPeriod>builder().fields(
            period.with(ObjectWithUid.uid),
            openingDate,
            closingDate
    ).build();

    @JsonProperty(PERIOD)
    public abstract ObjectWithUid period();

    String periodUid() {
        ObjectWithUid period = period();
        return period == null ? null : period.uid();
    }

    @Nullable
    @JsonProperty(OPENING_DATE)
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date openingDate();

    @Nullable
    @JsonProperty(CLOSING_DATE)
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date closingDate();

    @JsonCreator
    public static DataInputPeriod create(
            @JsonProperty(PERIOD) ObjectWithUid period,
            @JsonProperty(OPENING_DATE) Date openingDate,
            @JsonProperty(CLOSING_DATE) Date closingDate) {

        return new AutoValue_DataInputPeriod(period, openingDate, closingDate);
    }
}

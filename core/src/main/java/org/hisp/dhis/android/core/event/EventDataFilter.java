/*
 *  Copyright (c) 2004-2021, University of Oslo
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

package org.hisp.dhis.android.core.event;

import android.database.Cursor;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.DateFilterPeriodColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.StringSetColumnAdapter;
import org.hisp.dhis.android.core.common.CoreObject;
import org.hisp.dhis.android.core.common.DateFilterPeriod;

import java.util.Set;

@AutoValue
@JsonDeserialize(builder = $$AutoValue_EventDataFilter.Builder.class)
public abstract class EventDataFilter implements CoreObject {

    /**
     * The related event filter
     */
    @Nullable
    @JsonProperty()
    public abstract String eventFilter();

    /**
     * The data element id or data item
     */
    @Nullable
    @JsonProperty()
    public abstract String dataItem();

    /**
     * Less than or equal to
     */
    @Nullable
    @JsonProperty()
    public abstract String le();

    /**
     * Greater than or equal to
     */
    @Nullable
    @JsonProperty()
    public abstract String ge();

    /**
     * Greater than
     */
    @Nullable
    @JsonProperty()
    public abstract String gt();

    /**
     * Lesser than
     */
    @Nullable
    @JsonProperty()
    public abstract String lt();

    /**
     * Equal to
     */
    @Nullable
    @JsonProperty()
    public abstract String eq();

    /**
     * In a list
     */
    @Nullable
    @JsonProperty()
    @ColumnAdapter(StringSetColumnAdapter.class)
    @ColumnName(EventDataFilterTableInfo.Columns.IN)
    public abstract Set<String> in();

    /**
     * Like
     */
    @Nullable
    @JsonProperty()
    public abstract String like();

    /**
     * If the dataItem is of type date, then date filtering parameters are specified using this.
     */
    @Nullable
    @JsonProperty()
    @ColumnAdapter(DateFilterPeriodColumnAdapter.class)
    public abstract DateFilterPeriod dateFilter();

    public static Builder builder() {
        return new $$AutoValue_EventDataFilter.Builder();
    }

    public static EventDataFilter create(Cursor cursor) {
        return $AutoValue_EventDataFilter.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public static abstract class Builder {
        public abstract Builder id(Long id);

        public abstract Builder eventFilter(String eventFilter);

        public abstract Builder dataItem(String dataItem);

        public abstract Builder le(String le);

        public abstract Builder ge(String ge);

        public abstract Builder gt(String gt);

        public abstract Builder lt(String lt);

        public abstract Builder eq(String eq);

        public abstract Builder in(Set<String> in);

        public abstract Builder like(String like);

        public abstract Builder dateFilter(DateFilterPeriod dateFilter);

        public abstract EventDataFilter build();
    }
}
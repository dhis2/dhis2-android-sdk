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
import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.StringListColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.AssignedUserModeColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.EventStatusColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.OrganisationUnitModeColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreEventDataFilterListColumnAdapter;
import org.hisp.dhis.android.core.common.AssignedUserMode;
import org.hisp.dhis.android.core.common.CoreObject;
import org.hisp.dhis.android.core.common.DateFilterPeriod;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode;

import java.util.List;

@AutoValue
@JsonDeserialize(builder = $$AutoValue_EventQueryCriteria.Builder.class)
public abstract class EventQueryCriteria implements CoreObject {

    @Nullable
    @JsonProperty()
    public abstract Boolean followUp();

    @Nullable
    @JsonProperty()
    public abstract String organisationUnit();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(OrganisationUnitModeColumnAdapter.class)
    public abstract OrganisationUnitMode ouMode();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(AssignedUserModeColumnAdapter.class)
    public abstract AssignedUserMode assignedUserMode();

    @Nullable
    @JsonProperty()
    @ColumnName(EventFilterTableInfo.Columns.ORDER)
    public abstract String order();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(StringListColumnAdapter.class)
    public abstract List<String> displayColumnOrder();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreEventDataFilterListColumnAdapter.class)
    public abstract List<EventDataFilter> dataFilters();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(StringListColumnAdapter.class)
    public abstract List<String> events();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(EventStatusColumnAdapter.class)
    public abstract EventStatus eventStatus();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DateFilterPeriodColumnAdapter.class)
    public abstract DateFilterPeriod eventDate();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DateFilterPeriodColumnAdapter.class)
    public abstract DateFilterPeriod dueDate();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DateFilterPeriodColumnAdapter.class)
    public abstract DateFilterPeriod lastUpdatedDate();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DateFilterPeriodColumnAdapter.class)
    public abstract DateFilterPeriod completedDate();

    public static Builder builder() {
        return new $$AutoValue_EventQueryCriteria.Builder();
    }

    public static EventQueryCriteria create(Cursor cursor) {
        return $AutoValue_EventQueryCriteria.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public static abstract class Builder {
        public abstract Builder id(Long id);

        public abstract Builder followUp(Boolean followUp);

        public abstract Builder organisationUnit(String organisationUnit);

        public abstract Builder ouMode(OrganisationUnitMode ouMode);

        public abstract Builder assignedUserMode(AssignedUserMode assignedUserMode);

        public abstract Builder order(String order);

        public abstract Builder displayColumnOrder(List<String> displayColumnOrder);

        public abstract Builder dataFilters(List<EventDataFilter> dataFilters);

        public abstract Builder events(List<String> events);

        public abstract Builder eventStatus(EventStatus eventStatus);

        public abstract Builder eventDate(DateFilterPeriod eventDate);

        public abstract Builder dueDate(DateFilterPeriod dueDate);

        public abstract Builder lastUpdatedDate(DateFilterPeriod lastUpdatedDate);

        public abstract Builder completedDate(DateFilterPeriod completedDate);

        public abstract EventQueryCriteria build();
    }
}
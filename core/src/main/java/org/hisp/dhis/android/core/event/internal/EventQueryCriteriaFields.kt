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

package org.hisp.dhis.android.core.event.internal;

import org.hisp.dhis.android.core.arch.api.fields.internal.Fields;
import org.hisp.dhis.android.core.arch.fields.internal.FieldsHelper;
import org.hisp.dhis.android.core.common.DateFilterPeriod;
import org.hisp.dhis.android.core.event.EventDataFilter;
import org.hisp.dhis.android.core.event.EventFilterTableInfo.Columns;
import org.hisp.dhis.android.core.event.EventQueryCriteria;

public final class EventQueryCriteriaFields {

    public final static String DATA_FILTERS = "dataFilters";
    public final static String ORDER = "order";

    private static final FieldsHelper<EventQueryCriteria> fh = new FieldsHelper<>();

    public static final Fields<EventQueryCriteria> allFields = Fields.<EventQueryCriteria>builder()
            .fields(
                    fh.field(Columns.FOLLOW_UP),
                    fh.field(Columns.ORGANISATION_UNIT),
                    fh.field(Columns.OU_MODE),
                    fh.field(Columns.ASSIGNED_USER_MODE),
                    fh.field(ORDER),
                    fh.field(Columns.DISPLAY_COLUMN_ORDER),
                    fh.<EventDataFilter>nestedField(DATA_FILTERS).with(EventDataFilterFields.allFields),
                    fh.field(Columns.EVENTS),
                    fh.field(Columns.EVENT_STATUS),
                    fh.<DateFilterPeriod>nestedField(Columns.EVENT_DATE).with(DateFilterPeriodFields.allFields),
                    fh.<DateFilterPeriod>nestedField(Columns.DUE_DATE).with(DateFilterPeriodFields.allFields),
                    fh.<DateFilterPeriod>nestedField(Columns.LAST_UPDATED_DATE).with(DateFilterPeriodFields.allFields),
                    fh.<DateFilterPeriod>nestedField(Columns.COMPLETED_DATE).with(DateFilterPeriodFields.allFields)
            ).build();

    private EventQueryCriteriaFields() {
    }
}
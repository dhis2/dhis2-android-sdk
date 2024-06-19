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

package org.hisp.dhis.android.core.trackedentity.internal;

import org.hisp.dhis.android.core.arch.api.fields.internal.Field;
import org.hisp.dhis.android.core.arch.api.fields.internal.Fields;
import org.hisp.dhis.android.core.arch.fields.internal.FieldsHelper;
import org.hisp.dhis.android.core.common.Access;
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.common.internal.AccessFields;
import org.hisp.dhis.android.core.common.objectstyle.internal.ObjectStyleFields;
import org.hisp.dhis.android.core.legendset.LegendSet;
import org.hisp.dhis.android.core.legendset.internal.LegendSetFields;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeTableInfo;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeTableInfo.Columns;

public final class TrackedEntityAttributeFields {
    public static final String UNIQUE = "unique";
    public static final String LEGEND_SETS = "legendSets";
    private static final String STYLE = "style";
    private static final String ACCESS = "access";
    public static final String ORG_UNIT_SCOPE = "orgunitScope";

    private static final FieldsHelper<TrackedEntityAttribute> fh = new FieldsHelper<>();

    public static final Fields<TrackedEntityAttribute> allFields = Fields.<TrackedEntityAttribute>builder()
            .fields(fh.getNameableFields())
            .fields(
                    fh.field(Columns.PATTERN),
                    fh.field(Columns.SORT_ORDER_IN_LIST_NO_PROGRAM),
                    fh.field(Columns.VALUE_TYPE),
                    fh.field(Columns.EXPRESSION),
                    fh.field(Columns.PROGRAM_SCOPE),
                    fh.field(TrackedEntityAttributeTableInfo.Columns.AGGREGATION_TYPE),
                    fh.field(Columns.DISPLAY_IN_LIST_NO_PROGRAM),
                    fh.field(Columns.GENERATED),
                    fh.field(Columns.DISPLAY_ON_VISIT_SCHEDULE),
                    fh.field(Columns.ORG_UNIT_SCOPE),
                    fh.field(UNIQUE),
                    fh.field(Columns.INHERIT),
                    fh.field(Columns.FIELD_MASK),
                    fh.<LegendSet>nestedField(LEGEND_SETS).with(LegendSetFields.uid),
                    fh.nestedFieldWithUid(Columns.OPTION_SET),
                    fh.<ObjectStyle>nestedField(STYLE).with(ObjectStyleFields.allFields),
                    fh.<Access>nestedField(ACCESS).with(AccessFields.read),
                    fh.field(Columns.FORM_NAME),
                    fh.field(Columns.DISPLAY_FORM_NAME),
                    fh.field(Columns.CONFIDENTIAL)
                    ).build();
    static final Field<TrackedEntityAttribute> uid = fh.uid();
    static final Field<TrackedEntityAttribute> lastUpdated = fh.lastUpdated();

    private TrackedEntityAttributeFields() {
    }
}
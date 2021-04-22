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

package org.hisp.dhis.android.core.program.internal;

import org.hisp.dhis.android.core.arch.api.fields.internal.Fields;
import org.hisp.dhis.android.core.arch.api.fields.internal.Property;
import org.hisp.dhis.android.core.arch.fields.internal.FieldsHelper;
import org.hisp.dhis.android.core.common.AggregationType;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.legendset.LegendSet;
import org.hisp.dhis.android.core.legendset.internal.LegendSetFields;
import org.hisp.dhis.android.core.program.ProgramIndicator;
import org.hisp.dhis.android.core.program.ProgramIndicatorTableInfo.Columns;

import static org.hisp.dhis.android.core.common.BaseIdentifiableObject.UID;

public final class ProgramIndicatorFields {
    public static final String LEGEND_SETS = "legendSets";

    private static final FieldsHelper<ProgramIndicator> fh = new FieldsHelper<>();

    static final Property<ProgramIndicator, Boolean> uid = fh.field(UID);

    public static final Fields<ProgramIndicator> allFields = Fields.<ProgramIndicator>builder()
            .fields(fh.getNameableFields())
            .fields(
                    fh.<Boolean>field(Columns.DISPLAY_IN_FORM),
                    fh.<String>field(Columns.EXPRESSION),
                    fh.<String>field(Columns.DIMENSION_ITEM),
                    fh.<String>field(Columns.FILTER),
                    fh.<Integer>field(Columns.DECIMALS),
                    fh.<AggregationType>field(Columns.AGGREGATION_TYPE),
                    fh.<ObjectWithUid>nestedField(Columns.PROGRAM).with(ObjectWithUid.uid),
                    fh.<LegendSet>nestedField(LEGEND_SETS).with(LegendSetFields.allFields)
            ).build();

    private ProgramIndicatorFields() {
    }
}
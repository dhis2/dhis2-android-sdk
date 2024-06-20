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
package org.hisp.dhis.android.core.program.internal

import org.hisp.dhis.android.core.arch.api.fields.internal.BaseFields
import org.hisp.dhis.android.core.arch.api.fields.internal.Fields
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.legendset.LegendSet
import org.hisp.dhis.android.core.legendset.internal.LegendSetFields
import org.hisp.dhis.android.core.program.AnalyticsPeriodBoundary
import org.hisp.dhis.android.core.program.ProgramIndicator
import org.hisp.dhis.android.core.program.ProgramIndicatorTableInfo.Columns

internal object ProgramIndicatorFields : BaseFields<ProgramIndicator>() {
    const val ANALYTICS_PERIOD_BOUNDARIES = "analyticsPeriodBoundaries"
    const val LEGEND_SETS = "legendSets"

    val uid = fh.uid()
    val displayInForm = fh.field("displayInForm")

    @JvmField
    val allFields = Fields.from(
        fh.getNameableFields(),
        fh.field(Columns.DISPLAY_IN_FORM),
        fh.field(Columns.EXPRESSION),
        fh.field(Columns.DIMENSION_ITEM),
        fh.field(Columns.FILTER),
        fh.field(Columns.DECIMALS),
        fh.field(Columns.AGGREGATION_TYPE),
        fh.field(Columns.ANALYTICS_TYPE),
        fh.nestedField<ObjectWithUid>(Columns.PROGRAM).with(ObjectWithUid.uid),
        fh.nestedField<AnalyticsPeriodBoundary>(ANALYTICS_PERIOD_BOUNDARIES)
            .with(AnalyticsPeriodBoundaryFields.allFields),
        fh.nestedField<LegendSet>(LEGEND_SETS).with(LegendSetFields.uid),
    )
}

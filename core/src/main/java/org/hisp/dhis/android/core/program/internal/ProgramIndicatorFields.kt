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
package org.hisp.dhis.android.core.program.internal

import org.hisp.dhis.android.core.arch.api.fields.internal.Field
import org.hisp.dhis.android.core.arch.api.fields.internal.Fields
import org.hisp.dhis.android.core.arch.fields.internal.FieldsHelper
import org.hisp.dhis.android.core.common.AggregationType
import org.hisp.dhis.android.core.common.AnalyticsType
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.legendset.LegendSet
import org.hisp.dhis.android.core.legendset.internal.LegendSetFields
import org.hisp.dhis.android.core.program.AnalyticsPeriodBoundary
import org.hisp.dhis.android.core.program.ProgramIndicator
import org.hisp.dhis.android.core.program.ProgramIndicatorTableInfo

object ProgramIndicatorFields {
    const val ANALYTICS_PERIOD_BOUNDARIES = "analyticsPeriodBoundaries"
    const val LEGEND_SETS = "legendSets"
    private val fh = FieldsHelper<ProgramIndicator>()
    val uid = fh.uid()
    val displayInForm: Field<ProgramIndicator, Boolean> = Field.create("displayInForm")

    @JvmField
    val allFields: Fields<ProgramIndicator> = Fields.builder<ProgramIndicator>()
        .fields(fh.getNameableFields())
        .fields(
            fh.field<Boolean>(ProgramIndicatorTableInfo.Columns.DISPLAY_IN_FORM),
            fh.field<String>(ProgramIndicatorTableInfo.Columns.EXPRESSION),
            fh.field<String>(ProgramIndicatorTableInfo.Columns.DIMENSION_ITEM),
            fh.field<String>(ProgramIndicatorTableInfo.Columns.FILTER),
            fh.field<Int>(ProgramIndicatorTableInfo.Columns.DECIMALS),
            fh.field<AggregationType>(ProgramIndicatorTableInfo.Columns.AGGREGATION_TYPE),
            fh.nestedField<ObjectWithUid>(ProgramIndicatorTableInfo.Columns.PROGRAM).with(ObjectWithUid.uid),
            fh.field<AnalyticsType>(ProgramIndicatorTableInfo.Columns.ANALYTICS_TYPE),
            fh.nestedField<AnalyticsPeriodBoundary>(ANALYTICS_PERIOD_BOUNDARIES)
                .with(AnalyticsPeriodBoundaryFields.allFields),
            fh.nestedField<LegendSet>(LEGEND_SETS).with(LegendSetFields.uid)
        ).build()
}

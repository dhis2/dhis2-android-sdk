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

package org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator

import javax.inject.Inject
import org.hisp.dhis.android.core.analytics.aggregated.MetadataItem
import org.hisp.dhis.android.core.analytics.aggregated.internal.AnalyticsServiceEvaluationItem
import org.hisp.dhis.android.core.common.AnalyticsType
import org.hisp.dhis.android.core.program.ProgramIndicator
import org.hisp.dhis.android.core.program.programindicatorengine.internal.ProgramIndicatorSQLExecutor
import org.hisp.dhis.android.core.program.programindicatorengine.internal.ProgramIndicatorSQLParams

internal class ProgramIndicatorSQLEvaluator @Inject constructor(
    private val programIndicatorSQLExecutor: ProgramIndicatorSQLExecutor
) : AnalyticsEvaluator {

    override fun evaluate(
        evaluationItem: AnalyticsServiceEvaluationItem,
        metadata: Map<String, MetadataItem>
    ): String? {
        return programIndicatorSQLExecutor.getProgramIndicatorValue(getParams(evaluationItem, metadata))
    }

    override fun getSql(
        evaluationItem: AnalyticsServiceEvaluationItem,
        metadata: Map<String, MetadataItem>
    ): String {
        return programIndicatorSQLExecutor.getProgramIndicatorSQL(getParams(evaluationItem, metadata))
    }

    private fun getParams(
        evaluationItem: AnalyticsServiceEvaluationItem,
        metadata: Map<String, MetadataItem>
    ): ProgramIndicatorSQLParams {
        val programIndicator = ProgramIndicatorEvaluatorHelper.getProgramIndicator(evaluationItem, metadata)
        val contextWhereClause = getContextWhereClause(programIndicator, evaluationItem, metadata)
        val periods = AnalyticsEvaluatorHelper.getReportingPeriods(evaluationItem.allDimensionItems, metadata)

        return ProgramIndicatorSQLParams(
            programIndicator = programIndicator,
            contextWhereClause = contextWhereClause,
            periods = periods
        )
    }

    private fun getContextWhereClause(
        programIndicator: ProgramIndicator,
        evaluationItem: AnalyticsServiceEvaluationItem,
        metadata: Map<String, MetadataItem>
    ): String {
        return when (programIndicator.analyticsType()) {
            AnalyticsType.EVENT ->
                ProgramIndicatorEvaluatorHelper.getEventWhereClause(programIndicator, evaluationItem, metadata)
            AnalyticsType.ENROLLMENT, null ->
                ProgramIndicatorEvaluatorHelper.getEnrollmentWhereClause(programIndicator, evaluationItem, metadata)
        }
    }
}

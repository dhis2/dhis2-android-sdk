/*
 *  Copyright (c) 2004-2024, University of Oslo
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

package org.hisp.dhis.android.core.analytics.trackerlinelist

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorIntegrationShould
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.attribute1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.dataElement1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.generator
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.orgunitChild1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.period201911
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.period201912
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.period202001
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.program
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.programStage1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.trackedEntity1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.trackedEntityType
import org.hisp.dhis.android.core.program.programindicatorengine.BaseTrackerDataIntegrationHelper
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
internal class TrackerLineListRepositoryIntegrationShould : BaseEvaluatorIntegrationShould() {

    private val helper = BaseTrackerDataIntegrationHelper(databaseAdapter)

    @Test
    fun evaluate_program_attributes() {
        helper.createTrackedEntity(trackedEntity1.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment1 = generator.generate()
        helper.createEnrollment(trackedEntity1.uid(), enrollment1, program.uid(), orgunitChild1.uid())
        helper.insertTrackedEntityAttributeValue(trackedEntity1.uid(), attribute1.uid(), "45")

        val result = d2.analyticsModule().trackerLineList()
            .withEnrollmentOutput(program.uid())
            .withColumn(TrackerLineListItem.OrganisationUnitItem(filters = emptyList()))
            .withColumn(
                TrackerLineListItem.ProgramAttribute(
                    uid = attribute1.uid(),
                    filters = listOf(
                        DataFilter.GreaterThan("40"),
                        DataFilter.LowerThan("50"),
                    ),
                ),
            )
            .blockingEvaluate()

        assertThat(result.getOrThrow().rows.size).isEqualTo(1)
    }

    @Test
    fun evaluate_repeated_data_elements() {
        helper.createTrackedEntity(trackedEntity1.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment1 = generator.generate()
        helper.createEnrollment(trackedEntity1.uid(), enrollment1, program.uid(), orgunitChild1.uid())
        val event1 = generator.generate()
        helper.createTrackerEvent(event1, enrollment1, program.uid(), programStage1.uid(), orgunitChild1.uid(), eventDate = period201911.startDate())
        val event2 = generator.generate()
        helper.createTrackerEvent(event2, enrollment1, program.uid(), programStage1.uid(), orgunitChild1.uid(), eventDate = period201912.startDate())
        val event3 = generator.generate()
        helper.createTrackerEvent(event3, enrollment1, program.uid(), programStage1.uid(), orgunitChild1.uid(), eventDate = period202001.startDate())

        helper.insertTrackedEntityDataValue(event1, dataElement1.uid(), "8")
        helper.insertTrackedEntityDataValue(event2, dataElement1.uid(), "19")
        helper.insertTrackedEntityDataValue(event3, dataElement1.uid(), "2")

        val result1 = d2.analyticsModule().trackerLineList()
            .withEnrollmentOutput(program.uid())
            .withColumn(
                TrackerLineListItem.ProgramDataElement(
                    dataElement = dataElement1.uid(),
                    program = program.uid(),
                    programStage = programStage1.uid(),
                    filters = listOf(
                        DataFilter.GreaterThan("15")
                    ),
                    repetitionIndexes = listOf(1, 2, 0, -1)
                )
            )
            .blockingEvaluate()

        val rows = result1.getOrThrow().rows
        assertThat(rows.size).isEqualTo(1)

        val row = rows.first()
        assertThat(row.size).isEqualTo(4)

    }
}

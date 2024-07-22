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
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.attribute2
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.attributeOptionCombo
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.category
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.categoryOption
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.dataElement1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.generator
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.orgunitChild1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.orgunitChild2
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.period201911
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.period201912
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.period202001
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.program
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.programStage1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.trackedEntity1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.trackedEntity2
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.trackedEntityType
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.arch.repositories.paging.PageConfig
import org.hisp.dhis.android.core.common.AnalyticsType
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.program.programindicatorengine.BaseTrackerDataIntegrationHelper
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Date

@RunWith(D2JunitRunner::class)
internal class TrackerLineListRepositoryEvaluatorShould : BaseEvaluatorIntegrationShould() {

    private val helper = BaseTrackerDataIntegrationHelper(databaseAdapter)

    @Test
    fun evaluate_program_attributes() {
        helper.createTrackedEntity(trackedEntity1.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment1 = generator.generate()
        createDefaultEnrollment(trackedEntity1.uid(), enrollment1)
        helper.insertTrackedEntityAttributeValue(trackedEntity1.uid(), attribute1.uid(), "45")

        val result = d2.analyticsModule().trackerLineList()
            .withEnrollmentOutput(program.uid())
            .withColumn(TrackerLineListItem.OrganisationUnitItem())
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
        createDefaultEnrollment(trackedEntity1.uid(), enrollment1)
        val event1 = generator.generate()
        createDefaultTrackerEvent(event1, enrollment1, eventDate = period202001.startDate())
        val event2 = generator.generate()
        createDefaultTrackerEvent(event2, enrollment1, eventDate = period201912.startDate())
        val event3 = generator.generate()
        createDefaultTrackerEvent(event3, enrollment1, eventDate = period201911.startDate())

        helper.insertTrackedEntityDataValue(event1, dataElement1.uid(), "8")
        helper.insertTrackedEntityDataValue(event2, dataElement1.uid(), "19")
        helper.insertTrackedEntityDataValue(event3, dataElement1.uid(), "2")

        val result1 = d2.analyticsModule().trackerLineList()
            .withEnrollmentOutput(program.uid())
            .withColumn(
                TrackerLineListItem.ProgramDataElement(
                    dataElement = dataElement1.uid(),
                    programStage = programStage1.uid(),
                    filters = listOf(
                        DataFilter.GreaterThan("15"),
                    ),
                    repetitionIndexes = listOf(1, 2, 0, -1),
                ),
            )
            .blockingEvaluate()

        val rows = result1.getOrThrow().rows
        assertThat(rows.size).isEqualTo(1)

        val row = rows.first()
        assertThat(row.size).isEqualTo(4)

        rows.first().forEachIndexed { index, value ->
            when (index) {
                0 -> assertThat(value.value).isEqualTo("2")
                1 -> assertThat(value.value).isEqualTo("19")
                2 -> assertThat(value.value).isEqualTo("19")
                3 -> assertThat(value.value).isEqualTo("8")
            }
        }
    }

    @Test
    fun evaluate_line_list_category() {
        helper.createTrackedEntity(trackedEntity1.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment1 = generator.generate()
        createDefaultEnrollment(trackedEntity1.uid(), enrollment1)
        val event1 = generator.generate()
        createDefaultTrackerEvent(event1, enrollment1, eventDate = period202001.startDate())
        val event2 = generator.generate()
        createDefaultTrackerEvent(event2, enrollment1, eventDate = period201912.startDate())
        val event3 = generator.generate()
        createDefaultTrackerEvent(event3, enrollment1, eventDate = period201911.startDate())

        val result = d2.analyticsModule().trackerLineList()
            .withEventOutput(programStage1.uid())
            .withColumn(TrackerLineListItem.OrganisationUnitItem())
            .withColumn(TrackerLineListItem.Category(category.uid()))
            .blockingEvaluate()

        val rows = result.getOrThrow().rows
        val firstRow = rows.first()

        assertThat(rows.size).isEqualTo(3)
        assertThat(firstRow[1].value).isEqualTo(categoryOption.displayName())

    }

    @Test
    fun should_filter_by_date() {
        helper.createTrackedEntity(trackedEntity1.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment1 = generator.generate()
        createDefaultEnrollment(trackedEntity1.uid(), enrollment1, enrollmentDate = period202001.startDate())
        helper.insertTrackedEntityAttributeValue(trackedEntity1.uid(), attribute1.uid(), "123")

        helper.createTrackedEntity(trackedEntity2.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment2 = generator.generate()
        createDefaultEnrollment(trackedEntity2.uid(), enrollment2, enrollmentDate = period201912.startDate())
        helper.insertTrackedEntityAttributeValue(trackedEntity2.uid(), attribute1.uid(), "789")

        // Filter by absolute value
        val result1 = d2.analyticsModule().trackerLineList()
            .withEnrollmentOutput(program.uid())
            .withColumn(TrackerLineListItem.ProgramAttribute(attribute1.uid()))
            .withColumn(
                TrackerLineListItem.EnrollmentDate(
                    filters = listOf(DateFilter.Absolute("2020")),
                ),
            )
            .blockingEvaluate()

        val rows1 = result1.getOrThrow().rows
        assertThat(rows1.size).isEqualTo(1)
        assertThat(rows1[0][0].value).isEqualTo("123")
        assertThat(rows1[0][1].value).isEqualTo("2020-01-01T00:00:00.000")

        // Filter by range
        val result2 = d2.analyticsModule().trackerLineList()
            .withEnrollmentOutput(program.uid())
            .withColumn(TrackerLineListItem.ProgramAttribute(attribute1.uid()))
            .withColumn(
                TrackerLineListItem.EnrollmentDate(
                    filters = listOf(
                        DateFilter.Range(
                            startDate = DateUtils.DATE_FORMAT.format(period201911.startDate()!!),
                            endDate = DateUtils.DATE_FORMAT.format(period201912.endDate()!!),
                        ),
                    ),
                ),
            )
            .blockingEvaluate()

        val rows2 = result2.getOrThrow().rows
        assertThat(rows2.size).isEqualTo(1)
        assertThat(rows2[0][0].value).isEqualTo("789")
        assertThat(rows2[0][1].value).isEqualTo("2019-12-01T00:00:00.000")
    }

    @Test
    fun evaluate_program_indicator() {
        helper.createTrackedEntity(trackedEntity1.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment1 = generator.generate()
        createDefaultEnrollment(trackedEntity1.uid(), enrollment1, enrollmentDate = period202001.startDate())
        helper.insertTrackedEntityAttributeValue(trackedEntity1.uid(), attribute1.uid(), "123")

        val event1 = generator.generate()
        createDefaultTrackerEvent(event1, enrollment1, eventDate = period202001.startDate())
        val event2 = generator.generate()
        createDefaultTrackerEvent(event2, enrollment1, eventDate = period201912.startDate())

        helper.insertTrackedEntityDataValue(event1, dataElement1.uid(), "5")
        helper.insertTrackedEntityDataValue(event2, dataElement1.uid(), "10")

        val programIndicator = generator.generate()
        helper.setProgramIndicatorExpression(
            programIndicator,
            program.uid(),
            expression = "A{${attribute1.uid()}} + #{${programStage1.uid()}.${dataElement1.uid()}}",
            analyticsType = AnalyticsType.EVENT,
        )

        val result = d2.analyticsModule().trackerLineList()
            .withEventOutput(programStage1.uid())
            .withColumn(TrackerLineListItem.EventDate())
            .withColumn(TrackerLineListItem.ProgramIndicator(programIndicator))
            .blockingEvaluate()

        val rows = result.getOrThrow().rows
        assertThat(rows.size).isEqualTo(2)
        assertThat(rows[0][0].value).isEqualTo(DateUtils.DATE_FORMAT.format(period202001.startDate()!!))
        assertThat(rows[0][1].value).isEqualTo("128")
        assertThat(rows[1][0].value).isEqualTo(DateUtils.DATE_FORMAT.format(period201912.startDate()!!))
        assertThat(rows[1][1].value).isEqualTo("133")
    }

    @Test
    fun evaluate_contains_functions() {
        helper.createTrackedEntity(trackedEntity1.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment1 = generator.generate()
        createDefaultEnrollment(trackedEntity1.uid(), enrollment1, enrollmentDate = period202001.startDate())
        helper.insertTrackedEntityAttributeValue(trackedEntity1.uid(), attribute2.uid(), "ALLERGY,LATEX")

        fun evaluateExpression(expression: String, expected: String) {
            val programIndicator = generator.generate()
            helper.setProgramIndicatorExpression(
                programIndicator,
                program.uid(),
                expression = expression,
                analyticsType = AnalyticsType.ENROLLMENT,
            )

            val result = d2.analyticsModule().trackerLineList()
                .withEnrollmentOutput(program.uid())
                .withColumn(TrackerLineListItem.ProgramIndicator(programIndicator))
                .blockingEvaluate()

            val rows = result.getOrThrow().rows
            assertThat(rows.size).isEqualTo(1)
            assertThat(rows.first().first().value).isEqualTo(expected)
        }

        evaluateExpression("if(contains(A{${attribute2.uid()}}, 'LATEX', 'ALLERGY'), '1', '0')", "1")
        evaluateExpression("if(contains(A{${attribute2.uid()}}, 'GY,LA'), '1', '0')", "1")
        evaluateExpression("if(contains(A{${attribute2.uid()}}, 'xy', 'ALLERGY'), '1', '0')", "0")

        evaluateExpression("if(containsItems(A{${attribute2.uid()}}, 'LATEX', 'ALLERGY'), '1', '0')", "1")
        evaluateExpression("if(containsItems(A{${attribute2.uid()}}, 'GY,LA'), '1', '0')", "0")
        evaluateExpression("if(containsItems(A{${attribute2.uid()}}, 'xy', 'ALLERGY'), '1', '0')", "0")
    }

    @Test
    fun evaluate_orgunit_filters() {
        val event1 = generator.generate()
        helper.createSingleEvent(event1, program.uid(), programStage1.uid(), orgunitChild1.uid())
        val event2 = generator.generate()
        helper.createSingleEvent(event2, program.uid(), programStage1.uid(), orgunitChild2.uid())

        val result = d2.analyticsModule().trackerLineList()
            .withEventOutput(programStage1.uid())
            .withColumn(
                TrackerLineListItem.OrganisationUnitItem(
                    filters = listOf(
                        OrganisationUnitFilter.Like(orgunitChild1.displayName()!!),
                    ),
                ),
            )
            .blockingEvaluate()

        val rows = result.getOrThrow().rows
        assertThat(rows.size).isEqualTo(1)
        assertThat(rows.first().first().value).isEqualTo(orgunitChild1.displayName())
    }

    @Test
    fun evaluate_single_events() {
        val event1 = generator.generate()
        helper.createSingleEvent(event1, program.uid(), programStage1.uid(), orgunitChild1.uid())
        val event2 = generator.generate()
        helper.createSingleEvent(event2, program.uid(), programStage1.uid(), orgunitChild1.uid())

        helper.insertTrackedEntityDataValue(event1, dataElement1.uid(), "5")
        helper.insertTrackedEntityDataValue(event2, dataElement1.uid(), "10")

        val result = d2.analyticsModule().trackerLineList()
            .withEventOutput(programStage1.uid())
            .withColumn(TrackerLineListItem.ProgramDataElement(dataElement1.uid(), programStage1.uid()))
            .blockingEvaluate()

        val rows = result.getOrThrow().rows
        assertThat(rows.size).isEqualTo(2)
        assertThat(rows.flatten().map { it.value }).containsExactly("5", "10")
    }

    @Test
    fun evaluate_shared_properties() {
        val event1 = generator.generate()
        helper.createSingleEvent(
            event1,
            program.uid(),
            programStage1.uid(),
            orgunitChild1.uid(),
            lastUpdated = DateUtils.SIMPLE_DATE_FORMAT.parse("2024-01-04"),
        )

        val result = d2.analyticsModule().trackerLineList()
            .withEventOutput(programStage1.uid())
            .withColumn(
                TrackerLineListItem.LastUpdated(
                    filters = listOf(
                        DateFilter.Range(startDate = "2024-01-01", endDate = "2024-01-31"),
                    ),
                ),
            )
            .withColumn(TrackerLineListItem.LastUpdatedBy)
            .withColumn(TrackerLineListItem.CreatedBy)
            .withColumn(TrackerLineListItem.OrganisationUnitItem())
            .blockingEvaluate()

        val rows = result.getOrThrow().rows
        assertThat(rows.size).isEqualTo(1)
    }

    @Test
    fun evaluate_paging() {
        val event1 = generator.generate()
        helper.createSingleEvent(event1, program.uid(), programStage1.uid(), orgunitChild1.uid())
        val event2 = generator.generate()
        helper.createSingleEvent(event2, program.uid(), programStage1.uid(), orgunitChild2.uid())

        val baseRepo = d2.analyticsModule().trackerLineList()
            .withEventOutput(programStage1.uid())
            .withColumn(TrackerLineListItem.OrganisationUnitItem())

        // Page 1
        val resultPage1 = baseRepo
            .withPageConfig(PageConfig.Paging(1, 1))
            .blockingEvaluate()

        assertThat(resultPage1.getOrThrow().rows.size).isEqualTo(1)
        assertThat(resultPage1.getOrThrow().rows.first().first().value).isEqualTo(orgunitChild1.displayName())

        // Page 2
        val resultPage2 = baseRepo
            .withPageConfig(PageConfig.Paging(2, 1))
            .blockingEvaluate()

        assertThat(resultPage2.getOrThrow().rows.size).isEqualTo(1)
        assertThat(resultPage2.getOrThrow().rows.first().first().value).isEqualTo(orgunitChild2.displayName())

        // No paging
        val resultNoPaging = baseRepo
            .withPageConfig(PageConfig.NoPaging)
            .blockingEvaluate()

        assertThat(resultNoPaging.getOrThrow().rows.size).isEqualTo(2)
    }

    private fun createDefaultEnrollment(
        teiUid: String,
        enrollmentUid: String,
        programUid: String = program.uid(),
        orgunitUid: String = orgunitChild1.uid(),
        enrollmentDate: Date? = null,
        incidentDate: Date? = null,
        created: Date? = null,
        lastUpdated: Date? = null,
        status: EnrollmentStatus? = EnrollmentStatus.ACTIVE,
    ) {
        helper.createEnrollment(
            teiUid,
            enrollmentUid,
            programUid,
            orgunitUid,
            enrollmentDate,
            incidentDate,
            created,
            lastUpdated,
            status,
        )
    }

    private fun createDefaultTrackerEvent(
        eventUid: String,
        enrollmentUid: String,
        programUid: String = program.uid(),
        programStageUid: String = programStage1.uid(),
        orgunitUid: String = orgunitChild1.uid(),
        deleted: Boolean = false,
        eventDate: Date? = null,
        created: Date? = null,
        lastUpdated: Date? = null,
        status: EventStatus? = EventStatus.ACTIVE,
        attributeOptionComboUid: String = attributeOptionCombo.uid()
    ) {
        helper.createTrackerEvent(
            eventUid,
            enrollmentUid,
            programUid,
            programStageUid,
            orgunitUid,
            deleted,
            eventDate,
            created,
            lastUpdated,
            status,
            attributeOptionComboUid,
        )
    }
}

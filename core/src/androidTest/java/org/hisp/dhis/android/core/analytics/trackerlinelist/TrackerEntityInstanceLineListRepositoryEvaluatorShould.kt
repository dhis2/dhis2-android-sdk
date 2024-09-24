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
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.attributeOptionCombo
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.categoryCombo
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
import org.hisp.dhis.android.core.arch.helpers.DateUtils.DATE_FORMAT
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.common.ValueType
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.program.Program
import org.hisp.dhis.android.core.program.programindicatorengine.BaseTrackerDataIntegrationHelper
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Date

@RunWith(D2JunitRunner::class)
internal class TrackerEntityInstanceLineListRepositoryEvaluatorShould : BaseEvaluatorIntegrationShould() {

    private val helper = BaseTrackerDataIntegrationHelper(databaseAdapter)

    val programOther: Program = Program.builder()
        .uid(generator.generate())
        .name("Other tracker program")
        .displayName("Other tracker program")
        .trackedEntityType(trackedEntityType)
        .categoryCombo(ObjectWithUid.create(categoryCombo.uid()))
        .build()

    val attributeOther = TrackedEntityAttribute.builder()
        .uid(generator.generate())
        .displayName("Attribute Other")
        .valueType(ValueType.INTEGER)
        .build()

    @Before
    fun additionalSetUpBase() {
        programStore.insert(programOther)
        trackedEntityAttributeStore.insert(attributeOther)
        helper.createTrackedEntity(trackedEntity1.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        helper.createTrackedEntity(trackedEntity2.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        generator.generate()

        val enrollment1 = generator.generate()
        val enrollment2 = generator.generate()

        createDefaultEnrollment(
            trackedEntity1.uid(),
            enrollment1,
            programUid = program.uid(),
            incidentDate = period202001.startDate(),
            enrollmentDate = period202001.startDate(),
        )
        createDefaultEnrollment(
            trackedEntity2.uid(),
            enrollment2,
            orgunitUid = orgunitChild2.uid(),
            programUid = programOther.uid(),
            status = EnrollmentStatus.COMPLETED,
            incidentDate = period201912.startDate(),
            enrollmentDate = period201912.startDate(),
        )
        val event1 = generator.generate()
        createDefaultTrackerEvent(event1, enrollment1, eventDate = period202001.startDate())
        val event2 = generator.generate()
        createDefaultTrackerEvent(event2, enrollment1, eventDate = period201912.startDate())
        val event3 = generator.generate()
        createDefaultTrackerEvent(event3, enrollment1, eventDate = period201911.startDate())

        helper.insertTrackedEntityDataValue(event1, dataElement1.uid(), "8") //   0 3
        helper.insertTrackedEntityDataValue(event2, dataElement1.uid(), "19") // -1 2
        helper.insertTrackedEntityDataValue(event3, dataElement1.uid(), "2") //  -2 1
    }

    @Test
    fun evaluate_program_attributes() {
        val attributeValue1 = "45"
        val attributeValueOther = "55"
        helper.insertTrackedEntityAttributeValue(trackedEntity1.uid(), attribute1.uid(), attributeValue1)
        helper.insertTrackedEntityAttributeValue(trackedEntity2.uid(), attributeOther.uid(), attributeValueOther)

        val result = d2.analyticsModule().trackerLineList()
            .withTrackedEntityInstanceOutput(trackedEntityType.uid())
            .withColumn(
                TrackerLineListItem.ProgramAttribute(
                    uid = attribute1.uid(),
                    filters = listOf(DataFilter.GreaterThan("40"), DataFilter.LowerThan("50")),
                ),
            )
            .blockingEvaluate()

        val rows = result.getOrThrow().rows
        assertThat(rows.size).isEqualTo(1)
        assertThat(rows[0][0].value).isEqualTo(attributeValue1)

        val result2 = d2.analyticsModule().trackerLineList()
            .withTrackedEntityInstanceOutput(trackedEntityType.uid())
            .withColumn(
                TrackerLineListItem.ProgramAttribute(
                    uid = attributeOther.uid(),
                    filters = listOf(DataFilter.GreaterThan("40"), DataFilter.LowerThan("50")),
                ),
            )
            .blockingEvaluate()

        val rows2 = result2.getOrThrow().rows
        assertThat(rows2.size).isEqualTo(0)

        val result3 = d2.analyticsModule().trackerLineList()
            .withTrackedEntityInstanceOutput(trackedEntityType.uid())
            .withColumn(TrackerLineListItem.ProgramAttribute(uid = attribute1.uid()))
            .withColumn(TrackerLineListItem.ProgramAttribute(uid = attributeOther.uid()))
            .blockingEvaluate()

        val rows3 = result3.getOrThrow().rows
        assertThat(rows3.size).isEqualTo(2)
        assertThat(rows3[0][0].value).isEqualTo(attributeValue1)
        assertThat(rows3[1][1].value).isEqualTo(attributeValueOther)
    }

    @Test
    fun evaluate_repeated_data_elements() {
        val enrollment1_2 = generator.generate()
        createDefaultEnrollment(
            trackedEntity1.uid(),
            enrollment1_2,
            orgunitUid = orgunitChild2.uid(),
            programUid = program.uid(),
            enrollmentDate = period201911.startDate(),
        )
        val event1_2 = generator.generate()
        createDefaultTrackerEvent(event1_2, enrollment1_2, eventDate = period202001.startDate())
        val event2_2 = generator.generate()
        createDefaultTrackerEvent(event2_2, enrollment1_2, eventDate = period201912.startDate())
        val event3_2 = generator.generate()
        createDefaultTrackerEvent(event3_2, enrollment1_2, eventDate = period201911.startDate())

        helper.insertTrackedEntityDataValue(event1_2, dataElement1.uid(), "1008")
        helper.insertTrackedEntityDataValue(event2_2, dataElement1.uid(), "1019")
        helper.insertTrackedEntityDataValue(event3_2, dataElement1.uid(), "1002")

        val result1 = d2.analyticsModule().trackerLineList()
            .withTrackedEntityInstanceOutput(trackedEntityType.uid())
            .withColumn(
                TrackerLineListItem.ProgramDataElement(
                    dataElement = dataElement1.uid(),
                    programStage = programStage1.uid(),
                    filters = listOf(
                        DataFilter.GreaterThan("15"),
                    ),
                    repetitionIndexes = listOf(-3, -1, 0, 1, 3, 4),
                ),
            )
            .blockingEvaluate()

        val rows = result1.getOrThrow().rows
        assertThat(rows.size).isEqualTo(1)

        val row = rows.first()
        assertThat(row.size).isEqualTo(6)

        rows.first().forEachIndexed { index, value ->
            when (index) {
                0 -> assertThat(value.value).isEqualTo("2")
                1 -> assertThat(value.value).isEqualTo("8")
                2 -> assertThat(value.value).isEqualTo(null)
                3 -> assertThat(value.value).isEqualTo(null)
                4 -> assertThat(value.value).isEqualTo("19")
                5 -> assertThat(value.value).isEqualTo("8")
            }
        }
    }

    @Test
    fun evaluate_org_unit_for_tei_output() {
        val result = d2.analyticsModule().trackerLineList()
            .withTrackedEntityInstanceOutput(trackedEntityType.uid())
            .withColumn(TrackerLineListItem.OrganisationUnitItem())
            .withColumn(TrackerLineListItem.OrganisationUnitItem(programUid = program.uid()))
            .withColumn(TrackerLineListItem.OrganisationUnitItem(programUid = programOther.uid()))
            .blockingEvaluate()

        val rows = result.getOrThrow().rows
        assertThat(rows.size).isEqualTo(2)
        assertThat(rows[0].size).isEqualTo(3)
        assertThat(rows[0][0].value).isEqualTo(orgunitChild1.displayName())
        assertThat(rows[0][1].value).isEqualTo(orgunitChild1.displayName())
        assertThat(rows[0][2].value).isEqualTo(null)
        assertThat(rows[1][0].value).isEqualTo(orgunitChild1.displayName())
        assertThat(rows[1][1].value).isEqualTo(null)
        assertThat(rows[1][2].value).isEqualTo(orgunitChild2.displayName())
    }

    @Test
    fun evaluate_program_status_item_for_tei_output() {
        val result = d2.analyticsModule().trackerLineList()
            .withTrackedEntityInstanceOutput(trackedEntityType.uid())
            .withColumn(TrackerLineListItem.ProgramStatusItem(programUid = program.uid()))
            .withColumn(TrackerLineListItem.ProgramStatusItem(programUid = programOther.uid()))
            .blockingEvaluate()

        val rows = result.getOrThrow().rows
        assertThat(rows.size).isEqualTo(2)
        assertThat(rows[0][0].value).isEqualTo(EnrollmentStatus.ACTIVE.toString())
        assertThat(rows[1][1].value).isEqualTo(EnrollmentStatus.COMPLETED.toString())
    }

    @Test
    fun evaluate_last_updated_for_tei_output() {
        val result = d2.analyticsModule().trackerLineList()
            .withTrackedEntityInstanceOutput(trackedEntityType.uid())
            .withColumn(TrackerLineListItem.LastUpdated())
            .blockingEvaluate()

        val rows = result.getOrThrow().rows
        assertThat(rows.size).isEqualTo(2)
    }

    @Test
    fun evaluate_incident_date_for_tei_output() {
        var result = d2.analyticsModule().trackerLineList()
            .withTrackedEntityInstanceOutput(trackedEntityType.uid())
            .withColumn(TrackerLineListItem.IncidentDate(programUid = program.uid()))
            .blockingEvaluate()

        var rows = result.getOrThrow().rows
        assertThat(rows.size).isEqualTo(2)
        assertThat(rows[0][0].value).isEqualTo(DATE_FORMAT.format(period202001.startDate()!!))
        assertThat(rows[1][0].value).isEqualTo(null)

        result = d2.analyticsModule().trackerLineList()
            .withTrackedEntityInstanceOutput(trackedEntityType.uid())
            .withColumn(TrackerLineListItem.IncidentDate(programUid = program.uid()))
            .withColumn(TrackerLineListItem.IncidentDate(programUid = programOther.uid()))
            .blockingEvaluate()

        rows = result.getOrThrow().rows
        assertThat(rows.size).isEqualTo(2)
        assertThat(rows[0].size).isEqualTo(2)
        assertThat(rows[0][0].id).isEqualTo("${Label.IncidentDate}.${program.uid()}")
        assertThat(rows[1][1].id).isEqualTo("${Label.IncidentDate}.${programOther.uid()}")
        assertThat(rows[0][0].value).isEqualTo(DATE_FORMAT.format(period202001.startDate()!!))
        assertThat(rows[1][1].value).isEqualTo(DATE_FORMAT.format(period201912.startDate()!!))
    }

    @Test
    fun evaluate_enrollment_date_for_tei_output() {
        var result = d2.analyticsModule().trackerLineList()
            .withTrackedEntityInstanceOutput(trackedEntityType.uid())
            .withColumn(TrackerLineListItem.EnrollmentDate(programUid = program.uid()))
            .blockingEvaluate()

        var rows = result.getOrThrow().rows
        assertThat(rows.size).isEqualTo(2)
        assertThat(rows[0][0].value).isEqualTo(DATE_FORMAT.format(period202001.startDate()!!))
        assertThat(rows[1][0].value).isEqualTo(null)

        result = d2.analyticsModule().trackerLineList()
            .withTrackedEntityInstanceOutput(trackedEntityType.uid())
            .withColumn(TrackerLineListItem.EnrollmentDate(programUid = program.uid()))
            .withColumn(TrackerLineListItem.EnrollmentDate(programUid = programOther.uid()))
            .blockingEvaluate()

        rows = result.getOrThrow().rows
        assertThat(rows.size).isEqualTo(2)
        assertThat(rows[0].size).isEqualTo(2)
        assertThat(rows[0][0].id).isEqualTo("${Label.EnrollmentDate}.${program.uid()}")
        assertThat(rows[1][1].id).isEqualTo("${Label.EnrollmentDate}.${programOther.uid()}")
        assertThat(rows[0][0].value).isEqualTo(DATE_FORMAT.format(period202001.startDate()!!))
        assertThat(rows[1][1].value).isEqualTo(DATE_FORMAT.format(period201912.startDate()!!))
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
        attributeOptionComboUid: String = attributeOptionCombo.uid(),
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

    internal fun createDefaultEnrollment(
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
}

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

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.analytics.aggregated.DimensionItem
import org.hisp.dhis.android.core.analytics.aggregated.MetadataItem
import org.hisp.dhis.android.core.analytics.aggregated.internal.AnalyticsServiceEvaluationItem
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.attribute1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.dataElement1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.day20191101
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.day20191102
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.generator
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.orgunitChild1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.orgunitChild2
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.period2019Q4
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.period202001
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.program
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.programStage1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.trackedEntity1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.trackedEntity2
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.trackedEntityType
import org.hisp.dhis.android.core.common.AggregationType
import org.hisp.dhis.android.core.dataelement.DataElement
import org.hisp.dhis.android.core.period.Period
import org.hisp.dhis.android.core.program.Program
import org.hisp.dhis.android.core.program.programindicatorengine.BaseTrackerDataIntegrationHelper
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
internal class EventDataItemSQLEvaluatorIntegrationShould : BaseEvaluatorIntegrationShould() {

    private val eventDataItemEvaluator = EventDataItemSQLEvaluator(databaseAdapter)

    private val helper = BaseTrackerDataIntegrationHelper(databaseAdapter)

    @Test
    fun should_aggregate_data_from_multiple_teis() {
        helper.createTrackedEntity(trackedEntity1.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment1 = generator.generate()
        helper.createEnrollment(trackedEntity1.uid(), enrollment1, program.uid(), orgunitChild1.uid())
        val event1 = generator.generate()
        helper.createTrackerEvent(
            event1, enrollment1, program.uid(), programStage1.uid(), orgunitChild1.uid(),
            eventDate = day20191101
        )

        helper.createTrackedEntity(trackedEntity2.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment2 = generator.generate()
        helper.createEnrollment(trackedEntity2.uid(), enrollment2, program.uid(), orgunitChild1.uid())
        val event2 = generator.generate()
        helper.createTrackerEvent(
            event2, enrollment2, program.uid(), programStage1.uid(), orgunitChild1.uid(),
            eventDate = day20191101
        )

        helper.insertTrackedEntityDataValue(event1, dataElement1.uid(), "10")
        helper.insertTrackedEntityDataValue(event2, dataElement1.uid(), "20")

        assertThat(evaluateEventDataElement(program, dataElement1)).isEqualTo("30")

        helper.insertTrackedEntityAttributeValue(trackedEntity1.uid(), attribute1.uid(), "5")
        helper.insertTrackedEntityAttributeValue(trackedEntity2.uid(), attribute1.uid(), "3")

        assertThat(evaluateEventAttribute(program, attribute1)).isEqualTo("8")
    }

    @Test
    fun should_evaluate_event_data_values_aggregation_types() {
        helper.createTrackedEntity(trackedEntity1.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment1 = generator.generate()
        helper.createEnrollment(trackedEntity1.uid(), enrollment1, program.uid(), orgunitChild1.uid())
        val event1 = generator.generate()
        helper.createTrackerEvent(
            event1, enrollment1, program.uid(), programStage1.uid(), orgunitChild1.uid(),
            eventDate = day20191101
        )

        helper.createTrackedEntity(trackedEntity2.uid(), orgunitChild2.uid(), trackedEntityType.uid())
        val enrollment2 = generator.generate()
        helper.createEnrollment(trackedEntity2.uid(), enrollment2, program.uid(), orgunitChild2.uid())
        val event2 = generator.generate()
        helper.createTrackerEvent(
            event2, enrollment2, program.uid(), programStage1.uid(), orgunitChild2.uid(),
            eventDate = day20191102
        )

        helper.insertTrackedEntityDataValue(event1, dataElement1.uid(), "10")
        helper.insertTrackedEntityDataValue(event2, dataElement1.uid(), "20")

        // Event data values
        assertThat(
            evaluateEventDataElement(deAggregation = AggregationType.FIRST)
        ).isEqualTo("30")

        assertThat(
            evaluateEventDataElement(deAggregation = AggregationType.FIRST_AVERAGE_ORG_UNIT)
        ).isEqualTo("15")

        assertThat(
            evaluateEventDataElement(deAggregation = AggregationType.LAST, pe = period2019Q4)
        ).isEqualTo("30")

        assertThat(
            evaluateEventDataElement(deAggregation = AggregationType.LAST, pe = period202001)
        ).isEqualTo("30")

        assertThat(
            evaluateEventDataElement(deAggregation = AggregationType.LAST_AVERAGE_ORG_UNIT, pe = period2019Q4)
        ).isEqualTo("15")

        assertThat(
            evaluateEventDataElement(deAggregation = AggregationType.LAST_AVERAGE_ORG_UNIT, pe = period202001)
        ).isEqualTo("15")

        assertThat(
            evaluateEventDataElement(deAggregation = AggregationType.LAST_IN_PERIOD, pe = period2019Q4)
        ).isEqualTo("30")

        assertThat(
            evaluateEventDataElement(deAggregation = AggregationType.LAST_IN_PERIOD, pe = period202001)
        ).isEqualTo(null)

        assertThat(
            evaluateEventDataElement(deAggregation = AggregationType.LAST_IN_PERIOD_AVERAGE_ORG_UNIT, pe = period2019Q4)
        ).isEqualTo("15")

        assertThat(
            evaluateEventDataElement(deAggregation = AggregationType.LAST_IN_PERIOD_AVERAGE_ORG_UNIT, pe = period202001)
        ).isEqualTo(null)
    }

    @Test
    fun should_evaluate_event_attribute_values_aggregation_types() {
        helper.createTrackedEntity(trackedEntity1.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment1 = generator.generate()
        helper.createEnrollment(trackedEntity1.uid(), enrollment1, program.uid(), orgunitChild1.uid())
        val event1 = generator.generate()
        helper.createTrackerEvent(
            event1, enrollment1, program.uid(), programStage1.uid(), orgunitChild1.uid(),
            eventDate = day20191101
        )

        helper.createTrackedEntity(trackedEntity2.uid(), orgunitChild2.uid(), trackedEntityType.uid())
        val enrollment2 = generator.generate()
        helper.createEnrollment(trackedEntity2.uid(), enrollment2, program.uid(), orgunitChild2.uid())
        val event2 = generator.generate()
        helper.createTrackerEvent(
            event2, enrollment2, program.uid(), programStage1.uid(), orgunitChild2.uid(),
            eventDate = day20191102
        )

        helper.insertTrackedEntityAttributeValue(trackedEntity1.uid(), attribute1.uid(), "5")
        helper.insertTrackedEntityAttributeValue(trackedEntity2.uid(), attribute1.uid(), "3")

        // Event data values
        assertThat(
            evaluateEventAttribute(atAggregation = AggregationType.FIRST)
        ).isEqualTo("8")

        assertThat(
            evaluateEventAttribute(atAggregation = AggregationType.FIRST_AVERAGE_ORG_UNIT)
        ).isEqualTo("4")

        assertThat(
            evaluateEventAttribute(atAggregation = AggregationType.LAST, pe = period2019Q4)
        ).isEqualTo("8")

        assertThat(
            evaluateEventAttribute(atAggregation = AggregationType.LAST, pe = period202001)
        ).isEqualTo("8")

        assertThat(
            evaluateEventAttribute(atAggregation = AggregationType.LAST_AVERAGE_ORG_UNIT, pe = period2019Q4)
        ).isEqualTo("4")

        assertThat(
            evaluateEventAttribute(atAggregation = AggregationType.LAST_AVERAGE_ORG_UNIT, pe = period202001)
        ).isEqualTo("4")

        assertThat(
            evaluateEventAttribute(atAggregation = AggregationType.LAST_IN_PERIOD, pe = period2019Q4)
        ).isEqualTo("8")

        assertThat(
            evaluateEventAttribute(atAggregation = AggregationType.LAST_IN_PERIOD, pe = period202001)
        ).isEqualTo(null)

        assertThat(
            evaluateEventAttribute(atAggregation = AggregationType.LAST_IN_PERIOD_AVERAGE_ORG_UNIT, pe = period2019Q4)
        ).isEqualTo("4")

        assertThat(
            evaluateEventAttribute(atAggregation = AggregationType.LAST_IN_PERIOD_AVERAGE_ORG_UNIT, pe = period202001)
        ).isEqualTo(null)
    }

    @Test
    fun should_override_aggregation_type() {
        helper.createTrackedEntity(trackedEntity1.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment1 = generator.generate()
        helper.createEnrollment(trackedEntity1.uid(), enrollment1, program.uid(), orgunitChild1.uid())
        val event1 = generator.generate()
        helper.createTrackerEvent(
            event1, enrollment1, program.uid(), programStage1.uid(), orgunitChild1.uid(),
            eventDate = day20191101
        )

        helper.createTrackedEntity(trackedEntity2.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment2 = generator.generate()
        helper.createEnrollment(trackedEntity2.uid(), enrollment2, program.uid(), orgunitChild1.uid())
        val event2 = generator.generate()
        helper.createTrackerEvent(
            event2, enrollment2, program.uid(), programStage1.uid(), orgunitChild1.uid(),
            eventDate = day20191101
        )

        helper.insertTrackedEntityDataValue(event1, dataElement1.uid(), "10")
        helper.insertTrackedEntityDataValue(event2, dataElement1.uid(), "20")

        assertThat(evaluateEventDataElement(program, dataElement1)).isEqualTo("30")
        assertThat(evaluateEventDataElement(program, dataElement1, overrideAggregationType = AggregationType.AVERAGE))
            .isEqualTo("15")

        helper.insertTrackedEntityAttributeValue(trackedEntity1.uid(), attribute1.uid(), "5")
        helper.insertTrackedEntityAttributeValue(trackedEntity2.uid(), attribute1.uid(), "3")

        assertThat(evaluateEventAttribute(program, attribute1)).isEqualTo("8")
        assertThat(evaluateEventAttribute(program, attribute1, overrideAggregationType = AggregationType.AVERAGE))
            .isEqualTo("4")
    }

    private fun evaluateEventDataElement(
        program: Program = BaseEvaluatorSamples.program,
        dataElement: DataElement = dataElement1,
        deAggregation: AggregationType = AggregationType.SUM,
        pe: Period = period2019Q4,
        overrideAggregationType: AggregationType = AggregationType.DEFAULT
    ): String? {
        val evaluationItem = AnalyticsServiceEvaluationItem(
            dimensionItems = listOf(
                DimensionItem.DataItem.EventDataItem.DataElement(program.uid(), dataElement.uid())
            ),
            filters = listOf(
                DimensionItem.OrganisationUnitItem.Absolute(BaseEvaluatorSamples.orgunitParent.uid()),
                DimensionItem.PeriodItem.Absolute(pe.periodId()!!)
            ),
            aggregationType = overrideAggregationType
        )

        val aggDataElement = dataElement.toBuilder().aggregationType(deAggregation.name).build()
        val metadataItem = MetadataItem.EventDataElementItem(aggDataElement, program)

        return eventDataItemEvaluator.evaluate(
            evaluationItem,
            metadata + (metadataItem.id to metadataItem)
        )
    }

    private fun evaluateEventAttribute(
        program: Program = BaseEvaluatorSamples.program,
        attribute: TrackedEntityAttribute = attribute1,
        atAggregation: AggregationType = AggregationType.SUM,
        pe: Period = period2019Q4,
        overrideAggregationType: AggregationType = AggregationType.DEFAULT
    ): String? {
        val evaluationItem = AnalyticsServiceEvaluationItem(
            dimensionItems = listOf(
                DimensionItem.DataItem.EventDataItem.Attribute(program.uid(), attribute.uid())
            ),
            filters = listOf(
                DimensionItem.OrganisationUnitItem.Absolute(BaseEvaluatorSamples.orgunitParent.uid()),
                DimensionItem.PeriodItem.Absolute(pe.periodId()!!)
            ),
            aggregationType = overrideAggregationType
        )

        val aggAttribute = attribute.toBuilder().aggregationType(atAggregation).build()
        val metadataItem = MetadataItem.EventAttributeItem(aggAttribute, program)

        return eventDataItemEvaluator.evaluate(
            evaluationItem,
            metadata + (metadataItem.id to metadataItem)
        )
    }
}

/*
 *  Copyright (c) 2004-2026, University of Oslo
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
package org.hisp.dhis.android.core.program.programindicatorengine

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.analytics.aggregated.DimensionItem
import org.hisp.dhis.android.core.analytics.aggregated.MetadataItem
import org.hisp.dhis.android.core.analytics.aggregated.internal.AnalyticsServiceEvaluationItem
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.category
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.categoryCombo
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.categoryOption1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.categoryOption2
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.dataElement1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.day20191101
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.generator
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.orgunitChild1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.period201911
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.program
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.programStage1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.trackedEntity1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.trackedEntityType
import org.hisp.dhis.android.core.arch.d2.internal.DhisAndroidSdkKoinContext.koin
import org.hisp.dhis.android.core.category.CategoryOption
import org.hisp.dhis.android.core.common.AggregationType
import org.hisp.dhis.android.core.common.AnalyticsType
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.program.AnalyticsPeriodBoundary
import org.hisp.dhis.android.core.program.AnalyticsPeriodBoundaryType
import org.hisp.dhis.android.core.program.CategoryMapping
import org.hisp.dhis.android.core.program.CategoryOptionMapping
import org.hisp.dhis.android.core.program.ProgramIndicator
import org.hisp.dhis.android.core.program.internal.CategoryMappingStore
import org.hisp.dhis.android.core.program.internal.CategoryOptionMappingStore
import org.hisp.dhis.android.core.program.programindicatorengine.BaseTrackerDataIntegrationHelper.Companion.de
import org.hisp.dhis.android.core.program.programindicatorengine.BaseTrackerDataIntegrationHelper.Companion.`var`
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
internal class ProgramIndicatorDisaggregationSQLExecutorIntegrationShould :
    BaseProgramIndicatorSQLExecutorIntegrationShould() {

    private val categoryMappingStore: CategoryMappingStore = koin.get()
    private val categoryOptionMappingStore: CategoryOptionMappingStore = koin.get()

    @After
    fun tearDownMappings() {
        runBlocking {
            categoryOptionMappingStore.deleteLinksForMasterUid(mappingUid)
            categoryMappingStore.deleteLinksForMasterUid(program.uid())
        }
    }

    @Test
    fun should_filter_event_count_by_category_option_filter() = runTest {
        seedEvents(values = listOf("10", "10", "20"))

        seedCategoryMapping(listOf(
            categoryOption1 to "${de(programStage1.uid(), dataElement1.uid())} < 11",
            categoryOption2 to "${de(programStage1.uid(), dataElement1.uid())} >= 11",
        ))

        val pi = buildDisaggregatedProgramIndicator(
            expression = `var`("event_count"),
            aggregationType = AggregationType.COUNT,
        )

        // Baseline: without disaggregation the COUNT returns every event.
        assertThat(evaluate(pi)).isEqualTo("3")

        val countForOption1 = evaluate(
            pi,
            extraDimensions = listOf(DimensionItem.CategoryItem(category.uid(), categoryOption1.uid())),
        )
        val countForOption2 = evaluate(
            pi,
            extraDimensions = listOf(DimensionItem.CategoryItem(category.uid(), categoryOption1.uid())),
        )
        assertThat(countForOption1).isEqualTo("2")
        assertThat(countForOption2).isEqualTo("1")
    }

    @Test
    fun should_filter_event_sum_by_category_option_filter_using_data_element_comparison() = runTest {
        // Events with values 5, 10, 15, 20 — sum total 50.
        seedEvents(values = listOf("5", "10", "15", "20"))

        // Filter keeps only events whose data element value is at least 10.
        seedCategoryMapping(listOf(
            categoryOption1 to "${de(programStage1.uid(), dataElement1.uid())} < 11",
            categoryOption2 to "${de(programStage1.uid(), dataElement1.uid())} >= 11",
        ))

        val pi = buildDisaggregatedProgramIndicator(
            expression = de(programStage1.uid(), dataElement1.uid()),
            aggregationType = AggregationType.SUM,
        )

        // Baseline: SUM of all four events.
        assertThat(evaluate(pi)).isEqualTo("50")

        // With the option filter applied only 10 + 15 + 20 = 45 should remain.
        val sumForOption = evaluate(
            pi,
            extraDimensions = listOf(DimensionItem.CategoryItem(category.uid(), categoryOption1.uid())),
        )
        assertThat(sumForOption).isEqualTo("45")
    }

    private suspend fun seedEvents(values: List<String>) {
        helper.createTrackedEntity(trackedEntity1.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment = generator.generate()
        helper.createEnrollment(trackedEntity1.uid(), enrollment, program.uid(), orgunitChild1.uid())

        values.forEach { value ->
            val eventUid = generator.generate()
            helper.createTrackerEvent(
                eventUid,
                enrollment,
                program.uid(),
                programStage1.uid(),
                orgunitChild1.uid(),
                eventDate = day20191101,
            )
            helper.insertTrackedEntityDataValue(eventUid, dataElement1.uid(), value)
        }
    }

    private suspend fun seedCategoryMapping(filterList: List<Pair<CategoryOption, String>>) {
        val mapping = CategoryMapping.builder()
            .uid(mappingUid)
            .program(program.uid())
            .categoryId(category.uid())
            .mappingName("standard mapping")
            .optionMappings(emptyList())
            .build()
        categoryMappingStore.insertIfNotExists(mapping)

        filterList.forEach { (co, cf) ->
            val optionMapping = CategoryOptionMapping.builder()
                .categoryMapping(mappingUid)
                .optionId(co.uid)
                .filter(cf)
                .build()
            categoryOptionMappingStore.insertIfNotExists(optionMapping)
        }
    }

    private suspend fun buildDisaggregatedProgramIndicator(
        expression: String,
        aggregationType: AggregationType,
    ): ProgramIndicator {
        val boundaries = listOf(
            AnalyticsPeriodBoundary.builder()
                .boundaryTarget("EVENT_DATE")
                .analyticsPeriodBoundaryType(AnalyticsPeriodBoundaryType.AFTER_START_OF_REPORTING_PERIOD)
                .build(),
            AnalyticsPeriodBoundary.builder()
                .boundaryTarget("EVENT_DATE")
                .analyticsPeriodBoundaryType(AnalyticsPeriodBoundaryType.BEFORE_END_OF_REPORTING_PERIOD)
                .build(),
        )

        val pi = ProgramIndicator.builder()
            .uid(generator.generate())
            .displayName("Disaggregated program indicator")
            .program(ObjectWithUid.create(program.uid()))
            .aggregationType(aggregationType)
            .analyticsType(AnalyticsType.EVENT)
            .expression(expression)
            .filter("1") // Evaluate all events
            .analyticsPeriodBoundaries(boundaries)
            .categoryCombo(ObjectWithUid.create(categoryCombo.uid()))
            .attributeCombo(ObjectWithUid.create(categoryCombo.uid()))
            .categoryMappingIds(listOf(mappingUid))
            .build()

        helper.setProgramIndicator(pi)
        return pi
    }

    private suspend fun evaluate(
        pi: ProgramIndicator,
        extraDimensions: List<DimensionItem.CategoryItem> = emptyList(),
    ): String? {
        val evaluationItem = AnalyticsServiceEvaluationItem(
            dimensionItems = listOf(DimensionItem.DataItem.ProgramIndicatorItem(pi.uid())) + extraDimensions,
            filters = listOf(DimensionItem.PeriodItem.Absolute(period201911.periodId()!!)),
        )
        return programIndicatorEvaluator.getProgramIndicatorValue(
            evaluationItem = evaluationItem,
            metadata = metadata + (pi.uid() to MetadataItem.ProgramIndicatorItem(pi)),
            queryMods = null,
        )
    }

    companion object {
        private const val mappingUid = "disaggregationMapping"
    }
}

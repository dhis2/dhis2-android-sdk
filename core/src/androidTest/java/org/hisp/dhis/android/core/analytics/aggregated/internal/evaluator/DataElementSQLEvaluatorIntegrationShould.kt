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
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.attribute
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.attributeOption
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.attributeOptionCombo
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.category
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.categoryOption
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.categoryOptionCombo
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.dataElement1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.dataElement2
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.level1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.organisationUnitGroup
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.orgunitChild1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.orgunitChild2
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.orgunitParent
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.period201911
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.period201912
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.period2019Q4
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.period202012
import org.hisp.dhis.android.core.common.AggregationType
import org.hisp.dhis.android.core.common.RelativeOrganisationUnit
import org.hisp.dhis.android.core.common.RelativePeriod
import org.hisp.dhis.android.core.datavalue.DataValue
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
internal class DataElementSQLEvaluatorIntegrationShould : BaseEvaluatorIntegrationShould() {

    private val dataElementEvaluator = DataElementSQLEvaluator(databaseAdapter)

    @Test
    fun should_aggregate_value_in_hierarchy() {
        createDataValue("2", orgunitUid = orgunitParent.uid())
        createDataValue("3", orgunitUid = orgunitChild1.uid())
        createDataValue("4", orgunitUid = orgunitChild2.uid())

        val evaluationItem = AnalyticsServiceEvaluationItem(
            dimensionItems = listOf(
                DimensionItem.DataItem.DataElementItem(dataElement1.uid()),
                DimensionItem.PeriodItem.Absolute(period201912.periodId()!!)
            ),
            filters = listOf(
                DimensionItem.OrganisationUnitItem.Absolute(orgunitParent.uid())
            )
        )

        val value = dataElementEvaluator.evaluate(evaluationItem, metadata)

        assertThat(value).isEqualTo("9")
    }

    @Test
    fun should_aggregate_value_in_time() {
        createDataValue("2", periodId = period201911.periodId()!!)
        createDataValue("3", periodId = period201912.periodId()!!)

        val evaluationItem = AnalyticsServiceEvaluationItem(
            dimensionItems = listOf(
                DimensionItem.DataItem.DataElementItem(dataElement1.uid()),
                DimensionItem.PeriodItem.Absolute(period2019Q4.periodId()!!)
            ),
            filters = listOf(
                DimensionItem.OrganisationUnitItem.Absolute(orgunitParent.uid())
            )
        )

        val value = dataElementEvaluator.evaluate(evaluationItem, metadata)

        assertThat(value).isEqualTo("5")
    }

    @Test
    fun should_aggregate_relative_periods() {
        createDataValue("2", periodId = period201911.periodId()!!)
        createDataValue("3", periodId = period201912.periodId()!!)

        val evaluationItem = AnalyticsServiceEvaluationItem(
            dimensionItems = listOf(
                DimensionItem.DataItem.DataElementItem(dataElement1.uid())
            ),
            filters = listOf(
                DimensionItem.OrganisationUnitItem.Absolute(orgunitParent.uid()),
                DimensionItem.PeriodItem.Relative(RelativePeriod.LAST_MONTH),
                DimensionItem.PeriodItem.Relative(RelativePeriod.THIS_MONTH)
            )
        )

        val value = dataElementEvaluator.evaluate(evaluationItem, metadata)

        assertThat(value).isEqualTo("5")
    }

    @Test
    fun should_aggregate_data_elements_if_defined_as_filter() {
        createDataValue("2", dataElementUid = dataElement1.uid())
        createDataValue("3", dataElementUid = dataElement2.uid())

        val evaluationItem = AnalyticsServiceEvaluationItem(
            dimensionItems = listOf(
                DimensionItem.OrganisationUnitItem.Absolute(orgunitParent.uid())
            ),
            filters = listOf(
                DimensionItem.DataItem.DataElementItem(dataElement1.uid()),
                DimensionItem.DataItem.DataElementItem(dataElement2.uid()),
                DimensionItem.PeriodItem.Relative(RelativePeriod.THIS_MONTH)
            )
        )

        val value = dataElementEvaluator.evaluate(evaluationItem, metadata)

        assertThat(value).isEqualTo("5")
    }

    @Test
    fun should_use_data_element_aggregation_type() {
        createDataValue("2", orgunitUid = orgunitChild1.uid(), dataElementUid = dataElement2.uid())
        createDataValue("3", orgunitUid = orgunitChild2.uid(), dataElementUid = dataElement2.uid())

        val evaluationItem = AnalyticsServiceEvaluationItem(
            dimensionItems = listOf(
                DimensionItem.DataItem.DataElementItem(dataElement2.uid())
            ),
            filters = listOf(
                DimensionItem.OrganisationUnitItem.Absolute(orgunitParent.uid()),
                DimensionItem.PeriodItem.Relative(RelativePeriod.THIS_MONTH)
            )
        )

        val value = dataElementEvaluator.evaluate(evaluationItem, metadata)

        assertThat(value).isEqualTo("2.5")
    }

    @Test
    fun should_disaggregate_by_category_option() {
        createDataValue("2", categoryOptionComboUid = categoryOptionCombo.uid())
        createDataValue("5", categoryOptionComboUid = attributeOptionCombo.uid())

        val evaluationItem = AnalyticsServiceEvaluationItem(
            dimensionItems = listOf(
                DimensionItem.DataItem.DataElementItem(dataElement1.uid()),
                DimensionItem.CategoryItem(category.uid(), categoryOption.uid())
            ),
            filters = listOf(
                DimensionItem.OrganisationUnitItem.Absolute(orgunitParent.uid()),
                DimensionItem.PeriodItem.Relative(RelativePeriod.THIS_MONTH)
            )
        )

        val value = dataElementEvaluator.evaluate(evaluationItem, metadata)

        assertThat(value).isEqualTo("2")
    }

    @Test
    fun should_disaggregate_by_attribute_option() {
        createDataValue("2", attributeOptionComboUid = categoryOptionCombo.uid())
        createDataValue("5", attributeOptionComboUid = attributeOptionCombo.uid())

        val evaluationItem = AnalyticsServiceEvaluationItem(
            dimensionItems = listOf(
                DimensionItem.DataItem.DataElementItem(dataElement1.uid()),
                DimensionItem.CategoryItem(attribute.uid(), attributeOption.uid())
            ),
            filters = listOf(
                DimensionItem.OrganisationUnitItem.Absolute(orgunitParent.uid()),
                DimensionItem.PeriodItem.Relative(RelativePeriod.THIS_MONTH)
            )
        )

        val value = dataElementEvaluator.evaluate(evaluationItem, metadata)

        assertThat(value).isEqualTo("5")
    }

    @Test
    fun should_ignore_missing_category_option() {
        createDataValue("2")

        val evaluationItem = AnalyticsServiceEvaluationItem(
            dimensionItems = listOf(
                DimensionItem.DataItem.DataElementItem(dataElement1.uid()),
                DimensionItem.CategoryItem(category.uid(), categoryOption = "non-existing-co")
            ),
            filters = listOf(
                DimensionItem.OrganisationUnitItem.Absolute(orgunitParent.uid()),
                DimensionItem.PeriodItem.Relative(RelativePeriod.THIS_MONTH)
            )
        )

        val value = dataElementEvaluator.evaluate(evaluationItem, metadata)

        assertThat(value).isNull()
    }

    @Test
    fun should_evaluate_data_element_operand() {
        createDataValue("2")

        val evaluationItem = AnalyticsServiceEvaluationItem(
            dimensionItems = listOf(
                DimensionItem.DataItem.DataElementOperandItem(dataElement1.uid(), categoryOptionCombo.uid())
            ),
            filters = listOf(
                DimensionItem.OrganisationUnitItem.Absolute(orgunitParent.uid()),
                DimensionItem.PeriodItem.Relative(RelativePeriod.THIS_MONTH)
            )
        )

        val value = dataElementEvaluator.evaluate(evaluationItem, metadata)

        assertThat(value).isEqualTo("2")
    }

    @Test
    fun should_evaluate_relative_user_orgunit() {
        createDataValue("2", orgunitUid = orgunitChild1.uid())
        createDataValue("3", orgunitUid = orgunitChild2.uid())

        val evaluationItem = AnalyticsServiceEvaluationItem(
            dimensionItems = listOf(
                DimensionItem.DataItem.DataElementItem(dataElement1.uid())
            ),
            filters = listOf(
                DimensionItem.OrganisationUnitItem.Relative(RelativeOrganisationUnit.USER_ORGUNIT),
                DimensionItem.PeriodItem.Relative(RelativePeriod.THIS_MONTH)
            )
        )

        val value = dataElementEvaluator.evaluate(evaluationItem, metadata)

        assertThat(value).isEqualTo("5")
    }

    @Test
    fun should_aggregate_relative_user_children_as_filter() {
        createDataValue("2", orgunitUid = orgunitChild1.uid())
        createDataValue("3", orgunitUid = orgunitChild2.uid())

        val evaluationItem = AnalyticsServiceEvaluationItem(
            dimensionItems = listOf(
                DimensionItem.DataItem.DataElementItem(dataElement1.uid())
            ),
            filters = listOf(
                DimensionItem.OrganisationUnitItem.Relative(RelativeOrganisationUnit.USER_ORGUNIT_CHILDREN),
                DimensionItem.PeriodItem.Relative(RelativePeriod.THIS_MONTH)
            )
        )

        val value = dataElementEvaluator.evaluate(evaluationItem, metadata)

        assertThat(value).isEqualTo("5")
    }

    @Test
    fun should_use_organisation_unit_levels() {
        createDataValue("2", orgunitUid = orgunitChild1.uid(), periodId = period201911.periodId()!!)
        createDataValue("3", orgunitUid = orgunitChild2.uid(), periodId = period201911.periodId()!!)

        val evaluationItem = AnalyticsServiceEvaluationItem(
            dimensionItems = listOf(
                DimensionItem.DataItem.DataElementItem(dataElement1.uid()),
                DimensionItem.PeriodItem.Absolute(period201911.periodId()!!)
            ),
            filters = listOf(
                DimensionItem.OrganisationUnitItem.Level(level1.uid())
            )
        )

        val value = dataElementEvaluator.evaluate(evaluationItem, metadata)

        assertThat(value).isEqualTo("5")
    }

    @Test
    fun should_use_organisation_unit_groups() {
        createDataValue("2", orgunitUid = orgunitChild1.uid(), periodId = period201911.periodId()!!)
        createDataValue("3", orgunitUid = orgunitChild2.uid(), periodId = period201911.periodId()!!)

        val evaluationItem = AnalyticsServiceEvaluationItem(
            dimensionItems = listOf(
                DimensionItem.DataItem.DataElementItem(dataElement1.uid()),
                DimensionItem.PeriodItem.Absolute(period201911.periodId()!!)
            ),
            filters = listOf(
                DimensionItem.OrganisationUnitItem.Group(organisationUnitGroup.uid())
            )
        )

        val value = dataElementEvaluator.evaluate(evaluationItem, metadata)

        assertThat(value).isEqualTo("5")
    }

    @Test
    fun should_aggregate_by_aggregation_types() {
        createDataValue("3", orgunitUid = orgunitChild1.uid(), periodId = period201911.periodId()!!)
        createDataValue("5", orgunitUid = orgunitChild1.uid(), periodId = period201912.periodId()!!)
        createDataValue("8", orgunitUid = orgunitChild2.uid(), periodId = period201911.periodId()!!)

        assertThat(
            evaluateAggregation(
                periodId = period2019Q4.periodId()!!,
                aggregator = AggregationType.AVERAGE_SUM_ORG_UNIT
            )
        ).isEqualTo("12")

        assertThat(
            evaluateAggregation(
                periodId = period2019Q4.periodId()!!,
                aggregator = AggregationType.FIRST
            )
        ).isEqualTo("11")

        assertThat(
            evaluateAggregation(
                periodId = period202012.periodId()!!,
                aggregator = AggregationType.FIRST
            )
        ).isEqualTo("11")

        assertThat(
            evaluateAggregation(
                periodId = period2019Q4.periodId()!!,
                aggregator = AggregationType.FIRST_AVERAGE_ORG_UNIT
            )
        ).isEqualTo("5.5")

        assertThat(
            evaluateAggregation(
                periodId = period202012.periodId()!!,
                aggregator = AggregationType.FIRST_AVERAGE_ORG_UNIT
            )
        ).isEqualTo("5.5")

        assertThat(
            evaluateAggregation(
                periodId = period2019Q4.periodId()!!,
                aggregator = AggregationType.LAST
            )
        ).isEqualTo("13")

        assertThat(
            evaluateAggregation(
                periodId = period202012.periodId()!!,
                aggregator = AggregationType.LAST
            )
        ).isEqualTo("13")

        assertThat(
            evaluateAggregation(
                periodId = period2019Q4.periodId()!!,
                aggregator = AggregationType.LAST_AVERAGE_ORG_UNIT
            )
        ).isEqualTo("6.5")

        assertThat(
            evaluateAggregation(
                periodId = period202012.periodId()!!,
                aggregator = AggregationType.LAST_AVERAGE_ORG_UNIT
            )
        ).isEqualTo("6.5")

        assertThat(
            evaluateAggregation(
                periodId = period2019Q4.periodId()!!,
                aggregator = AggregationType.LAST_IN_PERIOD
            )
        ).isEqualTo("13")

        assertThat(
            evaluateAggregation(
                periodId = period202012.periodId()!!,
                aggregator = AggregationType.LAST_IN_PERIOD
            )
        ).isEqualTo(null)

        assertThat(
            evaluateAggregation(
                periodId = period2019Q4.periodId()!!,
                aggregator = AggregationType.LAST_IN_PERIOD_AVERAGE_ORG_UNIT
            )
        ).isEqualTo("6.5")

        assertThat(
            evaluateAggregation(
                periodId = period202012.periodId()!!,
                aggregator = AggregationType.LAST_IN_PERIOD_AVERAGE_ORG_UNIT
            )
        ).isEqualTo(null)
    }

    @Test
    fun should_override_aggregation_types() {
        createDataValue("3", orgunitUid = orgunitChild1.uid(), periodId = period201911.periodId()!!)
        createDataValue("8", orgunitUid = orgunitChild2.uid(), periodId = period201911.periodId()!!)

        val defaultEvaluationItem = AnalyticsServiceEvaluationItem(
            dimensionItems = listOf(
                DimensionItem.DataItem.DataElementItem(dataElement1.uid()),
                DimensionItem.PeriodItem.Absolute(period201911.periodId()!!)
            ),
            filters = listOf(
                DimensionItem.OrganisationUnitItem.Absolute(orgunitParent.uid())
            )
        )

        assertThat(dataElementEvaluator.evaluate(defaultEvaluationItem, metadata)).isEqualTo("11")

        val overrideEvaluationItem = AnalyticsServiceEvaluationItem(
            dimensionItems = listOf(
                DimensionItem.DataItem.DataElementItem(dataElement1.uid()),
                DimensionItem.PeriodItem.Absolute(period201911.periodId()!!)
            ),
            filters = listOf(
                DimensionItem.OrganisationUnitItem.Absolute(orgunitParent.uid())
            ),
            aggregationType = AggregationType.AVERAGE
        )

        assertThat(dataElementEvaluator.evaluate(overrideEvaluationItem, metadata)).isEqualTo("5.5")
    }

    private fun createDataValue(
        value: String,
        dataElementUid: String = dataElement1.uid(),
        orgunitUid: String = orgunitParent.uid(),
        periodId: String = period201912.periodId()!!,
        categoryOptionComboUid: String = categoryOptionCombo.uid(),
        attributeOptionComboUid: String = attributeOptionCombo.uid()
    ) {
        val dataValue = DataValue.builder()
            .value(value)
            .dataElement(dataElementUid)
            .period(periodId)
            .organisationUnit(orgunitUid)
            .categoryOptionCombo(categoryOptionComboUid)
            .attributeOptionCombo(attributeOptionComboUid)
            .build()

        dataValueStore.insert(dataValue)
    }

    private fun evaluateAggregation(
        periodId: String,
        aggregator: AggregationType
    ): String? {
        val evaluationItem = AnalyticsServiceEvaluationItem(
            dimensionItems = listOf(
                DimensionItem.DataItem.DataElementItem(dataElement1.uid()),
                DimensionItem.PeriodItem.Absolute(periodId)
            ),
            filters = listOf(
                DimensionItem.OrganisationUnitItem.Absolute(orgunitParent.uid())
            )
        )

        val updatedDataElement = dataElement1.toBuilder().aggregationType(aggregator.name).build()
        val updatedMetadata = metadata + (dataElement1.uid() to MetadataItem.DataElementItem(updatedDataElement))

        return dataElementEvaluator.evaluate(evaluationItem, updatedMetadata)
    }
}

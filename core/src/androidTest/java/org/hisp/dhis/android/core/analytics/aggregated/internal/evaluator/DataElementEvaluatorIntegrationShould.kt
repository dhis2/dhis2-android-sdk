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
package org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.analytics.aggregated.DimensionItem
import org.hisp.dhis.android.core.analytics.aggregated.MetadataItem
import org.hisp.dhis.android.core.analytics.aggregated.internal.AnalyticsServiceEvaluationItem
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.DataElementEvaluatorSamples.category
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.DataElementEvaluatorSamples.categoryCategoryComboLink
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.DataElementEvaluatorSamples.categoryCategoryOptionLink
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.DataElementEvaluatorSamples.categoryCombo
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.DataElementEvaluatorSamples.categoryOption
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.DataElementEvaluatorSamples.categoryOptionCombo
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.DataElementEvaluatorSamples.categoryOptionComboCategoryOptionLink
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.DataElementEvaluatorSamples.dataElement1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.DataElementEvaluatorSamples.dataElement2
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.DataElementEvaluatorSamples.dataElementOperand
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.DataElementEvaluatorSamples.orgunitChild1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.DataElementEvaluatorSamples.orgunitChild2
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.DataElementEvaluatorSamples.orgunitParent
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.DataElementEvaluatorSamples.periodDec
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.DataElementEvaluatorSamples.periodNov
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.DataElementEvaluatorSamples.periodQ4
import org.hisp.dhis.android.core.category.internal.*
import org.hisp.dhis.android.core.common.RelativeOrganisationUnit
import org.hisp.dhis.android.core.common.RelativePeriod
import org.hisp.dhis.android.core.dataelement.internal.DataElementStore
import org.hisp.dhis.android.core.datavalue.DataValue
import org.hisp.dhis.android.core.datavalue.internal.DataValueStore
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitStore
import org.hisp.dhis.android.core.period.internal.PeriodStoreImpl
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestEmptyDispatcher
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class DataElementEvaluatorIntegrationShould : BaseMockIntegrationTestEmptyDispatcher() {

    private val dataElementEvaluator = DataElementEvaluator(databaseAdapter)

    // Stores
    private val dataValueStore = DataValueStore.create(databaseAdapter)
    private val categoryStore = CategoryStore.create(databaseAdapter)
    private val categoryOptionStore = CategoryOptionStore.create(databaseAdapter)
    private val categoryCategoryOptionStore = CategoryCategoryOptionLinkStore.create(databaseAdapter)
    private val categoryComboStore = CategoryComboStore.create(databaseAdapter)
    private val categoryOptionComboStore = CategoryOptionComboStoreImpl.create(databaseAdapter)
    private val categoryCategoryComboLinkStore = CategoryCategoryComboLinkStore.create(databaseAdapter)
    private val categoryOptionComboCategoryOptionLinkStore = CategoryOptionComboCategoryOptionLinkStore.create(
        databaseAdapter
    )
    private val dataElementStore = DataElementStore.create(databaseAdapter)
    private val organisationUnitStore = OrganisationUnitStore.create(databaseAdapter)
    private val periodStore = PeriodStoreImpl.create(databaseAdapter)

    val metadata: Map<String, MetadataItem> = mapOf(
        orgunitParent.uid() to MetadataItem.OrganisationUnitItem(orgunitParent),
        orgunitChild1.uid() to MetadataItem.OrganisationUnitItem(orgunitChild1),
        orgunitChild2.uid() to MetadataItem.OrganisationUnitItem(orgunitChild2),
        dataElement1.uid() to MetadataItem.DataElementItem(dataElement1),
        dataElement2.uid() to MetadataItem.DataElementItem(dataElement2),
        dataElementOperand.uid()!! to MetadataItem.DataElementOperandItem(dataElementOperand),
        periodNov.periodId()!! to MetadataItem.PeriodItem(periodNov),
        periodDec.periodId()!! to MetadataItem.PeriodItem(periodDec),
        periodQ4.periodId()!! to MetadataItem.PeriodItem(periodQ4),
        RelativeOrganisationUnit.USER_ORGUNIT.name to MetadataItem.OrganisationUnitRelativeItem(
            RelativeOrganisationUnit.USER_ORGUNIT,
            listOf(orgunitParent)
        ),
        RelativeOrganisationUnit.USER_ORGUNIT_CHILDREN.name to MetadataItem.OrganisationUnitRelativeItem(
            RelativeOrganisationUnit.USER_ORGUNIT_CHILDREN,
            listOf(orgunitChild1, orgunitChild2)
        ),
        RelativePeriod.THIS_MONTH.name to MetadataItem.RelativePeriodItem(
            RelativePeriod.THIS_MONTH,
            listOf(periodDec)
        ),
        RelativePeriod.LAST_MONTH.name to MetadataItem.RelativePeriodItem(
            RelativePeriod.LAST_MONTH,
            listOf(periodNov)
        )
    )

    @Before
    fun setUp() {
        setUpClass()

        organisationUnitStore.insert(orgunitParent)
        organisationUnitStore.insert(orgunitChild1)
        organisationUnitStore.insert(orgunitChild2)

        categoryStore.insert(category)
        categoryOptionStore.insert(categoryOption)
        categoryCategoryOptionStore.insert(categoryCategoryOptionLink)
        categoryComboStore.insert(categoryCombo)
        categoryOptionComboStore.insert(categoryOptionCombo)
        categoryCategoryComboLinkStore.insert(categoryCategoryComboLink)
        categoryOptionComboCategoryOptionLinkStore.insert(categoryOptionComboCategoryOptionLink)

        dataElementStore.insert(dataElement1)
        dataElementStore.insert(dataElement2)

        periodStore.insert(periodNov)
        periodStore.insert(periodDec)
        periodStore.insert(periodQ4)
    }

    @After
    fun tearDown() {
        organisationUnitStore.delete()
        categoryStore.delete()
        categoryOptionStore.delete()
        categoryCategoryOptionStore.delete()
        categoryComboStore.delete()
        categoryOptionComboStore.delete()
        categoryCategoryComboLinkStore.delete()
        categoryOptionComboCategoryOptionLinkStore.delete()
        dataElementStore.delete()
        periodStore.delete()
        dataValueStore.delete()
    }

    @Test
    fun should_aggregate_value_in_hierarchy() {
        createDataValue("2", orgunitUid = orgunitParent.uid())
        createDataValue("3", orgunitUid = orgunitChild1.uid())
        createDataValue("4", orgunitUid = orgunitChild2.uid())

        val evaluationItem = AnalyticsServiceEvaluationItem(
            dimensionItems = listOf(
                DimensionItem.DataItem.DataElementItem(dataElement1.uid()),
                DimensionItem.PeriodItem.Absolute(periodDec.periodId()!!)
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
        createDataValue("2", periodId = periodNov.periodId()!!)
        createDataValue("3", periodId = periodDec.periodId()!!)

        val evaluationItem = AnalyticsServiceEvaluationItem(
            dimensionItems = listOf(
                DimensionItem.DataItem.DataElementItem(dataElement1.uid()),
                DimensionItem.PeriodItem.Absolute(periodQ4.periodId()!!)
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
        createDataValue("2", periodId = periodNov.periodId()!!)
        createDataValue("3", periodId = periodDec.periodId()!!)

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
        createDataValue("2")

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

    private fun createDataValue(
        value: String,
        dataElementUid: String = dataElement1.uid(),
        orgunitUid: String = orgunitParent.uid(),
        periodId: String = periodDec.periodId()!!
    ) {
        val dataValue = DataValue.builder()
            .value(value)
            .dataElement(dataElementUid)
            .period(periodId)
            .organisationUnit(orgunitUid)
            .categoryOptionCombo(categoryOptionCombo.uid())
            .attributeOptionCombo(categoryOptionCombo.uid())
            .build()

        dataValueStore.insert(dataValue)
    }
}

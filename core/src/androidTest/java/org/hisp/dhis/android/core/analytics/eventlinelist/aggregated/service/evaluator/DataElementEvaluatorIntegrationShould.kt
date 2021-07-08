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
package org.hisp.dhis.android.core.analytics.eventlinelist.aggregated.service.evaluator

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.analytics.aggregated.DimensionItem
import org.hisp.dhis.android.core.analytics.aggregated.MetadataItem
import org.hisp.dhis.android.core.analytics.aggregated.service.AnalyticsServiceEvaluationItem
import org.hisp.dhis.android.core.analytics.aggregated.service.evaluator.DataElementEvaluator
import org.hisp.dhis.android.core.analytics.eventlinelist.aggregated.service.evaluator.DataElementEvaluatorSamples.categoryCombo
import org.hisp.dhis.android.core.analytics.eventlinelist.aggregated.service.evaluator.DataElementEvaluatorSamples.categoryOptionCombo
import org.hisp.dhis.android.core.analytics.eventlinelist.aggregated.service.evaluator.DataElementEvaluatorSamples.dataElement
import org.hisp.dhis.android.core.analytics.eventlinelist.aggregated.service.evaluator.DataElementEvaluatorSamples.orgunitChild1
import org.hisp.dhis.android.core.analytics.eventlinelist.aggregated.service.evaluator.DataElementEvaluatorSamples.orgunitChild2
import org.hisp.dhis.android.core.analytics.eventlinelist.aggregated.service.evaluator.DataElementEvaluatorSamples.orgunitParent
import org.hisp.dhis.android.core.analytics.eventlinelist.aggregated.service.evaluator.DataElementEvaluatorSamples.periodJune
import org.hisp.dhis.android.core.analytics.eventlinelist.aggregated.service.evaluator.DataElementEvaluatorSamples.periodMay
import org.hisp.dhis.android.core.analytics.eventlinelist.aggregated.service.evaluator.DataElementEvaluatorSamples.periodQ2
import org.hisp.dhis.android.core.category.internal.CategoryComboStore
import org.hisp.dhis.android.core.category.internal.CategoryOptionComboStoreImpl
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
    private val categoryComboStore = CategoryComboStore.create(databaseAdapter)
    private val categoryOptionComboStore = CategoryOptionComboStoreImpl.create(databaseAdapter)
    private val dataElementStore = DataElementStore.create(databaseAdapter)
    private val organisationUnitStore = OrganisationUnitStore.create(databaseAdapter)
    private val periodStore = PeriodStoreImpl.create(databaseAdapter)

    val metadata: Map<String, MetadataItem> = mapOf(
        orgunitParent.uid() to MetadataItem.OrganisationUnitItem(orgunitParent),
        orgunitChild1.uid() to MetadataItem.OrganisationUnitItem(orgunitChild1),
        orgunitChild2.uid() to MetadataItem.OrganisationUnitItem(orgunitChild2),
        dataElement.uid() to MetadataItem.DataElementItem(dataElement),
        periodMay.periodId()!! to MetadataItem.PeriodItem(periodMay),
        periodJune.periodId()!! to MetadataItem.PeriodItem(periodJune),
        periodQ2.periodId()!! to MetadataItem.PeriodItem(periodQ2)
    )

    @Before
    fun setUp() {
        setUpClass()

        organisationUnitStore.insert(orgunitParent)
        organisationUnitStore.insert(orgunitChild1)
        organisationUnitStore.insert(orgunitChild2)

        categoryComboStore.insert(categoryCombo)
        categoryOptionComboStore.insert(categoryOptionCombo)

        dataElementStore.insert(dataElement)

        periodStore.insert(periodMay)
        periodStore.insert(periodJune)
        periodStore.insert(periodQ2)
    }

    @After
    fun tearDown() {
        organisationUnitStore.delete()
        categoryComboStore.delete()
        categoryOptionComboStore.delete()
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
                DimensionItem.DataItem.DataElementItem(dataElement.uid()),
                DimensionItem.PeriodItem.Absolute(periodJune.periodId()!!)
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
        createDataValue("2", periodId = periodMay.periodId()!!)
        createDataValue("3", periodId = periodJune.periodId()!!)

        val evaluationItem = AnalyticsServiceEvaluationItem(
            dimensionItems = listOf(
                DimensionItem.DataItem.DataElementItem(dataElement.uid()),
                DimensionItem.PeriodItem.Absolute(periodQ2.periodId()!!)
            ),
            filters = listOf(
                DimensionItem.OrganisationUnitItem.Absolute(orgunitParent.uid())
            )
        )

        val value = dataElementEvaluator.evaluate(evaluationItem, metadata)

        assertThat(value).isEqualTo("5")
    }

    private fun createDataValue(
        value: String,
        dataElementUid: String = dataElement.uid(),
        orgunitUid: String = orgunitParent.uid(),
        periodId: String = periodJune.periodId()!!
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

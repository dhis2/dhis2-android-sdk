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

package org.hisp.dhis.android.core.analytics.aggregated.internal

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.hisp.dhis.android.core.analytics.aggregated.internal.AnalyticsServiceHelperSamples as s
import org.hisp.dhis.android.core.analytics.aggregated.mock.AggregatedSamples
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.category.internal.CategoryOptionComboStore
import org.hisp.dhis.android.core.category.internal.CategoryOptionStore
import org.hisp.dhis.android.core.category.internal.CategoryStore
import org.hisp.dhis.android.core.dataelement.internal.DataElementStore
import org.hisp.dhis.android.core.expressiondimensionitem.internal.ExpressionDimensionItemStore
import org.hisp.dhis.android.core.indicator.Indicator
import org.hisp.dhis.android.core.legendset.internal.LegendStore
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitGroupStore
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitLevelStore
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitStore
import org.hisp.dhis.android.core.period.internal.ParentPeriodGenerator
import org.hisp.dhis.android.core.period.internal.PeriodHelper
import org.hisp.dhis.android.core.program.ProgramIndicatorCollectionRepository
import org.hisp.dhis.android.core.program.internal.ProgramStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeStore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class AnalyticsServiceMetadataHelperShould {

    private val categoryStore: CategoryStore = mock()
    private val categoryOptionStore: CategoryOptionStore = mock()
    private val dataElementStore: DataElementStore = mock()
    private val categoryOptionComboStore: CategoryOptionComboStore = mock()
    private val indicatorStore: IdentifiableObjectStore<Indicator> = mock()
    private val expressionDimensionItemStore: ExpressionDimensionItemStore = mock()
    private val legendStore: LegendStore = mock()
    private val organisationUnitStore: OrganisationUnitStore = mock()
    private val organisationUnitGroupStore: OrganisationUnitGroupStore = mock()
    private val organisationUnitLevelStore: OrganisationUnitLevelStore = mock()
    private val programStore: ProgramStore = mock()
    private val trackedEntityAttribute: TrackedEntityAttributeStore = mock()
    private val programIndicatorRepository: ProgramIndicatorCollectionRepository = mock()
    private val analyticsOrganisationUnitHelper: AnalyticsOrganisationUnitHelper = mock()
    private val parentPeriodGenerator: ParentPeriodGenerator = mock()
    private val periodHelper: PeriodHelper = mock()

    private val helper = AnalyticsServiceMetadataHelper(
        categoryStore,
        categoryOptionStore,
        categoryOptionComboStore,
        dataElementStore,
        indicatorStore,
        expressionDimensionItemStore,
        legendStore,
        organisationUnitStore,
        organisationUnitGroupStore,
        organisationUnitLevelStore,
        programStore,
        trackedEntityAttribute,
        programIndicatorRepository,
        analyticsOrganisationUnitHelper,
        parentPeriodGenerator,
        periodHelper
    )

    @Test
    fun `Should extract category and category option metadata items`() {
        whenever(categoryStore.selectByUid(s.categoryItem1_1.uid)).thenReturn(AggregatedSamples.cc1)
        whenever(categoryOptionStore.selectByUid(s.categoryItem1_1.categoryOption)).thenReturn(AggregatedSamples.co11)
        whenever(categoryOptionStore.selectByUid(s.categoryItem1_2.categoryOption)).thenReturn(AggregatedSamples.co12)

        val evaluationItems = listOf(
            AnalyticsServiceEvaluationItem(
                dimensionItems = listOf(s.categoryItem1_1, s.categoryItem1_2),
                filters = listOf()
            )
        )

        val metadataMap = helper.getMetadata(evaluationItems)

        assertThat(metadataMap.keys).containsExactly(
            s.categoryItem1_1.uid,
            s.categoryItem1_1.categoryOption,
            s.categoryItem1_2.categoryOption
        )
    }
}

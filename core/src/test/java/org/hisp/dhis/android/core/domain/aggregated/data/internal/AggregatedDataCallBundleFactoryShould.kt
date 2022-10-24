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
package org.hisp.dhis.android.core.domain.aggregated.data.internal

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore
import org.hisp.dhis.android.core.dataset.DataSet
import org.hisp.dhis.android.core.dataset.DataSetCollectionRepository
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitCollectionRepository
import org.hisp.dhis.android.core.period.Period
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.period.internal.PeriodForDataSetManager
import org.hisp.dhis.android.core.settings.DataSetSettings
import org.hisp.dhis.android.core.settings.DataSetSettingsObjectRepository
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class AggregatedDataCallBundleFactoryShould {

    private val dataSetRepository: DataSetCollectionRepository = mock()
    private val organisationUnitRepository: OrganisationUnitCollectionRepository = mock()
    private val dataSetSettingsObjectRepository: DataSetSettingsObjectRepository = mock()
    private val periodManager: PeriodForDataSetManager = mock()
    private val aggregatedDataSyncStore: ObjectWithoutUidStore<AggregatedDataSync> = mock()
    private val lastUpdatedCalculator: AggregatedDataSyncLastUpdatedCalculator = mock()
    private val dataSetSettings: DataSetSettings = mock()

    private val dataSet1: DataSet = mock()
    private val dataSet2: DataSet = mock()

    private val ds1 = "ds1"
    private val ds2 = "ds2"
    private val ou1 = "ou1"
    private val ou2 = "ou2"

    private val rootOrgUnits = listOf(ou1, ou2)
    private val allOrgUnits = rootOrgUnits.toSet()
    private val periods = listOf(Period.builder().periodId("202002").build())

    // Object to test
    private lateinit var bundleFactory: AggregatedDataCallBundleFactory

    @Before
    fun setUp() {
        whenever(dataSet1.uid()).doReturn(ds1)
        whenever(dataSet2.uid()).doReturn(ds2)
        whenever(dataSetSettings.specificSettings()).doReturn(emptyMap())
        whenever(periodManager.getPeriodsInRange(PeriodType.Monthly, 0, 0)).doReturn(emptyList())
        whenever(periodManager.getPeriodsInRange(any(), any(), any())).doReturn(periods)

        bundleFactory = AggregatedDataCallBundleFactory(
            dataSetRepository, organisationUnitRepository,
            dataSetSettingsObjectRepository, periodManager, aggregatedDataSyncStore, lastUpdatedCalculator
        )
    }

    @Test
    fun create_single_bundle_if_same_periods() {
        whenever(dataSet1.openFuturePeriods()).doReturn(1)
        whenever(dataSet1.periodType()).doReturn(PeriodType.Monthly)
        whenever(dataSet2.openFuturePeriods()).doReturn(1)
        whenever(dataSet2.periodType()).doReturn(PeriodType.Monthly)

        val bundles = bundleFactory.getBundlesInternal(
            listOf(dataSet1, dataSet2),
            dataSetSettings, rootOrgUnits, allOrgUnits, emptyMap()
        )

        assertThat(bundles.size).isEqualTo(1)
        assertThat(bundles[0].dataSets).containsExactly(dataSet1, dataSet2)
    }

    @Test
    fun create_different_bundles_if_different_periods() {
        whenever(dataSet1.openFuturePeriods()).doReturn(1)
        whenever(dataSet1.periodType()).doReturn(PeriodType.Monthly)
        whenever(dataSet2.openFuturePeriods()).doReturn(2)
        whenever(dataSet2.periodType()).doReturn(PeriodType.Monthly)

        val bundles = bundleFactory.getBundlesInternal(
            listOf(dataSet1, dataSet2),
            dataSetSettings, rootOrgUnits, allOrgUnits, emptyMap()
        )

        assertThat(bundles.size).isEqualTo(2)
    }
}

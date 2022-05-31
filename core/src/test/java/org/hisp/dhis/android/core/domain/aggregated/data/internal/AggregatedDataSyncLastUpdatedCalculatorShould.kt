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
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import java.util.*
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.dataset.DataSet
import org.junit.Before
import org.junit.Test

class AggregatedDataSyncLastUpdatedCalculatorShould {
    private val dataSet: DataSet = DataSetSamples.dataSet
    private val dataElementsHash = 1111111
    private val organisationUnitsHash = 22222222
    private val pastPeriods = 5
    private val syncLastUpdated = DateUtils.DATE_FORMAT.parse("2018-01-01T15:08:27.882")
    private val expectedLastUpdated = DateUtils.DATE_FORMAT.parse("2017-12-31T15:08:27.882")

    private val hashHelper: AggregatedDataSyncHashHelper = mock()
    private val syncValue = AggregatedDataSync.builder()
        .dataSet(dataSet.uid())
        .periodType(dataSet.periodType())
        .pastPeriods(pastPeriods)
        .futurePeriods(dataSet.openFuturePeriods())
        .dataElementsHash(dataElementsHash)
        .organisationUnitsHash(organisationUnitsHash)
        .lastUpdated(syncLastUpdated)
        .build()
    private lateinit var calculator: AggregatedDataSyncLastUpdatedCalculator

    @Before
    fun setUp() {
        whenever(hashHelper.getDataSetDataElementsHash(dataSet)).thenReturn(dataElementsHash)
        calculator = AggregatedDataSyncLastUpdatedCalculator(hashHelper)
    }

    @Test
    fun return_null_if_sync_value_null() {
        val lastUpdated = calculator.getLastUpdated(null, dataSet, 3, 5, 0)
        assertThat<Date>(lastUpdated).isNull()
    }

    @Test
    fun return_expected_last_updated_if_same_values() {
        val lastUpdated = calculator.getLastUpdated(
            syncValue,
            dataSet,
            pastPeriods,
            dataSet.openFuturePeriods()!!,
            organisationUnitsHash
        )
        assertThat(lastUpdated).isEqualTo(expectedLastUpdated)
    }

    @Test
    fun return_null_if_organisation_units_hash_changed() {
        val lastUpdated =
            calculator.getLastUpdated(syncValue, dataSet, pastPeriods, dataSet.openFuturePeriods()!!, 33333)
        assertThat(lastUpdated).isNull()
    }

    @Test
    fun return_null_if_data_set_elements_hash_changed() {
        whenever(hashHelper.getDataSetDataElementsHash(dataSet)).thenReturn(77777)

        val lastUpdated = calculator.getLastUpdated(
            syncValue,
            dataSet,
            pastPeriods,
            dataSet.openFuturePeriods()!!,
            organisationUnitsHash
        )
        assertThat(lastUpdated).isNull()
    }

    @Test
    fun return_null_if_future_periods_are_increased() {
        val lastUpdated = calculator.getLastUpdated(
            syncValue,
            dataSet,
            pastPeriods,
            dataSet.openFuturePeriods()!! + 1,
            organisationUnitsHash
        )
        assertThat(lastUpdated).isNull()
    }

    @Test
    fun return_expected_last_updated_if_future_periods_are_decreased() {
        val lastUpdated = calculator.getLastUpdated(
            syncValue,
            dataSet,
            pastPeriods,
            dataSet.openFuturePeriods()!! - 1,
            organisationUnitsHash
        )
        assertThat(lastUpdated).isEqualTo(expectedLastUpdated)
    }

    @Test
    fun return_null_if_past_periods_are_increased() {
        val lastUpdated = calculator.getLastUpdated(
            syncValue,
            dataSet,
            pastPeriods + 1,
            dataSet.openFuturePeriods()!!,
            organisationUnitsHash
        )
        assertThat<Date>(lastUpdated).isNull()
    }

    @Test
    fun return_expected_past_updated_if_future_periods_are_decreased() {
        val lastUpdated = calculator.getLastUpdated(
            syncValue,
            dataSet,
            pastPeriods - 1,
            dataSet.openFuturePeriods()!!,
            organisationUnitsHash
        )
        assertThat(lastUpdated).isEqualTo(expectedLastUpdated)
    }
}

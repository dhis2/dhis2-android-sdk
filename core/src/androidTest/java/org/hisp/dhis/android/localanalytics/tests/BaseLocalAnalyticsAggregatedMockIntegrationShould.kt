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
package org.hisp.dhis.android.localanalytics.tests

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.datavalue.DataValue
import org.hisp.dhis.android.core.datavalue.DataValueCollectionRepository
import org.junit.Test

internal abstract class BaseLocalAnalyticsAggregatedMockIntegrationShould : BaseLocalAnalyticsTest() {

    @Test
    fun count_data_values() {
        val dataValuesCount = d2.dataValueModule().dataValues().blockingCount()
        assertThat(dataValuesCount).isEqualTo(3000 * SizeFactor)
    }

    @Test
    fun raw_count_data_values() {
        val cursor = d2.databaseAdapter().rawQuery("SELECT Count(*) FROM DataValue")
        cursor.moveToFirst()
        val v = cursor.getInt(0)
        cursor.close()
        assertThat(v).isEqualTo(3000 * SizeFactor)
    }

    @Test
    fun count_data_values_for_data_element() {
        val firstDataValue = d2.dataValueModule().dataValues().one().blockingGet()
        val dataValuesCount = d2.dataValueModule().dataValues()
            .byDataElementUid().eq(firstDataValue.dataElement())
            .blockingCount()
        assertThat(dataValuesCount).isEqualTo(30 * SizeFactor)
    }

    @Test
    fun sum_data_values_for_data_element() {
        val dv = d2.dataValueModule().dataValues().one().blockingGet()
        val dataValues = d2.dataValueModule().dataValues()
            .byDataElementUid().eq(dv.dataElement())
            .blockingGet()
        val sum = dataValues.sumByDouble { it.value()!!.toDouble() }
        assertThat(sum).isFinite()
    }

    @Test
    fun avg_data_values_for_data_element() {
        val dv = d2.dataValueModule().dataValues().one().blockingGet()
        val dataValues = d2.dataValueModule().dataValues()
            .byDataElementUid().eq(dv.dataElement())
            .blockingGet()
        assertThat(getAvgValue(dataValues)).isFinite()
    }

    private fun getAvgValue(dataValues: List<DataValue>): Double {
        val sum = dataValues.sumByDouble { it.value()!!.toDouble() }
        return sum / dataValues.size
    }

    @Test
    fun count_data_values_for_data_element_for_ou_level_3() {
        val ou = d2.organisationUnitModule().organisationUnits()
            .byLevel().eq(3)
            .one().blockingGet()
        val firstDataValue = d2.dataValueModule().dataValues()
            .one().blockingGet()
        val dataValuesCount = d2.dataValueModule().dataValues()
            .byOrganisationUnitUid().eq(ou.uid())
            .byDataElementUid().eq(firstDataValue.dataElement())
            .blockingCount()
        assertThat(dataValuesCount).isEqualTo(10 * SizeFactor)
    }

    @Test
    fun count_data_values_for_data_element_for_ou_level_2_and_descendants() {
        assertThat(dataValuesForDataElementForOuAndDescendentsRepository(2).blockingCount()).isEqualTo(10 * SizeFactor)
    }

    @Test
    fun count_data_values_for_data_element_for_ou_level_1_and_descendants() {
        assertThat(dataValuesForDataElementForOuAndDescendentsRepository(1).blockingCount()).isEqualTo(30 * SizeFactor)
    }

    @Test
    fun avg_data_values_for_data_element_for_ou_level_2_and_descendants() {
        val dataValues = dataValuesForDataElementForOuAndDescendentsRepository(2).blockingGet()
        assertThat(getAvgValue(dataValues)).isFinite()
    }

    @Test
    fun avg_data_values_for_data_element_for_ou_level_1_and_descendants() {
        val dataValues = dataValuesForDataElementForOuAndDescendentsRepository(1).blockingGet()
        assertThat(getAvgValue(dataValues)).isFinite()
    }

    @Test
    fun avg_data_values_for_data_element_for_ou_level_2_and_descendants_group_by_period() {
        val dataValues = dataValuesForDataElementForOuAndDescendentsRepository(2).blockingGet()
        val groupedDataValues = dataValues.groupBy { it.period() }
        val groupedAverages = groupedDataValues.map { kv -> kv.key to getAvgValue(kv.value) }.toMap()
        assertThat(groupedAverages.size).isEqualTo(10 * SizeFactor)
    }

    @Test
    fun avg_data_values_for_data_element_for_ou_level_1_and_descendants_group_by_period() {
        val dataValues = dataValuesForDataElementForOuAndDescendentsRepository(1).blockingGet()
        val groupedDataValues = dataValues.groupBy { it.period() }
        val groupedAverages = groupedDataValues.map { kv -> kv.key to getAvgValue(kv.value) }.toMap()
        assertThat(groupedAverages.size).isEqualTo(10 * SizeFactor)
    }

    private fun dataValuesForDataElementForOuAndDescendentsRepository(level: Int): DataValueCollectionRepository {
        val ou3 = d2.organisationUnitModule().organisationUnits()
            .byLevel().eq(3)
            .one().blockingGet()
        val level2Uid = ou3.path()!!.split("/")[level]
        val ous2AndChildren = d2.organisationUnitModule().organisationUnits()
            .byPath().like(level2Uid)
            .blockingGet()
        val firstDataValue = d2.dataValueModule().dataValues()
            .one().blockingGet()
        return d2.dataValueModule().dataValues()
            .byOrganisationUnitUid().`in`(ous2AndChildren.map { it.uid() })
            .byDataElementUid().eq(firstDataValue.dataElement())
    }

    @Test
    fun count_data_values_for_data_element_for_ou_level_3_per_period() {
        val ou = d2.organisationUnitModule().organisationUnits()
            .byLevel().eq(3)
            .one().blockingGet()
        val firstDataValue = d2.dataValueModule().dataValues()
            .one().blockingGet()
        val dataValues = d2.dataValueModule().dataValues()
            .byOrganisationUnitUid().eq(ou.uid())
            .byDataElementUid().eq(firstDataValue.dataElement())
            .blockingGet()
        val dataValuesPerPeriod = dataValues.groupBy { it.period() }
        assertThat(dataValuesPerPeriod.size).isEqualTo(10 * SizeFactor)
    }
}

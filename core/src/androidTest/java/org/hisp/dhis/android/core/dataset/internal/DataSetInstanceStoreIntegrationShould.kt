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

package org.hisp.dhis.android.core.dataset.internal

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.datavalue.DataValue
import org.hisp.dhis.android.core.datavalue.internal.DataValueStore
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestMetadataDispatcher
import org.junit.After
import org.junit.Before
import org.junit.Test

class DataSetInstanceStoreIntegrationShould : BaseMockIntegrationTestMetadataDispatcher() {

    private lateinit var dataSetInstanceStore: DataSetInstanceStore
    private lateinit var dataValueStore: DataValueStore

    @Before
    fun setUp() {
        dataSetInstanceStore = DataSetInstanceStore.create(databaseAdapter)
        dataValueStore = DataValueStore.create(databaseAdapter)

        dataValueStore.delete()
    }

    @After
    fun tearDown() {
        dataValueStore.delete()
    }

    @Test
    fun should_prioritize_data_value_sync_states() {
        mapOf(
            listOf(State.SYNCED, State.TO_POST) to State.TO_POST,
            listOf(State.SYNCED, State.ERROR) to State.ERROR,
            listOf(State.SYNCED, State.WARNING) to State.WARNING,
            listOf(State.SYNCED, State.UPLOADING) to State.UPLOADING,
            listOf(State.TO_POST, State.ERROR) to State.ERROR,
            listOf(State.ERROR, State.TO_UPDATE) to State.ERROR,
            listOf(State.UPLOADING, State.TO_POST) to State.UPLOADING,
            listOf(State.UPLOADING, State.ERROR) to State.ERROR
        ).forEach { (valueStates, aggregated) ->
            insertDataValuesWithStates(valueStates[0], valueStates[1])
            checkSyncState(aggregated)
        }
    }

    private fun insertDataValuesWithStates(state1: State, state2: State) {
        val dataElements = d2.dataSetModule().dataSets()
            .withDataSetElements()
            .one()
            .blockingGet()
            .dataSetElements()!!.map { it.dataElement() }

        val orgunit = d2.organisationUnitModule().organisationUnits().one().blockingGet()!!

        val period = d2.periodModule().periodHelper().blockingGetPeriodForPeriodId("202208")

        val optionCombo = d2.categoryModule().categoryOptionCombos().one().blockingGet()

        val baseBuilder = DataValue.builder()
            .value("")
            .organisationUnit(orgunit.uid())
            .period(period.periodId()!!)
            .categoryOptionCombo(optionCombo.uid())
            .attributeOptionCombo(optionCombo.uid())

        dataValueStore.updateOrInsertWhere(
            baseBuilder
                .dataElement(dataElements[0].uid())
                .syncState(state1)
                .build()
        )

        dataValueStore.updateOrInsertWhere(
            baseBuilder
                .dataElement(dataElements[1].uid())
                .syncState(state2)
                .build()
        )
    }

    private fun checkSyncState(state: State) {
        val instances = d2.dataSetModule().dataSetInstances().blockingGet()

        assertThat(instances.size).isEqualTo(1)
        assertThat(instances.first().dataValueState()).isEqualTo(state)
    }
}

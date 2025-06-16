/*
 *  Copyright (c) 2004-2023, University of Oslo
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
package org.hisp.dhis.android.core.datavalue.internal

import com.google.common.truth.Truth
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.BaseRealIntegrationTest
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.datavalue.DataValue
import org.junit.Before

class DataValuePostCallRealIntegrationShould : BaseRealIntegrationTest() {
    private lateinit var dataValueStore: DataValueStore

    @Before
    override fun setUp() {
        super.setUp()
        dataValueStore = DataValueStoreImpl(d2.databaseAdapter())
    }

    // commented out since it is a flaky test that works against a real server.
    // @Test
    @Throws(Exception::class)
    fun dataValuesWithToPostState_shouldBeUploaded() = runTest {
        d2.userModule().logIn(username, password, url).blockingGet()

        d2.metadataModule().blockingDownload()
        d2.aggregatedModule().data().blockingDownload()

        val dataValue = getTestDataValueWith(State.TO_POST, 1)

        Truth.assertThat(insertToPostDataValue(dataValue)).isTrue()

        d2.dataValueModule().dataValues().blockingUpload()

        /*int importCountTotal = dataValueImportSummary.importCount().imported() +
                dataValueImportSummary.importCount().updated() +
                dataValueImportSummary.importCount().ignored();

        assertThat(importCountTotal == 1).isTrue();*/
    }

    // commented out since it is a flaky test that works against a real server.
    // @Test
    @Throws(Exception::class)
    fun dataValuesWithToUpdateState_shouldBeUploaded() = runTest {
        d2.metadataModule().blockingDownload()
        d2.aggregatedModule().data().blockingDownload()

        val dataValue = getTestDataValueWith(State.TO_UPDATE, 2)

        Truth.assertThat(insertToPostDataValue(dataValue)).isTrue()

        d2.dataValueModule().dataValues().blockingUpload()

        /*int importCountTotal = dataValueImportSummary.importCount().updated() +
                dataValueImportSummary.importCount().ignored();

        assertThat(importCountTotal == 1).isTrue();*/
    }

    private suspend fun insertToPostDataValue(dataValue: DataValue): Boolean {
        return (dataValueStore.insert(dataValue) > 0)
    }

    private fun getTestDataValueWith(state: State, value: Int): DataValue {
        val cocUid = d2.categoryModule().categoryOptionCombos().one().blockingGet()!!.uid()
        return DataValue.builder()
            .dataElement(d2.dataElementModule().dataElements().one().blockingGet()!!.uid())
            .categoryOptionCombo(cocUid)
            .attributeOptionCombo(cocUid)
            .period(d2.periodModule().periods().one().blockingGet()!!.periodId()!!)
            .organisationUnit(d2.organisationUnitModule().organisationUnits().one().blockingGet()!!.uid())
            .value(value.toString())
            .syncState(state)
            .build()
    }
}

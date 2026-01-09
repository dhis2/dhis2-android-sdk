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

import android.annotation.SuppressLint
import android.util.Log
import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.BaseRealIntegrationTest
import org.hisp.dhis.android.core.common.State

class DataValueWithDataSetRealIntegrationShould : BaseRealIntegrationTest() {

    // @Test
    fun upload_datavalue_for_shared_dataelement_to_both_datasets_v43() {
        uploadDataValuesForSharedDataElement(ServerConfig.V43_DEV)
    }

    // @Test
    fun upload_datavalue_for_shared_dataelement_to_both_datasets_v40() {
        uploadDataValuesForSharedDataElement(ServerConfig.V40_STABLE)
    }

    private fun uploadDataValuesForSharedDataElement(config: ServerConfig) {
        loginAndDownloadMetadata(config)

        val orgUnit1 = findOrgUnitForDataSet(config.dataSet1Uid)
        val orgUnit2 = findOrgUnitForDataSet(config.dataSet2Uid)

        val timestamp = System.currentTimeMillis()

        // Create DataValues for DataSet1
        val repo1Shared = createDataValue(
            config.period,
            orgUnit1,
            config.sharedDataElementUid,
            config.cocUid,
            config.cocUid,
            config.dataSet1Uid,
            timestamp,
        )
        val repo1Only = createDataValue(
            config.period,
            orgUnit1,
            config.dataElement1OnlyUid,
            config.cocDataElement1OnlyUid,
            config.cocUid,
            config.dataSet1Uid,
            timestamp + 1,
        )

        // Create DataValues for DataSet2
        val repo2Shared = createDataValue(
            config.period,
            orgUnit2,
            config.sharedDataElementUid,
            config.cocUid,
            config.cocUid,
            config.dataSet2Uid,
            timestamp + 2,
        )
        val repo2Only = createDataValue(
            config.period,
            orgUnit2,
            config.dataElement2OnlyUid,
            config.cocUid,
            config.cocUid,
            config.dataSet2Uid,
            timestamp + 3,
        )

        Log.d(TAG, "Uploading DataValues (expecting 2 POST requests)...")
        d2.dataValueModule().dataValues().blockingUpload()

        logAndAssertSynced("dataSet1/${config.sharedDataElementUid}", repo1Shared)
        logAndAssertSynced("dataSet1/${config.dataElement1OnlyUid}", repo1Only)
        logAndAssertSynced("dataSet2/${config.sharedDataElementUid}", repo2Shared)
        logAndAssertSynced("dataSet2/${config.dataElement2OnlyUid}", repo2Only)
    }

    @SuppressLint("CheckResult")
    private fun loginAndDownloadMetadata(config: ServerConfig) {
        d2.userModule().logIn(config.username, config.password, config.serverUrl).blockingGet()
        d2.metadataModule().blockingDownload()
    }

    private fun findOrgUnitForDataSet(dataSetUid: String): String {
        val orgUnit = d2.organisationUnitModule().organisationUnits()
            .byDataSetUids(listOf(dataSetUid))
            .one().blockingGet()
        assertThat(orgUnit).isNotNull()
        return orgUnit!!.uid()
    }

    private fun createDataValue(
        period: String,
        orgUnit: String,
        dataElement: String,
        coc: String,
        aoc: String,
        dataSet: String,
        timestamp: Long,
    ): org.hisp.dhis.android.core.datavalue.DataValueObjectRepository {
        val value = (timestamp % 1000).toString()
        val repo = d2.dataValueModule().dataValues()
            .value(period, orgUnit, dataElement, coc, aoc, dataSet)
        repo.blockingSet(value)
        Log.d(TAG, "Created: $dataSet/$dataElement = $value")
        return repo
    }

    private fun logAndAssertSynced(
        label: String,
        repo: org.hisp.dhis.android.core.datavalue.DataValueObjectRepository,
    ) {
        val state = repo.blockingGet()?.syncState()
        Log.d(TAG, "  $label: $state")
        assertThat(state).isEqualTo(State.SYNCED)
    }

    private data class ServerConfig(
        val serverUrl: String,
        val username: String,
        val password: String,
        val sharedDataElementUid: String,
        val dataElement1OnlyUid: String,
        val dataElement2OnlyUid: String,
        val dataSet1Uid: String,
        val dataSet2Uid: String,
        val cocUid: String,
        val cocDataElement1OnlyUid: String,
        val period: String,
    ) {
        companion object {
            val V43_DEV = ServerConfig(
                serverUrl = "https://play.im.dhis2.org/dev/",
                username = "android",
                password = "Android123",
                sharedDataElementUid = "LtMRWceAq1g",
                dataElement1OnlyUid = "s46m5MS0hxu",
                dataElement2OnlyUid = "N9vniUuCcqY",
                dataSet1Uid = "BfMAe6Itzgt",
                dataSet2Uid = "EDzMBk0RRji",
                cocUid = "HllvX50cXC0",
                cocDataElement1OnlyUid = "Prlt0C1RF0s",
                period = "202512",
            )

            val V40_STABLE = ServerConfig(
                serverUrl = "https://play.im.dhis2.org/stable-2-40-10",
                username = "android",
                password = "Android123",
                sharedDataElementUid = "KArt1uZah8R",
                dataElement1OnlyUid = "s46m5MS0hxu",
                dataElement2OnlyUid = "N9vniUuCcqY",
                dataSet1Uid = "BfMAe6Itzgt",
                dataSet2Uid = "EDzMBk0RRji",
                cocUid = "HllvX50cXC0",
                cocDataElement1OnlyUid = "Prlt0C1RF0s",
                period = "202512",
            )
        }
    }

    companion object {
        private const val TAG = "DataValueRealIntegration"
    }
}

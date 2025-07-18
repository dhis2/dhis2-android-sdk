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
package org.hisp.dhis.android.core.wipe

import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.d2.internal.DhisAndroidSdkKoinContext.koin
import org.hisp.dhis.android.core.data.database.DatabaseAssert.Companion.assertThatDatabase
import org.hisp.dhis.android.core.data.datastore.KeyValuePairSamples
import org.hisp.dhis.android.core.data.maps.MapLayerImageryProviderSamples
import org.hisp.dhis.android.core.data.maps.MapLayerSamples
import org.hisp.dhis.android.core.data.sms.SMSOngoingSubmissionSample
import org.hisp.dhis.android.core.data.trackedentity.ownership.ProgramTempOwnerSamples
import org.hisp.dhis.android.core.data.tracker.importer.internal.TrackerJobObjectSamples
import org.hisp.dhis.android.core.data.usecase.stock.InternalStockUseCaseSamples
import org.hisp.dhis.android.core.data.usecase.stock.InternalStockUseCaseTransactionSamples
import org.hisp.dhis.android.core.datastore.KeyValuePair
import org.hisp.dhis.android.core.datastore.internal.LocalDataStoreStore
import org.hisp.dhis.android.core.datavalue.DataValueConflict
import org.hisp.dhis.android.core.datavalue.internal.DataValueConflictStore
import org.hisp.dhis.android.core.fileresource.FileResource
import org.hisp.dhis.android.core.fileresource.internal.FileResourceStore
import org.hisp.dhis.android.core.imports.TrackerImportConflict
import org.hisp.dhis.android.core.imports.internal.TrackerImportConflictStore
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.maintenance.internal.D2ErrorStore
import org.hisp.dhis.android.core.map.layer.internal.MapLayerImageryProviderStore
import org.hisp.dhis.android.core.map.layer.internal.MapLayerStore
import org.hisp.dhis.android.core.sms.data.localdbrepository.internal.SMSConfigStore
import org.hisp.dhis.android.core.sms.data.localdbrepository.internal.SMSOngoingSubmissionStore
import org.hisp.dhis.android.core.trackedentity.ownership.ProgramTempOwnerStore
import org.hisp.dhis.android.core.tracker.importer.internal.TrackerJobObjectStore
import org.hisp.dhis.android.core.usecase.stock.internal.StockUseCaseStore
import org.hisp.dhis.android.core.usecase.stock.internal.StockUseCaseTransactionLinkStore
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestEmptyDispatcher
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class WipeDBCallMockIntegrationShould : BaseMockIntegrationTestEmptyDispatcher() {

    @Test
    fun have_empty_database_when_wipe_db_after_sync_data() = runTest {
        activateSMSModule()
        givenAMetadataInDatabase()
        givenDataInDatabase()
        givenOthersInDatabase()
        val d2Dao = databaseAdapter.getCurrentDatabase().d2Dao()

        assertThatDatabase(d2Dao).isFull()

        d2.wipeModule().wipeEverything()

        assertThatDatabase(d2Dao).isEmpty()
    }

    private fun activateSMSModule() {
        d2.smsModule().configCase().setModuleEnabled(true).blockingAwait()
    }

    private fun givenAMetadataInDatabase() {
        d2.metadataModule().blockingDownload()
        d2.trackedEntityModule().reservedValueManager().blockingDownloadAllReservedValues(1)
    }

    private fun givenDataInDatabase() {
        d2.eventModule().eventDownloader().limit(1).blockingDownload()
        d2.trackedEntityModule().trackedEntityInstanceDownloader().limit(1).blockingDownload()
        d2.aggregatedModule().data().blockingDownload()
        d2.dataStoreModule().dataStoreDownloader().blockingDownload()
    }

    private suspend fun givenOthersInDatabase() {
        koin.get<D2ErrorStore>().insert(
            D2Error.builder()
                .errorCode(D2ErrorCode.API_RESPONSE_PROCESS_ERROR)
                .errorDescription("Sample error")
                .build(),
        )
        koin.get<TrackerImportConflictStore>().insert(TrackerImportConflict.builder().build())
        koin.get<FileResourceStore>().insert(FileResource.builder().uid("uid").build())
        koin.get<TrackerJobObjectStore>().insert(TrackerJobObjectSamples.get1())
        koin.get<DataValueConflictStore>().insert(DataValueConflict.builder().build())
        koin.get<LocalDataStoreStore>().insert(
            KeyValuePair.builder()
                .key("key1")
                .value("value1")
                .build(),
        )
        koin.get<LocalDataStoreStore>().insert(
            KeyValuePair.builder()
                .key("key2")
                .value("value2")
                .build(),
        )
        koin.get<ProgramTempOwnerStore>().insert(ProgramTempOwnerSamples.programTempOwner)

        koin.get<SMSConfigStore>().insert(KeyValuePairSamples.keyValuePairSample)
        koin.get<SMSOngoingSubmissionStore>().insert(SMSOngoingSubmissionSample.get)

        koin.get<StockUseCaseStore>().insert(
            InternalStockUseCaseSamples.get()
                .toBuilder().uid("lxAQ7Zs9VYR").build(),
        )
        koin.get<StockUseCaseTransactionLinkStore>().insert(
            InternalStockUseCaseTransactionSamples.get()
                .toBuilder().programUid("lxAQ7Zs9VYR").build(),
        )

        koin.get<MapLayerStore>().insert(MapLayerSamples.get())
        koin.get<MapLayerImageryProviderStore>().insert(MapLayerImageryProviderSamples.get())
    }
}

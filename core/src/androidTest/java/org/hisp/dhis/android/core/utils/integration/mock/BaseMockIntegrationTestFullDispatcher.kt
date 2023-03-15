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
package org.hisp.dhis.android.core.utils.integration.mock

import org.hisp.dhis.android.core.data.imports.TrackerImportConflictSamples
import org.hisp.dhis.android.core.data.maintenance.D2ErrorSamples
import org.hisp.dhis.android.core.datastore.KeyValuePair
import org.hisp.dhis.android.core.datastore.internal.LocalDataStoreStore.create
import org.hisp.dhis.android.core.imports.ImportStatus
import org.hisp.dhis.android.core.imports.internal.TrackerImportConflictStoreImpl
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent
import org.hisp.dhis.android.core.maintenance.internal.D2ErrorStore
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
abstract class BaseMockIntegrationTestFullDispatcher : BaseMockIntegrationTest() {
    companion object {
        @BeforeClass
        @JvmStatic
        fun setUpClass() {
            val isNewInstance = setUpClass(MockIntegrationTestDatabaseContent.FullDispatcher)
            if (isNewInstance) {
                dhis2MockServer.setRequestDispatcher()
                freshLogin()
                downloadMetadata()
                downloadTrackedEntityInstances()
                downloadEvents()
                downloadAggregatedData()
                storeSomeD2Errors()
                storeSomeConflicts()
                storeSomeKeyValuesInLocalDataStore()
            }
        }

        @AfterClass
        @JvmStatic
        fun tearDownClass() {
            dhis2MockServer.shutdown()
        }

        private fun freshLogin() {
            try {
                d2.userModule().logOut().blockingAwait()
            } catch (e: RuntimeException) {
                // Do nothing
            } finally {
                d2.userModule().blockingLogIn("android", "Android123", dhis2MockServer.baseEndpoint)
            }
        }

        private fun downloadMetadata() {
            d2.metadataModule().blockingDownload()
        }

        private fun downloadTrackedEntityInstances() {
            d2.trackedEntityModule().trackedEntityInstanceDownloader().limit(2).blockingDownload()
        }

        private fun downloadEvents() {
            d2.eventModule().eventDownloader().limit(2).blockingDownload()
        }

        private fun downloadAggregatedData() {
            d2.aggregatedModule().data().blockingDownload()
        }

        private fun storeSomeD2Errors() {
            val d2ErrorStore = D2ErrorStore.create(databaseAdapter)
            d2ErrorStore.insert(D2ErrorSamples.get())
            d2ErrorStore.insert(
                D2Error.builder()
                    .errorComponent(D2ErrorComponent.SDK)
                    .errorCode(D2ErrorCode.BAD_CREDENTIALS)
                    .url("http://dhis2.org/api/programs/uid")
                    .errorDescription("Different server offline")
                    .httpErrorCode(402)
                    .build()
            )
        }

        private fun storeSomeConflicts() {
            val trackerImportConflictStore = TrackerImportConflictStoreImpl.create(databaseAdapter)
            trackerImportConflictStore.insert(
                TrackerImportConflictSamples.get().toBuilder()
                    .trackedEntityInstance(null)
                    .enrollment(null)
                    .event(null)
                    .build()
            )
            trackerImportConflictStore.insert(
                TrackerImportConflictSamples.get().toBuilder()
                    .conflict("conflict_2")
                    .value("value_2")
                    .trackedEntityInstance("nWrB0TfWlvh")
                    .enrollment("enroll2")
                    .event("event2")
                    .tableReference("table_reference_2")
                    .errorCode("error_code_2")
                    .status(ImportStatus.ERROR)
                    .build()
            )
        }

        private fun storeSomeKeyValuesInLocalDataStore() {
            val dataStore = create(
                databaseAdapter
            )
            dataStore.insert(
                KeyValuePair.builder()
                    .key("key1")
                    .value("value1")
                    .build()
            )
            dataStore.insert(
                KeyValuePair.builder()
                    .key("key2")
                    .value("value2")
                    .build()
            )
        }
    }
}

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
package org.hisp.dhis.android.testapp.trackedentity.search

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.d2.internal.DhisAndroidSdkKoinContext.koin
import org.hisp.dhis.android.core.settings.SynchronizationSettings
import org.hisp.dhis.android.core.settings.internal.SynchronizationSettingStore
import org.hisp.dhis.android.core.tracker.TrackerExporterVersion
import org.hisp.dhis.android.core.tracker.TrackerImporterVersion
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestMetadataEnqueable
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
abstract class TrackedEntityOnlineQueryCollectionRepositoryBaseIntegrationShould :
    BaseMockIntegrationTestMetadataEnqueable() {

    abstract val importerVersion: TrackerImporterVersion
    abstract val exporterVersion: TrackerExporterVersion

    abstract val filterByEventFile: String
    abstract val responseFile: String

    private lateinit var initSyncParams: SynchronizationSettings
    private val syncStore: SynchronizationSettingStore = koin.get()

    @Before
    fun setUp() {
        runBlocking {
            initSyncParams = syncStore.selectFirst()!!
            val testParams = initSyncParams.toBuilder().trackerImporterVersion(importerVersion)
                .trackerExporterVersion(exporterVersion).build()
            syncStore.delete()
            syncStore.insert(testParams)
        }
    }

    @After
    fun tearDown() {
        runBlocking {
            d2.wipeModule().wipeData()
            syncStore.delete()
            syncStore.insert(initSyncParams)
        }
    }

    @Test
    fun find_online_blocking() {
        dhis2MockServer.enqueueMockResponse(responseFile)

        val trackedEntityInstances = d2.trackedEntityModule().trackedEntityInstanceQuery()
            .onlineOnly()
            .blockingGet()

        assertThat(trackedEntityInstances.size).isEqualTo(2)
    }

    @Test
    fun find_by_data_value() {
        dhis2MockServer.enqueueMockResponse(filterByEventFile)
        dhis2MockServer.enqueueMockResponse(responseFile)

        val trackedEntityInstances = d2.trackedEntityModule().trackedEntityInstanceQuery()
            .byProgram().eq("programId")
            .byDataValue("dataElementId").eq("value")
            .onlineOnly()
            .blockingGet()

        assertThat(trackedEntityInstances.size).isEqualTo(2)
    }
}

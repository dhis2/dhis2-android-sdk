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
package org.hisp.dhis.android.core.event.internal

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.d2.internal.DhisAndroidSdkKoinContext.koin
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.data.trackedentity.TrackedEntityDataValueSamples
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.settings.SynchronizationSettings
import org.hisp.dhis.android.core.settings.internal.SynchronizationSettingStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityDataValueStore
import org.hisp.dhis.android.core.tracker.TrackerExporterVersion
import org.hisp.dhis.android.core.tracker.TrackerImporterVersion
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestMetadataEnqueable
import org.hisp.dhis.android.persistence.event.EventStoreImpl
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

abstract class EventPostBaseMockIntegrationShould : BaseMockIntegrationTestMetadataEnqueable() {
    abstract val importerVersion: TrackerImporterVersion
    abstract val exporterVersion: TrackerExporterVersion
    abstract val importConflictsFile1: List<String>
    abstract val importConflictsFile2: List<String>

    private val event1Id = "event1Id"
    private val event2Id = "event2Id"
    private val event3Id = "event3Id"
    private val event4Id = "event4Id"

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
    fun handle_import_conflicts_correctly() = runTest {
        storeEvents()
        importConflictsFile1.forEach { dhis2MockServer.enqueueMockResponse(it) }
        d2.eventModule().events().blockingUpload()
        assertThat(d2.importModule().trackerImportConflicts().blockingCount()).isEqualTo(3)
    }

    @Test
    fun delete_old_import_conflicts() = runTest {
        storeEvents()
        importConflictsFile1.forEach { dhis2MockServer.enqueueMockResponse(it) }
        d2.eventModule().events().blockingUpload()
        assertThat(d2.importModule().trackerImportConflicts().blockingCount()).isEqualTo(3)

        eventStore.setSyncState(event1Id, State.TO_POST)
        eventStore.setSyncState(event2Id, State.TO_POST)
        eventStore.setSyncState(event3Id, State.TO_POST)
        eventStore.setAggregatedSyncState(event1Id, State.TO_POST)
        eventStore.setAggregatedSyncState(event2Id, State.TO_POST)
        eventStore.setAggregatedSyncState(event3Id, State.TO_POST)

        importConflictsFile2.forEach { dhis2MockServer.enqueueMockResponse(it) }
        d2.eventModule().events().blockingUpload()
        assertThat(d2.importModule().trackerImportConflicts().blockingCount()).isEqualTo(2)
    }

    @Test
    fun handle_event_deletions() = runTest {
        storeEvents()
        assertThat(d2.eventModule().events().blockingCount()).isEqualTo(4)
        d2.eventModule().events().uid("event1Id").blockingDelete()

        importConflictsFile2.forEach { dhis2MockServer.enqueueMockResponse(it) }
        d2.eventModule().events().blockingUpload()

        assertThat(d2.eventModule().events().blockingCount()).isEqualTo(3)
    }

    private suspend fun storeEvents() {
        val orgUnit = d2.organisationUnitModule().organisationUnits().one().blockingGet()!!
        val program = d2.programModule().programs().one().blockingGet()!!
        val programStage = d2.programModule().programStages().one().blockingGet()!!
        val dataValue1 = TrackedEntityDataValueSamples.get().toBuilder().event(event1Id).build()

        val event1 = Event.builder()
            .uid(event1Id)
            .organisationUnit(orgUnit.uid())
            .program(program.uid())
            .programStage(programStage.uid())
            .syncState(State.TO_POST)
            .aggregatedSyncState(State.TO_POST)
            .trackedEntityDataValues(listOf(dataValue1))
            .build()

        val dataValue2 = TrackedEntityDataValueSamples.get().toBuilder().event(event2Id).build()

        val event2 = Event.builder()
            .uid(event2Id)
            .organisationUnit(orgUnit.uid())
            .program(program.uid())
            .programStage(programStage.uid())
            .syncState(State.TO_POST)
            .aggregatedSyncState(State.TO_POST)
            .trackedEntityDataValues(listOf(dataValue2))
            .build()

        val dataValue3 = TrackedEntityDataValueSamples.get().toBuilder().event(event3Id).build()

        val event3 = Event.builder()
            .uid(event3Id)
            .organisationUnit(orgUnit.uid())
            .program(program.uid())
            .programStage(programStage.uid())
            .syncState(State.TO_POST)
            .aggregatedSyncState(State.TO_POST)
            .trackedEntityDataValues(listOf(dataValue3))
            .build()

        val dataValue4 = TrackedEntityDataValueSamples.get().toBuilder().event(event4Id).build()

        val event4 = Event.builder()
            .uid(event4Id)
            .organisationUnit(orgUnit.uid())
            .program(program.uid())
            .programStage(programStage.uid())
            .syncState(State.ERROR)
            .aggregatedSyncState(State.ERROR)
            .trackedEntityDataValues(listOf(dataValue4))
            .build()

        eventStore.insert(event1)
        eventStore.insert(event2)
        eventStore.insert(event3)
        eventStore.insert(event4)

        val tedvStore: TrackedEntityDataValueStore = koin.get()
        tedvStore.insert(dataValue1)
        tedvStore.insert(dataValue2)
        tedvStore.insert(dataValue3)
        tedvStore.insert(dataValue4)

        assertThat(d2.eventModule().events().blockingCount()).isEqualTo(4)
    }

    companion object {
        private lateinit var eventStore: EventStore

        @BeforeClass
        @JvmStatic
        fun setUpClass() {
            BaseMockIntegrationTestMetadataEnqueable.setUpClass()
            eventStore = EventStoreImpl(objects.databaseAdapter)
        }
    }
}

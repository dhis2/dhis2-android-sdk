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
package org.hisp.dhis.android.core.tracker.exporter

import androidx.sqlite.db.SupportSQLiteQuery
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutorMock
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.access.internal.AppDatabase
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableDataHandlerParams
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitNetworkHandler
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitStore
import org.hisp.dhis.android.core.program.internal.ProgramDataDownloadParams
import org.hisp.dhis.android.core.relationship.internal.RelationshipDownloadAndPersistCallFactory
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemRelatives
import org.hisp.dhis.android.core.systeminfo.internal.SystemInfoModuleDownloader
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.internal.TrackerQueryBundle
import org.hisp.dhis.android.core.user.internal.UserOrganisationUnitLinkStore
import org.hisp.dhis.android.persistence.common.daos.D2Dao
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class TrackerDownloadCallShould {

    private val userOrganisationUnitLinkStore: UserOrganisationUnitLinkStore = mock()
    private val systemInfoModuleDownloader: SystemInfoModuleDownloader = mock()
    private val relationshipDownloadAndPersistCallFactory: RelationshipDownloadAndPersistCallFactory = mock()
    private val coroutineAPICallExecutor = CoroutineAPICallExecutorMock()
    private val organisationUnitStore: OrganisationUnitStore = mock()
    private val organisationUnitNetworkHandler: OrganisationUnitNetworkHandler = mock()
    private val databaseAdapter: DatabaseAdapter = mock()
    private val appDatabase: AppDatabase = mock()
    private val d2Dao: D2Dao = mock()

    private lateinit var call: TestTrackerDownloadCall

    @Before
    fun setUp() = runTest {
        whenever(databaseAdapter.getCurrentDatabase()).doReturn(appDatabase)
        whenever(appDatabase.d2Dao()).doReturn(d2Dao)
        whenever(userOrganisationUnitLinkStore.count()).doReturn(1)

        call = TestTrackerDownloadCall(
            userOrganisationUnitLinkStore,
            systemInfoModuleDownloader,
            relationshipDownloadAndPersistCallFactory,
            coroutineAPICallExecutor,
            organisationUnitStore,
            organisationUnitNetworkHandler,
            databaseAdapter,
        )
    }

    @Test
    fun download_missing_org_units_when_query_returns_uids() = runTest {
        val missingOrgUnitUid = "missingOrgUnit1"
        val orgUnit = OrganisationUnit.builder().uid(missingOrgUnitUid).build()
        val payload: Payload<OrganisationUnit> = mock()

        whenever(d2Dao.stringListRawQuery(any<SupportSQLiteQuery>()))
            .doReturn(listOf(missingOrgUnitUid))
        whenever(organisationUnitNetworkHandler.getOrganisationUnitsByUid(setOf(missingOrgUnitUid)))
            .doReturn(payload)
        whenever(payload.items).doReturn(listOf(orgUnit))

        val params = ProgramDataDownloadParams.builder().build()
        call.download(params).toList()

        verify(organisationUnitNetworkHandler).getOrganisationUnitsByUid(setOf(missingOrgUnitUid))
        verify(organisationUnitStore).updateOrInsert(listOf(orgUnit))
    }

    @Test
    fun not_call_network_when_no_missing_org_units() = runTest {
        whenever(d2Dao.stringListRawQuery(any<SupportSQLiteQuery>()))
            .doReturn(emptyList())

        val params = ProgramDataDownloadParams.builder().build()
        call.download(params).toList()

        verify(organisationUnitNetworkHandler, never()).getOrganisationUnitsByUid(any())
        verify(organisationUnitStore, never()).updateOrInsert(any<Collection<OrganisationUnit>>())
    }

    @Test
    fun download_multiple_missing_org_units() = runTest {
        val missingUids = listOf("orgUnit1", "orgUnit2", "orgUnit3")
        val orgUnits = missingUids.map { OrganisationUnit.builder().uid(it).build() }
        val payload: Payload<OrganisationUnit> = mock()

        whenever(d2Dao.stringListRawQuery(any<SupportSQLiteQuery>()))
            .doReturn(missingUids)
        whenever(organisationUnitNetworkHandler.getOrganisationUnitsByUid(missingUids.toSet()))
            .doReturn(payload)
        whenever(payload.items).doReturn(orgUnits)

        val params = ProgramDataDownloadParams.builder().build()
        call.download(params).toList()

        verify(organisationUnitNetworkHandler).getOrganisationUnitsByUid(missingUids.toSet())
        verify(organisationUnitStore).updateOrInsert(orgUnits)
    }

    @Test
    fun emit_progress_after_downloading_missing_org_units() = runTest {
        whenever(d2Dao.stringListRawQuery(any<SupportSQLiteQuery>()))
            .doReturn(emptyList())

        val params = ProgramDataDownloadParams.builder().build()
        val progressList = call.download(params).toList()

        assertThat(progressList).isNotEmpty()
        assertThat(progressList.last().isComplete).isTrue()
    }
}

private class TestTrackerDownloadCall(
    userOrganisationUnitLinkStore: UserOrganisationUnitLinkStore,
    systemInfoModuleDownloader: SystemInfoModuleDownloader,
    relationshipDownloadAndPersistCallFactory: RelationshipDownloadAndPersistCallFactory,
    coroutineAPICallExecutor: CoroutineAPICallExecutorMock,
    organisationUnitStore: OrganisationUnitStore,
    organisationUnitNetworkHandler: OrganisationUnitNetworkHandler,
    databaseAdapter: DatabaseAdapter,
) : TrackerDownloadCall<TrackedEntityInstance, TrackerQueryBundle>(
    userOrganisationUnitLinkStore,
    systemInfoModuleDownloader,
    relationshipDownloadAndPersistCallFactory,
    coroutineAPICallExecutor,
    organisationUnitStore,
    organisationUnitNetworkHandler,
    databaseAdapter,
) {
    override suspend fun getBundles(params: ProgramDataDownloadParams): List<TrackerQueryBundle> = emptyList()

    override suspend fun getPayloadResult(
        query: TrackerAPIQuery,
    ): Result<Payload<TrackedEntityInstance>, D2Error> {
        val emptyPayload: Payload<TrackedEntityInstance> = object : Payload<TrackedEntityInstance> {
            override val pager: Any? = null
            override val items: List<TrackedEntityInstance> = emptyList()

            @Deprecated("Use pager attribute instead", replaceWith = ReplaceWith("pager"))
            override fun pager(): Any? = null

            @Deprecated("Use items attribute instead", replaceWith = ReplaceWith("items"))
            override fun items(): List<TrackedEntityInstance> = emptyList()
        }
        return Result.Success(emptyPayload)
    }

    override suspend fun persistItems(
        items: List<TrackedEntityInstance>,
        params: IdentifiableDataHandlerParams,
        relatives: RelationshipItemRelatives,
    ) { }

    override suspend fun updateLastUpdated(bundle: TrackerQueryBundle) { }

    override suspend fun queryByUids(
        bundle: TrackerQueryBundle,
        overwrite: Boolean,
        relatives: RelationshipItemRelatives,
    ): ItemsWithPagingResult = ItemsWithPagingResult(0, true, null, true)

    override suspend fun getQuery(
        bundle: TrackerQueryBundle,
        program: String?,
        orgunitUid: String?,
        limit: Int,
    ): TrackerAPIQuery? = null
}

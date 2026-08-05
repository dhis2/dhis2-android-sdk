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
package org.hisp.dhis.android.core.trackedentity.internal

import io.ktor.http.HttpStatusCode
import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutor
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableDataHandlerParams
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.fileresource.FileResourceDataDomainType
import org.hisp.dhis.android.core.fileresource.FileResourceDomainType
import org.hisp.dhis.android.core.fileresource.internal.FileResourceDownloadCall
import org.hisp.dhis.android.core.fileresource.internal.FileResourceDownloadParams
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitNetworkHandler
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitStore
import org.hisp.dhis.android.core.program.internal.ProgramDataDownloadParams
import org.hisp.dhis.android.core.relationship.internal.RelationshipDownloadAndPersistCallFactory
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemRelatives
import org.hisp.dhis.android.core.systeminfo.internal.SystemInfoModuleDownloader
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.search.TrackedEntityInstanceQueryCollectionRepository
import org.hisp.dhis.android.core.tracker.exporter.DownloadOrgunit
import org.hisp.dhis.android.core.tracker.exporter.TrackerAPIQuery
import org.hisp.dhis.android.core.tracker.exporter.TrackerDownloadCall
import org.hisp.dhis.android.core.tracker.importer.internal.TrackerImporterBreakTheGlassHelper
import org.hisp.dhis.android.core.user.internal.UserOrganisationUnitLinkStore
import org.koin.core.annotation.Singleton
import java.util.Date

@Singleton
@Suppress("LongParameterList", "TooManyFunctions")
internal class TrackedEntityInstanceDownloadCall(
    userOrganisationUnitLinkStore: UserOrganisationUnitLinkStore,
    systemInfoModuleDownloader: SystemInfoModuleDownloader,
    relationshipDownloadAndPersistCallFactory: RelationshipDownloadAndPersistCallFactory,
    private val coroutineCallExecutor: CoroutineAPICallExecutor,
    organisationUnitStore: OrganisationUnitStore,
    organisationUnitNetworkHandler: OrganisationUnitNetworkHandler,
    databaseAdapter: DatabaseAdapter,
    fileResourceDownloadCall: FileResourceDownloadCall,
    private val queryFactory: TrackerQueryBundleFactory,
    private val trackerCallFactory: TrackerParentCallFactory,
    private val persistenceCallFactory: TrackedEntityInstancePersistenceCallFactory,
    private val lastUpdatedManager: TrackedEntityInstanceLastUpdatedManager,
    private val teiQueryCollectionRepository: TrackedEntityInstanceQueryCollectionRepository,
    private val breakTheGlassHelper: TrackerImporterBreakTheGlassHelper,
) : TrackerDownloadCall<TrackedEntityInstance, TrackerQueryBundle>(
    userOrganisationUnitLinkStore,
    systemInfoModuleDownloader,
    relationshipDownloadAndPersistCallFactory,
    coroutineCallExecutor,
    organisationUnitStore,
    organisationUnitNetworkHandler,
    databaseAdapter,
    fileResourceDownloadCall,
) {
    override suspend fun getBundles(params: ProgramDataDownloadParams): List<TrackerQueryBundle> {
        return queryFactory.getQueries(params)
    }

    override suspend fun getPayloadResult(
        query: TrackerAPIQuery,
    ): Result<Payload<TrackedEntityInstance>, D2Error> {
        return coroutineCallExecutor.wrap(storeError = true) {
            trackerCallFactory.getTrackedEntityCall().getCollectionCall(query)
        }
    }

    override suspend fun persistItems(
        items: List<TrackedEntityInstance>,
        params: IdentifiableDataHandlerParams,
        relatives: RelationshipItemRelatives,
    ) {
        persistenceCallFactory.persistTEIs(items, params, relatives)
    }

    override suspend fun updateLastUpdated(bundle: TrackerQueryBundle, syncDate: Date) {
        lastUpdatedManager.update(bundle, syncDate)
    }

    override fun getFileResourceDownloadParams(
        items: List<TrackedEntityInstance>,
        program: String?,
    ): FileResourceDownloadParams {
        return FileResourceDownloadParams(
            domainTypes = listOf(FileResourceDomainType.DATA_VALUE),
            dataDomainTypes = listOf(FileResourceDataDomainType.TRACKER),
            trackedEntityUids = items.map { it.uid },
            trackedEntityAttributeUids = items
                .flatMap { it.trackedEntityAttributeValues().orEmpty() }
                .map { it.trackedEntityAttribute() }
                .distinct(),
            programUids = listOfNotNull(program),
        )
    }

    override suspend fun queryByUids(
        bundle: TrackerQueryBundle,
        overwrite: Boolean,
        downloadFileResources: Boolean,
        relatives: RelationshipItemRelatives,
    ): ItemsWithPagingResult {
        val result = ItemsWithPagingResult(0, true, null, false)

        val teiQuery = TrackerAPIQuery(
            commonParams = bundle.commonParams,
            programStatus = bundle.programStatus,
        )

        val useEntityEndpoint = teiQuery.commonParams.program != null

        try {
            val teisList = mutableListOf<TrackedEntityInstance>()

            for (uid in bundle.commonParams.uids) {
                val tei = querySingleTei(uid, useEntityEndpoint, teiQuery).getOrThrow()

                if (tei != null) {
                    teisList.add(tei)
                    result.count++
                }
            }

            if (teisList.isNotEmpty()) {
                val persistParams = IdentifiableDataHandlerParams(
                    hasAllAttributes = !useEntityEndpoint,
                    overwrite = overwrite,
                    asRelationship = false,
                    program = teiQuery.commonParams.program,
                )

                persistItems(teisList, persistParams, relatives)

                downloadFileResourcesIfEnabled(
                    enabled = downloadFileResources,
                    items = teisList,
                    program = teiQuery.commonParams.program,
                )
            }
        } catch (d2Error: D2Error) {
            result.successfulSync = false
            result.d2Error = d2Error
        }

        return result
    }

    private suspend fun querySingleTei(
        uid: String,
        useEntityEndpoint: Boolean,
        query: TrackerAPIQuery,
    ): Result<TrackedEntityInstance?, D2Error> {
        if (!useEntityEndpoint) {
            val collectionQuery = query.copy(uids = listOf(uid))
            return coroutineCallExecutor.wrap(storeError = true) {
                trackerCallFactory.getTrackedEntityCall().getCollectionCall(collectionQuery)
            }.map { it.items.firstOrNull() }
        }

        val result = coroutineCallExecutor.wrap(
            storeError = true,
            errorCatcher = TrackedEntityInstanceCallErrorCatcher(),
        ) {
            trackerCallFactory.getTrackedEntityCall().getEntityCall(uid, query)
        }

        return if (result is Result.Failure && result.failure.httpErrorCode() == HttpStatusCode.NotFound.value) {
            checkOwnershipOnNotFound(uid, query, result.failure)
        } else {
            result
        }
    }

    private suspend fun checkOwnershipOnNotFound(
        uid: String,
        query: TrackerAPIQuery,
        originalError: D2Error,
    ): Result<TrackedEntityInstance?, D2Error> {
        val program = query.commonParams.program ?: return Result.Failure(originalError)

        val queryWithoutProgram = TrackerAPIQuery(
            commonParams = query.commonParams.copy(program = null),
        )

        val teiResult = coroutineCallExecutor.wrap(storeError = false) {
            trackerCallFactory.getTrackedEntityCall().getEntityCall(uid, queryWithoutProgram)
        }

        return when (teiResult) {
            is Result.Failure -> Result.Failure(originalError)
            is Result.Success -> {
                val tei = teiResult.value
                val programOwner = tei.programOwners()?.find { it.program() == program }

                if (programOwner != null &&
                    breakTheGlassHelper.isProtectedInSearchScope(program, programOwner.ownerOrgUnit())
                ) {
                    Result.Failure(
                        D2Error.builder()
                            .errorCode(D2ErrorCode.OWNERSHIP_ACCESS_DENIED)
                            .errorDescription("OWNERSHIP_ACCESS_DENIED")
                            .httpErrorCode(HttpStatusCode.NotFound.value)
                            .build(),
                    )
                } else {
                    Result.Failure(originalError)
                }
            }
        }
    }

    override suspend fun getQuery(
        bundle: TrackerQueryBundle,
        program: String?,
        orgunit: DownloadOrgunit?,
        limit: Int,
    ): TrackerAPIQuery? {
        val ouMode = orgunit?.resolveOuMode(bundle.commonParams.ouMode) ?: bundle.commonParams.ouMode

        val teiUids = if (
            bundle.trackedEntityInstanceFilters != null ||
            bundle.programStageWorkingLists != null
        ) {
            val filteredUids = getTeiUidsByFilter(bundle, orgunit?.uid, ouMode) +
                getTeiUidsByWorkingList(bundle, orgunit?.uid, ouMode)

            filteredUids.takeIf { it.isNotEmpty() }
        } else {
            emptyList()
        }

        return teiUids?.let {
            TrackerAPIQuery(
                commonParams = bundle.commonParams.copy(
                    program = program,
                    limit = limit,
                    ouMode = ouMode,
                ),
                programStatus = bundle.programStatus,
                lastUpdatedStr = lastUpdatedManager.getLastUpdatedStr(bundle.commonParams),
                orgUnit = orgunit?.uid,
                uids = teiUids.distinct(),
            )
        }
    }

    private suspend fun getTeiUidsByFilter(
        bundle: TrackerQueryBundle,
        orgunitUid: String?,
        ouMode: OrganisationUnitMode,
    ): List<String> {
        return bundle.trackedEntityInstanceFilters?.flatMap {
            teiQueryCollectionRepository
                .byTrackedEntityInstanceFilterObject().eq(it)
                .byOrgUnits().eq(orgunitUid)
                .byOrgUnitMode().eq(ouMode)
                .onlineOnly().suspendGetUids()
        } ?: emptyList()
    }

    private suspend fun getTeiUidsByWorkingList(
        bundle: TrackerQueryBundle,
        orgunitUid: String?,
        ouMode: OrganisationUnitMode,
    ): List<String> {
        return bundle.programStageWorkingLists?.flatMap {
            teiQueryCollectionRepository
                .byProgramStageWorkingListObject().eq(it)
                .byOrgUnits().eq(orgunitUid)
                .byOrgUnitMode().eq(ouMode)
                .onlineOnly().suspendGetUids()
        } ?: emptyList()
    }
}

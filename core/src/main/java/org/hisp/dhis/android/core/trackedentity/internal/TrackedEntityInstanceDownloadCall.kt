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

import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutor
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableDataHandlerParams
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.program.internal.ProgramDataDownloadParams
import org.hisp.dhis.android.core.relationship.internal.RelationshipDownloadAndPersistCallFactory
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemRelatives
import org.hisp.dhis.android.core.systeminfo.internal.SystemInfoModuleDownloader
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.tracker.exporter.TrackerAPIQuery
import org.hisp.dhis.android.core.tracker.exporter.TrackerDownloadCall
import org.hisp.dhis.android.core.user.internal.UserOrganisationUnitLinkStore
import org.koin.core.annotation.Singleton

@Singleton
internal class TrackedEntityInstanceDownloadCall(
    userOrganisationUnitLinkStore: UserOrganisationUnitLinkStore,
    systemInfoModuleDownloader: SystemInfoModuleDownloader,
    relationshipDownloadAndPersistCallFactory: RelationshipDownloadAndPersistCallFactory,
    private val coroutineCallExecutor: CoroutineAPICallExecutor,
    private val queryFactory: TrackerQueryBundleFactory,
    private val trackerCallFactory: TrackerParentCallFactory,
    private val persistenceCallFactory: TrackedEntityInstancePersistenceCallFactory,
    private val lastUpdatedManager: TrackedEntityInstanceLastUpdatedManager,
) : TrackerDownloadCall<TrackedEntityInstance, TrackerQueryBundle>(
    userOrganisationUnitLinkStore,
    systemInfoModuleDownloader,
    relationshipDownloadAndPersistCallFactory,
    coroutineCallExecutor,
) {
    override fun getBundles(params: ProgramDataDownloadParams): List<TrackerQueryBundle> {
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

    override fun updateLastUpdated(bundle: TrackerQueryBundle) {
        lastUpdatedManager.update(bundle)
    }

    override suspend fun queryByUids(
        bundle: TrackerQueryBundle,
        overwrite: Boolean,
        relatives: RelationshipItemRelatives,
    ): ItemsWithPagingResult {
        val result = ItemsWithPagingResult(0, true, null, false)

        val teiQuery = TrackerAPIQuery(
            commonParams = bundle.commonParams(),
            programStatus = bundle.programStatus(),
        )

        for (uid in bundle.commonParams().uids) {
            try {
                val useEntityEndpoint = teiQuery.commonParams.program != null

                val tei = querySingleTei(uid, useEntityEndpoint, teiQuery).getOrThrow()

                if (tei != null) {
                    val persistParams = IdentifiableDataHandlerParams(
                        hasAllAttributes = !useEntityEndpoint,
                        overwrite = overwrite,
                        asRelationship = false,
                        program = teiQuery.commonParams.program,
                    )

                    persistItems(listOf(tei), persistParams, relatives)

                    result.count++
                }
            } catch (d2Error: D2Error) {
                result.successfulSync = false
                if (result.d2Error == null) {
                    result.d2Error = d2Error
                }
            }
        }
        return result
    }

    private suspend fun querySingleTei(
        uid: String,
        useEntityEndpoint: Boolean,
        query: TrackerAPIQuery,
    ): Result<TrackedEntityInstance?, D2Error> {
        return if (useEntityEndpoint) {
            coroutineCallExecutor.wrap(
                storeError = true,
                errorCatcher = TrackedEntityInstanceCallErrorCatcher(),
            ) {
                trackerCallFactory.getTrackedEntityCall().getEntityCall(uid, query)
            }
        } else {
            val collectionQuery = query.copy(uids = listOf(uid))
            coroutineCallExecutor.wrap(storeError = true) {
                trackerCallFactory.getTrackedEntityCall().getCollectionCall(collectionQuery)
            }.map { it.items.firstOrNull() }
        }
    }

    override fun getQuery(
        bundle: TrackerQueryBundle,
        program: String?,
        orgunitUid: String?,
        limit: Int,
    ): TrackerAPIQuery {
        return TrackerAPIQuery(
            commonParams = bundle.commonParams().copy(
                program = program,
                limit = limit,
            ),
            programStatus = bundle.programStatus(),
            lastUpdatedStr = lastUpdatedManager.getLastUpdatedStr(bundle.commonParams()),
            orgUnit = orgunitUid,
        )
    }
}

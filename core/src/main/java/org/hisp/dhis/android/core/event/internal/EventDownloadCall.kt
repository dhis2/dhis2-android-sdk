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

import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutor
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableDataHandlerParams
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.program.internal.ProgramDataDownloadParams
import org.hisp.dhis.android.core.relationship.internal.RelationshipDownloadAndPersistCallFactory
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemRelatives
import org.hisp.dhis.android.core.systeminfo.internal.SystemInfoModuleDownloader
import org.hisp.dhis.android.core.trackedentity.internal.TrackerParentCallFactory
import org.hisp.dhis.android.core.tracker.exporter.TrackerAPIQuery
import org.hisp.dhis.android.core.tracker.exporter.TrackerDownloadCall
import org.hisp.dhis.android.core.user.internal.UserOrganisationUnitLinkStore
import org.koin.core.annotation.Singleton

@Singleton
internal class EventDownloadCall internal constructor(
    userOrganisationUnitLinkStore: UserOrganisationUnitLinkStore,
    systemInfoModuleDownloader: SystemInfoModuleDownloader,
    relationshipDownloadAndPersistCallFactory: RelationshipDownloadAndPersistCallFactory,
    private val coroutineAPICallExecutor: CoroutineAPICallExecutor,
    private val eventQueryBundleFactory: EventQueryBundleFactory,
    private val trackerParentCallFactory: TrackerParentCallFactory,
    private val persistenceCallFactory: EventPersistenceCallFactory,
    private val lastUpdatedManager: EventLastUpdatedManager,
) : TrackerDownloadCall<Event, EventQueryBundle>(
    userOrganisationUnitLinkStore,
    systemInfoModuleDownloader,
    relationshipDownloadAndPersistCallFactory,
    coroutineAPICallExecutor,
) {

    override fun getBundles(params: ProgramDataDownloadParams): List<EventQueryBundle> {
        return eventQueryBundleFactory.getQueries(params)
    }

    override suspend fun getPayloadResult(query: TrackerAPIQuery): Result<Payload<Event>, D2Error> {
        return coroutineAPICallExecutor.wrap(storeError = true) {
            trackerParentCallFactory.getEventCall().getCollectionCall(query)
        }
    }

    override suspend fun persistItems(
        items: List<Event>,
        params: IdentifiableDataHandlerParams,
        relatives: RelationshipItemRelatives,
    ) {
        persistenceCallFactory.persistEvents(items, relatives)
    }

    override fun updateLastUpdated(bundle: EventQueryBundle) {
        lastUpdatedManager.update(bundle)
    }

    override suspend fun queryByUids(
        bundle: EventQueryBundle,
        overwrite: Boolean,
        relatives: RelationshipItemRelatives,
    ): ItemsWithPagingResult {
        val result = ItemsWithPagingResult(0, true, null, false)

        val eventQuery = TrackerAPIQuery(
            commonParams = bundle.commonParams().copy(
                program = bundle.commonParams().program,
                limit = bundle.commonParams().uids.size,
            ),
            uids = bundle.commonParams().uids,
        )

        try {
            val items = getItems(eventQuery)

            // TODO Review
            val persistParams = IdentifiableDataHandlerParams(
                hasAllAttributes = true,
                overwrite = overwrite,
                asRelationship = false,
                program = eventQuery.commonParams.program,
            )

            persistItems(items, params = persistParams, relatives)

            result.count += items.size
        } catch (d2Error: D2Error) {
            result.successfulSync = false
            if (result.d2Error == null) {
                result.d2Error = d2Error
            }
        }

        return result
    }

    override fun getQuery(
        bundle: EventQueryBundle,
        program: String?,
        orgunitUid: String?,
        limit: Int,
    ): TrackerAPIQuery {
        return TrackerAPIQuery(
            commonParams = bundle.commonParams().copy(
                program = program,
                limit = limit,
            ),
            lastUpdatedStr = lastUpdatedManager.getLastUpdatedStr(bundle.commonParams()),
            orgUnit = orgunitUid,
            uids = bundle.commonParams().uids,
        )
    }
}

/*
 *  Copyright (c) 2004-2026, University of Oslo
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
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableDataHandlerParams
import org.hisp.dhis.android.core.common.DataColumns
import org.hisp.dhis.android.core.common.IdentifiableColumns
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.internal.EventHandler
import org.hisp.dhis.android.core.event.internal.EventStore
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemRelatives
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.tracker.TrackerPostParentCallHelper
import org.hisp.dhis.android.core.tracker.exporter.EventsPage
import org.hisp.dhis.android.core.tracker.exporter.TrackerAPIQuery
import org.hisp.dhis.android.core.tracker.exporter.TrackerEventsMode
import org.hisp.dhis.android.core.tracker.exporter.TrackerExporterNetworkHandler
import org.hisp.dhis.android.persistence.common.querybuilders.WhereClauseBuilder
import org.hisp.dhis.android.persistence.enrollment.EnrollmentTableInfo
import org.hisp.dhis.android.persistence.event.EventTableInfo
import org.koin.core.annotation.Singleton

/**
 * Downloads the events of the tracked entities apart from them, so that a page of tracked entities with many events
 * does not have to fit in memory and, in the incremental downloads, only new or modified events are downloaded.
 */
@Singleton
@Suppress("LongParameterList")
internal class SelectiveEventDownloader(
    private val parentCallHelper: TrackerPostParentCallHelper,
    private val enrollmentStore: EnrollmentStore,
    private val eventStore: EventStore,
    private val databaseAdapter: DatabaseAdapter,
    private val networkHandler: TrackerExporterNetworkHandler,
    private val coroutineAPICallExecutor: CoroutineAPICallExecutor,
    private val persistenceCallFactory: TrackedEntityInstancePersistenceCallFactory,
    private val eventHandler: EventHandler,
) {

    suspend fun eventsMode(program: String?): TrackerEventsMode {
        return when {
            program == null || !parentCallHelper.useNewTrackerExporter() -> TrackerEventsMode.FULL
            enrollmentStore.countWhere(
                WhereClauseBuilder().appendKeyStringValue(EnrollmentTableInfo.Columns.PROGRAM, program).build(),
            ) == 0 -> TrackerEventsMode.NONE
            else -> TrackerEventsMode.INDEX
        }
    }

    suspend fun persist(
        items: List<TrackedEntityInstance>,
        params: IdentifiableDataHandlerParams,
        relatives: RelationshipItemRelatives,
        query: TrackerAPIQuery,
    ) {
        val program = requireNotNull(query.commonParams.program) { "Events are only downloaded apart by program" }
        val enrollments = items
            .flatMap { it.enrollments().orEmpty() }
            .filter { it.program() == program && it.deleted() != true }

        // The index events are incomplete: they must never reach the EventHandler (it would delete the data values).
        // Without events, the enrollment handler does not run the event orphan cleaner either.
        persistenceCallFactory.persistTEIs(items.map { withoutEvents(it) }, params, relatives)

        val eventParams = IdentifiableDataHandlerParams(
            hasAllAttributes = false,
            overwrite = params.overwrite,
            asRelationship = false,
        )
        if (query.eventsMode == TrackerEventsMode.NONE) {
            downloadAllEvents(program, enrollments.map { it.uid() }, eventParams, relatives)
        } else {
            downloadChangedEvents(program, enrollments, eventParams, relatives)
        }
    }

    /**
     * Pages through the events of each batch of enrollments, or of each enrollment if the batch has many pages: the
     * server penalizes deep paging.
     */
    private suspend fun downloadAllEvents(
        program: String,
        enrollmentUids: List<String>,
        params: IdentifiableDataHandlerParams,
        relatives: RelationshipItemRelatives,
    ) {
        enrollmentUids.chunked(ENROLLMENTS_PER_REQUEST).forEach { batch ->
            val firstPage = downloadEventsPage(program, batch, page = 1, params, relatives)
            if ((firstPage.pageCount ?: 0) > MAX_PAGES_PER_BATCH) {
                batch.forEach { downloadEventPages(program, listOf(it), params, relatives) }
            } else {
                downloadEventPages(program, batch, params, relatives, firstPage)
            }
        }
    }

    /**
     * Downloads every page of the events of the enrollments, from the second one if [firstPage] is given.
     */
    private suspend fun downloadEventPages(
        program: String,
        enrollmentUids: List<String>,
        params: IdentifiableDataHandlerParams,
        relatives: RelationshipItemRelatives,
        firstPage: EventsPage? = null,
    ) {
        var lastPage = firstPage ?: downloadEventsPage(program, enrollmentUids, page = 1, params, relatives)
        val pageCount = lastPage.pageCount
        var page = 1
        while (lastPage.events.size == EVENTS_PAGE_SIZE && (pageCount == null || page < pageCount)) {
            lastPage = downloadEventsPage(program, enrollmentUids, ++page, params, relatives)
        }
    }

    private suspend fun downloadEventsPage(
        program: String,
        enrollmentUids: List<String>,
        page: Int,
        params: IdentifiableDataHandlerParams,
        relatives: RelationshipItemRelatives,
    ): EventsPage {
        val eventsPage = coroutineAPICallExecutor.wrap(storeError = true) {
            networkHandler.getEventsOfEnrollments(program, enrollmentUids, page, EVENTS_PAGE_SIZE)
        }.getOrThrow()
        val requested = enrollmentUids.toSet()
        eventHandler.handleMany(eventsPage.events.filter { requested.contains(it.enrollment()) }, params, relatives)
        return eventsPage
    }

    /**
     * Compares the event index of the [enrollments] with the local events: downloads the new or modified ones and
     * deletes the synced ones that are not in the index.
     */
    private suspend fun downloadChangedEvents(
        program: String,
        enrollments: List<Enrollment>,
        params: IdentifiableDataHandlerParams,
        relatives: RelationshipItemRelatives,
    ) {
        val localEvents = localEvents(enrollments.map { it.uid() })
        val toDownload = mutableListOf<String>()
        val toDelete = mutableListOf<String>()

        enrollments.forEach { enrollment ->
            val serverEvents = enrollment.events().orEmpty().filter { it.deleted() != true }.associateBy { it.uid() }
            val local = localEvents[enrollment.uid()].orEmpty()
            val localByUid = local.associateBy { it.uid() }

            toDownload += serverEvents.values.filter { needsDownload(localByUid[it.uid()], it, params.overwrite) }
                .map { it.uid() }
            toDelete += local.filter { SYNCED_STATES.contains(it.syncState()) && !serverEvents.containsKey(it.uid()) }
                .map { it.uid() }
        }

        deleteSyncedEvents(toDelete)

        toDownload.chunked(EVENTS_PER_REQUEST).forEach { uids ->
            val events = coroutineAPICallExecutor.wrap(storeError = true) {
                networkHandler.getEventsByUids(program, uids)
            }.getOrThrow()
            // A server that ignores the uids parameter returns other events
            val requested = uids.toSet()
            eventHandler.handleMany(events.filter { requested.contains(it.uid()) }, params, relatives)
        }
    }

    private fun needsDownload(local: Event?, server: Event, overwrite: Boolean): Boolean {
        return when {
            // Events downloaded as relationship items lack their data values
            local == null || local.syncState() == State.RELATIONSHIP -> true
            SYNCED_STATES.contains(local.syncState()) -> local.lastUpdated() != server.lastUpdated()
            else -> overwrite
        }
    }

    private suspend fun localEvents(enrollmentUids: List<String>): Map<String, List<Event>> {
        return enrollmentUids.chunked(UIDS_PER_STATEMENT).flatMap { chunk ->
            eventStore.selectWhere(
                WhereClauseBuilder().appendInKeyStringValues(EventTableInfo.Columns.ENROLLMENT, chunk).build(),
            )
        }.groupBy { it.enrollment().orEmpty() }
    }

    private suspend fun deleteSyncedEvents(uids: List<String>) {
        uids.chunked(UIDS_PER_STATEMENT).forEach { chunk ->
            val whereClause = WhereClauseBuilder()
                .appendInKeyStringValues(IdentifiableColumns.UID, chunk)
                .appendInKeyEnumValues(DataColumns.SYNC_STATE, SYNCED_STATES)
                .build()
            databaseAdapter.delete(EventTableInfo.TABLE_INFO.name(), whereClause)
        }
    }

    private fun withoutEvents(tei: TrackedEntityInstance): TrackedEntityInstance {
        return tei.toBuilder()
            .enrollments(tei.enrollments()?.map { it.toBuilder().events(null).build() })
            .build()
    }

    companion object {
        private val SYNCED_STATES = listOf(State.SYNCED, State.SYNCED_VIA_SMS)

        // 500 uids (~6 KB URL) work, 1000 return HTTP 414
        private const val EVENTS_PER_REQUEST = 300
        private const val ENROLLMENTS_PER_REQUEST = 50
        private const val EVENTS_PAGE_SIZE = 1000
        private const val MAX_PAGES_PER_BATCH = 3
        private const val UIDS_PER_STATEMENT = 500
    }
}

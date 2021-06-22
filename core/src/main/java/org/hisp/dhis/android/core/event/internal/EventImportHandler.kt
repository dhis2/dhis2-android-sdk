/*
 *  Copyright (c) 2004-2021, University of Oslo
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

import dagger.Reusable
import org.hisp.dhis.android.core.arch.db.stores.internal.StoreUtils.getSyncState
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.common.internal.DataStatePropagator
import org.hisp.dhis.android.core.event.EventTableInfo
import org.hisp.dhis.android.core.imports.TrackerImportConflict
import org.hisp.dhis.android.core.imports.internal.EventImportSummary
import org.hisp.dhis.android.core.imports.internal.TrackerImportConflictParser
import org.hisp.dhis.android.core.imports.internal.TrackerImportConflictStore
import org.hisp.dhis.android.core.tracker.importer.internal.JobReportEventHandler
import java.util.*
import javax.inject.Inject

@Reusable
internal class EventImportHandler @Inject constructor(
    private val eventStore: EventStore,
    private val trackerImportConflictStore: TrackerImportConflictStore,
    private val trackerImportConflictParser: TrackerImportConflictParser,
    private val jobReportEventHandler: JobReportEventHandler,
    private val dataStatePropagator: DataStatePropagator
) {

    fun handleEventImportSummaries(
        eventImportSummaries: List<EventImportSummary?>?,
        enrollmentUid: String?,
        teiUid: String?
    ) {
        var parentState: State = State.SYNCED

        eventImportSummaries?.filterNotNull()?.forEach { eventImportSummary ->
            val state = getSyncState(eventImportSummary.status())

            eventImportSummary.reference()?.let { eventUid ->
                val handleAction = eventStore.setSyncStateOrDelete(eventUid, state)
                if (state == State.ERROR || state == State.WARNING) {
                    parentState = if (parentState == State.ERROR) State.ERROR else state
                }
                trackerImportConflictStore.deleteEventConflicts(eventUid)

                if (handleAction !== HandleAction.Delete) {
                    jobReportEventHandler.handleEventNotes(eventUid, state)
                    storeEventImportConflicts(eventImportSummary, teiUid, enrollmentUid)
                }
            }
        }

        updateParentState(parentState, enrollmentUid)
    }

    private fun storeEventImportConflicts(
        importSummary: EventImportSummary,
        teiUid: String?,
        enrollmentUid: String?
    ) {
        val trackerImportConflicts: MutableList<TrackerImportConflict> = ArrayList()

        if (importSummary.description() != null) {
            trackerImportConflicts.add(
                getConflictBuilder(teiUid, enrollmentUid, importSummary)
                    .conflict(importSummary.description())
                    .displayDescription(importSummary.description())
                    .value(importSummary.reference())
                    .build()
            )
        }

        importSummary.conflicts()?.forEach { importConflict ->
            trackerImportConflicts.add(
                trackerImportConflictParser
                    .getEventConflict(importConflict, getConflictBuilder(teiUid, enrollmentUid, importSummary))
            )
        }

        trackerImportConflicts.forEach { trackerImportConflictStore.insert(it) }
    }

    private fun updateParentState(parentState: State, enrollmentUid: String?) {
        if (parentState != State.SYNCED && enrollmentUid != null) {
            dataStatePropagator.propagateEnrollmentError(enrollmentUid, parentState)
        }
    }

    private fun getConflictBuilder(
        trackedEntityInstanceUid: String?,
        enrollmentUid: String?,
        eventImportSummary: EventImportSummary
    ): TrackerImportConflict.Builder {
        return TrackerImportConflict.builder()
            .trackedEntityInstance(trackedEntityInstanceUid)
            .enrollment(enrollmentUid)
            .event(eventImportSummary.reference())
            .tableReference(EventTableInfo.TABLE_INFO.name())
            .status(eventImportSummary.status())
            .created(Date())
    }
}
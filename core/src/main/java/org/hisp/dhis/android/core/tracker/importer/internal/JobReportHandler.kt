/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.tracker.importer.internal

import dagger.Reusable
import javax.inject.Inject
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.event.internal.EventStore
import org.hisp.dhis.android.core.event.internal.TrackerImporterEventHandlerHelper
import org.hisp.dhis.android.core.imports.ImportStatus
import org.hisp.dhis.android.core.imports.internal.TrackerImportConflictStore

@Reusable
internal class JobReportHandler @Inject internal constructor(
    private val conflictStore: TrackerImportConflictStore,
    private val eventStore: EventStore,
    private val eventHandlerHelper: TrackerImporterEventHandlerHelper
) {

    fun handle(o: JobReport) {
        o.validationReport.errorReports.forEach { errorReport ->
            if (errorReport.trackerType == "EVENT") {
                handleEvent(errorReport.uid, State.ERROR)
                storeEventConflict(errorReport)
            }
        }

        if (o.bundleReport != null) {
            o.bundleReport.typeReportMap.event.objectReports.forEach { objectReport ->
                handleEvent(objectReport.uid, State.SYNCED)
            }
        }
    }

    private fun handleEvent(uid: String, state: State) {
        eventStore.setState(uid, state)
        conflictStore.deleteEventConflicts(uid)
        eventHandlerHelper.handleEventNotes(uid, state)
    }

    private fun storeEventConflict(error: JobValidationError) {
        conflictStore.insert(
            eventHandlerHelper.getConflictBuilder(null, null, error.uid, ImportStatus.ERROR)
                .conflict(error.message)
                .displayDescription(error.message)
                .value(error.uid)
                .errorCode(error.errorCode)
                .build()
        )
    }
}

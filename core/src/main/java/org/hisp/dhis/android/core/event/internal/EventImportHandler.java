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

package org.hisp.dhis.android.core.event.internal;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore;
import org.hisp.dhis.android.core.event.EventTableInfo;
import org.hisp.dhis.android.core.imports.TrackerImportConflict;
import org.hisp.dhis.android.core.imports.internal.EventImportSummary;
import org.hisp.dhis.android.core.imports.internal.ImportConflict;
import org.hisp.dhis.android.core.imports.internal.TrackerImportConflictParser;
import org.hisp.dhis.android.core.imports.internal.TrackerImportConflictStore;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStore;
import org.hisp.dhis.android.core.tracker.importer.internal.JobReportEventHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import dagger.Reusable;

import static org.hisp.dhis.android.core.arch.db.stores.internal.StoreUtils.getState;

@Reusable
public class EventImportHandler {
    private final EventStore eventStore;
    private final EnrollmentStore enrollmentStore;
    private final TrackedEntityInstanceStore trackedEntityInstanceStore;
    private final TrackerImportConflictStore trackerImportConflictStore;
    private final TrackerImportConflictParser trackerImportConflictParser;
    private final JobReportEventHandler jobReportEventHandler;

    @Inject
    public EventImportHandler(@NonNull EventStore eventStore,
                              @NonNull EnrollmentStore enrollmentStore,
                              @NonNull TrackedEntityInstanceStore trackedEntityInstanceStore,
                              @NonNull TrackerImportConflictStore trackerImportConflictStore,
                              @NonNull TrackerImportConflictParser trackerImportConflictParser,
                              JobReportEventHandler jobReportEventHandler) {
        this.eventStore = eventStore;
        this.enrollmentStore = enrollmentStore;
        this.trackedEntityInstanceStore = trackedEntityInstanceStore;
        this.trackerImportConflictStore = trackerImportConflictStore;
        this.trackerImportConflictParser = trackerImportConflictParser;
        this.jobReportEventHandler = jobReportEventHandler;
    }

    public void handleEventImportSummaries(List<EventImportSummary> eventImportSummaries,
                                           String enrollmentUid,
                                           String teiUid) {
        if (eventImportSummaries == null) {
            return;
        }

        State parentState = null;
        for (EventImportSummary eventImportSummary : eventImportSummaries) {
            if (eventImportSummary == null) {
                break;
            }

            State state = getState(eventImportSummary.status());

            HandleAction handleAction = null;

            if (eventImportSummary.reference() != null) {
                handleAction = eventStore.setStateOrDelete(eventImportSummary.reference(), state);
                if (state == State.ERROR || state == State.WARNING) {
                    parentState = parentState == State.ERROR ? State.ERROR : state;
                }

                trackerImportConflictStore.deleteEventConflicts(eventImportSummary.reference());
            }

            if (handleAction != HandleAction.Delete) {
                jobReportEventHandler.handleEventNotes(eventImportSummary.reference(), state);

                storeEventImportConflicts(eventImportSummary, teiUid, enrollmentUid);
            }
        }

        updateParentState(parentState, teiUid, enrollmentUid);
    }

    private void storeEventImportConflicts(EventImportSummary importSummary,
                                           String teiUid,
                                           String enrollmentUid) {
        List<TrackerImportConflict> trackerImportConflicts = new ArrayList<>();
        if (importSummary.description() != null) {
            trackerImportConflicts.add(getConflictBuilder(teiUid, enrollmentUid, importSummary)
                    .conflict(importSummary.description())
                    .displayDescription(importSummary.description())
                    .value(importSummary.reference())
                    .build());
        }

        if (importSummary.conflicts() != null) {
            for (ImportConflict importConflict : importSummary.conflicts()) {
                trackerImportConflicts.add(trackerImportConflictParser
                        .getEventConflict(importConflict, getConflictBuilder(teiUid, enrollmentUid, importSummary)));
            }

        }

        for (TrackerImportConflict trackerImportConflict : trackerImportConflicts) {
            trackerImportConflictStore.insert(trackerImportConflict);
        }
    }

    private void updateParentState(State parentState, String teiUid, String enrollmentUid) {
        if (parentState != null) {
            if (teiUid != null) {
                trackedEntityInstanceStore.setState(teiUid, parentState);
            }
            if (enrollmentUid != null) {
                enrollmentStore.setState(enrollmentUid, parentState);
            }
        }
    }

    private TrackerImportConflict.Builder getConflictBuilder(String trackedEntityInstanceUid,
                                                             String enrollmentUid,
                                                             EventImportSummary eventImportSummary) {
        return TrackerImportConflict.builder()
                .trackedEntityInstance(trackedEntityInstanceUid)
                .enrollment(enrollmentUid)
                .event(eventImportSummary.reference())
                .tableReference(EventTableInfo.TABLE_INFO.name())
                .status(eventImportSummary.status())
                .created(new Date());
    }
}
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

package org.hisp.dhis.android.core.event;

import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectStore;
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore;
import org.hisp.dhis.android.core.imports.EventImportSummary;
import org.hisp.dhis.android.core.imports.ImportConflict;
import org.hisp.dhis.android.core.imports.TrackerImportConflict;
import org.hisp.dhis.android.core.imports.TrackerImportConflictTableInfo;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceStore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import dagger.Reusable;

import static org.hisp.dhis.android.core.utils.StoreUtils.getState;

@Reusable
public class EventImportHandler {
    private final EventStore eventStore;
    private final EnrollmentStore enrollmentStore;
    private final TrackedEntityInstanceStore trackedEntityInstanceStore;
    private final ObjectStore<TrackerImportConflict> trackerImportConflictStore;

    @Inject
    public EventImportHandler(@NonNull EventStore eventStore,
                              @NonNull EnrollmentStore enrollmentStore,
                              @NonNull TrackedEntityInstanceStore trackedEntityInstanceStore,
                              @NonNull ObjectStore<TrackerImportConflict> trackerImportConflictStore) {
        this.eventStore = eventStore;
        this.enrollmentStore = enrollmentStore;
        this.trackedEntityInstanceStore = trackedEntityInstanceStore;
        this.trackerImportConflictStore = trackerImportConflictStore;
    }

    public void handleEventImportSummaries(List<EventImportSummary> eventImportSummaries,
                                           TrackerImportConflict.Builder trackerImportConflictBuilder,
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

            HandleAction handleAction = null;

            if (eventImportSummary.reference() != null) {
                State state = getState(eventImportSummary.status());
                handleAction = eventStore.setStateOrDelete(eventImportSummary.reference(), state);
                if (state == State.ERROR || state == State.WARNING) {
                    parentState = parentState == State.ERROR ? State.ERROR : state;
                }

                deleteEventConflicts(eventImportSummary.reference());
            }

            if (handleAction != HandleAction.Delete) {
                storeEventImportConflicts(eventImportSummary, trackerImportConflictBuilder);
            }
        }

        updateParentState(parentState, teiUid, enrollmentUid);
    }

    private void storeEventImportConflicts(EventImportSummary eventImportSummary,
                                           TrackerImportConflict.Builder trackerImportConflictBuilder) {
        trackerImportConflictBuilder
                .event(eventImportSummary.reference())
                .tableReference(EventTableInfo.TABLE_INFO.name())
                .status(eventImportSummary.status())
                .created(new Date());

        List<TrackerImportConflict> trackerImportConflicts = new ArrayList<>();
        if (eventImportSummary.description() != null) {
            trackerImportConflicts.add(trackerImportConflictBuilder
                    .conflict(eventImportSummary.description())
                    .value(eventImportSummary.reference())
                    .build());
        }

        if (eventImportSummary.conflicts() != null) {
            for (ImportConflict importConflict : eventImportSummary.conflicts()) {
                trackerImportConflicts.add(trackerImportConflictBuilder
                        .conflict(importConflict.value())
                        .value(importConflict.object())
                        .build());
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

    private void deleteEventConflicts(String eventUid) {
        String whereClause = new WhereClauseBuilder()
                .appendKeyStringValue(TrackerImportConflictTableInfo.Columns.EVENT, eventUid)
                .appendKeyStringValue(
                        TrackerImportConflictTableInfo.Columns.TABLE_REFERENCE,
                        EventTableInfo.TABLE_INFO.name())
                .build();
        trackerImportConflictStore.deleteWhereIfExists(whereClause);
    }
}
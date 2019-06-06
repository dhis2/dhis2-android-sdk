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

package org.hisp.dhis.android.core.enrollment.internal;

import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectStore;
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction;
import org.hisp.dhis.android.core.common.BaseDataModel;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.EnrollmentTableInfo;
import org.hisp.dhis.android.core.enrollment.note.Note;
import org.hisp.dhis.android.core.enrollment.note.NoteTableInfo;
import org.hisp.dhis.android.core.event.EventImportHandler;
import org.hisp.dhis.android.core.imports.EnrollmentImportSummary;
import org.hisp.dhis.android.core.imports.EventImportSummaries;
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
public class EnrollmentImportHandler {
    private final EnrollmentStore enrollmentStore;
    private final TrackedEntityInstanceStore trackedEntityInstanceStore;
    private final ObjectWithoutUidStore<Note> noteStore;
    private final EventImportHandler eventImportHandler;
    private final ObjectStore<TrackerImportConflict> trackerImportConflictStore;

    @Inject
    public EnrollmentImportHandler(@NonNull EnrollmentStore enrollmentStore,
                                   @NonNull TrackedEntityInstanceStore trackedEntityInstanceStore,
                                   @NonNull ObjectWithoutUidStore<Note> noteStore,
                                   @NonNull EventImportHandler eventImportHandler,
                                   @NonNull ObjectStore<TrackerImportConflict> trackerImportConflictStore) {
        this.enrollmentStore = enrollmentStore;
        this.trackedEntityInstanceStore = trackedEntityInstanceStore;
        this.noteStore = noteStore;
        this.eventImportHandler = eventImportHandler;
        this.trackerImportConflictStore = trackerImportConflictStore;
    }

    public void handleEnrollmentImportSummary(List<EnrollmentImportSummary> enrollmentImportSummaries,
                                              TrackerImportConflict.Builder trackerImportConflictBuilder,
                                              String teiUid) {
        if (enrollmentImportSummaries == null) {
            return;
        }

        State parentState = null;
        for (EnrollmentImportSummary enrollmentImportSummary : enrollmentImportSummaries) {
            if (enrollmentImportSummary == null) {
                break;
            }

            State state = getState(enrollmentImportSummary.status());

            HandleAction handleAction = null;

            if (enrollmentImportSummary.reference() != null) {
                handleAction = enrollmentStore.setStateOrDelete(enrollmentImportSummary.reference(), state);
                if (state == State.ERROR || state == State.WARNING) {
                    parentState = parentState == State.ERROR ? State.ERROR : state;
                }

                deleteEnrollmentConflicts(enrollmentImportSummary.reference());
            }

            if (handleAction != HandleAction.Delete) {
                handleNoteImportSummary(enrollmentImportSummary.reference(), state);

                storeEnrollmentImportConflicts(enrollmentImportSummary, trackerImportConflictBuilder);

                handleEventImportSummaries(enrollmentImportSummary, trackerImportConflictBuilder, teiUid);
            }
        }

        updateParentState(parentState, teiUid);
    }

    private void handleEventImportSummaries(EnrollmentImportSummary enrollmentImportSummary,
                                            TrackerImportConflict.Builder trackerImportConflictBuilder,
                                            String teiUid) {

        if (enrollmentImportSummary.events() != null) {
            EventImportSummaries eventImportSummaries = enrollmentImportSummary.events();

            if (eventImportSummaries.importSummaries() != null) {
                eventImportHandler.handleEventImportSummaries(
                        eventImportSummaries.importSummaries(),
                        trackerImportConflictBuilder.enrollment(enrollmentImportSummary.reference()),
                        enrollmentImportSummary.reference(),
                        teiUid);

            }
        }
    }

    private void handleNoteImportSummary(String enrollmentUid, State state) {
        String whereClause = new WhereClauseBuilder()
                .appendKeyStringValue(BaseDataModel.Columns.STATE, State.TO_POST)
                .appendKeyStringValue(NoteTableInfo.Columns.ENROLLMENT, enrollmentUid).build();
        List<Note> notes = noteStore.selectWhere(whereClause);
        for (Note note : notes) {
            noteStore.updateWhere(note.toBuilder().state(state).build());
        }
    }

    private void storeEnrollmentImportConflicts(EnrollmentImportSummary enrollmentImportSummary,
                                                TrackerImportConflict.Builder trackerImportConflictBuilder) {
        trackerImportConflictBuilder
                .enrollment(enrollmentImportSummary.reference())
                .tableReference(EnrollmentTableInfo.TABLE_INFO.name())
                .status(enrollmentImportSummary.status())
                .created(new Date());

        List<TrackerImportConflict> trackerImportConflicts = new ArrayList<>();
        if (enrollmentImportSummary.description() != null) {
            trackerImportConflicts.add(trackerImportConflictBuilder
                    .conflict(enrollmentImportSummary.description())
                    .value(enrollmentImportSummary.reference())
                    .build());
        }

        if (enrollmentImportSummary.conflicts() != null) {
            for (ImportConflict importConflict : enrollmentImportSummary.conflicts()) {
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


    private void updateParentState(State parentState, String teiUid) {
        if (parentState != null && teiUid != null) {
            trackedEntityInstanceStore.setState(teiUid, parentState);
        }
    }

    private void deleteEnrollmentConflicts(String enrollmentUid) {
        String whereClause = new WhereClauseBuilder()
                .appendKeyStringValue(TrackerImportConflictTableInfo.Columns.ENROLLMENT, enrollmentUid)
                .appendKeyStringValue(
                        TrackerImportConflictTableInfo.Columns.TABLE_REFERENCE,
                        EnrollmentTableInfo.TABLE_INFO.name())
                .build();
        trackerImportConflictStore.deleteWhereIfExists(whereClause);
    }
}
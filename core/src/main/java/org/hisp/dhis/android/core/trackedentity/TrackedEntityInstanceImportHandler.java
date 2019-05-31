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

package org.hisp.dhis.android.core.trackedentity;


import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder;
import org.hisp.dhis.android.core.common.HandleAction;
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectStore;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.EnrollmentImportHandler;
import org.hisp.dhis.android.core.imports.EnrollmentImportSummaries;
import org.hisp.dhis.android.core.imports.ImportConflict;
import org.hisp.dhis.android.core.imports.TEIImportSummary;
import org.hisp.dhis.android.core.imports.TrackerImportConflict;
import org.hisp.dhis.android.core.imports.TrackerImportConflictTableInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import dagger.Reusable;

import static org.hisp.dhis.android.core.utils.StoreUtils.getState;

@Reusable
public final class TrackedEntityInstanceImportHandler {
    private final TrackedEntityInstanceStore trackedEntityInstanceStore;
    private final EnrollmentImportHandler enrollmentImportHandler;
    private final ObjectStore<TrackerImportConflict> trackerImportConflictStore;

    @Inject
    TrackedEntityInstanceImportHandler(@NonNull TrackedEntityInstanceStore trackedEntityInstanceStore,
                                       @NonNull EnrollmentImportHandler enrollmentImportHandler,
                                       @NonNull ObjectStore<TrackerImportConflict> trackerImportConflictStore) {
        this.trackedEntityInstanceStore = trackedEntityInstanceStore;
        this.enrollmentImportHandler = enrollmentImportHandler;
        this.trackerImportConflictStore = trackerImportConflictStore;
    }

    public void handleTrackedEntityInstanceImportSummaries(List<TEIImportSummary> teiImportSummaries) {
        if (teiImportSummaries == null) {
            return;
        }

        for (TEIImportSummary teiImportSummary : teiImportSummaries) {
            if (teiImportSummary == null) {
                break;
            }

            State state = getState(teiImportSummary.status());
            HandleAction handleAction = null;

            if (teiImportSummary.reference() != null) {
                handleAction = trackedEntityInstanceStore.setStateOrDelete(teiImportSummary.reference(), state);

                deleteTEIConflicts(teiImportSummary.reference());
            }

            if (handleAction != HandleAction.Delete) {
                storeTEIImportConflicts(teiImportSummary);

                if (teiImportSummary.enrollments() != null) {
                    EnrollmentImportSummaries importEnrollment = teiImportSummary.enrollments();

                    enrollmentImportHandler.handleEnrollmentImportSummary(
                            importEnrollment.importSummaries(),
                            TrackerImportConflict.builder().trackedEntityInstance(teiImportSummary.reference()),
                            teiImportSummary.reference());
                }

            }
        }
    }

    private void storeTEIImportConflicts(TEIImportSummary teiImportSummary) {
        TrackerImportConflict.Builder trackerImportConflictBuilder = TrackerImportConflict.builder()
                .trackedEntityInstance(teiImportSummary.reference())
                .tableReference(TrackedEntityInstanceTableInfo.TABLE_INFO.name())
                .status(teiImportSummary.status())
                .created(new Date());

        List<TrackerImportConflict> trackerImportConflicts = new ArrayList<>();
        if (teiImportSummary.description() != null) {
            trackerImportConflicts.add(trackerImportConflictBuilder
                    .conflict(teiImportSummary.description())
                    .value(teiImportSummary.reference())
                    .build());
        }

        if (teiImportSummary.conflicts() != null) {
            for (ImportConflict importConflict : teiImportSummary.conflicts()) {
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

    private void deleteTEIConflicts(String teiUid) {
        String whereClause = new WhereClauseBuilder()
                .appendKeyStringValue(TrackerImportConflictTableInfo.Columns.TRACKED_ENTITY_INSTANCE, teiUid)
                .appendKeyStringValue(
                        TrackerImportConflictTableInfo.Columns.TABLE_REFERENCE,
                        TrackedEntityInstanceTableInfo.TABLE_INFO.name())
                .build();
        trackerImportConflictStore.deleteWhereIfExists(whereClause);
    }
}
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

package org.hisp.dhis.android.core.trackedentity.internal;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectStore;
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.common.internal.DataStatePropagator;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentImportHandler;
import org.hisp.dhis.android.core.imports.TrackerImportConflict;
import org.hisp.dhis.android.core.imports.TrackerImportConflictTableInfo;
import org.hisp.dhis.android.core.imports.internal.BaseImportSummaryHelper;
import org.hisp.dhis.android.core.imports.internal.EnrollmentImportSummaries;
import org.hisp.dhis.android.core.imports.internal.ImportConflict;
import org.hisp.dhis.android.core.imports.internal.TEIImportSummary;
import org.hisp.dhis.android.core.imports.internal.TrackerImportConflictParser;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.core.relationship.RelationshipCollectionRepository;
import org.hisp.dhis.android.core.relationship.RelationshipHelper;
import org.hisp.dhis.android.core.relationship.internal.RelationshipDHISVersionManager;
import org.hisp.dhis.android.core.relationship.internal.RelationshipStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceInternalAccessor;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceTableInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import dagger.Reusable;

import static org.hisp.dhis.android.core.arch.db.stores.internal.StoreUtils.getState;

@Reusable
@SuppressWarnings("PMD.ExcessiveImports")
public final class TrackedEntityInstanceImportHandler {
    private final TrackedEntityInstanceStore trackedEntityInstanceStore;
    private final EnrollmentImportHandler enrollmentImportHandler;
    private final ObjectStore<TrackerImportConflict> trackerImportConflictStore;
    private final TrackerImportConflictParser trackerImportConflictParser;
    private final RelationshipStore relationshipStore;
    private final DataStatePropagator dataStatePropagator;
    private final RelationshipDHISVersionManager relationshipDHISVersionManager;
    private final RelationshipCollectionRepository relationshipRepository;

    @Inject
    TrackedEntityInstanceImportHandler(@NonNull TrackedEntityInstanceStore trackedEntityInstanceStore,
                                       @NonNull EnrollmentImportHandler enrollmentImportHandler,
                                       @NonNull ObjectStore<TrackerImportConflict> trackerImportConflictStore,
                                       @NonNull TrackerImportConflictParser trackerImportConflictParser,
                                       @NonNull RelationshipStore relationshipStore,
                                       @NonNull DataStatePropagator dataStatePropagator,
                                       @NonNull RelationshipDHISVersionManager relationshipDHISVersionManager,
                                       @NonNull RelationshipCollectionRepository relationshipRepository) {
        this.trackedEntityInstanceStore = trackedEntityInstanceStore;
        this.enrollmentImportHandler = enrollmentImportHandler;
        this.trackerImportConflictStore = trackerImportConflictStore;
        this.trackerImportConflictParser = trackerImportConflictParser;
        this.relationshipStore = relationshipStore;
        this.dataStatePropagator = dataStatePropagator;
        this.relationshipDHISVersionManager = relationshipDHISVersionManager;
        this.relationshipRepository = relationshipRepository;
    }

    public void handleTrackedEntityInstanceImportSummaries(List<TEIImportSummary> teiImportSummaries,
                                                           List<TrackedEntityInstance> instances) {
        if (teiImportSummaries != null) {
            for (TEIImportSummary teiImportSummary : teiImportSummaries) {
                String teiUid = teiImportSummary == null ? null : teiImportSummary.reference();

                if (teiUid == null) {
                    continue;
                }

                State state = getState(teiImportSummary.status());
                deleteTEIConflicts(teiUid);

                HandleAction handleAction = trackedEntityInstanceStore.setStateOrDelete(teiUid, state);

                if (state.equals(State.ERROR) || state.equals(State.WARNING)) {
                    dataStatePropagator.resetUploadingEnrollmentAndEventStates(teiUid);
                    setRelationshipsState(teiUid, State.TO_UPDATE);
                } else {
                    setRelationshipsState(teiUid, State.SYNCED);
                }

                if (handleAction != HandleAction.Delete) {
                    storeTEIImportConflicts(teiImportSummary);
                    if (teiImportSummary.enrollments() != null) {
                        EnrollmentImportSummaries importEnrollment = teiImportSummary.enrollments();

                        enrollmentImportHandler.handleEnrollmentImportSummary(
                                importEnrollment.importSummaries(),
                                getEnrollments(teiUid, instances),
                                teiUid);
                    }
                }
            }
        }

        processIgnoredTEIs(teiImportSummaries, instances);
    }

    private void storeTEIImportConflicts(TEIImportSummary teiImportSummary) {
        List<TrackerImportConflict> trackerImportConflicts = new ArrayList<>();
        if (teiImportSummary.description() != null) {
            trackerImportConflicts.add(getConflictBuilder(teiImportSummary)
                    .conflict(teiImportSummary.description())
                    .displayDescription(teiImportSummary.description())
                    .value(teiImportSummary.reference())
                    .build());
        }

        if (teiImportSummary.conflicts() != null) {
            for (ImportConflict importConflict : teiImportSummary.conflicts()) {
                trackerImportConflicts.add(trackerImportConflictParser
                        .getTrackedEntityInstanceConflict(importConflict, getConflictBuilder(teiImportSummary)));
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

    private void setRelationshipsState(String trackedEntityInstanceUid, State state) {
        List<Relationship> dbRelationships =
                relationshipRepository.getByItem(RelationshipHelper.teiItem(trackedEntityInstanceUid), true);

        List<Relationship> ownedRelationships = relationshipDHISVersionManager
                .getOwnedRelationships(dbRelationships, trackedEntityInstanceUid);

        for (Relationship relationship : ownedRelationships) {
            relationshipStore.setStateOrDelete(relationship.uid(), state);
        }
    }

    private void processIgnoredTEIs(List<TEIImportSummary> teiImportSummaries,
                                    List<TrackedEntityInstance> instances) {

        List<String> processedTEIs = BaseImportSummaryHelper.getReferences(teiImportSummaries);
        for (TrackedEntityInstance instance : instances) {
            if (!processedTEIs.contains(instance.uid())) {
                trackedEntityInstanceStore.setStateOrDelete(instance.uid(), State.TO_UPDATE);
                dataStatePropagator.resetUploadingEnrollmentAndEventStates(instance.uid());
                setRelationshipsState(instance.uid(), State.TO_UPDATE);
            }
        }
    }

    private List<Enrollment> getEnrollments(String trackedEntityInstanceUid,
                                            List<TrackedEntityInstance> instances) {
        for (TrackedEntityInstance instance : instances) {
            if (trackedEntityInstanceUid.equals(instance.uid())) {
                return TrackedEntityInstanceInternalAccessor.accessEnrollments(instance);
            }
        }
        return Collections.emptyList();
    }

    private TrackerImportConflict.Builder getConflictBuilder(TEIImportSummary teiImportSummary) {
        return TrackerImportConflict.builder()
                .trackedEntityInstance(teiImportSummary.reference())
                .tableReference(TrackedEntityInstanceTableInfo.TABLE_INFO.name())
                .status(teiImportSummary.status())
                .created(new Date());
    }
}
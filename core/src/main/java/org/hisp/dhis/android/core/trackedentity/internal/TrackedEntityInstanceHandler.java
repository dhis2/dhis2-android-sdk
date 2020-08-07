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

package org.hisp.dhis.android.core.trackedentity.internal;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.arch.cleaners.internal.OrphanCleaner;
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction;
import org.hisp.dhis.android.core.arch.handlers.internal.HandlerWithTransformer;
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableDataHandler;
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableDataHandlerImpl;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.core.relationship.internal.Relationship229Compatible;
import org.hisp.dhis.android.core.relationship.internal.RelationshipDHISVersionManager;
import org.hisp.dhis.android.core.relationship.internal.RelationshipHandler;
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemRelatives;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceInternalAccessor;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
final class TrackedEntityInstanceHandler extends IdentifiableDataHandlerImpl<TrackedEntityInstance> {
    private final RelationshipDHISVersionManager relationshipVersionManager;
    private final HandlerWithTransformer<TrackedEntityAttributeValue> trackedEntityAttributeValueHandler;
    private final IdentifiableDataHandler<Enrollment> enrollmentHandler;
    private final OrphanCleaner<TrackedEntityInstance, Enrollment> enrollmentOrphanCleaner;
    private final OrphanCleaner<TrackedEntityInstance, Relationship229Compatible> relationshipOrphanCleaner;

    @Inject
    TrackedEntityInstanceHandler(
            @NonNull RelationshipDHISVersionManager relationshipVersionManager,
            @NonNull RelationshipHandler relationshipHandler,
            @NonNull TrackedEntityInstanceStore trackedEntityInstanceStore,
            @NonNull HandlerWithTransformer<TrackedEntityAttributeValue> trackedEntityAttributeValueHandler,
            @NonNull IdentifiableDataHandler<Enrollment> enrollmentHandler,
            @NonNull OrphanCleaner<TrackedEntityInstance, Enrollment> enrollmentOrphanCleaner,
            @NonNull OrphanCleaner<TrackedEntityInstance, Relationship229Compatible> relationshipOrphanCleaner) {
        super(trackedEntityInstanceStore, relationshipVersionManager, relationshipHandler);
        this.relationshipVersionManager = relationshipVersionManager;
        this.trackedEntityAttributeValueHandler = trackedEntityAttributeValueHandler;
        this.enrollmentHandler = enrollmentHandler;
        this.enrollmentOrphanCleaner = enrollmentOrphanCleaner;
        this.relationshipOrphanCleaner = relationshipOrphanCleaner;
    }

    @Override
    protected void afterObjectHandled(final TrackedEntityInstance trackedEntityInstance, HandleAction action,
                                      Boolean overwrite, RelationshipItemRelatives relatives) {
        if (action != HandleAction.Delete) {
            trackedEntityAttributeValueHandler.handleMany(
                    trackedEntityInstance.trackedEntityAttributeValues(),
                    value -> value.toBuilder().trackedEntityInstance(trackedEntityInstance.uid()).build());

            List<Enrollment> enrollments =
                    TrackedEntityInstanceInternalAccessor.accessEnrollments(trackedEntityInstance);
            if (enrollments != null) {
                enrollmentHandler.handleMany(enrollments, enrollment -> enrollment.toBuilder()
                                .state(State.SYNCED)
                                .build(),
                        overwrite);
            }

            List<Relationship229Compatible> relationships =
                    TrackedEntityInstanceInternalAccessor.accessRelationships(trackedEntityInstance);
            if (relationships != null && !relationships.isEmpty()) {
                Collection<Relationship> relationshipsList = relationshipVersionManager.from229Compatible(relationships);
                handleRelationships(relationshipsList, trackedEntityInstance, relatives);
            }
        }
    }

    @Override
    protected TrackedEntityInstance addRelationshipState(TrackedEntityInstance object) {
        return object.toBuilder().state(State.RELATIONSHIP).build();
    }

    @Override
    protected TrackedEntityInstance addSyncedState(TrackedEntityInstance object) {
        return object.toBuilder().state(State.SYNCED).build();
    }

    @Override
    protected void deleteOrphans(TrackedEntityInstance object) {
        enrollmentOrphanCleaner.deleteOrphan(object,
                TrackedEntityInstanceInternalAccessor.accessEnrollments(object));

        relationshipOrphanCleaner.deleteOrphan(object,
                TrackedEntityInstanceInternalAccessor.accessRelationships(object));
    }
}
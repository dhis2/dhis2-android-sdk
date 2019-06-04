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

import org.hisp.dhis.android.core.arch.cleaners.internal.OrphanCleaner;
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction;
import org.hisp.dhis.android.core.arch.handlers.internal.HandlerWithTransformer;
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableDataHandlerImpl;
import org.hisp.dhis.android.core.arch.handlers.internal.Transformer;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.core.relationship.Relationship229Compatible;
import org.hisp.dhis.android.core.relationship.RelationshipDHISVersionManager;
import org.hisp.dhis.android.core.relationship.RelationshipHandler;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import dagger.Reusable;

@Reusable
final class TrackedEntityInstanceHandler extends IdentifiableDataHandlerImpl<TrackedEntityInstance> {
    private final RelationshipDHISVersionManager relationshipVersionManager;
    private final RelationshipHandler relationshipHandler;
    private final TrackedEntityInstanceStore trackedEntityInstanceStore;
    private final HandlerWithTransformer<TrackedEntityAttributeValue> trackedEntityAttributeValueHandler;
    private final HandlerWithTransformer<Enrollment> enrollmentHandler;
    private final OrphanCleaner<TrackedEntityInstance, Enrollment> enrollmentOrphanCleaner;

    @Inject
    TrackedEntityInstanceHandler(
            @NonNull RelationshipDHISVersionManager relationshipVersionManager,
            @NonNull RelationshipHandler relationshipHandler,
            @NonNull TrackedEntityInstanceStore trackedEntityInstanceStore,
            @NonNull HandlerWithTransformer<TrackedEntityAttributeValue> trackedEntityAttributeValueHandler,
            @NonNull HandlerWithTransformer<Enrollment> enrollmentHandler,
            @NonNull OrphanCleaner<TrackedEntityInstance, Enrollment> enrollmentOrphanCleaner) {
        super(trackedEntityInstanceStore);
        this.relationshipVersionManager = relationshipVersionManager;
        this.relationshipHandler = relationshipHandler;
        this.trackedEntityInstanceStore = trackedEntityInstanceStore;
        this.trackedEntityAttributeValueHandler = trackedEntityAttributeValueHandler;
        this.enrollmentHandler = enrollmentHandler;
        this.enrollmentOrphanCleaner = enrollmentOrphanCleaner;
    }

    @Override
    protected void afterObjectHandled(final TrackedEntityInstance trackedEntityInstance, HandleAction action) {
        if (action != HandleAction.Delete) {
            trackedEntityAttributeValueHandler.handleMany(
                    trackedEntityInstance.trackedEntityAttributeValues(),
                    value -> value.toBuilder().trackedEntityInstance(trackedEntityInstance.uid()).build());

            List<Enrollment> enrollments = trackedEntityInstance.enrollments();
            if (enrollments != null) {
                enrollmentHandler.handleMany(enrollments, enrollment -> enrollment.toBuilder()
                        .state(State.SYNCED)
                        .build());
            }

            handleRelationships(trackedEntityInstance);
        }

        enrollmentOrphanCleaner.deleteOrphan(trackedEntityInstance, trackedEntityInstance.enrollments());
    }

    private void handleRelationships(TrackedEntityInstance trackedEntityInstance) {
        List<Relationship229Compatible> relationships = trackedEntityInstance.relationships();
        if (relationships != null) {
            for (Relationship229Compatible relationship229 : relationships) {
                TrackedEntityInstance relativeTEI =
                        relationshipVersionManager.getRelativeTei(relationship229, trackedEntityInstance.uid());

                if (relativeTEI != null) {
                    handleRelationship(relativeTEI, relationship229);
                }
            }
        }
    }

    private void handleRelationship(TrackedEntityInstance relativeTEI, Relationship229Compatible relationship229) {
        if (!trackedEntityInstanceStore.exists(relativeTEI.uid())) {
            handle(relativeTEI, relationshipTransformer());
        }

        Relationship relationship = relationshipVersionManager.from229Compatible(relationship229);
        relationshipHandler.handle(relationship);
    }

    public void handleMany(final Collection<TrackedEntityInstance> trackedEntityInstances, boolean asRelationship) {
        if (asRelationship) {
            handleMany(trackedEntityInstances, relationshipTransformer());
        } else {
            handleMany(trackedEntityInstances,
                    trackedEntityInstance -> trackedEntityInstance.toBuilder()
                            .state(State.SYNCED)
                            .build());
        }
    }

    private Transformer<TrackedEntityInstance, TrackedEntityInstance> relationshipTransformer() {
        return trackedEntityInstance -> {
            State currentState = trackedEntityInstanceStore.getState(trackedEntityInstance.uid());
            if (currentState == State.RELATIONSHIP || currentState == null) {
                return trackedEntityInstance.toBuilder()
                        .state(State.RELATIONSHIP)
                        .build();
            } else {
                return trackedEntityInstance;
            }
        };
    }
}
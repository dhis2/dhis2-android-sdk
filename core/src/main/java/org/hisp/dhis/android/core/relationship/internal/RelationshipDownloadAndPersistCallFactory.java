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

package org.hisp.dhis.android.core.relationship.internal;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.arch.api.payload.internal.Payload;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentFields;
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentPersistenceCallFactory;
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentService;
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.internal.EventFields;
import org.hisp.dhis.android.core.event.internal.EventPersistenceCallFactory;
import org.hisp.dhis.android.core.event.internal.EventService;
import org.hisp.dhis.android.core.event.internal.EventStore;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.core.relationship.RelationshipItem;
import org.hisp.dhis.android.core.relationship.RelationshipItemEnrollment;
import org.hisp.dhis.android.core.relationship.RelationshipItemEvent;
import org.hisp.dhis.android.core.relationship.RelationshipItemTableInfo;
import org.hisp.dhis.android.core.relationship.RelationshipItemTrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceFields;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstancePersistenceCallFactory;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceService;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import dagger.Reusable;
import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.Single;

@Reusable
final class RelationshipDownloadAndPersistCallFactory {

    private final RelationshipStore relationshipStore;

    private final TrackedEntityInstanceStore trackedEntityInstanceStore;
    private final TrackedEntityInstanceService trackedEntityInstanceService;
    private final TrackedEntityInstancePersistenceCallFactory teiPersistenceCallFactory;

    private final EnrollmentStore enrollmentStore;
    private final EnrollmentService enrollmentService;
    private final EnrollmentPersistenceCallFactory enrollmentPersistenceCallFactory;

    private final EventStore eventStore;
    private final EventService eventService;
    private final EventPersistenceCallFactory eventPersistenceCallFactory;

    @Inject
    RelationshipDownloadAndPersistCallFactory(
            @NonNull TrackedEntityInstanceStore trackedEntityInstanceStore,
            @NonNull RelationshipStore relationshipStore,
            @NonNull TrackedEntityInstanceService trackedEntityInstanceService,
            @NonNull TrackedEntityInstancePersistenceCallFactory teiPersistenceCallFactory,
            @NonNull EnrollmentStore enrollmentStore,
            @NonNull EnrollmentService enrollmentService,
            @NonNull EnrollmentPersistenceCallFactory enrollmentPersistenceCallFactory,
            @NonNull EventStore eventStore,
            @NonNull EventService eventService,
            @NonNull EventPersistenceCallFactory eventPersistenceCallFactory) {
        this.trackedEntityInstanceStore = trackedEntityInstanceStore;
        this.relationshipStore = relationshipStore;
        this.trackedEntityInstanceService = trackedEntityInstanceService;
        this.teiPersistenceCallFactory = teiPersistenceCallFactory;
        this.enrollmentStore = enrollmentStore;
        this.enrollmentService = enrollmentService;
        this.enrollmentPersistenceCallFactory = enrollmentPersistenceCallFactory;
        this.eventStore = eventStore;
        this.eventService = eventService;
        this.eventPersistenceCallFactory = eventPersistenceCallFactory;
    }

    Completable downloadAndPersist() {
        return Single.just(Collections.emptyList()).flatMapCompletable(emptyMap -> {

            List<String> eventRelationships = eventStore.queryMissingRelationshipsUids();
            if (!eventRelationships.isEmpty()) {
                List<Single<Event>> singles = new ArrayList<>();
                List<String> failedEvents = new ArrayList<>();
                for (String uid : eventRelationships) {
                    Single<Event> single = eventService.getEventSingle(uid, EventFields.asRelationshipFields)
                            .onErrorResumeNext((err) -> {
                                failedEvents.add(uid);
                                return Single.error(err);
                            });

                    singles.add(single);
                }

                return Single.merge(singles)
                        .collect((Callable<List<Event>>) ArrayList::new, List::add)
                        .flatMapCompletable(events -> Completable.fromAction(() -> {
                            eventPersistenceCallFactory.persistRelationships(events).blockingAwait();
                            cleanFailedRelationships(failedEvents, RelationshipItemTableInfo.Columns.EVENT);
                        }));
            }

            List<String> enrollmentRelationships = enrollmentStore.queryMissingRelationshipsUids();
            if (!enrollmentRelationships.isEmpty()) {
                List<Single<Enrollment>> singles = new ArrayList<>();
                List<String> failedEnrollments = new ArrayList<>();
                for (String uid : enrollmentRelationships) {
                    Single<Enrollment> single = enrollmentService.getEnrollmentSingle(uid,
                            EnrollmentFields.asRelationshipFields)
                            .onErrorResumeNext((err) -> {
                                failedEnrollments.add(uid);
                                return Single.error(err);
                            });

                    singles.add(single);
                }

                return Single.merge(singles)
                        .collect((Callable<List<Enrollment>>) ArrayList::new, List::add)
                        .flatMapCompletable(enrollments -> Completable.fromAction(() -> {
                            enrollmentPersistenceCallFactory.persistRelationships(enrollments).blockingAwait();
                            cleanFailedRelationships(failedEnrollments, RelationshipItemTableInfo.Columns.ENROLLMENT);
                        }));
            }

            List<String> teiRelationships = trackedEntityInstanceStore.queryMissingRelationshipsUids();
            if (!teiRelationships.isEmpty()) {
                List<Single<Payload<TrackedEntityInstance>>> singles = new ArrayList<>();
                List<String> failedTeis = new ArrayList<>();
                for (String uid : teiRelationships) {
                    Single<Payload<TrackedEntityInstance>> single =
                            trackedEntityInstanceService.getTrackedEntityInstance(uid,
                                    TrackedEntityInstanceFields.asRelationshipFields, true, true)
                                    .onErrorResumeNext((err) -> {
                                        failedTeis.add(uid);
                                        return Single.just(Payload.emptyPayload());
                                    });

                    singles.add(single);
                }

                return Single.merge(singles)
                        .collect((Callable<List<TrackedEntityInstance>>) ArrayList::new,
                                (teis, payload) -> teis.addAll(payload.items()))
                        .flatMapCompletable(teis -> Completable.fromAction(() -> {
                            teiPersistenceCallFactory.persistRelationships(teis).blockingAwait();
                            cleanFailedRelationships(failedTeis,
                                    RelationshipItemTableInfo.Columns.TRACKED_ENTITY_INSTANCE);
                        }));
            }

            return Completable.create(CompletableEmitter::onComplete);
        });

    }

    private void cleanFailedRelationships(List<String> failedTeis, String elementType) {
        List<Relationship> corruptedRelationships = new ArrayList<>();
        for (String uid : failedTeis) {
            RelationshipItem.Builder builder = RelationshipItem.builder();
            switch (elementType) {
                case RelationshipItemTableInfo.Columns.EVENT:
                    builder.event(RelationshipItemEvent.builder().event(uid).build());
                    break;
                case RelationshipItemTableInfo.Columns.ENROLLMENT:
                    builder.enrollment(RelationshipItemEnrollment.builder().enrollment(uid).build());
                    break;
                case RelationshipItemTableInfo.Columns.TRACKED_ENTITY_INSTANCE:
                    builder.trackedEntityInstance(RelationshipItemTrackedEntityInstance.builder()
                            .trackedEntityInstance(uid).build());
                    break;
            }
            corruptedRelationships.addAll(relationshipStore.getRelationshipsByItem(builder.build()));
        }

        for (Relationship r : corruptedRelationships) {
            relationshipStore.deleteById(r);
        }
    }
}

/*
 *  Copyright (c) 2004-2022, University of Oslo
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

package org.hisp.dhis.android.core.relationship.internal;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.arch.api.payload.internal.Payload;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentFields;
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentPersistenceCallFactory;
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentService;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.internal.EventFields;
import org.hisp.dhis.android.core.event.internal.EventPersistenceCallFactory;
import org.hisp.dhis.android.core.event.internal.EventService;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import dagger.Reusable;
import io.reactivex.Completable;
import io.reactivex.Single;

@Reusable
@SuppressWarnings({"PMD.ExcessiveImports", "PMD.CouplingBetweenObjects"})
public final class RelationshipDownloadAndPersistCallFactory {

    private final RelationshipStore relationshipStore;

    private final TrackedEntityInstanceService trackedEntityInstanceService;
    private final TrackedEntityInstancePersistenceCallFactory teiPersistenceCallFactory;

    private final EnrollmentService enrollmentService;
    private final EnrollmentPersistenceCallFactory enrollmentPersistenceCallFactory;

    private final EventService eventService;
    private final EventPersistenceCallFactory eventPersistenceCallFactory;

    private final String ouMode = OrganisationUnitMode.ACCESSIBLE.name();

    @Inject
    RelationshipDownloadAndPersistCallFactory(
            @NonNull RelationshipStore relationshipStore,
            @NonNull TrackedEntityInstanceService trackedEntityInstanceService,
            @NonNull TrackedEntityInstancePersistenceCallFactory teiPersistenceCallFactory,
            @NonNull EnrollmentService enrollmentService,
            @NonNull EnrollmentPersistenceCallFactory enrollmentPersistenceCallFactory,
            @NonNull EventService eventService,
            @NonNull EventPersistenceCallFactory eventPersistenceCallFactory) {
        this.relationshipStore = relationshipStore;
        this.trackedEntityInstanceService = trackedEntityInstanceService;
        this.teiPersistenceCallFactory = teiPersistenceCallFactory;
        this.enrollmentService = enrollmentService;
        this.enrollmentPersistenceCallFactory = enrollmentPersistenceCallFactory;
        this.eventService = eventService;
        this.eventPersistenceCallFactory = eventPersistenceCallFactory;
    }

    public Completable downloadAndPersist(RelationshipItemRelatives relatives) {
        return Completable.defer(() -> downloadRelativeEvents(relatives).andThen(
                downloadRelativeEnrolments(relatives).andThen(
                        downloadRelativeTEIs(relatives)
                )
        ));
    }

    private Completable downloadRelativeEvents(RelationshipItemRelatives relatives) {
        return Completable.defer(() -> {
            Set<String> eventRelationships = relatives.getRelativeEventUids();
            List<Single<Payload<Event>>> singles = new ArrayList<>();
            List<String> failedEvents = new ArrayList<>();

            if (!eventRelationships.isEmpty()) {
                for (String uid : eventRelationships) {
                    Single<Payload<Event>> single = eventService
                            .getEventSingle(uid, EventFields.asRelationshipFields, ouMode)
                            .onErrorResumeNext((err) -> {
                                failedEvents.add(uid);
                                return Single.error(err);
                            });
                    singles.add(single);
                }
            }

            return Single.merge(singles)
                    .collect((Callable<List<Payload<Event>>>) ArrayList::new, List::add)
                    .flatMapCompletable(eventPayloads -> Completable.fromAction(() -> {
                        List<Event> events = new ArrayList<>();
                        for (Payload<Event> eventPayload : eventPayloads) {
                            events.addAll(eventPayload.items());
                        }
                        eventPersistenceCallFactory.persistAsRelationships(events).blockingAwait();
                        for (Event event : events) {
                            if (event.enrollment() != null) {
                                relatives.addEnrollment(event.enrollment());
                            }
                        }
                        cleanFailedRelationships(failedEvents, RelationshipItemTableInfo.Columns.EVENT);
                    }));
        });
    }

    private Completable downloadRelativeEnrolments(RelationshipItemRelatives relatives) {
        return Completable.defer(() -> {
            List<Single<Enrollment>> singles = new ArrayList<>();
            List<String> failedEnrollments = new ArrayList<>();
            Set<String> enrollmentRelationships = relatives.getRelativeEnrollmentUids();

            if (!enrollmentRelationships.isEmpty()) {
                for (String uid : enrollmentRelationships) {
                    Single<Enrollment> single = enrollmentService.getEnrollmentSingle(uid,
                            EnrollmentFields.asRelationshipFields)
                            .onErrorResumeNext((err) -> {
                                failedEnrollments.add(uid);
                                return Single.error(err);
                            });
                    singles.add(single);
                }
            }

            return Single.merge(singles)
                    .collect((Callable<List<Enrollment>>) ArrayList::new, List::add)
                    .flatMapCompletable(enrollments -> Completable.fromAction(() -> {
                        enrollmentPersistenceCallFactory.persistAsRelationships(enrollments).blockingAwait();
                        for (Enrollment enrollment : enrollments) {
                            if (enrollment.trackedEntityInstance() != null) {
                                relatives.addTrackedEntityInstance(enrollment.trackedEntityInstance());
                            }
                        }
                        cleanFailedRelationships(failedEnrollments, RelationshipItemTableInfo.Columns.ENROLLMENT);
                    }));
        });
    }

    private Completable downloadRelativeTEIs(RelationshipItemRelatives relatives) {
        return Completable.defer(() -> {
            List<Single<Payload<TrackedEntityInstance>>> singles = new ArrayList<>();
            List<String> failedTeis = new ArrayList<>();
            Set<String> teiRelationships = relatives.getRelativeTrackedEntityInstanceUids();

            if (!teiRelationships.isEmpty()) {
                for (String uid : teiRelationships) {
                    Single<Payload<TrackedEntityInstance>> single =
                            trackedEntityInstanceService.getTrackedEntityInstance(uid,
                                    OrganisationUnitMode.ACCESSIBLE.name(),
                                    TrackedEntityInstanceFields.asRelationshipFields,
                                    true,
                                    true)
                                    .onErrorResumeNext((err) -> {
                                        failedTeis.add(uid);
                                        return Single.just(Payload.emptyPayload());
                                    });

                    singles.add(single);
                }
            }

            return Single.merge(singles)
                    .collect((Callable<List<TrackedEntityInstance>>) ArrayList::new,
                            (teis, payload) -> teis.addAll(payload.items()))
                    .flatMapCompletable(teis -> Completable.fromAction(() -> {
                        teiPersistenceCallFactory.persistRelationships(teis).blockingAwait();
                        cleanFailedRelationships(failedTeis, RelationshipItemTableInfo.Columns.TRACKED_ENTITY_INSTANCE);
                    }));
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
                default:
                    break;
            }
            corruptedRelationships.addAll(relationshipStore.getRelationshipsByItem(builder.build()));
        }

        for (Relationship r : corruptedRelationships) {
            relationshipStore.deleteById(r);
        }
    }
}

/*
 *  Copyright (c) 2004-2023, University of Oslo
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
package org.hisp.dhis.android.core.relationship.internal

import dagger.Reusable
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentPersistenceCallFactory
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.internal.EventPersistenceCallFactory
import org.hisp.dhis.android.core.relationship.*
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstancePersistenceCallFactory
import org.hisp.dhis.android.core.trackedentity.internal.TrackerParentCallFactory

@Reusable
class RelationshipDownloadAndPersistCallFactory @Inject internal constructor(
    private val relationshipStore: RelationshipStore,
    private val trackerParentCallFactory: TrackerParentCallFactory,
    private val teiPersistenceCallFactory: TrackedEntityInstancePersistenceCallFactory,
    private val enrollmentPersistenceCallFactory: EnrollmentPersistenceCallFactory,
    private val eventPersistenceCallFactory: EventPersistenceCallFactory
) {
    fun downloadAndPersist(relatives: RelationshipItemRelatives): Completable {
        return Completable.defer {
            downloadRelativeEvents(relatives).andThen(
                downloadRelativeEnrolments(relatives).andThen(
                    downloadRelativeTEIs(relatives)
                )
            )
        }
    }

    private fun downloadRelativeEvents(relatives: RelationshipItemRelatives): Completable {
        return Completable.defer {
            val eventRelationships = relatives.relativeEventUids
            val singles: MutableList<Single<Payload<Event>>> = mutableListOf()
            val failedEvents: MutableList<String> = mutableListOf()

            if (eventRelationships.isNotEmpty()) {
                for (uid in eventRelationships) {
                    val single: Single<Payload<Event>> = trackerParentCallFactory.getEventCall()
                        .getRelationshipEntityCall(uid)
                        .onErrorResumeNext { err: Throwable ->
                            failedEvents.add(uid)
                            Single.error(err)
                        }
                    singles.add(single)
                }
            }
            Single.merge(singles)
                .collect({ ArrayList() }) { obj: MutableList<Payload<Event>>, e: Payload<Event> ->
                    obj.add(e)
                }
                .flatMapCompletable { eventPayloads: List<Payload<Event>> ->
                    Completable.fromAction {
                        val events = eventPayloads.flatMap { it.items() }
                        eventPersistenceCallFactory.persistAsRelationships(events).blockingAwait()
                        for (event in events) {
                            if (event.enrollment() != null) {
                                relatives.addEnrollment(event.enrollment())
                            }
                        }
                        cleanFailedRelationships(failedEvents, RelationshipItemTableInfo.Columns.EVENT)
                    }
                }
        }
    }

    private fun downloadRelativeEnrolments(relatives: RelationshipItemRelatives): Completable {
        return Completable.defer {
            val singles: MutableList<Single<Enrollment>> = mutableListOf()
            val failedEnrollments: MutableList<String> = mutableListOf()
            val enrollmentRelationships = relatives.relativeEnrollmentUids
            if (enrollmentRelationships.isNotEmpty()) {
                for (uid in enrollmentRelationships) {
                    val single = trackerParentCallFactory.getEnrollmentCall()
                        .getRelationshipEntityCall(uid)
                        .onErrorResumeNext { err: Throwable ->
                            failedEnrollments.add(uid)
                            Single.error(err)
                        }
                    singles.add(single)
                }
            }
            Single.merge(singles)
                .collect({ ArrayList() }) { obj: MutableList<Enrollment>, e: Enrollment ->
                    obj.add(e)
                }
                .flatMapCompletable { enrollments: List<Enrollment> ->
                    Completable.fromAction {
                        enrollmentPersistenceCallFactory.persistAsRelationships(enrollments).blockingAwait()
                        for (enrollment in enrollments) {
                            if (enrollment.trackedEntityInstance() != null) {
                                relatives.addTrackedEntityInstance(enrollment.trackedEntityInstance())
                            }
                        }
                        cleanFailedRelationships(failedEnrollments, RelationshipItemTableInfo.Columns.ENROLLMENT)
                    }
                }
        }
    }

    private fun downloadRelativeTEIs(relatives: RelationshipItemRelatives): Completable {
        return Completable.defer {
            val singles: MutableList<Single<Payload<TrackedEntityInstance>>> = mutableListOf()
            val failedTeis: MutableList<String> = mutableListOf()
            val teiRelationships = relatives.relativeTrackedEntityInstanceUids
            if (teiRelationships.isNotEmpty()) {
                for (uid in teiRelationships) {
                    val single = trackerParentCallFactory.getTrackedEntityCall()
                        .getRelationshipEntityCall(uid)
                        .onErrorResumeNext {
                            failedTeis.add(uid)
                            Single.just(Payload.emptyPayload())
                        }
                    singles.add(single)
                }
            }
            Single.merge(singles)
                .collect(
                    { ArrayList() }
                ) { teis: MutableList<TrackedEntityInstance>, payload: Payload<TrackedEntityInstance> ->
                    teis.addAll(payload.items())
                }
                .flatMapCompletable { teis: List<TrackedEntityInstance>? ->
                    Completable.fromAction {
                        teiPersistenceCallFactory.persistRelationships(teis!!).blockingAwait()
                        cleanFailedRelationships(failedTeis, RelationshipItemTableInfo.Columns.TRACKED_ENTITY_INSTANCE)
                    }
                }
        }
    }

    private fun cleanFailedRelationships(failedTeis: List<String>, elementType: String) {
        val corruptedRelationships: MutableList<Relationship> = mutableListOf()
        for (uid in failedTeis) {
            val builder = RelationshipItem.builder()
            when (elementType) {
                RelationshipItemTableInfo.Columns.EVENT -> builder.event(
                    RelationshipItemEvent.builder().event(uid).build()
                )
                RelationshipItemTableInfo.Columns.ENROLLMENT -> builder.enrollment(
                    RelationshipItemEnrollment.builder().enrollment(uid).build()
                )
                RelationshipItemTableInfo.Columns.TRACKED_ENTITY_INSTANCE -> builder.trackedEntityInstance(
                    RelationshipItemTrackedEntityInstance.builder()
                        .trackedEntityInstance(uid).build()
                )
                else -> {}
            }
            corruptedRelationships.addAll(relationshipStore.getRelationshipsByItem(builder.build()))
        }
        for (r in corruptedRelationships) {
            relationshipStore.deleteById(r)
        }
    }
}

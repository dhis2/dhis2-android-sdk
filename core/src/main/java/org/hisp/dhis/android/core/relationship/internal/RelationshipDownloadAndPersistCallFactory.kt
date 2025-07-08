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

import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutor
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentPersistenceCallFactory
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.internal.EventPersistenceCallFactory
import org.hisp.dhis.android.core.relationship.Relationship
import org.hisp.dhis.android.core.relationship.RelationshipItem
import org.hisp.dhis.android.core.relationship.RelationshipItemEnrollment
import org.hisp.dhis.android.core.relationship.RelationshipItemEvent
import org.hisp.dhis.android.persistence.relationship.RelationshipItemTableInfo
import org.hisp.dhis.android.core.relationship.RelationshipItemTrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstancePersistenceCallFactory
import org.hisp.dhis.android.core.trackedentity.internal.TrackerParentCallFactory
import org.koin.core.annotation.Singleton

@Singleton
internal class RelationshipDownloadAndPersistCallFactory(
    private val relationshipStore: RelationshipStore,
    private val trackerParentCallFactory: TrackerParentCallFactory,
    private val teiPersistenceCallFactory: TrackedEntityInstancePersistenceCallFactory,
    private val enrollmentPersistenceCallFactory: EnrollmentPersistenceCallFactory,
    private val eventPersistenceCallFactory: EventPersistenceCallFactory,
    private val coroutineAPICallExecutor: CoroutineAPICallExecutor,
    private val relationshipItemStoreSelector: RelationshipItemElementStoreSelector,
) {
    suspend fun downloadAndPersist(relatives: RelationshipItemRelatives) {
        downloadRelativeEvents(relatives)
        downloadRelativeEnrolments(relatives)
        downloadRelativeTEIs(relatives)
    }

    private suspend fun downloadRelativeEvents(relatives: RelationshipItemRelatives) {
        val events: MutableList<Event> = mutableListOf()
        val failedEvents: MutableList<String> = mutableListOf()

        for (item in relatives.getRelativeEvents()) {
            if (itemDoesNotExist(item)) {
                coroutineAPICallExecutor.wrap(storeError = true) {
                    trackerParentCallFactory.getEventCall().getRelationshipEntityCall(item)
                }.fold(
                    onSuccess = { eventPayload ->
                        events.addAll(eventPayload.items)
                        eventPayload.items.mapNotNull { it.enrollment() }.forEach { enrollment ->
                            val relativeEnrollment = RelationshipItemRelative(
                                itemUid = enrollment,
                                itemType = RelationshipItemTableInfo.Columns.ENROLLMENT,
                                relationshipTypeUid = item.relationshipTypeUid,
                                constraintType = item.constraintType,
                            )
                            relatives.addEnrollment(relativeEnrollment)
                        }
                    },
                    onFailure = { failedEvents.add(item.itemUid) },
                )
            }
        }

        eventPersistenceCallFactory.persistAsRelationships(events)

        cleanFailedRelationships(failedEvents, RelationshipItemTableInfo.Columns.EVENT)
    }

    private suspend fun downloadRelativeEnrolments(relatives: RelationshipItemRelatives) {
        val enrollments: MutableList<Enrollment> = mutableListOf()
        val failedEnrollments: MutableList<String> = mutableListOf()

        for (item in relatives.getRelativeEnrollments()) {
            if (itemDoesNotExist(item)) {
                coroutineAPICallExecutor.wrap(storeError = true) {
                    trackerParentCallFactory.getEnrollmentCall().getRelationshipEntityCall(item)
                }.fold(
                    onSuccess = { enrollment ->
                        enrollments.add(enrollment)
                        enrollment.trackedEntityInstance()?.let { tei ->
                            val relativeTei = RelationshipItemRelative(
                                itemUid = tei,
                                itemType = RelationshipItemTableInfo.Columns.TRACKED_ENTITY_INSTANCE,
                                relationshipTypeUid = item.relationshipTypeUid,
                                constraintType = item.constraintType,
                            )
                            relatives.addTrackedEntityInstance(relativeTei)
                        }
                    },
                    onFailure = { failedEnrollments.add(item.itemUid) },
                )
            }
        }

        enrollmentPersistenceCallFactory.persistAsRelationships(enrollments)

        cleanFailedRelationships(failedEnrollments, RelationshipItemTableInfo.Columns.ENROLLMENT)
    }

    private suspend fun downloadRelativeTEIs(relatives: RelationshipItemRelatives) {
        val teis: MutableList<TrackedEntityInstance> = mutableListOf()
        val failedTeis: MutableList<String> = mutableListOf()

        for (item in relatives.getRelativeTrackedEntityInstances()) {
            if (itemDoesNotExist(item)) {
                coroutineAPICallExecutor.wrap(storeError = true) {
                    trackerParentCallFactory.getTrackedEntityCall().getRelationshipEntityCall(item)
                }.fold(
                    onSuccess = { teiPayload -> teis.addAll(teiPayload.items) },
                    onFailure = { failedTeis.add(item.itemUid) },
                )
            }
        }

        teiPersistenceCallFactory.persistRelationships(teis)

        cleanFailedRelationships(failedTeis, RelationshipItemTableInfo.Columns.TRACKED_ENTITY_INSTANCE)
    }

    private suspend fun cleanFailedRelationships(failedTeis: List<String>, elementType: String) {
        val corruptedRelationships: MutableList<Relationship> = mutableListOf()
        for (uid in failedTeis) {
            val builder = RelationshipItem.builder()
            when (elementType) {
                RelationshipItemTableInfo.Columns.EVENT -> builder.event(
                    RelationshipItemEvent.builder().event(uid).build(),
                )

                RelationshipItemTableInfo.Columns.ENROLLMENT -> builder.enrollment(
                    RelationshipItemEnrollment.builder().enrollment(uid).build(),
                )

                RelationshipItemTableInfo.Columns.TRACKED_ENTITY_INSTANCE -> builder.trackedEntityInstance(
                    RelationshipItemTrackedEntityInstance.builder()
                        .trackedEntityInstance(uid).build(),
                )

                else -> {}
            }
            corruptedRelationships.addAll(relationshipStore.getRelationshipsByItem(builder.build()))
        }
        for (r in corruptedRelationships) {
            r.uid()?.let { relationshipStore.deleteIfExists(it) }
        }
    }

    private suspend fun itemDoesNotExist(item: RelationshipItemRelative): Boolean {
        return !relationshipItemStoreSelector.getElementStore(item).exists(item.itemUid)
    }
}

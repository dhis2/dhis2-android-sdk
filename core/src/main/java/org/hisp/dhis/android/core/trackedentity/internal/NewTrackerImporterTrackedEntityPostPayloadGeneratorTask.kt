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
package org.hisp.dhis.android.core.trackedentity.internal

import dagger.Reusable
import javax.inject.Inject
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.enrollment.NewTrackerImporterEnrollment
import org.hisp.dhis.android.core.event.NewTrackerImporterEvent
import org.hisp.dhis.android.core.note.NewTrackerImporterNote
import org.hisp.dhis.android.core.relationship.NewTrackerImporterRelationship
import org.hisp.dhis.android.core.trackedentity.*

@Reusable
internal class NewTrackerImporterTrackedEntityPostPayloadGeneratorTask @Inject internal constructor(
    private val trackedEntities: List<TrackedEntityInstance>,
    private val dataValueMap: Map<String, List<NewTrackerImporterTrackedEntityDataValue>>,
    private val eventMap: Map<String, List<NewTrackerImporterEvent>>,
    private val enrollmentMap: Map<String, List<NewTrackerImporterEnrollment>>,
    private val attributeValueMap: Map<String, List<NewTrackerImporterTrackedEntityAttributeValue>>,
    private val relationshipMap: Map<String, List<NewTrackerImporterRelationship>>,
    private val notes: List<NewTrackerImporterNote>
) {

    fun generate(): NewTrackerImporterPayloadWrapper {
        val trackedEntityTransformer = NewTrackerImporterTranckedEntityTransformer()
        val wrapper = NewTrackerImporterPayloadWrapper()

        val partitioned = trackedEntities
            .map { trackedEntityTransformer.transform(it) }
            .map { getTrackedEntity(it) }
            .partition { it.deleted()!! }

        partitioned.first.forEach {
            wrapper.deleted.trackedEntities.add(getTrackedEntityWithEnrollments(it))
        }

        partitioned.second.forEach { entity ->
            if (entity.syncState() != State.SYNCED) {
                wrapper.updated.trackedEntities.add(entity)
            }

            addRelationshipsToWrapper(wrapper, relationshipMap[entity.uid()])

            val partitionedEnrollments = getEnrollments(entity.uid())
                .partition { it.deleted()!! }

            partitionedEnrollments.first.forEach {
                wrapper.deleted.enrollments.add(getEnrollmentWithEvents(it))
            }

            partitionedEnrollments.second.forEach { enrollment ->
                if (enrollment.syncState() != State.SYNCED) {
                    wrapper.updated.enrollments.add(enrollment)
                }

                addRelationshipsToWrapper(wrapper, relationshipMap[enrollment.uid()])

                val partitionedEvents = getEvents(enrollment.uid())
                    .filter { it.syncState() != State.SYNCED }
                    .partition { it.deleted()!! }

                partitionedEvents.first.forEach {
                    wrapper.deleted.events.add(it)
                }

                partitionedEvents.second.forEach { event ->
                    wrapper.updated.events.add(event)

                    addRelationshipsToWrapper(wrapper, relationshipMap[event.uid()])
                }
            }
        }

        return wrapper
    }

    private fun getTrackedEntityWithEnrollments(
        trackedEntity: NewTrackerImporterTrackedEntity
    ): NewTrackerImporterTrackedEntity {
        return getTrackedEntity(trackedEntity).toBuilder()
            .enrollments(getEnrollmentsWithEvents(trackedEntity.uid()))
            .build()
    }

    private fun getTrackedEntity(
        trackedEntity: NewTrackerImporterTrackedEntity
    ): NewTrackerImporterTrackedEntity {
        return trackedEntity.toBuilder()
            .trackedEntityAttributeValues(attributeValueMap[trackedEntity.uid()] ?: emptyList())
            .build()
    }

    private fun getEnrollmentsWithEvents(
        trackedEntityInstanceUid: String
    ): List<NewTrackerImporterEnrollment> {
        return getEnrollments(trackedEntityInstanceUid).map { getEnrollmentWithEvents(it) }
    }

    private fun getEnrollmentWithEvents(
        enrollment: NewTrackerImporterEnrollment
    ): NewTrackerImporterEnrollment {
        return enrollment.toBuilder()
            .events(getEvents(enrollment.uid()))
            .build()
    }

    private fun getEnrollments(
        trackedEntityInstanceUid: String
    ): List<NewTrackerImporterEnrollment> {
        return enrollmentMap[trackedEntityInstanceUid]?.map { enrollment ->
            enrollment.toBuilder()
                .notes(notes.filter { it.enrollment() == enrollment.uid() })
                .build()
        } ?: emptyList()
    }

    private fun getEvents(
        enrollmentUid: String
    ): List<NewTrackerImporterEvent> {
        return eventMap[enrollmentUid]?.map { event ->
            val eventBuilder = event.toBuilder()
                .trackedEntityDataValues(dataValueMap[event.uid()])
                .notes(notes.filter { it.event() == event.uid() })
            eventBuilder.build()
        } ?: emptyList()
    }

    private fun addRelationshipsToWrapper(
        wrapper: NewTrackerImporterPayloadWrapper,
        relationships: List<NewTrackerImporterRelationship>?
    ) {
        val partitionedRelationships = relationships?.partition { it.deleted()!! }

        partitionedRelationships?.first?.let { deleted ->
            wrapper.deleted.relationships.addAll(deleted)
        }

        partitionedRelationships?.second?.let { updated ->
            wrapper.updated.relationships.addAll(updated)
        }
    }
}

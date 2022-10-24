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
package org.hisp.dhis.android.core.trackedentity.internal

import dagger.Reusable
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.arch.db.stores.internal.LinkStore
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.EnrollmentInternalAccessor.accessEvents
import org.hisp.dhis.android.core.enrollment.NewTrackerImporterEnrollmentTransformer
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.NewTrackerImporterEventTransformer
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute
import org.hisp.dhis.android.core.relationship.NewTrackerImporterRelationshipTransformer
import org.hisp.dhis.android.core.relationship.Relationship
import org.hisp.dhis.android.core.trackedentity.NewTrackerImporterTranckedEntityTransformer
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceInternalAccessor.accessEnrollments
import org.hisp.dhis.android.core.trackedentity.TrackedEntityTypeAttribute

@Reusable
internal class NewTrackerImporterTrackedEntityPostPayloadGenerator @Inject internal constructor(
    private val programTrackedEntityAttributeStore: IdentifiableObjectStore<ProgramTrackedEntityAttribute>,
    private val trackedEntityTypeAttributeStore: LinkStore<TrackedEntityTypeAttribute>,
    private val oldTrackerImporterPayloadGenerator: OldTrackerImporterPayloadGenerator
) {

    fun getTrackedEntityPayload(
        instances: List<TrackedEntityInstance>
    ): NewTrackerImporterPayloadWrapper {
        val oldPayload = oldTrackerImporterPayloadGenerator.getTrackedEntityInstancePayload(instances)
        return transformPayload(oldPayload)
    }

    fun getEventPayload(
        events: List<Event>
    ): NewTrackerImporterPayloadWrapper {
        val oldPayload = oldTrackerImporterPayloadGenerator.getEventPayload(events)
        return transformPayload(oldPayload)
    }

    private fun transformPayload(
        oldPayload: OldTrackerImporterPayload
    ): NewTrackerImporterPayloadWrapper {
        val wrapper = NewTrackerImporterPayloadWrapper(programOwners = oldPayload.programOwners)

        val tetAttributeMap = getTrackedEntityTypeAttributeMap()
        val programAttributeMap = getProgramAttributesMap()

        oldPayload.trackedEntityInstances.forEach { tei ->
            addTrackedEntityToWrapper(wrapper, tei, tetAttributeMap)

            accessEnrollments(tei).forEach { enrollment ->
                addEnrollmentToWrapper(wrapper, enrollment, tei.trackedEntityAttributeValues(), programAttributeMap)

                accessEvents(enrollment).forEach { event ->
                    addEventToWrapper(wrapper, event)
                }
            }
        }

        oldPayload.events.forEach { event ->
            addEventToWrapper(wrapper, event)
        }

        oldPayload.relationships.forEach { relationship ->
            addRelationshipToWrapper(wrapper, relationship)
        }

        return wrapper
    }

    private fun addTrackedEntityToWrapper(
        wrapper: NewTrackerImporterPayloadWrapper,
        instance: TrackedEntityInstance,
        tetAttributeMap: Map<String, List<String>>
    ) {
        if (instance.syncState() != State.SYNCED) {
            val transformed = NewTrackerImporterTranckedEntityTransformer.transform(instance, tetAttributeMap)

            transformed.let {
                when (it.deleted()) {
                    true -> wrapper.deleted.trackedEntities.add(it)
                    else -> wrapper.updated.trackedEntities.add(it)
                }
            }
        }
    }

    private fun addEnrollmentToWrapper(
        wrapper: NewTrackerImporterPayloadWrapper,
        enrollment: Enrollment,
        attributes: List<TrackedEntityAttributeValue>?,
        programAttributeMap: Map<String, List<String>>
    ) {
        if (enrollment.syncState() != State.SYNCED) {
            val transformed =
                NewTrackerImporterEnrollmentTransformer.transform(enrollment, attributes, programAttributeMap)

            transformed.let {
                when (it.deleted()) {
                    true -> wrapper.deleted.enrollments.add(it)
                    else -> wrapper.updated.enrollments.add(it)
                }
            }
        }
    }

    private fun addEventToWrapper(
        wrapper: NewTrackerImporterPayloadWrapper,
        event: Event
    ) {
        if (event.syncState() != State.SYNCED) {
            val transformed = NewTrackerImporterEventTransformer.transform(event)

            transformed.let {
                when (it.deleted()) {
                    true -> wrapper.deleted.events.add(it)
                    else -> wrapper.updated.events.add(it)
                }
            }
        }
    }

    private fun addRelationshipToWrapper(
        wrapper: NewTrackerImporterPayloadWrapper,
        relationship: Relationship
    ) {
        val transformed = NewTrackerImporterRelationshipTransformer.transform(relationship)

        transformed.let {
            when (it.deleted()) {
                true -> wrapper.deleted.relationships.add(it)
                else -> wrapper.updated.relationships.add(it)
            }
        }
    }

    private fun getProgramAttributesMap(): Map<String, List<String>> {
        return programTrackedEntityAttributeStore.selectAll()
            .filter { it.program()?.uid() != null }
            .groupBy { it.program()?.uid()!! }
            .mapValues { it.value.mapNotNull { a -> a.trackedEntityAttribute()?.uid() } }
    }

    private fun getTrackedEntityTypeAttributeMap(): Map<String, List<String>> {
        return trackedEntityTypeAttributeStore.selectAll()
            .filter { it.trackedEntityType()?.uid() != null }
            .groupBy { it.trackedEntityType()?.uid()!! }
            .mapValues { it.value.mapNotNull { a -> a.trackedEntityAttribute()?.uid() } }
    }
}

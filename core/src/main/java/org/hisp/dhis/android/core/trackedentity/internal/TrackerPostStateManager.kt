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
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableDataObjectStore
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.enrollment.EnrollmentInternalAccessor
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.internal.EventStore
import org.hisp.dhis.android.core.fileresource.FileResource
import org.hisp.dhis.android.core.relationship.Relationship
import org.hisp.dhis.android.core.relationship.internal.RelationshipStore
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceInternalAccessor

@Reusable
internal class TrackerPostStateManager @Inject internal constructor(
    private val trackedEntityInstanceStore: TrackedEntityInstanceStore,
    private val enrollmentStore: EnrollmentStore,
    private val eventStore: EventStore,
    private val relationshipStore: RelationshipStore,
    private val fileResourceStore: IdentifiableDataObjectStore<FileResource>,
    private val h: StatePersistorHelper
) {

    fun restorePayloadStates(
        trackedEntityInstances: List<TrackedEntityInstance> = emptyList(),
        events: List<Event> = emptyList(),
        relationships: List<Relationship> = emptyList(),
        fileResources: List<String> = emptyList()
    ) {
        setPayloadStates(trackedEntityInstances, events, relationships, fileResources, null)
    }

    @Suppress("NestedBlockDepth")
    fun setPayloadStates(
        trackedEntityInstances: List<TrackedEntityInstance> = emptyList(),
        events: List<Event> = emptyList(),
        relationships: List<Relationship> = emptyList(),
        fileResources: List<String> = emptyList(),
        forcedState: State?
    ) {
        val teiMap: MutableMap<State, MutableList<String>> = mutableMapOf()
        val enrollmentMap: MutableMap<State, MutableList<String>> = mutableMapOf()
        val eventMap: MutableMap<State, MutableList<String>> = mutableMapOf()
        val relationshipMap: MutableMap<State, MutableList<String>> = mutableMapOf()
        val fileResourceMap: MutableMap<State, MutableList<String>> = mutableMapOf()

        trackedEntityInstances.forEach { instance ->
            h.addState(teiMap, instance, forcedState)
            TrackedEntityInstanceInternalAccessor.accessEnrollments(instance)?.forEach { enrollment ->
                h.addState(enrollmentMap, enrollment, forcedState)
                for (event in EnrollmentInternalAccessor.accessEvents(enrollment)) {
                    h.addState(eventMap, event, forcedState)
                }
            }
            TrackedEntityInstanceInternalAccessor.accessRelationships(instance)?.forEach { r ->
                h.addState(relationshipMap, r, forcedState)
            }
        }

        events.forEach { h.addState(eventMap, it, forcedState) }

        relationships.forEach { h.addState(relationshipMap, it, forcedState) }

        fileResources.forEach { fileResource ->
            fileResourceStore.selectByUid(fileResource)?.let {
                h.addState(fileResourceMap, it, forcedState)
            }
        }

        h.persistStates(teiMap, trackedEntityInstanceStore)
        h.persistStates(enrollmentMap, enrollmentStore)
        h.persistStates(eventMap, eventStore)
        h.persistStates(relationshipMap, relationshipStore)
        h.persistStates(fileResourceMap, fileResourceStore)
    }
}

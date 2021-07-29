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
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore
import org.hisp.dhis.android.core.event.internal.EventStore
import org.hisp.dhis.android.core.relationship.internal.RelationshipStore

@Reusable
internal class NewTrackerImporterTrackedEntityPostStateManager @Inject internal constructor(
    private val trackedEntityInstanceStore: TrackedEntityInstanceStore,
    private val enrollmentStore: EnrollmentStore,
    private val eventStore: EventStore,
    private val relationshipStore: RelationshipStore,
    private val h: StatePersistorHelper
) {

    fun restoreStates(payload: NewTrackerImporterPayload) {
        setStates(payload, null)
    }

    fun setStates(payload: NewTrackerImporterPayload, forcedState: State?) {
        val teiMap = mutableMapOf<State, MutableList<String>>()
        val enrollmentMap = mutableMapOf<State, MutableList<String>>()
        val eventMap = mutableMapOf<State, MutableList<String>>()
        val relationshipMap = mutableMapOf<State, MutableList<String>>()

        val trackedEntities = payload.trackedEntities
        val enrollments = trackedEntities.flatMap { it.enrollments() ?: emptyList() } + payload.enrollments
        val events = enrollments.flatMap { it.events() ?: emptyList() } + payload.events
        val relationships = payload.relationships

        trackedEntities.forEach { h.addState(teiMap, it, forcedState) }
        enrollments.forEach { h.addState(enrollmentMap, it, forcedState) }
        events.forEach { h.addState(eventMap, it, forcedState) }
        relationships.forEach { h.addState(relationshipMap, it, forcedState) }

        h.persistStates(teiMap, trackedEntityInstanceStore)
        h.persistStates(enrollmentMap, enrollmentStore)
        h.persistStates(eventMap, eventStore)
        h.persistStates(relationshipMap, relationshipStore)
    }
}

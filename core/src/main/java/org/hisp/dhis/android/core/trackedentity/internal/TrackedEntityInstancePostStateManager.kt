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
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.common.CoreColumns
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.enrollment.EnrollmentInternalAccessor
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore
import org.hisp.dhis.android.core.event.internal.EventStore
import org.hisp.dhis.android.core.relationship.internal.RelationshipStore
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceInternalAccessor

@Reusable
internal class TrackedEntityInstancePostStateManager @Inject internal constructor(
    private val versionManager: DHISVersionManager,
    private val trackedEntityInstanceStore: TrackedEntityInstanceStore,
    private val enrollmentStore: EnrollmentStore,
    private val eventStore: EventStore,
    private val relationshipStore: RelationshipStore,
    private val h: StatePersistorHelper
) {

    fun restorePartitionStates(partition: List<TrackedEntityInstance>) {
        setPartitionStates(partition, null)
    }

    @Suppress("NestedBlockDepth")
    fun setPartitionStates(partition: List<TrackedEntityInstance>, forcedState: State?) {
        val teiMap: MutableMap<State, MutableList<String>> = mutableMapOf()
        val enrollmentMap: MutableMap<State, MutableList<String>> = mutableMapOf()
        val eventMap: MutableMap<State, MutableList<String>> = mutableMapOf()
        val relationshipMap: MutableMap<State, MutableList<String>> = mutableMapOf()

        for (instance in partition) {
            h.addState(teiMap, instance, forcedState)
            for (enrollment in TrackedEntityInstanceInternalAccessor.accessEnrollments(instance)) {
                h.addState(enrollmentMap, enrollment, forcedState)
                for (event in EnrollmentInternalAccessor.accessEvents(enrollment)) {
                    h.addState(eventMap, event, forcedState)
                }
            }
            for (r in TrackedEntityInstanceInternalAccessor.accessRelationships(instance)) {
                if (versionManager.is2_29) {
                    val whereClause = WhereClauseBuilder().appendKeyStringValue(CoreColumns.ID, r.id()).build()
                    val dbRelationship = relationshipStore.selectOneWhere(whereClause)
                    dbRelationship?.let { h.addState(relationshipMap, it, forcedState) }
                } else {
                    h.addState(relationshipMap, r, forcedState)
                }
            }
        }

        h.persistStates(teiMap, trackedEntityInstanceStore)
        h.persistStates(enrollmentMap, enrollmentStore)
        h.persistStates(eventMap, eventStore)
        h.persistStates(relationshipMap, relationshipStore)
    }
}

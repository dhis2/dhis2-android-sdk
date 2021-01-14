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
package org.hisp.dhis.android.core.trackedentity.internal

import dagger.Reusable
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableDeletableDataObjectStore
import org.hisp.dhis.android.core.common.CoreColumns
import org.hisp.dhis.android.core.common.DataObject
import org.hisp.dhis.android.core.common.ObjectWithUidInterface
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.enrollment.EnrollmentInternalAccessor
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore
import org.hisp.dhis.android.core.event.internal.EventStore
import org.hisp.dhis.android.core.relationship.internal.RelationshipStore
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceInternalAccessor
import java.util.ArrayList
import javax.inject.Inject

@Reusable
internal class TrackedEntityInstancePostStateManager @Inject internal constructor(
    private val versionManager: DHISVersionManager,
    private val trackedEntityInstanceStore: TrackedEntityInstanceStore,
    private val enrollmentStore: EnrollmentStore,
    private val eventStore: EventStore,
    private val relationshipStore: RelationshipStore
) {

    fun restorePartitionStates(partition: List<TrackedEntityInstance>) {
        setPartitionStates(partition, null)
    }

    fun setPartitionStates(partition: List<TrackedEntityInstance>, forcedState: State?) {
        val teiMap: MutableMap<State, MutableList<String>> = mutableMapOf()
        val enrollmentMap: MutableMap<State, MutableList<String>> = mutableMapOf()
        val eventMap: MutableMap<State, MutableList<String>> = mutableMapOf()
        val relationshipMap: MutableMap<State, MutableList<String>> = mutableMapOf()
        for (instance in partition) {
            addState(teiMap, instance, forcedState)
            for (enrollment in TrackedEntityInstanceInternalAccessor.accessEnrollments(instance)) {
                addState(enrollmentMap, enrollment, forcedState)
                for (event in EnrollmentInternalAccessor.accessEvents(enrollment)) {
                    addState(eventMap, event, forcedState)
                }
            }
            for (r in TrackedEntityInstanceInternalAccessor.accessRelationships(instance)) {
                if (versionManager.is2_29) {
                    val whereClause = WhereClauseBuilder().appendKeyStringValue(CoreColumns.ID, r.id()).build()
                    val dbRelationship = relationshipStore.selectOneWhere(whereClause)
                    dbRelationship?.let { addState(relationshipMap, it, forcedState) }
                } else {
                    addState(relationshipMap, r, forcedState)
                }
            }
        }
        persistStates(teiMap, trackedEntityInstanceStore)
        persistStates(enrollmentMap, enrollmentStore)
        persistStates(eventMap, eventStore)
        persistStates(relationshipMap, relationshipStore)
    }

    private fun <O> addState(
        stateMap: MutableMap<State, MutableList<String>>, o: O,
        forcedState: State?
    ) where O : DataObject, O : ObjectWithUidInterface {
        val s = getStateToSet(o, forcedState)
        if (!stateMap.containsKey(s)) {
            stateMap[s] = ArrayList()
        }
        stateMap[s]!!.add(o.uid())
    }

    private fun <O> getStateToSet(o: O, forcedState: State?): State where O : DataObject, O : ObjectWithUidInterface {
        return forcedState
            ?: if (o.state() == State.UPLOADING) State.TO_UPDATE else o.state()
    }

    private fun persistStates(map: Map<State, MutableList<String>>, store: IdentifiableDeletableDataObjectStore<*>) {
        for ((key, value) in map) {
            store.setState(value, key)
        }
    }
}
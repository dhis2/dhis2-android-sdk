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
package org.hisp.dhis.android.core.common.internal

import dagger.Reusable
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableDeletableDataObjectStore
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.common.DeletableDataObject
import org.hisp.dhis.android.core.common.ObjectWithUidInterface
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.EnrollmentTableInfo
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.EventTableInfo
import org.hisp.dhis.android.core.event.internal.EventStore
import org.hisp.dhis.android.core.relationship.Relationship
import org.hisp.dhis.android.core.relationship.RelationshipHelper
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemChildrenAppender
import org.hisp.dhis.android.core.relationship.internal.RelationshipStore
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStore
import org.hisp.dhis.android.core.trackedentity.ownership.ProgramOwner
import org.hisp.dhis.android.core.trackedentity.ownership.ProgramOwnerTableInfo

@Reusable
@Suppress("TooManyFunctions")
internal class TrackerDataManagerImpl @Inject constructor(
    private val trackedEntityStore: TrackedEntityInstanceStore,
    private val enrollmentStore: EnrollmentStore,
    private val eventStore: EventStore,
    private val relationshipStore: RelationshipStore,
    private val relationshipChildrenAppender: RelationshipItemChildrenAppender,
    private val dataStatePropagator: DataStatePropagator,
    private val programOwner: ObjectWithoutUidStore<ProgramOwner>
) : TrackerDataManager {

    override fun deleteTrackedEntity(tei: TrackedEntityInstance?) {
        tei?.let {
            deleteRelationships(tei)
            deleteCascadeItems(tei)
            internalDelete(tei, trackedEntityStore) {
                propagateTrackedEntityUpdate(tei, HandleAction.Delete)
            }
        }
    }

    override fun deleteEnrollment(enrollment: Enrollment?) {
        enrollment?.let {
            deleteRelationships(enrollment)
            deleteCascadeItems(enrollment)
            internalDelete(enrollment, enrollmentStore) {
                propagateEnrollmentUpdate(enrollment, HandleAction.Delete)
            }
        }
    }

    override fun deleteEvent(event: Event?) {
        event?.let {
            deleteRelationships(event)
            internalDelete(event, eventStore) {
                propagateEventUpdate(event, HandleAction.Delete)
            }
        }
    }

    override fun deleteRelationship(relationship: Relationship?) {
        relationship?.let {
            internalDelete(relationship, relationshipStore) {
                propagateRelationshipUpdate(relationship, HandleAction.Delete)
            }
        }
    }

    private fun <O> internalDelete(
        obj: O,
        store: IdentifiableDeletableDataObjectStore<O>,
        propagateState: (O) -> Any
    ) where O : ObjectWithUidInterface, O : DeletableDataObject {
        if (obj.syncState() === State.TO_POST) {
            store.delete(obj.uid())
        } else {
            store.setDeleted(obj.uid())
            store.setSyncState(obj.uid(), State.TO_UPDATE)
            propagateState(obj)
        }
    }

    override fun propagateTrackedEntityUpdate(tei: TrackedEntityInstance?, action: HandleAction) {
        dataStatePropagator.propagateTrackedEntityInstanceUpdate(tei)
    }

    override fun propagateEnrollmentUpdate(enrollment: Enrollment?, action: HandleAction) {
        dataStatePropagator.propagateEnrollmentUpdate(enrollment)
        if (action == HandleAction.Insert) {
            createProgramOwnerIfNeeded(enrollment)
        }
    }

    override fun propagateEventUpdate(event: Event?, action: HandleAction) {
        dataStatePropagator.propagateEventUpdate(event)
    }

    override fun propagateRelationshipUpdate(relationship: Relationship, action: HandleAction) {
        val withChildren =
            if (relationship.from() == null || relationship.to() == null) {
                relationshipChildrenAppender.appendChildren(relationship)
            } else {
                relationship
            }
        dataStatePropagator.propagateRelationshipUpdate(withChildren)
    }

    private fun deleteRelationships(tei: TrackedEntityInstance) {
        relationshipStore.getRelationshipsByItem(RelationshipHelper.teiItem(tei.uid()))
            .forEach { deleteRelationship(it) }
    }

    private fun deleteRelationships(enrollment: Enrollment) {
        relationshipStore.getRelationshipsByItem(RelationshipHelper.enrollmentItem(enrollment.uid()))
            .forEach { deleteRelationship(it) }
    }

    private fun deleteRelationships(event: Event) {
        relationshipStore.getRelationshipsByItem(RelationshipHelper.eventItem(event.uid()))
            .forEach { deleteRelationship(it) }
    }

    private fun deleteCascadeItems(tei: TrackedEntityInstance) {
        val whereClause = WhereClauseBuilder()
            .appendKeyStringValue(EnrollmentTableInfo.Columns.TRACKED_ENTITY_INSTANCE, tei.uid())
            .build()

        enrollmentStore.selectWhere(whereClause).forEach {
            deleteEnrollment(it)
        }
    }

    private fun deleteCascadeItems(enrollment: Enrollment) {
        val whereClause = WhereClauseBuilder()
            .appendKeyStringValue(EventTableInfo.Columns.ENROLLMENT, enrollment.uid())
            .build()

        eventStore.selectWhere(whereClause).forEach {
            deleteEvent(it)
        }
    }

    private fun createProgramOwnerIfNeeded(enrollment: Enrollment?) {
        enrollment?.let {
            val program = enrollment.program()
            val instance = enrollment.trackedEntityInstance()
            val orgunit = enrollment.organisationUnit()

            if (program != null && instance != null && orgunit != null) {
                val where = WhereClauseBuilder()
                    .appendKeyStringValue(ProgramOwnerTableInfo.Columns.PROGRAM, program)
                    .appendKeyStringValue(ProgramOwnerTableInfo.Columns.TRACKED_ENTITY_INSTANCE, instance)
                    .build()

                val existingOwnership = programOwner.selectWhere(where)

                if (existingOwnership.isEmpty()) {
                    programOwner.insert(
                        ProgramOwner.builder()
                            .trackedEntityInstance(instance)
                            .program(program)
                            .ownerOrgUnit(orgunit)
                            .syncState(State.SYNCED)
                            .build()
                    )
                }
            }
        }
    }
}

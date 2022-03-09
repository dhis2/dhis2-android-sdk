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

import android.util.Log
import dagger.Reusable
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.cleaners.internal.OrphanCleaner
import org.hisp.dhis.android.core.arch.handlers.internal.*
import org.hisp.dhis.android.core.arch.helpers.GeometryHelper
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.relationship.internal.Relationship229Compatible
import org.hisp.dhis.android.core.relationship.internal.RelationshipDHISVersionManager
import org.hisp.dhis.android.core.relationship.internal.RelationshipHandler
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemRelatives
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceInternalAccessor

@Reusable
internal class TrackedEntityInstanceHandler @Inject constructor(
    private val relationshipVersionManager: RelationshipDHISVersionManager,
    relationshipHandler: RelationshipHandler,
    trackedEntityInstanceStore: TrackedEntityInstanceStore,
    private val trackedEntityAttributeValueStore: TrackedEntityAttributeValueStore,
    private val trackedEntityAttributeValueHandler: HandlerWithTransformer<TrackedEntityAttributeValue>,
    private val enrollmentHandler: IdentifiableDataHandler<Enrollment>,
    private val enrollmentOrphanCleaner: OrphanCleaner<TrackedEntityInstance, Enrollment>,
    private val relationshipOrphanCleaner: OrphanCleaner<TrackedEntityInstance, Relationship229Compatible>
) : IdentifiableDataHandlerImpl<TrackedEntityInstance>(
    trackedEntityInstanceStore,
    relationshipVersionManager,
    relationshipHandler
) {
    override fun beforeObjectHandled(
        o: TrackedEntityInstance,
        params: IdentifiableDataHandlerParams
    ): TrackedEntityInstance {
        return if (GeometryHelper.isValid(o.geometry())) {
            o
        } else {
            Log.i(
                this.javaClass.simpleName,
                "TrackedEntityInstance " + o.uid() + " has invalid geometry value"
            )
            o.toBuilder().geometry(null).build()
        }
    }

    override fun afterObjectHandled(
        o: TrackedEntityInstance,
        action: HandleAction?,
        params: IdentifiableDataHandlerParams,
        relatives: RelationshipItemRelatives?
    ) {
        if (action !== HandleAction.Delete) {
            trackedEntityAttributeValueHandler.handleMany(
                o.trackedEntityAttributeValues()
            ) { value: TrackedEntityAttributeValue ->
                value.toBuilder().trackedEntityInstance(o.uid()).build()
            }

            if (params.hasAllAttributes) {
                deleteOrphanAttributes(o)
            } else {
                // TODO Delete program attributes
            }

            val enrollments = TrackedEntityInstanceInternalAccessor.accessEnrollments(o)
            if (enrollments != null) {
                val thisParams = IdentifiableDataHandlerParams(
                    hasAllAttributes = false,
                    hasAllEnrollments = false,
                    params.overwrite,
                    asRelationship = false
                )
                enrollmentHandler.handleMany(enrollments, thisParams, relatives)
            }
            val relationships = TrackedEntityInstanceInternalAccessor.accessRelationships(o)
            if (relationships != null && relationships.isNotEmpty()) {
                val relationshipsList = relationshipVersionManager.from229Compatible(relationships)
                handleRelationships(relationshipsList, o, relatives)
            }
            if (params.hasAllEnrollments) {
                enrollmentOrphanCleaner.deleteOrphan(
                    o,
                    TrackedEntityInstanceInternalAccessor.accessEnrollments(o)
                )
                relationshipOrphanCleaner.deleteOrphan(
                    o,
                    TrackedEntityInstanceInternalAccessor.accessRelationships(o)
                )
            }
        }
    }

    override fun addRelationshipState(o: TrackedEntityInstance): TrackedEntityInstance {
        return o.toBuilder().aggregatedSyncState(State.RELATIONSHIP).syncState(State.RELATIONSHIP).build()
    }

    override fun addSyncedState(o: TrackedEntityInstance): TrackedEntityInstance {
        return o.toBuilder().aggregatedSyncState(State.SYNCED).syncState(State.SYNCED).build()
    }

    private fun deleteOrphanAttributes(tei: TrackedEntityInstance) {
        val trackedEntityAttributeValues = tei.trackedEntityAttributeValues()

        if (trackedEntityAttributeValues.isNullOrEmpty()) {
            return
        }

        val attributeUids = trackedEntityAttributeValues.mapNotNull { it.trackedEntityAttribute() }

        trackedEntityAttributeValueStore.deleteByInstanceAndNotInAttributes(tei.uid(), attributeUids)
    }
}

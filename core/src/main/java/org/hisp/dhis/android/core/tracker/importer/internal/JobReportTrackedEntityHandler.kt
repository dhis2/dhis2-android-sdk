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
package org.hisp.dhis.android.core.tracker.importer.internal

import dagger.Reusable
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.imports.internal.TrackerImportConflictStore
import org.hisp.dhis.android.core.relationship.RelationshipHelper
import org.hisp.dhis.android.core.relationship.internal.RelationshipStore
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceTableInfo
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeValueStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStore

@Reusable
internal class JobReportTrackedEntityHandler @Inject internal constructor(
    private val trackedEntityAttributeValueStore: TrackedEntityAttributeValueStore,
    private val conflictStore: TrackerImportConflictStore,
    private val trackedEntityStore: TrackedEntityInstanceStore,
    private val conflictHelper: TrackerConflictHelper,
    relationshipStore: RelationshipStore
) : JobReportTypeHandler(relationshipStore) {

    override fun handleObject(uid: String, state: State): HandleAction {
        conflictStore.deleteTrackedEntityConflicts(uid)
        val handleAction = trackedEntityStore.setSyncStateOrDelete(uid, state)

        if (state == State.SYNCED && (handleAction == HandleAction.Update || handleAction == HandleAction.Insert)) {
            trackedEntityAttributeValueStore.removeDeletedAttributeValuesByInstance(uid)
        }
        return handleAction
    }

    override fun storeConflict(errorReport: JobValidationError) {
        trackedEntityStore.selectByUid(errorReport.uid)?.let { trackedEntity ->
            if (errorReport.errorCode == ImporterError.E1063.name && trackedEntity.deleted() == true) {
                trackedEntityStore.delete(trackedEntity.uid())
            } else {
                conflictStore.insert(
                    conflictHelper.getConflictBuilder(errorReport)
                        .tableReference(TrackedEntityInstanceTableInfo.TABLE_INFO.name())
                        .trackedEntityInstance(errorReport.uid).build()
                )
            }
        }
    }

    override fun getRelatedRelationships(uid: String): List<String> {
        return relationshipStore.getRelationshipsByItem(RelationshipHelper.teiItem(uid)).mapNotNull { it.uid() }
    }
}

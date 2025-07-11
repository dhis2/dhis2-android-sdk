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
package org.hisp.dhis.android.core.relationship

import org.hisp.dhis.android.core.arch.handlers.internal.Transformer

internal object NewTrackerImporterRelationshipTransformer :
    Transformer<Relationship, NewTrackerImporterRelationship> {
    override fun transform(o: Relationship): NewTrackerImporterRelationship {
        val isBidirectional = o.from() != null && o.to() != null
        return NewTrackerImporterRelationship(
            uid = o.uid(),
            relationshipType = o.relationshipType(),
            relationshipName = o.name(),
            createdAt = o.created(),
            updatedAt = o.lastUpdated(),
            from = transformRelationshipItem(o.from()),
            to = transformRelationshipItem(o.to()),
            deleted = o.deleted(),
            syncState = o.syncState(),
            bidirectional = isBidirectional,
        )
    }

    private fun transformRelationshipItem(item: RelationshipItem?): NewTrackerImporterRelationshipItem? {
        return item?.let {
            val relationshipItem = NewTrackerImporterRelationshipItem(
                relationship = item.relationship()?.uid(),
                relationshipItemType = item.relationshipItemType(),
                trackedEntity = if (item.hasTrackedEntityInstance()) {
                    NewTrackerImporterRelationshipItemTrackedEntity(trackedEntity = item.elementUid())
                } else {
                    null
                },
                enrollment = if (item.hasEnrollment()) {
                    NewTrackerImporterRelationshipItemEnrollment(enrollment = item.elementUid())
                } else {
                    null
                },
                event = if (item.hasEvent()) {
                    NewTrackerImporterRelationshipItemEvent(event = item.elementUid())
                } else {
                    null
                },
            )
            relationshipItem.takeIf {
                it.trackedEntity != null || it.enrollment != null || it.event != null
            }
        }
    }
}

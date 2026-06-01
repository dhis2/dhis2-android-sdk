/*
 *  Copyright (c) 2004-2026, University of Oslo
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

import org.hisp.dhis.android.annotations.ModelBuilder
import org.hisp.dhis.android.core.common.CoreObject
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.persistence.relationship.RelationshipItemTableInfo.Columns

@Suppress("TooManyFunctions")
@ModelBuilder
data class RelationshipItem(
    val relationship: ObjectWithUid?,
    val relationshipItemType: RelationshipConstraintType?,
    val trackedEntityInstance: RelationshipItemTrackedEntityInstance?,
    val enrollment: RelationshipItemEnrollment?,
    val event: RelationshipItemEvent?,
) : CoreObject {

    fun relationship(): ObjectWithUid? = relationship
    fun relationshipItemType(): RelationshipConstraintType? = relationshipItemType
    fun trackedEntityInstance(): RelationshipItemTrackedEntityInstance? = trackedEntityInstance
    fun enrollment(): RelationshipItemEnrollment? = enrollment
    fun event(): RelationshipItemEvent? = event

    fun hasTrackedEntityInstance(): Boolean = trackedEntityInstance != null
    fun hasEnrollment(): Boolean = enrollment != null
    fun hasEvent(): Boolean = event != null

    fun elementUid(): String? = when {
        hasTrackedEntityInstance() -> trackedEntityInstance!!.trackedEntityInstance
        hasEnrollment() -> enrollment!!.enrollment
        hasEvent() -> event!!.event
        else -> null
    }

    fun elementType(): String = when {
        hasTrackedEntityInstance() -> Columns.TRACKED_ENTITY_INSTANCE
        hasEnrollment() -> Columns.ENROLLMENT
        else -> Columns.EVENT
    }

    fun toBuilder(): Builder = RelationshipItemBuilder.from(this)

    class Builder : RelationshipItemBuilder() {
        override fun build(): RelationshipItem {
            val item = super.build()
            val teiCount = if (item.trackedEntityInstance == null) 0 else 1
            val enrollmentCount = if (item.enrollment == null) 0 else 1
            val eventCount = if (item.event == null) 0 else 1
            require(teiCount + enrollmentCount + eventCount == 1) {
                "Item must have either a TEI, enrollment or event"
            }
            return item
        }
    }

    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()
    }
}

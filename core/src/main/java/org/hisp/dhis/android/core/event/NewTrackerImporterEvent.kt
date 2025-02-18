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
package org.hisp.dhis.android.core.event

import org.hisp.dhis.android.core.common.Geometry
import org.hisp.dhis.android.core.common.ObjectWithDeleteInterface
import org.hisp.dhis.android.core.common.ObjectWithSyncStateInterface
import org.hisp.dhis.android.core.common.ObjectWithUidInterface
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.note.NewTrackerImporterNote
import org.hisp.dhis.android.core.relationship.NewTrackerImporterRelationship
import org.hisp.dhis.android.core.trackedentity.NewTrackerImporterTrackedEntityDataValue
import org.hisp.dhis.android.core.trackedentity.NewTrackerImporterUserInfo
import java.util.Date

internal data class NewTrackerImporterEvent(
    val uid: String,
    val deleted: Boolean?,
    val syncState: State?,
    val enrollment: String?,
    val createdAt: Date?,
    val updatedAt: Date?,
    val createdAtClient: Date?,
    val updatedAtClient: Date?,
    val program: String?,
    val programStage: String?,
    val organisationUnit: String?,
    val occurredAt: Date?,
    val status: EventStatus?,
    val geometry: Geometry?,
    val completedAt: Date?,
    val completedBy: String?,
    val scheduledAt: Date?,
    val attributeOptionCombo: String?,
    val assignedUser: NewTrackerImporterUserInfo?,
    val notes: List<NewTrackerImporterNote>? = emptyList(),
    val trackedEntityDataValues: List<NewTrackerImporterTrackedEntityDataValue>? = emptyList(),
    val relationships: List<NewTrackerImporterRelationship>? = emptyList(),
    val aggregatedSyncState: State?,
    val trackedEntity: String? = null,
) : ObjectWithUidInterface, ObjectWithSyncStateInterface, ObjectWithDeleteInterface {
    override fun uid(): String = uid
    override fun syncState(): State? = syncState
    override fun deleted(): Boolean? = deleted
}

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

package org.hisp.dhis.android.core.trackedentity.search

import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance

object TrackedEntitySearchItemHelper {
    fun from(i: TrackedEntityInstance): TrackedEntitySearchItem {
        return TrackedEntitySearchItem(
            uid = i.uid(),
            created = i.created(),
            lastUpdated = i.lastUpdated(),
            createdAtClient = i.createdAtClient(),
            lastUpdatedAtClient = i.lastUpdatedAtClient(),
            organisationUnit = i.organisationUnit(),
            trackedEntityType = i.trackedEntityType(),
            geometry = i.geometry(),
            trackedEntityAttributeValues = i.trackedEntityAttributeValues(),
            syncState = i.syncState(),
            aggregatedSyncState = i.aggregatedSyncState(),
            deleted = i.deleted() ?: false,
        )
    }

    fun toTrackedEntityInstance(i: TrackedEntitySearchItem): TrackedEntityInstance {
        return TrackedEntityInstance.builder()
            .uid(i.uid)
            .created(i.created)
            .lastUpdated(i.lastUpdated)
            .createdAtClient(i.createdAtClient)
            .lastUpdatedAtClient(i.lastUpdatedAtClient)
            .organisationUnit(i.organisationUnit)
            .trackedEntityType(i.trackedEntityType)
            .geometry(i.geometry)
            .trackedEntityAttributeValues(i.trackedEntityAttributeValues)
            .syncState(i.syncState)
            .aggregatedSyncState(i.aggregatedSyncState)
            .build()
    }
}

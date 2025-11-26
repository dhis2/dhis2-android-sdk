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
package org.hisp.dhis.android.core.trackedentity

import kotlinx.coroutines.runBlocking
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.arch.helpers.GeometryHelper
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.`object`.internal.ObjectRepositoryFactory
import org.hisp.dhis.android.core.arch.repositories.`object`.internal.ReadWriteWithUidDataObjectRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.common.Geometry
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.common.Unit
import org.hisp.dhis.android.core.common.internal.TrackerDataManager
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStore
import java.util.Date

class TrackedEntityInstanceObjectRepository internal constructor(
    store: TrackedEntityInstanceStore,
    uid: String?,
    childrenAppenders: ChildrenAppenderGetter<TrackedEntityInstance>,
    scope: RepositoryScope,
    private val trackerDataManager: TrackerDataManager,
) : ReadWriteWithUidDataObjectRepositoryImpl<TrackedEntityInstance, TrackedEntityInstanceObjectRepository>(
    store,
    childrenAppenders,
    scope,
    ObjectRepositoryFactory { s: RepositoryScope ->
        TrackedEntityInstanceObjectRepository(
            store,
            uid,
            childrenAppenders,
            s,
            trackerDataManager,
        )
    },
) {

    @Throws(D2Error::class)
    fun setOrganisationUnitUid(organisationUnitUid: String?): Unit {
        return runBlocking { setOrganisationUnitUidInternal(organisationUnitUid) }
    }

    @Throws(D2Error::class)
    internal suspend fun setOrganisationUnitUidInternal(organisationUnitUid: String?): Unit {
        return updateIfChangedInternal(
            organisationUnitUid,
            { it.organisationUnit() },
        ) { tei: TrackedEntityInstance, value ->
            updateBuilder(tei).organisationUnit(value).build()
        }
    }

    @Throws(D2Error::class)
    fun setGeometry(geometry: Geometry?): Unit {
        return runBlocking { setGeometryInternal(geometry) }
    }

    @Throws(D2Error::class)
    internal suspend fun setGeometryInternal(geometry: Geometry?): Unit {
        GeometryHelper.validateGeometry(geometry)
        return updateIfChangedInternal(geometry, { it.geometry() }) { tei: TrackedEntityInstance, value ->
            updateBuilder(tei).geometry(value).build()
        }
    }

    @Throws(D2Error::class)
    private fun updateBuilder(trackedEntityInstance: TrackedEntityInstance): TrackedEntityInstance.Builder {
        var state = trackedEntityInstance.aggregatedSyncState()
        if (state === State.RELATIONSHIP) {
            throw D2Error
                .builder()
                .errorComponent(D2ErrorComponent.SDK)
                .errorCode(D2ErrorCode.RELATIONSHIPS_CANT_BE_UPDATED)
                .errorDescription("Relationships can't be updated")
                .build()
        }
        val updateDate = Date()
        state = if (state === State.TO_POST) state else State.TO_UPDATE
        return trackedEntityInstance.toBuilder()
            .syncState(state)
            .aggregatedSyncState(state)
            .lastUpdated(updateDate)
            .lastUpdatedAtClient(updateDate)
    }

    override suspend fun propagateState(m: TrackedEntityInstance, action: HandleAction) {
        trackerDataManager.propagateTrackedEntityUpdate(m, action)
    }

    override suspend fun deleteObject(m: TrackedEntityInstance) {
        trackerDataManager.deleteTrackedEntity(m)
    }
}

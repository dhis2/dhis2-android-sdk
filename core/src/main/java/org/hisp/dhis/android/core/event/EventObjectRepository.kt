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

import kotlinx.coroutines.runBlocking
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.arch.helpers.CoordinateHelper
import org.hisp.dhis.android.core.arch.helpers.GeometryHelper
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.`object`.internal.ObjectRepositoryFactory
import org.hisp.dhis.android.core.arch.repositories.`object`.internal.ReadWriteWithUidDataObjectRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.common.Geometry
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.common.Unit
import org.hisp.dhis.android.core.common.internal.TrackerDataManager
import org.hisp.dhis.android.core.event.internal.EventStore
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.user.internal.UserStore
import java.util.Date

@Suppress("TooManyFunctions")
class EventObjectRepository internal constructor(
    store: EventStore,
    private val userStore: UserStore,
    uid: String?,
    childrenAppenders: ChildrenAppenderGetter<Event>,
    scope: RepositoryScope,
    private val trackerDataManager: TrackerDataManager,
) : ReadWriteWithUidDataObjectRepositoryImpl<Event, EventObjectRepository>(
    store,
    childrenAppenders,
    scope,
    ObjectRepositoryFactory { s: RepositoryScope ->
        EventObjectRepository(
            store,
            userStore,
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
        return updateIfChangedInternal(organisationUnitUid, { it.organisationUnit() }) { event: Event, value ->
            updateBuilder(event).organisationUnit(value).build()
        }
    }

    @Throws(D2Error::class)
    fun setEventDate(eventDate: Date?): Unit {
        return runBlocking { setEventDateInternal(eventDate) }
    }

    @Throws(D2Error::class)
    internal suspend fun setEventDateInternal(eventDate: Date?): Unit {
        return updateIfChangedInternal(eventDate, { it.eventDate() }) { event: Event, value ->
            updateBuilder(event).eventDate(value).build()
        }
    }

    @Throws(D2Error::class)
    fun setStatus(eventStatus: EventStatus): Unit {
        return runBlocking { setStatusInternal(eventStatus) }
    }

    @Throws(D2Error::class)
    internal suspend fun setStatusInternal(eventStatus: EventStatus): Unit {
        return updateIfChangedInternal(eventStatus, { it.status() }) { event: Event, value ->
            val completedDate = if (value == EventStatus.COMPLETED) Date() else null
            val completedBy = if (value == EventStatus.COMPLETED) userStore.selectFirst()!!.username() else null
            updateBuilder(event).status(value).completedDate(completedDate).completedBy(completedBy).build()
        }
    }

    @Throws(D2Error::class)
    fun setCompletedDate(completedDate: Date?): Unit {
        return runBlocking { setCompletedDateInternal(completedDate) }
    }

    @Throws(D2Error::class)
    internal suspend fun setCompletedDateInternal(completedDate: Date?): Unit {
        return updateIfChangedInternal(completedDate, { it.completedDate() }) { event: Event, value ->
            updateBuilder(event).completedDate(value).build()
        }
    }

    @Throws(D2Error::class)
    fun setCompletedBy(completedBy: String?): Unit {
        return runBlocking { setCompletedByInternal(completedBy) }
    }

    @Throws(D2Error::class)
    internal suspend fun setCompletedByInternal(completedBy: String?): Unit {
        return updateIfChangedInternal(completedBy, { it.completedBy() }) { event: Event, value ->
            updateBuilder(event).completedBy(value).build()
        }
    }

    @Throws(D2Error::class)
    fun setDueDate(dueDate: Date?): Unit {
        return runBlocking { setDueDateInternal(dueDate) }
    }

    @Throws(D2Error::class)
    internal suspend fun setDueDateInternal(dueDate: Date?): Unit {
        return updateIfChangedInternal(dueDate, { it.dueDate() }) { event: Event, value ->
            updateBuilder(event).dueDate(value).build()
        }
    }

    @Throws(D2Error::class)
    fun setGeometry(geometry: Geometry?): Unit {
        return runBlocking { setGeometryInternal(geometry) }
    }

    @Throws(D2Error::class)
    internal suspend fun setGeometryInternal(geometry: Geometry?): Unit {
        GeometryHelper.validateGeometry(geometry)
        return updateIfChangedInternal(geometry, { it.geometry() }) { event: Event, value ->
            updateBuilder(event)
                .geometry(value)
                .coordinate(value?.let { CoordinateHelper.getCoordinatesFromGeometry(it) })
                .build()
        }
    }

    @Throws(D2Error::class)
    fun setAttributeOptionComboUid(attributeOptionComboUid: String?): Unit {
        return runBlocking { setAttributeOptionComboUidInternal(attributeOptionComboUid) }
    }

    @Throws(D2Error::class)
    internal suspend fun setAttributeOptionComboUidInternal(attributeOptionComboUid: String?): Unit {
        return updateIfChangedInternal(attributeOptionComboUid, { it.attributeOptionCombo() }) { event: Event, value ->
            updateBuilder(event).attributeOptionCombo(value).build()
        }
    }

    @Throws(D2Error::class)
    fun setAssignedUser(assignedUser: String?): Unit {
        return runBlocking { setAssignedUserInternal(assignedUser) }
    }

    @Throws(D2Error::class)
    internal suspend fun setAssignedUserInternal(assignedUser: String?): Unit {
        return updateIfChangedInternal(assignedUser, { it.assignedUser() }) { event: Event, value ->
            updateBuilder(event).assignedUser(value).build()
        }
    }

    private fun updateBuilder(event: Event): Event.Builder {
        val updateDate = Date()
        var state = event.syncState()
        state = if (state === State.TO_POST) state else State.TO_UPDATE
        return event.toBuilder()
            .syncState(state)
            .aggregatedSyncState(state)
            .lastUpdated(updateDate)
            .lastUpdatedAtClient(updateDate)
    }

    override suspend fun propagateState(m: Event, action: HandleAction) {
        trackerDataManager.propagateEventUpdate(m, action)
    }

    override suspend fun deleteObject(m: Event) {
        trackerDataManager.deleteEvent(m)
    }
}

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

import dagger.Reusable
import io.reactivex.Observable
import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.collection.ReadWriteWithUploadWithUidCollectionRepository
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadWriteWithUidCollectionRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.filters.internal.BooleanFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.DateFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.ValueSubQueryFilterConnector
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope.OrderByDirection
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeHelper.withUidFilterItem
import org.hisp.dhis.android.core.common.FeatureType
import org.hisp.dhis.android.core.common.IdentifiableColumns
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.common.State.Companion.uploadableStatesIncludingError
import org.hisp.dhis.android.core.common.internal.TrackerDataManager
import org.hisp.dhis.android.core.enrollment.EnrollmentTableInfo
import org.hisp.dhis.android.core.event.internal.EventFields
import org.hisp.dhis.android.core.event.internal.EventPostParentCall
import org.hisp.dhis.android.core.event.internal.EventProjectionTransformer
import org.hisp.dhis.android.core.event.internal.EventStore
import org.hisp.dhis.android.core.note.internal.NoteForEventChildrenAppender
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitTableInfo
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueTableInfo
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityDataValueChildrenAppender
import org.hisp.dhis.android.core.tracker.importer.internal.JobQueryCall
import org.hisp.dhis.android.core.user.internal.UserStore
import javax.inject.Inject

@Reusable
@Suppress("TooManyFunctions")
class EventCollectionRepository @Inject internal constructor(
    private val eventStore: EventStore,
    private val userStore: UserStore,
    databaseAdapter: DatabaseAdapter,
    scope: RepositoryScope,
    private val postCall: EventPostParentCall,
    transformer: EventProjectionTransformer,
    private val trackerDataManager: TrackerDataManager,
    private val jobQueryCall: JobQueryCall,
) : ReadWriteWithUidCollectionRepositoryImpl<Event, EventCreateProjection, EventCollectionRepository>(
    eventStore,
    databaseAdapter,
    childrenAppenders,
    scope,
    transformer,
    FilterConnectorFactory(scope) { s: RepositoryScope ->
        EventCollectionRepository(
            eventStore,
            userStore,
            databaseAdapter,
            s,
            postCall,
            transformer,
            trackerDataManager,
            jobQueryCall,
        )
    },
),
    ReadWriteWithUploadWithUidCollectionRepository<Event, EventCreateProjection> {

    @Suppress("SpreadOperator")
    override fun upload(): Observable<D2Progress> {
        return Observable.concat(
            jobQueryCall.queryPendingJobs(),
            Observable.fromCallable {
                byAggregatedSyncState().`in`(*uploadableStatesIncludingError())
                    .byEnrollmentUid().isNull
                    .blockingGetWithoutChildren()
            }
                .flatMap { events: List<Event> -> postCall.uploadEvents(events) },
        )
    }

    override fun blockingUpload() {
        upload().blockingSubscribe()
    }

    override fun propagateState(m: Event, action: HandleAction?) {
        trackerDataManager.propagateEventUpdate(m, action!!)
    }

    override fun uid(uid: String?): EventObjectRepository {
        val updatedScope = withUidFilterItem(scope, uid)
        return EventObjectRepository(
            eventStore,
            userStore,
            uid,
            databaseAdapter,
            childrenAppenders,
            updatedScope,
            trackerDataManager,
        )
    }

    fun byUid(): StringFilterConnector<EventCollectionRepository> {
        return cf.string(EventTableInfo.Columns.UID)
    }

    fun byEnrollmentUid(): StringFilterConnector<EventCollectionRepository> {
        return cf.string(EventTableInfo.Columns.ENROLLMENT)
    }

    fun byCreated(): DateFilterConnector<EventCollectionRepository> {
        return cf.date(EventTableInfo.Columns.CREATED)
    }

    fun byLastUpdated(): DateFilterConnector<EventCollectionRepository> {
        return cf.date(EventTableInfo.Columns.LAST_UPDATED)
    }

    fun byCreatedAtClient(): StringFilterConnector<EventCollectionRepository> {
        return cf.string(EventTableInfo.Columns.CREATED_AT_CLIENT)
    }

    fun byLastUpdatedAtClient(): StringFilterConnector<EventCollectionRepository> {
        return cf.string(EventTableInfo.Columns.LAST_UPDATED_AT_CLIENT)
    }

    fun byStatus(): EnumFilterConnector<EventCollectionRepository, EventStatus> {
        return cf.enumC(EventTableInfo.Columns.STATUS)
    }

    fun byGeometryType(): EnumFilterConnector<EventCollectionRepository, FeatureType> {
        return cf.enumC(EventTableInfo.Columns.GEOMETRY_TYPE)
    }

    fun byGeometryCoordinates(): StringFilterConnector<EventCollectionRepository> {
        return cf.string(EventTableInfo.Columns.GEOMETRY_COORDINATES)
    }

    fun byProgramUid(): StringFilterConnector<EventCollectionRepository> {
        return cf.string(EventTableInfo.Columns.PROGRAM)
    }

    fun byProgramStageUid(): StringFilterConnector<EventCollectionRepository> {
        return cf.string(EventTableInfo.Columns.PROGRAM_STAGE)
    }

    fun byOrganisationUnitUid(): StringFilterConnector<EventCollectionRepository> {
        return cf.string(EventTableInfo.Columns.ORGANISATION_UNIT)
    }

    fun byEventDate(): DateFilterConnector<EventCollectionRepository> {
        return cf.simpleDate(EventTableInfo.Columns.EVENT_DATE)
    }

    fun byCompleteDate(): DateFilterConnector<EventCollectionRepository> {
        return cf.simpleDate(EventTableInfo.Columns.COMPLETE_DATE)
    }

    fun byCompletedBy(): StringFilterConnector<EventCollectionRepository> {
        return cf.string(EventTableInfo.Columns.COMPLETED_BY)
    }

    fun byDueDate(): DateFilterConnector<EventCollectionRepository> {
        return cf.simpleDate(EventTableInfo.Columns.DUE_DATE)
    }

    /**
     * @return
     */
    @Deprecated("Use {@link #bySyncState()} instead.", ReplaceWith("bySyncState()"))
    fun byState(): EnumFilterConnector<EventCollectionRepository, State> {
        return bySyncState()
    }

    fun bySyncState(): EnumFilterConnector<EventCollectionRepository, State> {
        return cf.enumC(EventTableInfo.Columns.SYNC_STATE)
    }

    fun byAggregatedSyncState(): EnumFilterConnector<EventCollectionRepository, State> {
        return cf.enumC(EventTableInfo.Columns.AGGREGATED_SYNC_STATE)
    }

    fun byAttributeOptionComboUid(): StringFilterConnector<EventCollectionRepository> {
        return cf.string(EventTableInfo.Columns.ATTRIBUTE_OPTION_COMBO)
    }

    fun byDeleted(): BooleanFilterConnector<EventCollectionRepository> {
        return cf.bool(EventTableInfo.Columns.DELETED)
    }

    fun byTrackedEntityInstanceUids(uids: List<String>): EventCollectionRepository {
        return cf.subQuery(EventTableInfo.Columns.ENROLLMENT).inLinkTable(
            EnrollmentTableInfo.TABLE_INFO.name(),
            IdentifiableColumns.UID,
            EnrollmentTableInfo.Columns.TRACKED_ENTITY_INSTANCE,
            uids,
        )
    }

    fun byDataValue(dataElementId: String): ValueSubQueryFilterConnector<EventCollectionRepository> {
        return cf.valueSubQuery(
            IdentifiableColumns.UID,
            TrackedEntityDataValueTableInfo.TABLE_INFO.name(),
            TrackedEntityDataValueTableInfo.Columns.EVENT,
            TrackedEntityDataValueTableInfo.Columns.VALUE,
            TrackedEntityDataValueTableInfo.Columns.DATA_ELEMENT,
            dataElementId,
        )
    }

    fun byFollowUp(followUp: Boolean): EventCollectionRepository {
        return cf.subQuery(EventTableInfo.Columns.ENROLLMENT).inLinkTable(
            EnrollmentTableInfo.TABLE_INFO.name(),
            IdentifiableColumns.UID,
            EnrollmentTableInfo.Columns.FOLLOW_UP,
            listOf(if (followUp) "1" else "0"),
        )
    }

    fun byAssignedUser(): StringFilterConnector<EventCollectionRepository> {
        return cf.string(EventTableInfo.Columns.ASSIGNED_USER)
    }

    fun withTrackedEntityDataValues(): EventCollectionRepository {
        return cf.withChild(EventFields.TRACKED_ENTITY_DATA_VALUES)
    }

    fun withNotes(): EventCollectionRepository {
        return cf.withChild(EventFields.NOTES)
    }

    fun orderByEventDate(direction: OrderByDirection?): EventCollectionRepository {
        return cf.withOrderBy(EventTableInfo.Columns.EVENT_DATE, direction)
    }

    fun orderByDueDate(direction: OrderByDirection?): EventCollectionRepository {
        return cf.withOrderBy(EventTableInfo.Columns.DUE_DATE, direction)
    }

    fun orderByCompleteDate(direction: OrderByDirection?): EventCollectionRepository {
        return cf.withOrderBy(EventTableInfo.Columns.COMPLETE_DATE, direction)
    }

    fun orderByCreated(direction: OrderByDirection?): EventCollectionRepository {
        return cf.withOrderBy(EventTableInfo.Columns.CREATED, direction)
    }

    fun orderByLastUpdated(direction: OrderByDirection?): EventCollectionRepository {
        return cf.withOrderBy(EventTableInfo.Columns.LAST_UPDATED, direction)
    }

    fun orderByCreatedAtClient(direction: OrderByDirection?): EventCollectionRepository {
        return cf.withOrderBy(EventTableInfo.Columns.CREATED_AT_CLIENT, direction)
    }

    fun orderByLastUpdatedAtClient(direction: OrderByDirection?): EventCollectionRepository {
        return cf.withOrderBy(EventTableInfo.Columns.LAST_UPDATED_AT_CLIENT, direction)
    }

    fun orderByOrganisationUnitName(direction: OrderByDirection?): EventCollectionRepository {
        return cf.withExternalOrderBy(
            OrganisationUnitTableInfo.TABLE_INFO.name(),
            IdentifiableColumns.NAME,
            IdentifiableColumns.UID,
            EventTableInfo.Columns.ORGANISATION_UNIT,
            direction!!,
        )
    }

    fun orderByTimeline(direction: OrderByDirection?): EventCollectionRepository {
        return cf.withConditionalOrderBy(
            EventTableInfo.Columns.STATUS,
            String.format("IN ('%s', '%s', '%s')", EventStatus.ACTIVE, EventStatus.COMPLETED, EventStatus.VISITED),
            EventTableInfo.Columns.EVENT_DATE,
            EventTableInfo.Columns.DUE_DATE,
            direction,
        )
    }

    fun orderByDataElement(
        direction: OrderByDirection,
        dataElement: String?,
    ): EventCollectionRepository {
        return cf.withExternalOrderBy(
            TrackedEntityDataValueTableInfo.TABLE_INFO.name(),
            TrackedEntityDataValueTableInfo.Columns.VALUE,
            TrackedEntityDataValueTableInfo.Columns.EVENT,
            EventTableInfo.Columns.UID,
            direction,
            TrackedEntityDataValueTableInfo.Columns.DATA_ELEMENT + " = '" + dataElement + "'",
        )
    }

    fun countTrackedEntityInstances(): Int {
        return eventStore.countTeisWhereEvents(whereClause)
    }

    internal companion object {
        val childrenAppenders: ChildrenAppenderGetter<Event> = mapOf(
            EventFields.TRACKED_ENTITY_DATA_VALUES to TrackedEntityDataValueChildrenAppender::create,
            EventFields.NOTES to NoteForEventChildrenAppender::create,
        )
    }
}

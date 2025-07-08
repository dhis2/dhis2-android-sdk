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
package org.hisp.dhis.android.core.enrollment

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadWriteWithUidCollectionRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.filters.internal.BooleanFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.DateFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope.OrderByDirection
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeHelper.withUidFilterItem
import org.hisp.dhis.android.core.common.DataColumns
import org.hisp.dhis.android.core.common.FeatureType
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.common.internal.TrackerDataManager
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentProjectionTransformer
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore
import org.hisp.dhis.android.core.note.internal.NoteForEnrollmentChildrenAppender
import org.koin.core.annotation.Singleton

@Singleton
@Suppress("TooManyFunctions")
class EnrollmentCollectionRepository internal constructor(
    private val enrollmentStore: EnrollmentStore,
    databaseAdapter: DatabaseAdapter,
    scope: RepositoryScope,
    transformer: EnrollmentProjectionTransformer,
    private val trackerDataManager: TrackerDataManager,
) : ReadWriteWithUidCollectionRepositoryImpl<Enrollment, EnrollmentCreateProjection, EnrollmentCollectionRepository>(
    enrollmentStore,
    databaseAdapter,
    childrenAppenders,
    scope,
    transformer,
    FilterConnectorFactory(scope) { s: RepositoryScope ->
        EnrollmentCollectionRepository(
            enrollmentStore,
            databaseAdapter,
            s,
            transformer,
            trackerDataManager,
        )
    },
) {
    override fun propagateState(m: Enrollment, action: HandleAction?) {
        trackerDataManager.propagateEnrollmentUpdate(m, action!!)
    }

    override fun uid(uid: String?): EnrollmentObjectRepository {
        val updatedScope = withUidFilterItem(scope, uid)
        return EnrollmentObjectRepository(
            enrollmentStore,
            uid,
            databaseAdapter,
            childrenAppenders,
            updatedScope,
            trackerDataManager,
        )
    }

    fun byUid(): StringFilterConnector<EnrollmentCollectionRepository> {
        return cf.string(EnrollmentTableInfo.Columns.UID)
    }

    fun byCreated(): DateFilterConnector<EnrollmentCollectionRepository> {
        return cf.date(EnrollmentTableInfo.Columns.CREATED)
    }

    fun byLastUpdated(): DateFilterConnector<EnrollmentCollectionRepository> {
        return cf.date(EnrollmentTableInfo.Columns.LAST_UPDATED)
    }

    fun byCreatedAtClient(): StringFilterConnector<EnrollmentCollectionRepository> {
        return cf.string(EnrollmentTableInfo.Columns.CREATED_AT_CLIENT)
    }

    fun byLastUpdatedAtClient(): StringFilterConnector<EnrollmentCollectionRepository> {
        return cf.string(EnrollmentTableInfo.Columns.LAST_UPDATED_AT_CLIENT)
    }

    fun byOrganisationUnit(): StringFilterConnector<EnrollmentCollectionRepository> {
        return cf.string(EnrollmentTableInfo.Columns.ORGANISATION_UNIT)
    }

    fun byProgram(): StringFilterConnector<EnrollmentCollectionRepository> {
        return cf.string(EnrollmentTableInfo.Columns.PROGRAM)
    }

    fun byEnrollmentDate(): DateFilterConnector<EnrollmentCollectionRepository> {
        return cf.simpleDate(EnrollmentTableInfo.Columns.ENROLLMENT_DATE)
    }

    fun byIncidentDate(): DateFilterConnector<EnrollmentCollectionRepository> {
        return cf.simpleDate(EnrollmentTableInfo.Columns.INCIDENT_DATE)
    }

    fun byFollowUp(): BooleanFilterConnector<EnrollmentCollectionRepository> {
        return cf.bool(EnrollmentTableInfo.Columns.FOLLOW_UP)
    }

    fun byStatus(): EnumFilterConnector<EnrollmentCollectionRepository, EnrollmentStatus> {
        return cf.enumC(EnrollmentTableInfo.Columns.STATUS)
    }

    fun byTrackedEntityInstance(): StringFilterConnector<EnrollmentCollectionRepository> {
        return cf.string(EnrollmentTableInfo.Columns.TRACKED_ENTITY_INSTANCE)
    }

    fun byGeometryType(): EnumFilterConnector<EnrollmentCollectionRepository, FeatureType> {
        return cf.enumC(EnrollmentTableInfo.Columns.GEOMETRY_TYPE)
    }

    fun byGeometryCoordinates(): StringFilterConnector<EnrollmentCollectionRepository> {
        return cf.string(EnrollmentTableInfo.Columns.GEOMETRY_COORDINATES)
    }

    fun byAggregatedSyncState(): EnumFilterConnector<EnrollmentCollectionRepository, State> {
        return cf.enumC(DataColumns.AGGREGATED_SYNC_STATE)
    }

    /**
     * Use [.byAggregatedSyncState] instead.
     */
    @Deprecated("", ReplaceWith("byAggregatedSyncState()"))
    fun byState(): EnumFilterConnector<EnrollmentCollectionRepository, State> {
        return byAggregatedSyncState()
    }

    fun bySyncState(): EnumFilterConnector<EnrollmentCollectionRepository, State> {
        return cf.enumC(DataColumns.SYNC_STATE)
    }

    fun byDeleted(): BooleanFilterConnector<EnrollmentCollectionRepository> {
        return cf.bool(EnrollmentTableInfo.Columns.DELETED)
    }

    fun orderByCreated(direction: OrderByDirection?): EnrollmentCollectionRepository {
        return cf.withOrderBy(EnrollmentTableInfo.Columns.CREATED, direction)
    }

    fun orderByLastUpdated(direction: OrderByDirection?): EnrollmentCollectionRepository {
        return cf.withOrderBy(EnrollmentTableInfo.Columns.LAST_UPDATED, direction)
    }

    fun orderByCreatedAtClient(direction: OrderByDirection?): EnrollmentCollectionRepository {
        return cf.withOrderBy(EnrollmentTableInfo.Columns.CREATED_AT_CLIENT, direction)
    }

    fun orderByLastUpdatedAtClient(direction: OrderByDirection?): EnrollmentCollectionRepository {
        return cf.withOrderBy(EnrollmentTableInfo.Columns.LAST_UPDATED_AT_CLIENT, direction)
    }

    fun orderByEnrollmentDate(direction: OrderByDirection?): EnrollmentCollectionRepository {
        return cf.withOrderBy(EnrollmentTableInfo.Columns.ENROLLMENT_DATE, direction)
    }

    fun orderByIncidentDate(direction: OrderByDirection?): EnrollmentCollectionRepository {
        return cf.withOrderBy(EnrollmentTableInfo.Columns.INCIDENT_DATE, direction)
    }

    fun withNotes(): EnrollmentCollectionRepository {
        return cf.withChild(NOTES)
    }

    internal companion object {
        private const val NOTES = "notes"

        val childrenAppenders: ChildrenAppenderGetter<Enrollment> = mapOf(
            NOTES to NoteForEnrollmentChildrenAppender::create,
        )
    }
}

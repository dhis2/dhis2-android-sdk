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

import io.reactivex.Observable
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.rx2.asObservable
import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.collection.ReadWriteWithUploadWithUidCollectionRepository
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadWriteWithUidCollectionRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.filters.internal.BooleanFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.DateFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope.OrderByDirection
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeHelper.withUidFilterItem
import org.hisp.dhis.android.core.common.FeatureType
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.common.State.Companion.uploadableStatesIncludingError
import org.hisp.dhis.android.core.common.internal.TrackerDataManager
import org.hisp.dhis.android.core.trackedentity.internal.ProgramOwnerChildrenAppender
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeValueChildrenAppender
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstancePostParentCall
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceProjectionTransformer
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStore
import org.hisp.dhis.android.core.tracker.importer.internal.JobQueryCall
import org.hisp.dhis.android.persistence.enrollment.EnrollmentTableInfo
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityInstanceTableInfo
import org.koin.core.annotation.Singleton

@Singleton
@Suppress("TooManyFunctions")
class TrackedEntityInstanceCollectionRepository internal constructor(
    private val trackedEntityInstanceStore: TrackedEntityInstanceStore,
    scope: RepositoryScope,
    transformer: TrackedEntityInstanceProjectionTransformer,
    private val trackerDataManager: TrackerDataManager,
    private val postCall: TrackedEntityInstancePostParentCall,
    private val jobQueryCall: JobQueryCall,
) : ReadWriteWithUidCollectionRepositoryImpl<
    TrackedEntityInstance,
    TrackedEntityInstanceCreateProjection,
    TrackedEntityInstanceCollectionRepository,
    >(
    trackedEntityInstanceStore,
    childrenAppenders,
    scope,
    transformer,
    FilterConnectorFactory(
        scope,
    ) { s: RepositoryScope ->
        TrackedEntityInstanceCollectionRepository(
            trackedEntityInstanceStore,
            s,
            transformer,
            trackerDataManager,
            postCall,
            jobQueryCall,
        )
    },
),
    ReadWriteWithUploadWithUidCollectionRepository<TrackedEntityInstance, TrackedEntityInstanceCreateProjection> {
    override suspend fun propagateState(m: TrackedEntityInstance, action: HandleAction?) {
        trackerDataManager.propagateTrackedEntityUpdate(m, action!!)
    }

    @Suppress("SpreadOperator")
    override fun upload(): Observable<D2Progress> = flow {
        emitAll(jobQueryCall.queryPendingJobs())
        val trackedEntityInstances = byAggregatedSyncState()
            .`in`(*uploadableStatesIncludingError())
            .getWithoutChildrenInternal()
        emitAll(postCall.uploadTrackedEntityInstances(trackedEntityInstances))
    }.asObservable()

    override fun blockingUpload() {
        upload().blockingSubscribe()
    }

    override fun uid(uid: String?): TrackedEntityInstanceObjectRepository {
        val updatedScope = withUidFilterItem(scope, uid)
        return TrackedEntityInstanceObjectRepository(
            trackedEntityInstanceStore,
            uid,
            childrenAppenders,
            updatedScope,
            trackerDataManager,
        )
    }

    fun byUid(): StringFilterConnector<TrackedEntityInstanceCollectionRepository> {
        return cf.string(TrackedEntityInstanceTableInfo.Columns.UID)
    }

    fun byCreated(): DateFilterConnector<TrackedEntityInstanceCollectionRepository> {
        return cf.date(TrackedEntityInstanceTableInfo.Columns.CREATED)
    }

    fun byLastUpdated(): DateFilterConnector<TrackedEntityInstanceCollectionRepository> {
        return cf.date(TrackedEntityInstanceTableInfo.Columns.LAST_UPDATED)
    }

    fun byCreatedAtClient(): StringFilterConnector<TrackedEntityInstanceCollectionRepository> {
        return cf.string(TrackedEntityInstanceTableInfo.Columns.CREATED_AT_CLIENT)
    }

    fun byLastUpdatedAtClient(): StringFilterConnector<TrackedEntityInstanceCollectionRepository> {
        return cf.string(TrackedEntityInstanceTableInfo.Columns.LAST_UPDATED_AT_CLIENT)
    }

    fun byOrganisationUnitUid(): StringFilterConnector<TrackedEntityInstanceCollectionRepository> {
        return cf.string(TrackedEntityInstanceTableInfo.Columns.ORGANISATION_UNIT)
    }

    fun byTrackedEntityType(): StringFilterConnector<TrackedEntityInstanceCollectionRepository> {
        return cf.string(TrackedEntityInstanceTableInfo.Columns.TRACKED_ENTITY_TYPE)
    }

    fun byGeometryType(): EnumFilterConnector<TrackedEntityInstanceCollectionRepository, FeatureType> {
        return cf.enumC(TrackedEntityInstanceTableInfo.Columns.GEOMETRY_TYPE)
    }

    fun byGeometryCoordinates(): StringFilterConnector<TrackedEntityInstanceCollectionRepository> {
        return cf.string(TrackedEntityInstanceTableInfo.Columns.GEOMETRY_COORDINATES)
    }

    @Deprecated(
        "Use {@link #byAggregatedSyncState()} instead.",
        ReplaceWith("byAggregatedSyncState()"),
    )
    fun byState(): EnumFilterConnector<TrackedEntityInstanceCollectionRepository, State> {
        return byAggregatedSyncState()
    }

    fun bySyncState(): EnumFilterConnector<TrackedEntityInstanceCollectionRepository, State> {
        return cf.enumC(TrackedEntityInstanceTableInfo.Columns.SYNC_STATE)
    }

    fun byAggregatedSyncState(): EnumFilterConnector<TrackedEntityInstanceCollectionRepository, State> {
        return cf.enumC(TrackedEntityInstanceTableInfo.Columns.AGGREGATED_SYNC_STATE)
    }

    fun byDeleted(): BooleanFilterConnector<TrackedEntityInstanceCollectionRepository> {
        return cf.bool(TrackedEntityInstanceTableInfo.Columns.DELETED)
    }

    fun byProgramUids(programUids: List<String>): TrackedEntityInstanceCollectionRepository {
        return cf.subQuery(TrackedEntityInstanceTableInfo.Columns.UID).inLinkTable(
            EnrollmentTableInfo.TABLE_INFO.name(),
            EnrollmentTableInfo.Columns.TRACKED_ENTITY_INSTANCE,
            EnrollmentTableInfo.Columns.PROGRAM,
            programUids,
        )
    }

    fun orderByCreated(direction: OrderByDirection?): TrackedEntityInstanceCollectionRepository {
        return cf.withOrderBy(TrackedEntityInstanceTableInfo.Columns.CREATED, direction)
    }

    fun orderByLastUpdated(direction: OrderByDirection?): TrackedEntityInstanceCollectionRepository {
        return cf.withOrderBy(TrackedEntityInstanceTableInfo.Columns.LAST_UPDATED, direction)
    }

    fun orderByCreatedAtClient(
        direction: OrderByDirection?,
    ): TrackedEntityInstanceCollectionRepository {
        return cf.withOrderBy(TrackedEntityInstanceTableInfo.Columns.CREATED_AT_CLIENT, direction)
    }

    fun orderByLastUpdatedAtClient(
        direction: OrderByDirection?,
    ): TrackedEntityInstanceCollectionRepository {
        return cf.withOrderBy(TrackedEntityInstanceTableInfo.Columns.LAST_UPDATED_AT_CLIENT, direction)
    }

    fun withTrackedEntityAttributeValues(): TrackedEntityInstanceCollectionRepository {
        return cf.withChild(TRACKED_ENTITY_ATTRIBUTE_VALUES)
    }

    fun withProgramOwners(): TrackedEntityInstanceCollectionRepository {
        return cf.withChild(PROGRAM_OWNERS)
    }

    internal companion object {
        internal const val TRACKED_ENTITY_ATTRIBUTE_VALUES = "attributes"
        private const val PROGRAM_OWNERS = "programOwners"

        val childrenAppenders: ChildrenAppenderGetter<TrackedEntityInstance> = mapOf(
            TRACKED_ENTITY_ATTRIBUTE_VALUES to TrackedEntityAttributeValueChildrenAppender::create,
            PROGRAM_OWNERS to ProgramOwnerChildrenAppender::create,
        )
    }
}

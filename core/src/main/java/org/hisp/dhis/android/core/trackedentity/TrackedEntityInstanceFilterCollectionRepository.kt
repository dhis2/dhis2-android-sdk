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

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyIdentifiableCollectionRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.filters.internal.BooleanFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.filters.internal.IntegerFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.common.AssignedUserMode
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.core.trackedentity.internal.EntityQueryCriteriaFields
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceFilterAttributeValueFilterChildrenAppender
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceFilterEvenFilterChildrenAppender
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceFilterFields
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceFilterStore
import org.koin.core.annotation.Singleton

@Singleton
@Suppress("TooManyFunctions")
class TrackedEntityInstanceFilterCollectionRepository internal constructor(
    store: TrackedEntityInstanceFilterStore,
    databaseAdapter: DatabaseAdapter,
    scope: RepositoryScope,
) : ReadOnlyIdentifiableCollectionRepositoryImpl<
    TrackedEntityInstanceFilter,
    TrackedEntityInstanceFilterCollectionRepository,
    >(
    store,
    databaseAdapter,
    childrenAppenders,
    scope,
    FilterConnectorFactory(
        scope,
    ) { s: RepositoryScope ->
        TrackedEntityInstanceFilterCollectionRepository(
            store,
            databaseAdapter,
            s,
        )
    },
) {
    fun byProgram(): StringFilterConnector<TrackedEntityInstanceFilterCollectionRepository> {
        return cf.string(TrackedEntityInstanceFilterTableInfo.Columns.PROGRAM)
    }

    fun byDescription(): StringFilterConnector<TrackedEntityInstanceFilterCollectionRepository> {
        return cf.string(TrackedEntityInstanceFilterTableInfo.Columns.DESCRIPTION)
    }

    fun bySortOrder(): IntegerFilterConnector<TrackedEntityInstanceFilterCollectionRepository> {
        return cf.integer(TrackedEntityInstanceFilterTableInfo.Columns.SORT_ORDER)
    }

    fun byEnrollmentStatus(): EnumFilterConnector<TrackedEntityInstanceFilterCollectionRepository, EnrollmentStatus> {
        return cf.enumC(TrackedEntityInstanceFilterTableInfo.Columns.ENROLLMENT_STATUS)
    }

    fun byFollowUp(): BooleanFilterConnector<TrackedEntityInstanceFilterCollectionRepository> {
        return cf.bool(TrackedEntityInstanceFilterTableInfo.Columns.FOLLOW_UP)
    }

    fun byOrganisationUnit(): StringFilterConnector<TrackedEntityInstanceFilterCollectionRepository> {
        return cf.string(TrackedEntityInstanceFilterTableInfo.Columns.ORGANISATION_UNIT)
    }

    fun byOuMode(): EnumFilterConnector<TrackedEntityInstanceFilterCollectionRepository, OrganisationUnitMode> {
        return cf.enumC(TrackedEntityInstanceFilterTableInfo.Columns.OU_MODE)
    }

    fun byAssignedUserMode(): EnumFilterConnector<TrackedEntityInstanceFilterCollectionRepository, AssignedUserMode> {
        return cf.enumC(TrackedEntityInstanceFilterTableInfo.Columns.ASSIGNED_USER_MODE)
    }

    fun byOrderProperty(): StringFilterConnector<TrackedEntityInstanceFilterCollectionRepository> {
        return cf.string(TrackedEntityInstanceFilterTableInfo.Columns.ORDER)
    }

    fun byDisplayColumnOrder(): StringFilterConnector<TrackedEntityInstanceFilterCollectionRepository> {
        return cf.string(TrackedEntityInstanceFilterTableInfo.Columns.DISPLAY_COLUMN_ORDER)
    }

    fun byEventStatus(): EnumFilterConnector<TrackedEntityInstanceFilterCollectionRepository, EventStatus> {
        return cf.enumC(TrackedEntityInstanceFilterTableInfo.Columns.EVENT_STATUS)
    }

    fun byEventDate(): StringFilterConnector<TrackedEntityInstanceFilterCollectionRepository> {
        return cf.string(TrackedEntityInstanceFilterTableInfo.Columns.EVENT_DATE)
    }

    fun byLastUpdatedDate(): StringFilterConnector<TrackedEntityInstanceFilterCollectionRepository> {
        return cf.string(TrackedEntityInstanceFilterTableInfo.Columns.LAST_UPDATED_DATE)
    }

    fun byProgramStage(): StringFilterConnector<TrackedEntityInstanceFilterCollectionRepository> {
        return cf.string(TrackedEntityInstanceFilterTableInfo.Columns.PROGRAM_STAGE)
    }

    fun byTrackedEntityInstances(): StringFilterConnector<TrackedEntityInstanceFilterCollectionRepository> {
        return cf.string(TrackedEntityInstanceFilterTableInfo.Columns.TRACKED_ENTITY_INSTANCES)
    }

    fun byEnrollmentIncidentDate(): StringFilterConnector<TrackedEntityInstanceFilterCollectionRepository> {
        return cf.string(TrackedEntityInstanceFilterTableInfo.Columns.ENROLLMENT_INCIDENT_DATE)
    }

    fun byEnrollmentCreatedDate(): StringFilterConnector<TrackedEntityInstanceFilterCollectionRepository> {
        return cf.string(TrackedEntityInstanceFilterTableInfo.Columns.ENROLLMENT_CREATED_DATE)
    }

    fun byColor(): StringFilterConnector<TrackedEntityInstanceFilterCollectionRepository> {
        return cf.string(TrackedEntityInstanceFilterTableInfo.Columns.COLOR)
    }

    fun byIcon(): StringFilterConnector<TrackedEntityInstanceFilterCollectionRepository> {
        return cf.string(TrackedEntityInstanceFilterTableInfo.Columns.ICON)
    }

    fun withTrackedEntityInstanceEventFilters(): TrackedEntityInstanceFilterCollectionRepository {
        return cf.withChild(TrackedEntityInstanceFilterFields.EVENT_FILTERS)
    }

    fun withAttributeValueFilters(): TrackedEntityInstanceFilterCollectionRepository {
        return cf.withChild(EntityQueryCriteriaFields.ATTRIBUTE_VALUE_FILTER)
    }

    internal companion object {
        val childrenAppenders: ChildrenAppenderGetter<TrackedEntityInstanceFilter> = mapOf(
            TrackedEntityInstanceFilterFields.EVENT_FILTERS to
                TrackedEntityInstanceFilterEvenFilterChildrenAppender::create,
            EntityQueryCriteriaFields.ATTRIBUTE_VALUE_FILTER to
                TrackedEntityInstanceFilterAttributeValueFilterChildrenAppender::create,
        )
    }
}

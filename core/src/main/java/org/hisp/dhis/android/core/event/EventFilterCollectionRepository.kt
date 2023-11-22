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

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyIdentifiableCollectionRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.filters.internal.BooleanFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.common.AssignedUserMode
import org.hisp.dhis.android.core.event.internal.EventFilterEventDataFilterChildrenAppender
import org.hisp.dhis.android.core.event.internal.EventFilterStore
import org.hisp.dhis.android.core.event.internal.EventQueryCriteriaFields
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.koin.core.annotation.Singleton

@Singleton
@Suppress("TooManyFunctions")
class EventFilterCollectionRepository internal constructor(
    store: EventFilterStore,
    databaseAdapter: DatabaseAdapter,
    scope: RepositoryScope,
) : ReadOnlyIdentifiableCollectionRepositoryImpl<EventFilter, EventFilterCollectionRepository>(
    store,
    databaseAdapter,
    childrenAppenders,
    scope,
    FilterConnectorFactory(
        scope,
    ) { s: RepositoryScope ->
        EventFilterCollectionRepository(
            store,
            databaseAdapter,
            s,
        )
    },
) {
    fun byProgram(): StringFilterConnector<EventFilterCollectionRepository> {
        return cf.string(EventFilterTableInfo.Columns.PROGRAM)
    }

    fun byProgramStage(): StringFilterConnector<EventFilterCollectionRepository> {
        return cf.string(EventFilterTableInfo.Columns.PROGRAM_STAGE)
    }

    fun byDescription(): StringFilterConnector<EventFilterCollectionRepository> {
        return cf.string(EventFilterTableInfo.Columns.DESCRIPTION)
    }

    fun byFollowUp(): BooleanFilterConnector<EventFilterCollectionRepository> {
        return cf.bool(EventFilterTableInfo.Columns.FOLLOW_UP)
    }

    fun byOrganisationUnit(): StringFilterConnector<EventFilterCollectionRepository> {
        return cf.string(EventFilterTableInfo.Columns.ORGANISATION_UNIT)
    }

    fun byOuMode(): EnumFilterConnector<EventFilterCollectionRepository, OrganisationUnitMode> {
        return cf.enumC(EventFilterTableInfo.Columns.OU_MODE)
    }

    fun byAssignedUserMode(): EnumFilterConnector<EventFilterCollectionRepository, AssignedUserMode> {
        return cf.enumC(EventFilterTableInfo.Columns.ASSIGNED_USER_MODE)
    }

    fun byOrder(): StringFilterConnector<EventFilterCollectionRepository> {
        return cf.string(EventFilterTableInfo.Columns.ORDER)
    }

    fun byDisplayColumnOrder(): StringFilterConnector<EventFilterCollectionRepository> {
        return cf.string(EventFilterTableInfo.Columns.DISPLAY_COLUMN_ORDER)
    }

    fun byEvents(): StringFilterConnector<EventFilterCollectionRepository> {
        return cf.string(EventFilterTableInfo.Columns.EVENTS)
    }

    fun byEventStatus(): EnumFilterConnector<EventFilterCollectionRepository, EventStatus> {
        return cf.enumC(EventFilterTableInfo.Columns.EVENT_STATUS)
    }

    fun byEventDate(): StringFilterConnector<EventFilterCollectionRepository> {
        return cf.string(EventFilterTableInfo.Columns.EVENT_DATE)
    }

    fun byDueDate(): StringFilterConnector<EventFilterCollectionRepository> {
        return cf.string(EventFilterTableInfo.Columns.DUE_DATE)
    }

    fun byLastUpdatedDate(): StringFilterConnector<EventFilterCollectionRepository> {
        return cf.string(EventFilterTableInfo.Columns.LAST_UPDATED_DATE)
    }

    fun byCompletedDate(): StringFilterConnector<EventFilterCollectionRepository> {
        return cf.string(EventFilterTableInfo.Columns.COMPLETED_DATE)
    }

    fun withEventDataFilters(): EventFilterCollectionRepository {
        return cf.withChild(EventQueryCriteriaFields.DATA_FILTERS)
    }

    internal companion object {
        val childrenAppenders: ChildrenAppenderGetter<EventFilter> = mapOf(
            EventQueryCriteriaFields.DATA_FILTERS to EventFilterEventDataFilterChildrenAppender::create,
        )
    }
}

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
package org.hisp.dhis.android.core.programstageworkinglist

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyIdentifiableCollectionRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.common.AssignedUserMode
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.core.programstageworkinglist.internal.ProgramStageQueryCriteriaFields
import org.hisp.dhis.android.core.programstageworkinglist.internal.ProgramStageWorkingListAttributeValueFilterChildrenAppender
import org.hisp.dhis.android.core.programstageworkinglist.internal.ProgramStageWorkingListDataFilterChildrenAppender
import org.hisp.dhis.android.core.programstageworkinglist.internal.ProgramStageWorkingListStore
import org.hisp.dhis.android.core.programstageworkinglist.internal.ProgramStageWorkingListTableInfo
import org.koin.core.annotation.Singleton

@Singleton
@Suppress("TooManyFunctions")
class ProgramStageWorkingListCollectionRepository internal constructor(
    store: ProgramStageWorkingListStore,
    databaseAdapter: DatabaseAdapter,
    scope: RepositoryScope,
) : ReadOnlyIdentifiableCollectionRepositoryImpl<ProgramStageWorkingList, ProgramStageWorkingListCollectionRepository>(
    store,
    databaseAdapter,
    childrenAppenders,
    scope,
    FilterConnectorFactory(
        scope,
    ) { s: RepositoryScope ->
        ProgramStageWorkingListCollectionRepository(
            store,
            databaseAdapter,
            s,
        )
    },
) {
    fun byProgram(): StringFilterConnector<ProgramStageWorkingListCollectionRepository> {
        return cf.string(ProgramStageWorkingListTableInfo.Columns.PROGRAM)
    }

    fun byProgramStage(): StringFilterConnector<ProgramStageWorkingListCollectionRepository> {
        return cf.string(ProgramStageWorkingListTableInfo.Columns.PROGRAM_STAGE)
    }

    fun byDescription(): StringFilterConnector<ProgramStageWorkingListCollectionRepository> {
        return cf.string(ProgramStageWorkingListTableInfo.Columns.DESCRIPTION)
    }

    fun byEventStatus(): EnumFilterConnector<ProgramStageWorkingListCollectionRepository, EventStatus> {
        return cf.enumC(ProgramStageWorkingListTableInfo.Columns.EVENT_STATUS)
    }

    fun byEnrollmentStatus(): EnumFilterConnector<ProgramStageWorkingListCollectionRepository, EnrollmentStatus> {
        return cf.enumC(ProgramStageWorkingListTableInfo.Columns.ENROLLMENT_STATUS)
    }

    fun byOrder(): StringFilterConnector<ProgramStageWorkingListCollectionRepository> {
        return cf.string(ProgramStageWorkingListTableInfo.Columns.ORDER)
    }

    fun byDisplayColumnOrder(): StringFilterConnector<ProgramStageWorkingListCollectionRepository> {
        return cf.string(ProgramStageWorkingListTableInfo.Columns.DISPLAY_COLUMN_ORDER)
    }

    fun byOrganisationUnit(): StringFilterConnector<ProgramStageWorkingListCollectionRepository> {
        return cf.string(ProgramStageWorkingListTableInfo.Columns.ORG_UNIT)
    }

    fun byOuMode(): EnumFilterConnector<ProgramStageWorkingListCollectionRepository, OrganisationUnitMode> {
        return cf.enumC(ProgramStageWorkingListTableInfo.Columns.OU_MODE)
    }

    fun byAssignedUserMode(): EnumFilterConnector<ProgramStageWorkingListCollectionRepository, AssignedUserMode> {
        return cf.enumC(ProgramStageWorkingListTableInfo.Columns.ASSIGNED_USER_MODE)
    }

    fun withDataFilters(): ProgramStageWorkingListCollectionRepository {
        return cf.withChild(ProgramStageQueryCriteriaFields.DATA_FILTERS)
    }

    fun withAttributeValueFilters(): ProgramStageWorkingListCollectionRepository {
        return cf.withChild(ProgramStageQueryCriteriaFields.ATTRIBUTE_VALUE_FILTER)
    }

    internal companion object {
        val childrenAppenders: ChildrenAppenderGetter<ProgramStageWorkingList> = mapOf(
            ProgramStageQueryCriteriaFields.DATA_FILTERS to
                ProgramStageWorkingListDataFilterChildrenAppender::create,
            ProgramStageQueryCriteriaFields.ATTRIBUTE_VALUE_FILTER to
                ProgramStageWorkingListAttributeValueFilterChildrenAppender::create,
        )
    }
}

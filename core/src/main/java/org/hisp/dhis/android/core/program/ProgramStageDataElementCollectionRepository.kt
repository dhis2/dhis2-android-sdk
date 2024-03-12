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
package org.hisp.dhis.android.core.program

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyIdentifiableCollectionRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.filters.internal.BooleanFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.filters.internal.IntegerFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope.OrderByDirection
import org.hisp.dhis.android.core.program.internal.DataElementValueTypeRenderingChildrenAppender
import org.hisp.dhis.android.core.program.internal.ProgramStageDataElementFields
import org.hisp.dhis.android.core.program.internal.ProgramStageDataElementStore
import org.koin.core.annotation.Singleton

@Singleton
class ProgramStageDataElementCollectionRepository internal constructor(
    store: ProgramStageDataElementStore,
    databaseAdapter: DatabaseAdapter,
    scope: RepositoryScope,
) : ReadOnlyIdentifiableCollectionRepositoryImpl<ProgramStageDataElement, ProgramStageDataElementCollectionRepository>(
    store,
    databaseAdapter,
    childrenAppenders,
    scope,
    FilterConnectorFactory(
        scope,
    ) { s: RepositoryScope ->
        ProgramStageDataElementCollectionRepository(
            store,
            databaseAdapter,
            s,
        )
    },
) {
    fun byDisplayInReports(): BooleanFilterConnector<ProgramStageDataElementCollectionRepository> {
        return cf.bool(ProgramStageDataElementTableInfo.Columns.DISPLAY_IN_REPORTS)
    }

    fun byCompulsory(): BooleanFilterConnector<ProgramStageDataElementCollectionRepository> {
        return cf.bool(ProgramStageDataElementTableInfo.Columns.COMPULSORY)
    }

    fun byAllowProvidedElsewhere(): BooleanFilterConnector<ProgramStageDataElementCollectionRepository> {
        return cf.bool(ProgramStageDataElementTableInfo.Columns.ALLOW_PROVIDED_ELSEWHERE)
    }

    fun bySortOrder(): IntegerFilterConnector<ProgramStageDataElementCollectionRepository> {
        return cf.integer(ProgramStageDataElementTableInfo.Columns.SORT_ORDER)
    }

    fun byAllowFutureDate(): BooleanFilterConnector<ProgramStageDataElementCollectionRepository> {
        return cf.bool(ProgramStageDataElementTableInfo.Columns.ALLOW_FUTURE_DATE)
    }

    fun byDataElement(): StringFilterConnector<ProgramStageDataElementCollectionRepository> {
        return cf.string(ProgramStageDataElementTableInfo.Columns.DATA_ELEMENT)
    }

    fun byProgramStage(): StringFilterConnector<ProgramStageDataElementCollectionRepository> {
        return cf.string(ProgramStageDataElementTableInfo.Columns.PROGRAM_STAGE)
    }

    fun withRenderType(): ProgramStageDataElementCollectionRepository {
        return cf.withChild(ProgramStageDataElementFields.RENDER_TYPE)
    }

    fun orderBySortOrder(direction: OrderByDirection?): ProgramStageDataElementCollectionRepository {
        return cf.withOrderBy(ProgramStageDataElementTableInfo.Columns.SORT_ORDER, direction)
    }

    internal companion object {
        val childrenAppenders: ChildrenAppenderGetter<ProgramStageDataElement> = mapOf(
            ProgramStageDataElementFields.RENDER_TYPE to ::DataElementValueTypeRenderingChildrenAppender,
        )
    }
}

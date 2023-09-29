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

import dagger.Reusable
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyNameableCollectionRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.filters.internal.BooleanFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.filters.internal.IntegerFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope.OrderByDirection
import org.hisp.dhis.android.core.program.internal.ProgramTrackedEntityAttributeFields
import org.hisp.dhis.android.core.program.internal.ProgramTrackedEntityAttributeStore
import javax.inject.Inject

@Reusable
class ProgramTrackedEntityAttributeCollectionRepository @Inject internal constructor(
    store: ProgramTrackedEntityAttributeStore,
    childrenAppenders: MutableMap<String, ChildrenAppender<ProgramTrackedEntityAttribute>>,
    scope: RepositoryScope,
) : ReadOnlyNameableCollectionRepositoryImpl<ProgramTrackedEntityAttribute, ProgramTrackedEntityAttributeCollectionRepository>(
    store,
    childrenAppenders,
    scope,
    FilterConnectorFactory(
        scope,
    ) { s: RepositoryScope ->
        ProgramTrackedEntityAttributeCollectionRepository(
            store,
            childrenAppenders,
            s,
        )
    },
) {
    fun byMandatory(): BooleanFilterConnector<ProgramTrackedEntityAttributeCollectionRepository> {
        return cf.bool(ProgramTrackedEntityAttributeTableInfo.Columns.MANDATORY)
    }

    fun byTrackedEntityAttribute(): StringFilterConnector<ProgramTrackedEntityAttributeCollectionRepository> {
        return cf.string(ProgramTrackedEntityAttributeTableInfo.Columns.TRACKED_ENTITY_ATTRIBUTE)
    }

    fun byAllowFutureDate(): BooleanFilterConnector<ProgramTrackedEntityAttributeCollectionRepository> {
        return cf.bool(ProgramTrackedEntityAttributeTableInfo.Columns.ALLOW_FUTURE_DATE)
    }

    fun byDisplayInList(): BooleanFilterConnector<ProgramTrackedEntityAttributeCollectionRepository> {
        return cf.bool(ProgramTrackedEntityAttributeTableInfo.Columns.DISPLAY_IN_LIST)
    }

    fun byProgram(): StringFilterConnector<ProgramTrackedEntityAttributeCollectionRepository> {
        return cf.string(ProgramTrackedEntityAttributeTableInfo.Columns.PROGRAM)
    }

    fun bySortOrder(): IntegerFilterConnector<ProgramTrackedEntityAttributeCollectionRepository> {
        return cf.integer(ProgramTrackedEntityAttributeTableInfo.Columns.SORT_ORDER)
    }

    fun bySearchable(): BooleanFilterConnector<ProgramTrackedEntityAttributeCollectionRepository> {
        return cf.bool(ProgramTrackedEntityAttributeTableInfo.Columns.SEARCHABLE)
    }

    fun withRenderType(): ProgramTrackedEntityAttributeCollectionRepository {
        return cf.withChild(ProgramTrackedEntityAttributeFields.RENDER_TYPE)
    }

    fun orderBySortOrder(
        direction: OrderByDirection?,
    ): ProgramTrackedEntityAttributeCollectionRepository {
        return cf.withOrderBy(ProgramTrackedEntityAttributeTableInfo.Columns.SORT_ORDER, direction)
    }
}

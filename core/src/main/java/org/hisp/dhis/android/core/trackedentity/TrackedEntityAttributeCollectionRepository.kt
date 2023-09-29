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

import dagger.Reusable
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyNameableCollectionRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.filters.internal.BooleanFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.filters.internal.IntegerFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.common.ValueType
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeFields
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeStore
import javax.inject.Inject

@Reusable
class TrackedEntityAttributeCollectionRepository @Inject internal constructor(
    store: TrackedEntityAttributeStore,
    childrenAppenders: MutableMap<String, ChildrenAppender<TrackedEntityAttribute>>,
    scope: RepositoryScope,
) : ReadOnlyNameableCollectionRepositoryImpl<TrackedEntityAttribute, TrackedEntityAttributeCollectionRepository>(
    store,
    childrenAppenders,
    scope,
    FilterConnectorFactory(
        scope,
    ) { s: RepositoryScope ->
        TrackedEntityAttributeCollectionRepository(
            store,
            childrenAppenders,
            s,
        )
    },
) {
    fun byPattern(): StringFilterConnector<TrackedEntityAttributeCollectionRepository> {
        return cf.string(TrackedEntityAttributeTableInfo.Columns.PATTERN)
    }

    fun bySortOrderInListNoProgram(): IntegerFilterConnector<TrackedEntityAttributeCollectionRepository> {
        return cf.integer(TrackedEntityAttributeTableInfo.Columns.SORT_ORDER_IN_LIST_NO_PROGRAM)
    }

    fun byOptionSetUid(): StringFilterConnector<TrackedEntityAttributeCollectionRepository> {
        return cf.string(TrackedEntityAttributeTableInfo.Columns.OPTION_SET)
    }

    fun byValueType(): EnumFilterConnector<TrackedEntityAttributeCollectionRepository, ValueType> {
        return cf.enumC(TrackedEntityAttributeTableInfo.Columns.VALUE_TYPE)
    }

    fun byExpression(): StringFilterConnector<TrackedEntityAttributeCollectionRepository> {
        return cf.string(TrackedEntityAttributeTableInfo.Columns.EXPRESSION)
    }

    fun byProgramScope(): BooleanFilterConnector<TrackedEntityAttributeCollectionRepository> {
        return cf.bool(TrackedEntityAttributeTableInfo.Columns.PROGRAM_SCOPE)
    }

    fun byDisplayInListNoProgram(): BooleanFilterConnector<TrackedEntityAttributeCollectionRepository> {
        return cf.bool(TrackedEntityAttributeTableInfo.Columns.DISPLAY_IN_LIST_NO_PROGRAM)
    }

    fun byGenerated(): BooleanFilterConnector<TrackedEntityAttributeCollectionRepository> {
        return cf.bool(TrackedEntityAttributeTableInfo.Columns.GENERATED)
    }

    fun byDisplayOnVisitSchedule(): BooleanFilterConnector<TrackedEntityAttributeCollectionRepository> {
        return cf.bool(TrackedEntityAttributeTableInfo.Columns.DISPLAY_ON_VISIT_SCHEDULE)
    }

    fun byConfidential(): BooleanFilterConnector<TrackedEntityAttributeCollectionRepository> {
        return cf.bool(TrackedEntityAttributeTableInfo.Columns.CONFIDENTIAL)
    }

    fun byOrgUnitScope(): BooleanFilterConnector<TrackedEntityAttributeCollectionRepository> {
        return cf.bool(TrackedEntityAttributeFields.ORG_UNIT_SCOPE)
    }

    fun byUnique(): BooleanFilterConnector<TrackedEntityAttributeCollectionRepository> {
        return cf.bool(TrackedEntityAttributeTableInfo.Columns.UNIQUE)
    }

    fun byInherit(): BooleanFilterConnector<TrackedEntityAttributeCollectionRepository> {
        return cf.bool(TrackedEntityAttributeTableInfo.Columns.INHERIT)
    }

    fun byFieldMask(): StringFilterConnector<TrackedEntityAttributeCollectionRepository> {
        return cf.string(TrackedEntityAttributeTableInfo.Columns.FIELD_MASK)
    }

    fun byFormName(): StringFilterConnector<TrackedEntityAttributeCollectionRepository> {
        return cf.string(TrackedEntityAttributeTableInfo.Columns.FORM_NAME)
    }

    fun byDisplayFormName(): StringFilterConnector<TrackedEntityAttributeCollectionRepository> {
        return cf.string(TrackedEntityAttributeTableInfo.Columns.DISPLAY_FORM_NAME)
    }

    fun byColor(): StringFilterConnector<TrackedEntityAttributeCollectionRepository> {
        return cf.string(TrackedEntityAttributeTableInfo.Columns.COLOR)
    }

    fun byIcon(): StringFilterConnector<TrackedEntityAttributeCollectionRepository> {
        return cf.string(TrackedEntityAttributeTableInfo.Columns.ICON)
    }

    fun withLegendSets(): TrackedEntityAttributeCollectionRepository {
        return cf.withChild(TrackedEntityAttributeFields.LEGEND_SETS)
    }
}

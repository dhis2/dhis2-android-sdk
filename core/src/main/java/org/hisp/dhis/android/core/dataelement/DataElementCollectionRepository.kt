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
package org.hisp.dhis.android.core.dataelement

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyIdentifiableCollectionRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.filters.internal.BooleanFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.common.ValueType
import org.hisp.dhis.android.core.dataelement.internal.DataElementAttributeChildrenAppender
import org.hisp.dhis.android.core.dataelement.internal.DataElementLegendSetChildrenAppender
import org.hisp.dhis.android.core.dataelement.internal.DataElementStore
import org.koin.core.annotation.Singleton

@Singleton
@Suppress("TooManyFunctions")
class DataElementCollectionRepository internal constructor(
    store: DataElementStore,
    databaseAdapter: DatabaseAdapter,
    scope: RepositoryScope,
) : ReadOnlyIdentifiableCollectionRepositoryImpl<DataElement, DataElementCollectionRepository>(
    store,
    databaseAdapter,
    childrenAppenders,
    scope,
    FilterConnectorFactory(
        scope,
    ) { s: RepositoryScope ->
        DataElementCollectionRepository(
            store,
            databaseAdapter,
            s,
        )
    },
) {
    fun byValueType(): EnumFilterConnector<DataElementCollectionRepository, ValueType> {
        return cf.enumC(DataElementTableInfo.Columns.VALUE_TYPE)
    }

    fun byZeroIsSignificant(): BooleanFilterConnector<DataElementCollectionRepository> {
        return cf.bool(DataElementTableInfo.Columns.ZERO_IS_SIGNIFICANT)
    }

    fun byAggregationType(): StringFilterConnector<DataElementCollectionRepository> {
        return cf.string(DataElementTableInfo.Columns.AGGREGATION_TYPE)
    }

    fun byFormName(): StringFilterConnector<DataElementCollectionRepository> {
        return cf.string(DataElementTableInfo.Columns.FORM_NAME)
    }

    fun byDomainType(): StringFilterConnector<DataElementCollectionRepository> {
        return cf.string(DataElementTableInfo.Columns.DOMAIN_TYPE)
    }

    fun byDisplayFormName(): StringFilterConnector<DataElementCollectionRepository> {
        return cf.string(DataElementTableInfo.Columns.DISPLAY_FORM_NAME)
    }

    fun byOptionSetUid(): StringFilterConnector<DataElementCollectionRepository> {
        return cf.string(DataElementTableInfo.Columns.OPTION_SET)
    }

    fun byCategoryComboUid(): StringFilterConnector<DataElementCollectionRepository> {
        return cf.string(DataElementTableInfo.Columns.CATEGORY_COMBO)
    }

    fun byFieldMask(): StringFilterConnector<DataElementCollectionRepository> {
        return cf.string(DataElementTableInfo.Columns.FIELD_MASK)
    }

    fun byColor(): StringFilterConnector<DataElementCollectionRepository> {
        return cf.string(DataElementTableInfo.Columns.COLOR)
    }

    fun byIcon(): StringFilterConnector<DataElementCollectionRepository> {
        return cf.string(DataElementTableInfo.Columns.ICON)
    }

    fun withLegendSets(): DataElementCollectionRepository {
        return cf.withChild(LEGEND_SETS)
    }

    fun withAttributes(): DataElementCollectionRepository {
        return cf.withChild(ATTRIBUTE_VALUES)
    }

    internal companion object {
        private const val LEGEND_SETS = "legendSets"
        private const val ATTRIBUTE_VALUES = "attributeValues"

        val childrenAppenders: ChildrenAppenderGetter<DataElement> = mapOf(
            LEGEND_SETS to DataElementLegendSetChildrenAppender::create,
            ATTRIBUTE_VALUES to DataElementAttributeChildrenAppender::create,
        )
    }
}

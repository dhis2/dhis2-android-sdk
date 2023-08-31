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
package org.hisp.dhis.android.core.datavalue

import dagger.Reusable
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyCollectionRepository
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyCollectionRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.filters.internal.DateFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.datavalue.DataValueByDataSetQueryHelper.dataValueConflictKey
import org.hisp.dhis.android.core.datavalue.DataValueByDataSetQueryHelper.operator
import org.hisp.dhis.android.core.datavalue.DataValueByDataSetQueryHelper.whereClause
import org.hisp.dhis.android.core.datavalue.internal.DataValueConflictStore
import org.hisp.dhis.android.core.imports.ImportStatus
import javax.inject.Inject

@Reusable
@Suppress("TooManyFunctions")
class DataValueConflictCollectionRepository @Inject internal constructor(
    store: DataValueConflictStore,
    childrenAppenders: MutableMap<String, ChildrenAppender<DataValueConflict>>,
    scope: RepositoryScope,
) : ReadOnlyCollectionRepositoryImpl<DataValueConflict, DataValueConflictCollectionRepository>(
    store,
    childrenAppenders,
    scope,
    FilterConnectorFactory(
        scope,
    ) { s: RepositoryScope ->
        DataValueConflictCollectionRepository(
            store,
            childrenAppenders,
            s,
        )
    },
),
    ReadOnlyCollectionRepository<DataValueConflict> {

    fun byDataSet(dataSetUid: String): DataValueConflictCollectionRepository {
        return cf.subQuery(dataValueConflictKey)
            .rawSubQuery(
                operator,
                whereClause(dataSetUid),
            )
    }

    fun byConflict(): StringFilterConnector<DataValueConflictCollectionRepository> {
        return cf.string(DataValueConflictTableInfo.Columns.CONFLICT)
    }

    fun byValue(): StringFilterConnector<DataValueConflictCollectionRepository> {
        return cf.string(DataValueConflictTableInfo.Columns.VALUE)
    }

    fun byAttributeOptionCombo(): StringFilterConnector<DataValueConflictCollectionRepository> {
        return cf.string(DataValueConflictTableInfo.Columns.ATTRIBUTE_OPTION_COMBO)
    }

    fun byCategoryOptionCombo(): StringFilterConnector<DataValueConflictCollectionRepository> {
        return cf.string(DataValueConflictTableInfo.Columns.CATEGORY_OPTION_COMBO)
    }

    fun byDataElement(): StringFilterConnector<DataValueConflictCollectionRepository> {
        return cf.string(DataValueConflictTableInfo.Columns.DATA_ELEMENT)
    }

    fun byPeriod(): StringFilterConnector<DataValueConflictCollectionRepository> {
        return cf.string(DataValueConflictTableInfo.Columns.PERIOD)
    }

    fun byOrganisationUnitUid(): StringFilterConnector<DataValueConflictCollectionRepository> {
        return cf.string(DataValueConflictTableInfo.Columns.ORG_UNIT)
    }

    fun byErrorCode(): StringFilterConnector<DataValueConflictCollectionRepository> {
        return cf.string(DataValueConflictTableInfo.Columns.ERROR_CODE)
    }

    fun byDisplayDescription(): StringFilterConnector<DataValueConflictCollectionRepository> {
        return cf.string(DataValueConflictTableInfo.Columns.DISPLAY_DESCRIPTION)
    }

    fun byStatus(): EnumFilterConnector<DataValueConflictCollectionRepository, ImportStatus> {
        return cf.enumC(DataValueConflictTableInfo.Columns.STATUS)
    }

    fun byCreated(): DateFilterConnector<DataValueConflictCollectionRepository> {
        return cf.date(DataValueConflictTableInfo.Columns.CREATED)
    }
}

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
package org.hisp.dhis.android.core.dataset

import dagger.Reusable
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyIdentifiableCollectionRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.filters.internal.BooleanFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.filters.internal.IntegerFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.dataset.internal.SectionDataElementChildrenAppender
import org.hisp.dhis.android.core.dataset.internal.SectionFields
import org.hisp.dhis.android.core.dataset.internal.SectionGreyedFieldsChildrenAppender
import org.hisp.dhis.android.core.dataset.internal.SectionIndicatorsChildrenAppender
import org.hisp.dhis.android.core.dataset.internal.SectionStore
import javax.inject.Inject

@Reusable
class SectionCollectionRepository @Inject internal constructor(
    store: SectionStore,
    databaseAdapter: DatabaseAdapter,
    scope: RepositoryScope,
) : ReadOnlyIdentifiableCollectionRepositoryImpl<Section, SectionCollectionRepository>(
    store,
    databaseAdapter,
    childrenAppenders,
    scope,
    FilterConnectorFactory(
        scope,
    ) { s: RepositoryScope ->
        SectionCollectionRepository(
            store,
            databaseAdapter,
            s,
        )
    },
) {
    fun byDescription(): StringFilterConnector<SectionCollectionRepository> {
        return cf.string(SectionTableInfo.Columns.DESCRIPTION)
    }

    fun bySortOrder(): IntegerFilterConnector<SectionCollectionRepository> {
        return cf.integer(SectionTableInfo.Columns.SORT_ORDER)
    }

    fun byShowRowTotals(): BooleanFilterConnector<SectionCollectionRepository> {
        return cf.bool(SectionTableInfo.Columns.SHOW_ROW_TOTALS)
    }

    fun byShowColumnTotals(): BooleanFilterConnector<SectionCollectionRepository> {
        return cf.bool(SectionTableInfo.Columns.SHOW_COLUMN_TOTALS)
    }

    fun byDataSetUid(): StringFilterConnector<SectionCollectionRepository> {
        return cf.string(SectionTableInfo.Columns.DATA_SET)
    }

    fun withDataElements(): SectionCollectionRepository {
        return cf.withChild(SectionFields.DATA_ELEMENTS)
    }

    fun withGreyedFields(): SectionCollectionRepository {
        return cf.withChild(SectionFields.GREYED_FIELDS)
    }

    fun withIndicators(): SectionCollectionRepository {
        return cf.withChild(SectionFields.INDICATORS)
    }

    internal companion object {
        val childrenAppenders: ChildrenAppenderGetter<Section> = mapOf(
            SectionFields.GREYED_FIELDS to SectionGreyedFieldsChildrenAppender::create,
            SectionFields.DATA_ELEMENTS to SectionDataElementChildrenAppender::create,
            SectionFields.INDICATORS to SectionIndicatorsChildrenAppender::create
        )
    }
}

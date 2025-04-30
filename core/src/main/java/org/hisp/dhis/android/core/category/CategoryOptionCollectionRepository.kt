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
package org.hisp.dhis.android.core.category

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyNameableCollectionRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.filters.internal.BooleanFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.DateFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.category.internal.CategoryOptionOrganisationUnitChildrenAppender
import org.hisp.dhis.android.core.category.internal.CategoryOptionStore
import org.hisp.dhis.android.core.common.IdentifiableColumns
import org.koin.core.annotation.Singleton

@Singleton
class CategoryOptionCollectionRepository internal constructor(
    store: CategoryOptionStore,
    databaseAdapter: DatabaseAdapter,
    scope: RepositoryScope,
) : ReadOnlyNameableCollectionRepositoryImpl<CategoryOption, CategoryOptionCollectionRepository>(
    store,
    databaseAdapter,
    childrenAppenders,
    scope,
    FilterConnectorFactory(
        scope,
    ) { s: RepositoryScope ->
        CategoryOptionCollectionRepository(
            store,
            databaseAdapter,
            s,
        )
    },
) {
    fun byStartDate(): DateFilterConnector<CategoryOptionCollectionRepository> {
        return cf.date(CategoryOptionTableInfo.Columns.START_DATE)
    }

    fun byEndDate(): DateFilterConnector<CategoryOptionCollectionRepository> {
        return cf.date(CategoryOptionTableInfo.Columns.END_DATE)
    }

    fun byAccessDataWrite(): BooleanFilterConnector<CategoryOptionCollectionRepository> {
        return cf.bool(CategoryOptionTableInfo.Columns.ACCESS_DATA_WRITE)
    }

    fun byCategoryUid(categoryUid: String): CategoryOptionCollectionRepository {
        return cf.subQuery(IdentifiableColumns.UID).inLinkTable(
            CategoryCategoryOptionLinkTableInfo.TABLE_INFO.name(),
            CategoryCategoryOptionLinkTableInfo.Columns.CATEGORY_OPTION,
            CategoryCategoryOptionLinkTableInfo.Columns.CATEGORY,
            listOf(categoryUid),
        )
    }

    fun byCategoryOptionComboUid(categoryOptionComboUid: String): CategoryOptionCollectionRepository {
        return cf.subQuery(IdentifiableColumns.UID).inLinkTable(
            CategoryOptionComboCategoryOptionLinkTableInfo.TABLE_INFO.name(),
            CategoryOptionComboCategoryOptionLinkTableInfo.Columns.CATEGORY_OPTION,
            CategoryOptionComboCategoryOptionLinkTableInfo.Columns.CATEGORY_OPTION_COMBO,
            listOf(categoryOptionComboUid),
        )
    }

    /**
     * This method only return results in versions greater or equal to 2.37.
     * @return Collection repository
     */
    fun withOrganisationUnits(): CategoryOptionCollectionRepository {
        return cf.withChild(ORGANISATION_UNITS)
    }

    internal companion object {
        private const val ORGANISATION_UNITS = "organisationUnits"

        val childrenAppenders: ChildrenAppenderGetter<CategoryOption> = mapOf(
            ORGANISATION_UNITS to CategoryOptionOrganisationUnitChildrenAppender::create,
        )
    }
}

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
package org.hisp.dhis.android.core.category.internal

import android.database.Cursor
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.SQLStatementBuilderImpl
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.NameableStatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStoreImpl
import org.hisp.dhis.android.core.arch.db.stores.projections.internal.LinkTableChildProjection
import org.hisp.dhis.android.core.category.CategoryCategoryOptionLinkTableInfo
import org.hisp.dhis.android.core.category.CategoryOption
import org.hisp.dhis.android.core.category.CategoryOptionComboCategoryOptionLinkTableInfo
import org.hisp.dhis.android.core.category.CategoryOptionTableInfo
import org.koin.core.annotation.Singleton

@Singleton
@Suppress("MagicNumber")
internal class CategoryOptionStoreImpl(
    databaseAdapter: DatabaseAdapter,
) : CategoryOptionStore,
    IdentifiableObjectStoreImpl<CategoryOption>(
        databaseAdapter,
        CategoryOptionTableInfo.TABLE_INFO,
        BINDER,
        { cursor: Cursor -> CategoryOption.create(cursor) },
    ) {

    companion object {
        private val BINDER: StatementBinder<CategoryOption> = object : NameableStatementBinder<CategoryOption>() {
            override fun bindToStatement(o: CategoryOption, w: StatementWrapper) {
                super.bindToStatement(o, w)
                w.bind(11, o.startDate())
                w.bind(12, o.endDate())
                w.bind(13, o.access().data().write())
            }
        }
    }

    override suspend fun getForCategoryOptionCombo(categoryOptionComboUid: String): List<CategoryOption> {
        val projection = LinkTableChildProjection(
            CategoryOptionTableInfo.TABLE_INFO,
            CategoryOptionComboCategoryOptionLinkTableInfo.Columns.CATEGORY_OPTION_COMBO,
            CategoryOptionComboCategoryOptionLinkTableInfo.Columns.CATEGORY_OPTION,
        )
        val sectionSqlBuilder = SQLStatementBuilderImpl(CategoryOptionComboCategoryOptionLinkTableInfo.TABLE_INFO)
        val query = sectionSqlBuilder.selectChildrenWithLinkTable(
            projection,
            categoryOptionComboUid,
            null,
        )
        return selectRawQuery(query)
    }

    override suspend fun getForCategory(categoryUid: String): List<CategoryOption> {
        val projection = LinkTableChildProjection(
            CategoryOptionTableInfo.TABLE_INFO,
            CategoryCategoryOptionLinkTableInfo.Columns.CATEGORY,
            CategoryCategoryOptionLinkTableInfo.Columns.CATEGORY_OPTION,
        )
        val sectionSqlBuilder = SQLStatementBuilderImpl(CategoryCategoryOptionLinkTableInfo.TABLE_INFO)
        val query = sectionSqlBuilder.selectChildrenWithLinkTable(
            projection,
            categoryUid,
            null,
        )
        return selectRawQuery(query)
    }
}

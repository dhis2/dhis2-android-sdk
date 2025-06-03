/*
 *  Copyright (c) 2004-2025, University of Oslo
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

package org.hisp.dhis.android.persistence.category

import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.category.CategoryOptionOrganisationUnitLink
import org.hisp.dhis.android.core.category.CategoryOptionOrganisationUnitLinkTableInfo
import org.hisp.dhis.android.persistence.common.querybuilders.SQLStatementBuilder
import org.hisp.dhis.android.persistence.common.querybuilders.SQLStatementBuilderImpl
import org.hisp.dhis.android.persistence.common.stores.ObjectStoreImpl

internal class CategoryOptionOrganisationUnitLinkStoreImpl(
    val dao: CategoryOptionOrganisationUnitLinkDao,
    override val builder: SQLStatementBuilder = SQLStatementBuilderImpl(
        CategoryOptionOrganisationUnitLinkTableInfo.TABLE_INFO.name(), false
    ),
) : ObjectStoreImpl<CategoryOptionOrganisationUnitLink, CategoryOptionOrganisationUnitLinkDB>(
    dao,
    CategoryOptionOrganisationUnitLink::toDB,
    builder,
) {
    suspend fun getLinksForCategoryOption(categoryOptionUid: String): List<CategoryOptionOrganisationUnitLink> {
        val whereClause = WhereClauseBuilder()
            .appendKeyStringValue(
                CategoryOptionOrganisationUnitLinkTableInfo.Columns.CATEGORY_OPTION,
                categoryOptionUid,
            ).build()
        val query = builder.selectWhere(whereClause)
        val dbEntities = dao.objectListRawQuery(query)
        return dbEntities.map { it.toDomain() }
    }

    suspend fun getLinksForOrganisationUnit(organisationUnitUid: String): List<CategoryOptionOrganisationUnitLink> {
        val whereClause = WhereClauseBuilder()
            .appendKeyStringValue(
                CategoryOptionOrganisationUnitLinkTableInfo.Columns.ORGANISATION_UNIT,
                organisationUnitUid,
            ).build()
        val query = builder.selectWhere(whereClause)
        val dbEntities = dao.objectListRawQuery(query)
        return dbEntities.map { it.toDomain() }
    }
}

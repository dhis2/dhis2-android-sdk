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

package org.hisp.dhis.android.persistence.user

import org.hisp.dhis.android.core.arch.db.access.internal.AppDatabase
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.user.UserOrganisationUnitLink
import org.hisp.dhis.android.core.user.internal.UserOrganisationUnitLinkStore
import org.hisp.dhis.android.persistence.common.querybuilders.LinkSQLStatementBuilderImpl
import org.hisp.dhis.android.persistence.common.stores.LinkStoreImpl
import org.koin.core.annotation.Singleton

@Singleton
internal class UserOrganisationUnitLinkStoreImpl(
    private val appDatabase: AppDatabase,
) : UserOrganisationUnitLinkStore, LinkStoreImpl<UserOrganisationUnitLink, UserOrganisationUnitDB>(
    appDatabase.userOrganisationUnitDao(),
    UserOrganisationUnitLink::toDB,
    LinkSQLStatementBuilderImpl(
        UserOrganisationUnitTableInfo.TABLE_INFO,
        UserOrganisationUnitTableInfo.Columns.ORGANISATION_UNIT_SCOPE,
    ),
) {

    @Throws(RuntimeException::class)
    override suspend fun queryRootCaptureOrganisationUnitUids(): List<String> {
        return selectStringColumnsWhereClause(
            UserOrganisationUnitTableInfo.Columns.ORGANISATION_UNIT,
            WhereClauseBuilder()
                .appendKeyNumberValue(UserOrganisationUnitTableInfo.Columns.ROOT, 1)
                .appendKeyStringValue(
                    UserOrganisationUnitTableInfo.Columns.ORGANISATION_UNIT_SCOPE,
                    OrganisationUnit.Scope.SCOPE_DATA_CAPTURE,
                ).build(),
        )
    }

    override suspend fun queryOrganisationUnitUidsByScope(scope: OrganisationUnit.Scope): List<String> {
        return selectStringColumnsWhereClause(
            UserOrganisationUnitTableInfo.Columns.ORGANISATION_UNIT,
            WhereClauseBuilder()
                .appendKeyStringValue(
                    UserOrganisationUnitTableInfo.Columns.ORGANISATION_UNIT_SCOPE,
                    scope.name,
                ).build(),
        )
    }

    override suspend fun queryAssignedOrganisationUnitUidsByScope(scope: OrganisationUnit.Scope): List<String> {
        return selectStringColumnsWhereClause(
            UserOrganisationUnitTableInfo.Columns.ORGANISATION_UNIT,
            WhereClauseBuilder()
                .appendKeyNumberValue(UserOrganisationUnitTableInfo.Columns.USER_ASSIGNED, 1)
                .appendKeyStringValue(
                    UserOrganisationUnitTableInfo.Columns.ORGANISATION_UNIT_SCOPE,
                    scope.name,
                ).build(),
        )
    }

    override suspend fun isCaptureScope(organisationUnit: String): Boolean {
        val whereClause = WhereClauseBuilder()
            .appendKeyStringValue(UserOrganisationUnitTableInfo.Columns.ORGANISATION_UNIT, organisationUnit)
            .appendKeyStringValue(
                UserOrganisationUnitTableInfo.Columns.ORGANISATION_UNIT_SCOPE,
                OrganisationUnit.Scope.SCOPE_DATA_CAPTURE,
            )
            .build()

        return countWhere(whereClause) == 1
    }
}

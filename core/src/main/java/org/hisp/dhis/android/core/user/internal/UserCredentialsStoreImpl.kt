/*
 *  Copyright (c) 2004-2022, University of Oslo
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
package org.hisp.dhis.android.core.user.internal

import android.database.Cursor
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.SQLStatementBuilderImpl
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.WhereStatementBinder
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStoreImpl
import org.hisp.dhis.android.core.arch.helpers.UidsHelper.getUidOrNull
import org.hisp.dhis.android.core.user.UserCredentials
import org.hisp.dhis.android.core.user.UserCredentialsTableInfo

internal class UserCredentialsStoreImpl private constructor(
    databaseAdapter: DatabaseAdapter,
    statementBuilder: SQLStatementBuilderImpl
) : ObjectWithoutUidStoreImpl<UserCredentials>(
    databaseAdapter,
    statementBuilder,
    BINDER,
    WHERE_UPDATE_BINDER,
    DELETE_UPDATE_BINDER,
    { cursor: Cursor -> UserCredentials.create(cursor) }), UserCredentialsStore {

    override fun getForUser(userUid: String): UserCredentials? {
        val whereClause = WhereClauseBuilder()
            .appendKeyStringValue(UserCredentialsTableInfo.Columns.USER, userUid)
            .build()
        return selectOneWhere(whereClause)
    }

    companion object {
        private val BINDER = StatementBinder { o: UserCredentials, w: StatementWrapper ->
            w.bind(1, o.username())
            w.bind(2, getUidOrNull(o.user()))
        }

        private val WHERE_UPDATE_BINDER = WhereStatementBinder { o: UserCredentials, w: StatementWrapper ->
            w.bind(3, o.username())
        }

        private val DELETE_UPDATE_BINDER = WhereStatementBinder { o: UserCredentials, w: StatementWrapper ->
            w.bind(1, o.username())
        }

        @JvmStatic
        fun create(databaseAdapter: DatabaseAdapter): UserCredentialsStore {
            val statementBuilder = SQLStatementBuilderImpl(UserCredentialsTableInfo.TABLE_INFO)
            return UserCredentialsStoreImpl(databaseAdapter, statementBuilder)
        }
    }
}
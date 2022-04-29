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
package org.hisp.dhis.android.core.sms.data.localdbrepository.internal

import android.database.Cursor
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.SQLStatementBuilder
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.SQLStatementBuilderImpl
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.WhereStatementBinder
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStoreImpl
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.datastore.KeyValuePair

@Suppress("MagicNumber")
internal class SMSConfigStoreImpl private constructor(
    databaseAdapter: DatabaseAdapter,
    builder: SQLStatementBuilder,
    binder: StatementBinder<KeyValuePair>,
    whereUpdateBinder: WhereStatementBinder<KeyValuePair>,
    whereDeleteBinder: WhereStatementBinder<KeyValuePair>,
    objectFactory: (Cursor) -> KeyValuePair
) : ObjectWithoutUidStoreImpl<KeyValuePair>(
    databaseAdapter,
    builder,
    binder,
    whereUpdateBinder,
    whereDeleteBinder,
    objectFactory
),
    SMSConfigStore {

    override fun get(key: SMSConfigKey): String? {
        val whereClause = WhereClauseBuilder()
            .appendKeyStringValue(SMSConfigTableInfo.Columns.KEY, key.name)
            .build()

        return selectOneWhere(whereClause)?.value()
    }

    override fun set(key: SMSConfigKey, value: String): HandleAction {
        val keyValuePair = KeyValuePair.builder()
            .key(key.name)
            .value(value)
            .build()

        return updateOrInsertWhere(keyValuePair)
    }

    override fun delete(key: SMSConfigKey) {
        val whereClause = WhereClauseBuilder()
            .appendKeyStringValue(SMSConfigTableInfo.Columns.KEY, key.name)
            .build()

        return deleteWhereIfExists(whereClause)
    }

    companion object {
        private val BINDER: StatementBinder<KeyValuePair> = StatementBinder { o, w ->
            w.bind(1, o.key())
            w.bind(2, o.value())
        }

        private val WHERE_UPDATE_BINDER = WhereStatementBinder<KeyValuePair> { o: KeyValuePair, w: StatementWrapper ->
            w.bind(3, o.key())
        }

        private val WHERE_DELETE_BINDER = WhereStatementBinder<KeyValuePair> { o: KeyValuePair, w: StatementWrapper ->
            w.bind(1, o.key())
        }

        @JvmStatic
        fun create(databaseAdapter: DatabaseAdapter): SMSConfigStore {
            val statementBuilder: SQLStatementBuilder = SQLStatementBuilderImpl(
                SMSConfigTableInfo.TABLE_INFO.name(), SMSConfigTableInfo.TABLE_INFO.columns().all(),
                SMSConfigTableInfo.TABLE_INFO.columns().whereUpdate()
            )
            return SMSConfigStoreImpl(
                databaseAdapter, statementBuilder, BINDER, WHERE_UPDATE_BINDER,
                WHERE_DELETE_BINDER
            ) { cursor -> KeyValuePair.create(cursor) }
        }
    }
}

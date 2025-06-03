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
package org.hisp.dhis.android.core.arch.db.stores.internal

import android.database.Cursor
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.SQLStatementBuilder
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.SQLStatementBuilderImpl
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper
import org.hisp.dhis.android.core.arch.db.tableinfos.TableInfo
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.common.DataColumns
import org.hisp.dhis.android.core.common.DeletableDataColumns
import org.hisp.dhis.android.core.common.DeletableDataObject
import org.hisp.dhis.android.core.common.IdentifiableColumns
import org.hisp.dhis.android.core.common.ObjectWithUidInterface
import org.hisp.dhis.android.core.common.State

internal open class IdentifiableDeletableDataObjectStoreImpl<O>(
    databaseAdapter: DatabaseAdapter,
    builder: SQLStatementBuilder,
    binder: StatementBinder<O>,
    objectFactory: (Cursor) -> O,
) : IdentifiableDataObjectStoreImpl<O>(databaseAdapter, builder, binder, objectFactory),
    IdentifiableDeletableDataObjectStore<O> where O : ObjectWithUidInterface, O : DeletableDataObject {

    constructor(
        databaseAdapter: DatabaseAdapter,
        tableInfo: TableInfo,
        binder: StatementBinder<O>,
        objectFactory: (Cursor) -> O,
    ) : this(
        databaseAdapter,
        SQLStatementBuilderImpl(tableInfo),
        binder,
        objectFactory,
    )

    private var setDeletedStatement: StatementWrapper? = null
    private var adapterHashCode: Int? = null

    private fun compileStatements() {
        resetStatementsIfDbChanged()
        if (setDeletedStatement == null) {
            val whereUid = " WHERE " + IdentifiableColumns.UID + " =?"
            val setDeleted = "UPDATE " + tableName + " SET " +
                DeletableDataColumns.DELETED + " = 1" + whereUid
            setDeletedStatement = databaseAdapter.compileStatement(setDeleted)
        }
    }

    private fun hasAdapterChanged(): Boolean {
        val oldCode = adapterHashCode
        adapterHashCode = databaseAdapter.hashCode()
        return oldCode != null && databaseAdapter.hashCode() != oldCode
    }

    private fun resetStatementsIfDbChanged() {
        if (hasAdapterChanged()) {
            setDeletedStatement!!.close()
            setDeletedStatement = null
        }
    }

    override suspend fun setSyncStateOrDelete(uid: String, state: State): HandleAction {
        var deleted = false
        if (state == State.SYNCED) {
            val whereClause = WhereClauseBuilder()
                .appendKeyStringValue(IdentifiableColumns.UID, uid)
                .appendKeyNumberValue(DeletableDataColumns.DELETED, 1)
                .appendKeyStringValue(DataColumns.SYNC_STATE, State.UPLOADING)
                .build()
            deleted = deleteWhere(whereClause)
        }
        return if (deleted) {
            HandleAction.Delete
        } else {
            if (setSyncStateIfUploading(uid, state) == 0) HandleAction.NoAction else HandleAction.Update
        }
    }

    override suspend fun setDeleted(uid: String): Int {
        compileStatements()
        setDeletedStatement!!.bind(1, uid)
        val updatedRow = databaseAdapter.executeUpdateDelete(setDeletedStatement)
        setDeletedStatement!!.clearBindings()
        return updatedRow
    }

    override suspend fun selectSyncStateWhere(where: String): List<State> {
        val statesStr = selectStringColumnsWhereClause(DataColumns.SYNC_STATE, where)
        return statesStr.map { State.valueOf(it) }
    }
}

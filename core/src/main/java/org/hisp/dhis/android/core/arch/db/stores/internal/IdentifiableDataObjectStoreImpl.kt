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
package org.hisp.dhis.android.core.arch.db.stores.internal

import android.content.ContentValues
import android.database.Cursor
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.SQLStatementBuilder
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper
import org.hisp.dhis.android.core.arch.helpers.internal.EnumHelper
import org.hisp.dhis.android.core.common.*

internal open class IdentifiableDataObjectStoreImpl<O>(
    databaseAdapter: DatabaseAdapter,
    builder: SQLStatementBuilder,
    binder: StatementBinder<O>,
    objectFactory: (Cursor) -> O
) : IdentifiableObjectStoreImpl<O>(databaseAdapter, builder, binder, objectFactory),
    IdentifiableDataObjectStore<O> where O : ObjectWithUidInterface, O : DataObject {

    private var selectStateQuery: String? = null
    private var existsQuery: String? = null
    private var setStateStatement: StatementWrapper? = null
    private var setStateIfUploadingStatement: StatementWrapper? = null

    val tableName: String = builder.tableName
    private var adapterHashCode: Int? = null

    companion object {
        private const val EQ = " = "
    }

    private fun compileStatements() {
        resetStatementsIfDbChanged()
        if (setStateStatement == null) {
            val whereUid = " WHERE " + IdentifiableColumns.UID + " =?"
            val setState = "UPDATE " + tableName + " SET " +
                DataColumns.SYNC_STATE + " =?" + whereUid
            setStateStatement = databaseAdapter.compileStatement(setState)

            val setStateIfUploading = setState + " AND " + DataColumns.SYNC_STATE + EQ + "'" + State.UPLOADING + "'"
            setStateIfUploadingStatement = databaseAdapter.compileStatement(setStateIfUploading)

            selectStateQuery = "SELECT " + DataColumns.SYNC_STATE + " FROM " + tableName + whereUid
            existsQuery = "SELECT 1 FROM $tableName$whereUid"
        }
    }

    private fun hasAdapterChanged(): Boolean {
        val oldCode = adapterHashCode
        adapterHashCode = databaseAdapter.hashCode()
        return oldCode != null && databaseAdapter.hashCode() != oldCode
    }

    private fun resetStatementsIfDbChanged() {
        if (hasAdapterChanged()) {
            setStateStatement!!.close()
            setStateIfUploadingStatement!!.close()
            setStateStatement = null
            setStateIfUploadingStatement = null
        }
    }

    override fun setSyncState(uid: String, state: State): Int {
        compileStatements()
        setStateStatement!!.bind(1, state)

        // bind the where argument
        setStateStatement!!.bind(2, uid)
        val updatedRow = databaseAdapter.executeUpdateDelete(setStateStatement)
        setStateStatement!!.clearBindings()
        return updatedRow
    }

    override fun setSyncState(uids: List<String>, state: State): Int {
        val updates = ContentValues()
        updates.put(DataColumns.SYNC_STATE, state.toString())
        val whereClause = WhereClauseBuilder()
            .appendInKeyStringValues(IdentifiableColumns.UID, uids)
            .build()
        return databaseAdapter.update(tableName, updates, whereClause, null)
    }

    override fun setSyncStateIfUploading(uid: String, state: State): Int {
        compileStatements()
        setStateIfUploadingStatement!!.bind(1, state)

        // bind the where argument
        setStateIfUploadingStatement!!.bind(2, uid)
        val affectedRows = databaseAdapter.executeUpdateDelete(setStateIfUploadingStatement)
        setStateIfUploadingStatement!!.clearBindings()
        return affectedRows
    }

    override fun getSyncState(uid: String): State? {
        compileStatements()
        val cursor = databaseAdapter.rawQuery(selectStateQuery, uid)
        var state: State? = null
        if (cursor.count > 0) {
            cursor.moveToFirst()
            state = if (cursor.getString(0) == null) null else State.valueOf(cursor.getString(0))
        }
        cursor.close()
        return state
    }

    override fun exists(uid: String): Boolean {
        compileStatements()
        val cursor = databaseAdapter.rawQuery(existsQuery, uid)
        val count = cursor.count
        cursor.close()
        return count > 0
    }

    override fun getUploadableSyncStatesIncludingError(): List<O> {
        val whereClause = WhereClauseBuilder()
            .appendInKeyStringValues(
                DataColumns.SYNC_STATE,
                EnumHelper.asStringList(State.uploadableStatesIncludingError().toList())
            ).build()

        return selectWhere(whereClause)
    }
}

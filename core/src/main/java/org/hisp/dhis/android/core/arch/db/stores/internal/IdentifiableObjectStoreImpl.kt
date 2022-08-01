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

import android.database.Cursor
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.SQLStatementBuilder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper
import org.hisp.dhis.android.core.common.CoreObject
import org.hisp.dhis.android.core.common.IdentifiableColumns.UID
import org.hisp.dhis.android.core.common.ObjectWithUidInterface

@Suppress("TooManyFunctions")
internal open class IdentifiableObjectStoreImpl<O>(
    databaseAdapter: DatabaseAdapter,
    builder: SQLStatementBuilder,
    binder: StatementBinder<O>,
    objectFactory: (Cursor) -> O
) : ObjectStoreImpl<O>(databaseAdapter, builder, binder, objectFactory),
    IdentifiableObjectStore<O> where O : CoreObject, O : ObjectWithUidInterface {

    private var updateStatement: StatementWrapper? = null
    private var deleteStatement: StatementWrapper? = null
    private var adapterHashCode: Int? = null

    @Throws(RuntimeException::class)
    override fun insert(o: O): Long {
        CollectionsHelper.isNull(o)
        CollectionsHelper.isNull(o.uid())
        return super.insert(o)
    }

    @Throws(RuntimeException::class)
    override fun delete(uid: String) {
        CollectionsHelper.isNull(uid)
        compileStatements()
        deleteStatement!!.bind(1, uid)
        executeUpdateDelete(deleteStatement!!)
    }

    private fun compileStatements() {
        resetStatementsIfDbChanged()
        if (deleteStatement == null) {
            deleteStatement = databaseAdapter.compileStatement(builder.deleteById())
            updateStatement = databaseAdapter.compileStatement(builder.update())
        }
    }

    private fun hasAdapterChanged(): Boolean {
        val oldCode = adapterHashCode
        adapterHashCode = databaseAdapter.hashCode()
        return oldCode != null && databaseAdapter.hashCode() != oldCode
    }

    private fun resetStatementsIfDbChanged() {
        if (hasAdapterChanged()) {
            updateStatement!!.close()
            deleteStatement!!.close()
            updateStatement = null
            deleteStatement = null
        }
    }

    @Throws(RuntimeException::class)
    @Suppress("TooGenericExceptionCaught")
    override fun deleteIfExists(uid: String) {
        try {
            delete(uid)
        } catch (e: RuntimeException) {
            if (e.message != "No rows affected") {
                throw e
            }
        }
    }

    @Throws(RuntimeException::class)
    override fun update(o: O) {
        CollectionsHelper.isNull(o)
        compileStatements()
        binder.bindToStatement(o, updateStatement!!)
        updateStatement!!.bind(builder.columns.size + 1, o.uid())
        executeUpdateDelete(updateStatement!!)
    }

    @Throws(RuntimeException::class)
    @Suppress("TooGenericExceptionCaught")
    @Synchronized
    override fun updateOrInsert(o: O): HandleAction {
        return try {
            update(o)
            HandleAction.Update
        } catch (e: Exception) {
            insert(o)
            HandleAction.Insert
        }
    }

    @Throws(RuntimeException::class)
    override fun selectUids(): List<String> {
        val cursor = databaseAdapter.rawQuery(builder.selectUids())
        return mapStringColumnSetFromCursor(cursor)
    }

    @Throws(RuntimeException::class)
    override fun selectUidsWhere(whereClause: String): List<String> {
        val cursor = databaseAdapter.rawQuery(builder.selectUidsWhere(whereClause))
        return mapStringColumnSetFromCursor(cursor)
    }

    @Throws(RuntimeException::class)
    override fun selectUidsWhere(whereClause: String, orderByClause: String): List<String> {
        val cursor = databaseAdapter.rawQuery(builder.selectUidsWhere(whereClause, orderByClause))
        return mapStringColumnSetFromCursor(cursor)
    }

    @Throws(RuntimeException::class)
    override fun selectByUid(uid: String): O? {
        val cursor = databaseAdapter.rawQuery(builder.selectByUid(), uid)
        return mapObjectFromCursor(cursor)
    }

    @Throws(RuntimeException::class)
    override fun selectByUids(uid: List<String>): List<O> {
        return selectWhere("$UID IN (${uid.joinToString(",") { "'$it'" }})")
    }

    private fun mapObjectFromCursor(cursor: Cursor): O? {
        cursor.use { c ->
            if (c.count > 0) {
                c.moveToFirst()
                return objectFactory(c)
            }
        }
        return null
    }
}

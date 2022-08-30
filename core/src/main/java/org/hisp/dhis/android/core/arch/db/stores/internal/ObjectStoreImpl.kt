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
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper
import org.hisp.dhis.android.core.common.CoreColumns
import org.hisp.dhis.android.core.common.CoreObject

@Suppress("TooManyFunctions")
internal open class ObjectStoreImpl<O : CoreObject> internal constructor(
    databaseAdapter: DatabaseAdapter,
    override val builder: SQLStatementBuilder,
    protected val binder: StatementBinder<O>,
    objectFactory: (Cursor) -> O
) : ReadableStoreImpl<O>(databaseAdapter, builder, objectFactory), ObjectStore<O> {

    private var insertStatement: StatementWrapper? = null
    private var adapterHashCode: Int? = null

    @Throws(RuntimeException::class)
    @Suppress("TooGenericExceptionThrown")
    override fun insert(o: O): Long {
        CollectionsHelper.isNull(o)
        compileStatements()
        binder.bindToStatement(o, insertStatement!!)
        val insertedRowId = databaseAdapter.executeInsert(insertStatement)
        insertStatement!!.clearBindings()
        if (insertedRowId == -1L) {
            throw RuntimeException("Nothing was inserted.")
        }
        return insertedRowId
    }

    @Throws(RuntimeException::class)
    override fun insert(objects: Collection<O>) {
        for (m in objects) {
            insert(m)
        }
    }

    private fun compileStatements() {
        resetStatementsIfDbChanged()
        if (insertStatement == null) {
            insertStatement = databaseAdapter.compileStatement(builder.insert())
        }
    }

    private fun resetStatementsIfDbChanged() {
        if (hasAdapterChanged()) {
            insertStatement!!.close()
            insertStatement = null
        }
    }

    private fun hasAdapterChanged(): Boolean {
        val oldCode = adapterHashCode
        adapterHashCode = databaseAdapter.hashCode()
        return oldCode != null && databaseAdapter.hashCode() != oldCode
    }

    override fun selectStringColumnsWhereClause(column: String, clause: String): List<String> {
        val cursor = databaseAdapter.rawQuery(builder.selectColumnWhere(column, clause))
        return mapStringColumnSetFromCursor(cursor)
    }

    override fun delete(): Int {
        return databaseAdapter.delete(builder.tableName)
    }

    @Throws(RuntimeException::class)
    @Suppress("TooGenericExceptionThrown")
    fun executeUpdateDelete(statement: StatementWrapper) {
        val numberOfAffectedRows = databaseAdapter.executeUpdateDelete(statement)
        statement.clearBindings()
        if (numberOfAffectedRows == 0) {
            throw RuntimeException("No rows affected")
        } else if (numberOfAffectedRows > 1) {
            throw RuntimeException("Unexpected number of affected rows: $numberOfAffectedRows")
        }
    }

    override fun deleteById(o: O): Boolean {
        return deleteWhere(CoreColumns.ID + "='" + o.id() + "';")
    }

    protected fun popOneWhere(whereClause: String): O? {
        val m = selectOneWhere(whereClause)
        if (m != null) {
            deleteById(m)
        }
        return m
    }

    override fun deleteWhere(clause: String): Boolean {
        return databaseAdapter.delete(builder.tableName, clause, null) > 0
    }

    override fun updateWhere(updates: ContentValues, whereClause: String): Int {
        return databaseAdapter.update(builder.tableName, updates, whereClause, null)
    }

    @Throws(RuntimeException::class)
    @Suppress("TooGenericExceptionCaught")
    override fun deleteWhereIfExists(whereClause: String) {
        try {
            deleteWhere(whereClause)
        } catch (e: RuntimeException) {
            if (e.message != "No rows affected") {
                throw e
            }
        }
    }

    override val isReady: Boolean
        get() = databaseAdapter.isReady
}

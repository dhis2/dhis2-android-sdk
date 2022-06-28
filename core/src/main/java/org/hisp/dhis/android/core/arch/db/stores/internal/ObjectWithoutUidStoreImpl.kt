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
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.WhereStatementBinder
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper
import org.hisp.dhis.android.core.common.CoreObject

internal open class ObjectWithoutUidStoreImpl<O : CoreObject>(
    databaseAdapter: DatabaseAdapter,
    builder: SQLStatementBuilder,
    binder: StatementBinder<O>,
    private val whereUpdateBinder: WhereStatementBinder<O>,
    private val whereDeleteBinder: WhereStatementBinder<O>,
    objectFactory: (Cursor) -> O
) : ObjectStoreImpl<O>(databaseAdapter, builder, binder, objectFactory), ObjectWithoutUidStore<O> {
    private var updateWhereStatement: StatementWrapper? = null
    private var deleteWhereStatement: StatementWrapper? = null
    private var adapterHashCode: Int? = null

    @Throws(RuntimeException::class)
    override fun updateWhere(o: O) {
        CollectionsHelper.isNull(o)
        compileStatements()
        binder.bindToStatement(o, updateWhereStatement!!)
        whereUpdateBinder.bindWhereStatement(o, updateWhereStatement!!)
        executeUpdateDelete(updateWhereStatement!!)
    }

    private fun compileStatements() {
        resetStatementsIfDbChanged()
        if (updateWhereStatement == null) {
            updateWhereStatement = databaseAdapter.compileStatement(builder.updateWhere())
            deleteWhereStatement = databaseAdapter.compileStatement(builder.deleteWhere())
        }
    }

    private fun hasAdapterChanged(): Boolean {
        val oldCode = adapterHashCode
        adapterHashCode = databaseAdapter.hashCode()
        return oldCode != null && databaseAdapter.hashCode() != oldCode
    }

    private fun resetStatementsIfDbChanged() {
        if (hasAdapterChanged()) {
            updateWhereStatement!!.close()
            deleteWhereStatement!!.close()
            updateWhereStatement = null
            deleteWhereStatement = null
        }
    }

    @Throws(RuntimeException::class)
    override fun deleteWhere(o: O) {
        CollectionsHelper.isNull(o)
        compileStatements()
        whereDeleteBinder.bindWhereStatement(o, deleteWhereStatement!!)
        executeUpdateDelete(deleteWhereStatement!!)
    }

    @Throws(RuntimeException::class)
    @Suppress("TooGenericExceptionCaught")
    override fun deleteWhereIfExists(o: O) {
        try {
            deleteWhere(o)
        } catch (e: RuntimeException) {
            if (e.message != "No rows affected") {
                throw e
            }
        }
    }

    @Throws(RuntimeException::class)
    @Suppress("TooGenericExceptionCaught")
    @Synchronized
    override fun updateOrInsertWhere(o: O): HandleAction {
        return try {
            updateWhere(o)
            HandleAction.Update
        } catch (e: Exception) {
            insert(o)
            HandleAction.Insert
        }
    }
}

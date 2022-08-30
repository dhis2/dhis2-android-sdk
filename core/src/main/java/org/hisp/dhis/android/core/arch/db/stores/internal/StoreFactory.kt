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
import org.hisp.dhis.android.core.arch.db.cursors.internal.CursorExecutorImpl
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.SQLStatementBuilder
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.SQLStatementBuilderImpl
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.WhereStatementBinder
import org.hisp.dhis.android.core.arch.db.stores.projections.internal.LinkTableChildProjection
import org.hisp.dhis.android.core.arch.db.stores.projections.internal.SingleParentChildProjection
import org.hisp.dhis.android.core.arch.db.tableinfos.TableInfo
import org.hisp.dhis.android.core.common.CoreObject
import org.hisp.dhis.android.core.common.ObjectWithUidInterface

internal object StoreFactory {

    @JvmStatic
    fun <O> objectWithUidStore(
        databaseAdapter: DatabaseAdapter,
        tableInfo: TableInfo,
        binder: StatementBinder<O>,
        objectFactory: (Cursor) -> O
    ): IdentifiableObjectStore<O> where O : CoreObject, O : ObjectWithUidInterface {
        val statementBuilder: SQLStatementBuilder =
            SQLStatementBuilderImpl(tableInfo.name(), tableInfo.columns().all(), arrayOf())
        return IdentifiableObjectStoreImpl(databaseAdapter, statementBuilder, binder, objectFactory)
    }

    @JvmStatic
    fun <O : CoreObject> objectStore(
        databaseAdapter: DatabaseAdapter,
        tableInfo: TableInfo,
        binder: StatementBinder<O>,
        objectFactory: (Cursor) -> O
    ): ObjectStore<O> {
        val statementBuilder: SQLStatementBuilder =
            SQLStatementBuilderImpl(tableInfo.name(), tableInfo.columns().all(), arrayOf())
        return ObjectStoreImpl(databaseAdapter, statementBuilder, binder, objectFactory)
    }

    @Suppress("LongParameterList")
    @JvmStatic
    fun <O : CoreObject> objectWithoutUidStore(
        databaseAdapter: DatabaseAdapter,
        tableInfo: TableInfo,
        binder: StatementBinder<O>,
        whereUpdateBinder: WhereStatementBinder<O>,
        whereDeleteBinder: WhereStatementBinder<O>,
        objectFactory: (Cursor) -> O
    ): ObjectWithoutUidStore<O> {
        val statementBuilder: SQLStatementBuilder = SQLStatementBuilderImpl(
            tableInfo.name(), tableInfo.columns().all(),
            tableInfo.columns().whereUpdate()
        )
        return ObjectWithoutUidStoreImpl(
            databaseAdapter, statementBuilder, binder, whereUpdateBinder,
            whereDeleteBinder, objectFactory
        )
    }

    @JvmStatic
    fun <O : CoreObject> linkStore(
        databaseAdapter: DatabaseAdapter,
        tableInfo: TableInfo,
        masterColumn: String,
        binder: StatementBinder<O>,
        objectFactory: (Cursor) -> O
    ): LinkStore<O> {
        val statementBuilder: SQLStatementBuilder = SQLStatementBuilderImpl(
            tableInfo.name(), tableInfo.columns().all(),
            tableInfo.columns().whereUpdate()
        )
        return LinkStoreImpl(databaseAdapter, statementBuilder, masterColumn, binder, objectFactory)
    }

    @JvmStatic
    fun <P : ObjectWithUidInterface, C : ObjectWithUidInterface> linkChildStore(
        databaseAdapter: DatabaseAdapter,
        linkTableInfo: TableInfo,
        linkTableChildProjection: LinkTableChildProjection,
        childFactory: (Cursor) -> C
    ): LinkChildStore<P, C> {
        return LinkChildStoreImpl(
            linkTableChildProjection,
            databaseAdapter,
            SQLStatementBuilderImpl(linkTableInfo),
            CursorExecutorImpl(childFactory)
        )
    }

    @JvmStatic
    fun <P : ObjectWithUidInterface, C> singleParentChildStore(
        databaseAdapter: DatabaseAdapter,
        childProjection: SingleParentChildProjection,
        childFactory: (Cursor) -> C
    ): SingleParentChildStore<P, C> {
        return SingleParentChildStoreImpl(
            childProjection,
            databaseAdapter,
            SQLStatementBuilderImpl(childProjection.childTableInfo),
            CursorExecutorImpl(childFactory)
        )
    }

    @JvmStatic
    fun <P : ObjectWithUidInterface> objectWithUidChildStore(
        databaseAdapter: DatabaseAdapter,
        linkTableInfo: TableInfo,
        childProjection: LinkTableChildProjection
    ): ObjectWithUidChildStore<P> {
        return ObjectWithUidChildStoreImpl(
            childProjection,
            databaseAdapter,
            SQLStatementBuilderImpl(linkTableInfo)
        )
    }
}

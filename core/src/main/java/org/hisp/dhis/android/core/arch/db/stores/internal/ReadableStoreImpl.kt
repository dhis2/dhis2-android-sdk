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
import java.util.ArrayList
import java.util.HashMap
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.ReadOnlySQLStatementBuilder
import org.hisp.dhis.android.core.arch.db.sqlorder.internal.SQLOrderType
import org.hisp.dhis.android.core.common.CoreObject

@Suppress("TooManyFunctions")
internal open class ReadableStoreImpl<O : CoreObject>(
    protected val databaseAdapter: DatabaseAdapter,
    protected open val builder: ReadOnlySQLStatementBuilder,
    val objectFactory: (Cursor) -> O
) : ReadableStore<O> {

    override fun selectAll(): List<O> {
        val query = builder.selectAll()
        return selectRawQuery(query)
    }

    override fun selectWhere(whereClause: String): List<O> {
        val query = builder.selectWhere(whereClause)
        return selectRawQuery(query)
    }

    override fun selectWhere(filterWhereClause: String, orderByClause: String): List<O> {
        val query = builder.selectWhere(filterWhereClause, orderByClause)
        return selectRawQuery(query)
    }

    override fun selectWhere(filterWhereClause: String, orderByClause: String, limit: Int): List<O> {
        val query = builder.selectWhere(filterWhereClause, orderByClause, limit)
        return selectRawQuery(query)
    }

    override fun selectOneOrderedBy(orderingColumName: String, orderingType: SQLOrderType): O? {
        val cursor = databaseAdapter.rawQuery(builder.selectOneOrderedBy(orderingColumName, orderingType))
        return getFirstFromCursor(cursor)
    }

    override fun selectRawQuery(sqlRawQuery: String): List<O> {
        val cursor = databaseAdapter.rawQuery(sqlRawQuery)
        val list: MutableList<O> = ArrayList()
        addObjectsToCollection(cursor, list)
        return list
    }

    override fun selectOneWhere(whereClause: String): O? {
        val cursor = databaseAdapter.rawQuery(builder.selectWhere(whereClause, 1))
        return getFirstFromCursor(cursor)
    }

    override fun selectFirst(): O? {
        val cursor = databaseAdapter.rawQuery(builder.selectAll())
        return getFirstFromCursor(cursor)
    }

    private fun getFirstFromCursor(cursor: Cursor): O? {
        return cursor.use { c ->
            if (c.count >= 1) {
                c.moveToFirst()
                objectFactory(c)
            } else {
                null
            }
        }
    }

    override fun count(): Int {
        return processCount(databaseAdapter.rawQuery(builder.count()))
    }

    override fun countWhere(whereClause: String): Int {
        return processCount(databaseAdapter.rawQuery(builder.countWhere(whereClause)))
    }

    override fun groupAndGetCountBy(column: String): Map<String, Int> {
        val result: MutableMap<String, Int> = HashMap()
        databaseAdapter.rawQuery(builder.countAndGroupBy(column)).use { cursor ->
            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    val columnValue = cursor.getString(0)
                    val countValue = cursor.getInt(1)
                    result[columnValue] = countValue
                } while (cursor.moveToNext())
            }
        }
        return result
    }

    protected fun processCount(cursor: Cursor): Int {
        return cursor.use { c ->
            c.moveToFirst()
            c.getInt(0)
        }
    }

    protected fun addObjectsToCollection(cursor: Cursor, collection: MutableCollection<O>) {
        cursor.use { c ->
            if (c.count > 0) {
                c.moveToFirst()
                do {
                    collection.add(objectFactory(c))
                } while (c.moveToNext())
            }
        }
    }

    fun mapStringColumnSetFromCursor(cursor: Cursor): List<String> {
        val columns: MutableList<String> = ArrayList(cursor.count)
        cursor.use { c ->
            if (c.count > 0) {
                c.moveToFirst()
                do {
                    columns.add(c.getString(0))
                } while (c.moveToNext())
            }
        }
        return columns
    }
}

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

package org.hisp.dhis.android.persistence.common.querybuilders

import androidx.room.RoomRawQuery
import org.hisp.dhis.android.core.arch.db.sqlorder.internal.SQLOrderType
import org.hisp.dhis.android.core.arch.db.tableinfos.TableInfo

@Suppress("TooManyFunctions")
internal open class ReadOnlySQLStatementBuilderImpl(
    private val tableInfo: TableInfo,
) : ReadOnlySQLStatementBuilder {
    @get:JvmName("fetchTableName")
    val tableName = tableInfo.name()

    override fun selectWhere(whereClause: String): RoomRawQuery {
        return RoomRawQuery(
            SELECT + "*" + FROM + tableName + WHERE + whereClause + ";",
        )
    }

    override fun selectWhere(whereClause: String, limit: Int): RoomRawQuery {
        return selectWhere(whereClause + LIMIT + limit)
    }

    override fun selectWhere(whereClause: String, orderByClause: String?): RoomRawQuery {
        return selectWhere(whereClause + getOrderBy(orderByClause))
    }

    override fun selectWhere(whereClause: String, orderByClause: String?, limit: Int): RoomRawQuery {
        return selectWhere(whereClause + getOrderBy(orderByClause) + LIMIT + limit)
    }

    override fun selectWhere(whereClause: String, orderByClause: String?, limit: Int, offset: Int?): RoomRawQuery {
        return selectWhere(whereClause + getOrderBy(orderByClause) + LIMIT + limit + getOffset(offset))
    }

    override fun selectOneOrderedBy(
        orderingColumName: String,
        orderingType: SQLOrderType,
    ): RoomRawQuery {
        return RoomRawQuery(
            SELECT + "*" + FROM + tableName +
                ORDER_BY + orderingColumName + " " + orderingType.name +
                LIMIT + "1;",
        )
    }

    override fun selectAll(): RoomRawQuery {
        return RoomRawQuery(
            SELECT + "*" + FROM + tableName + ";",
        )
    }

    override fun count(): RoomRawQuery {
        return RoomRawQuery(
            SELECT + "COUNT(*)" + FROM + tableName + ";",
        )
    }

    override fun selectStringColumn(column: String, clause: String): RoomRawQuery {
        return RoomRawQuery(
            "SELECT $column FROM $tableName WHERE $clause;",
        )
    }

    override fun countWhere(whereClause: String): RoomRawQuery {
        return RoomRawQuery(
            SELECT + "COUNT(*)" + FROM + tableName + WHERE + whereClause + ";",
        )
    }

    override fun countAndGroupBy(column: String): RoomRawQuery {
        return RoomRawQuery(
            "SELECT $column AS key, COUNT(*) AS count FROM $tableName GROUP BY $column",
        )
    }

    override fun deleteTable(): RoomRawQuery {
        return RoomRawQuery("DELETE FROM $tableName;")
    }

    override fun deleteWhere(whereClause: String): RoomRawQuery {
        return RoomRawQuery("DELETE FROM $tableName WHERE $whereClause;")
    }

    override fun updateWhere(updates: Map<String, Any>, whereClause: String): RoomRawQuery {
        val setClause = updates.entries.joinToString(", ") { "${it.key} = ${formatValue(it.value)}" }
        return RoomRawQuery("UPDATE $tableName SET $setClause WHERE $whereClause")
    }

    private fun formatValue(value: Any): String = when (value) {
        is String -> "'${value.replace("'", "''")}'"
        is Boolean -> if (value) "1" else "0"
        else -> value.toString()
    }

    companion object {
        internal const val WHERE = " WHERE "
        internal const val LIMIT = " LIMIT "
        internal const val FROM = " FROM "
        internal const val SELECT = "SELECT "
        internal const val AND = " AND "
        internal const val ORDER_BY = " ORDER BY "
        internal const val OFFSET = " OFFSET "

        internal fun getOrderBy(orderByClause: String?): String {
            return orderByClause?.let { ORDER_BY + it } ?: ""
        }

        internal fun getOffset(offset: Int?): String {
            return offset?.let { OFFSET + it } ?: ""
        }

        internal fun getLimit(limit: Int): String {
            return LIMIT + limit
        }
    }
}

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
package org.hisp.dhis.android.core.arch.db.querybuilders.internal

import org.hisp.dhis.android.core.arch.db.sqlorder.internal.SQLOrderType
import org.hisp.dhis.android.core.arch.db.stores.projections.internal.LinkTableChildProjection
import org.hisp.dhis.android.core.arch.db.tableinfos.TableInfo
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper
import org.hisp.dhis.android.core.common.CoreColumns
import org.hisp.dhis.android.core.common.IdentifiableColumns

// TODO save TableInfo instead of separate files when architecture 1.0 is ready
@Suppress("SpreadOperator", "TooManyFunctions")
internal class SQLStatementBuilderImpl internal constructor(
    private val tableName: String,
    columns: Array<String>,
    updateWhereColumns: Array<String>,
    private val hasSortOrder: Boolean,
) : SQLStatementBuilder {
    private val columns = columns.clone()
    private val whereColumns = updateWhereColumns.clone()

    constructor(
        tableName: String,
        columns: Array<String>,
        updateWhereColumns: Array<String>,
    ) : this(tableName, columns, updateWhereColumns, false)

    constructor(tableInfo: TableInfo) : this(
        tableInfo.name(),
        tableInfo.columns().all().clone(),
        tableInfo.columns().whereUpdate().clone(),
        tableInfo.hasSortOrder(),
    )

    private fun commaSeparatedColumns(): String {
        return CollectionsHelper.commaAndSpaceSeparatedArrayValues(columns)
    }

    private fun commaSeparatedInterrogationMarks(): String {
        val array = Array(columns.size) { "?" }
        return CollectionsHelper.commaAndSpaceSeparatedArrayValues(array)
    }

    private fun commaSeparatedColumnEqualInterrogationMark(vararg cols: String): String {
        val array = cols.map { "$it=?" }.toTypedArray()
        return CollectionsHelper.commaAndSpaceSeparatedArrayValues(array)
    }

    private fun andSeparatedColumnEqualInterrogationMark(vararg cols: String): String {
        return commaSeparatedColumnEqualInterrogationMark(*cols)
            .replace(",", " AND")
    }

    override fun getTableName(): String {
        return tableName
    }

    override fun getColumns(): Array<String> {
        return columns.clone()
    }

    override fun insert(): String {
        return "INSERT INTO " + tableName + " (" + commaSeparatedColumns() + ") " +
            "VALUES (" + commaSeparatedInterrogationMarks() + ");"
    }

    override fun deleteById(): String {
        return "DELETE" + FROM + tableName + WHERE + IdentifiableColumns.UID + "=?;"
    }

    override fun selectUids(): String {
        return SELECT + IdentifiableColumns.UID + FROM + tableName
    }

    override fun selectUidsWhere(whereClause: String): String {
        return SELECT + IdentifiableColumns.UID + FROM + tableName + WHERE + whereClause + ";"
    }

    override fun selectUidsWhere(whereClause: String, orderByClause: String?): String {
        return SELECT + IdentifiableColumns.UID + FROM + tableName + WHERE + whereClause +
            getOrderBy(orderByClause) + ";"
    }

    override fun selectColumnWhere(column: String, whereClause: String): String {
        return SELECT + column + FROM + tableName + WHERE + whereClause + ";"
    }

    override fun selectOneOrderedBy(
        orderingColumName: String,
        orderingType: SQLOrderType,
    ): String {
        return SELECT + "*" +
            FROM + tableName +
            ORDER_BY + orderingColumName + " " + orderingType.name +
            LIMIT + "1;"
    }

    override fun selectChildrenWithLinkTable(
        projection: LinkTableChildProjection,
        parentUid: String,
        whereClause: String?,
    ): String {
        val whereClauseStr = if (whereClause == null) "" else AND + whereClause

        return SELECT + "c.*" + FROM + tableName + " AS l, " +
            projection.childTableInfo.name() + " AS c" +
            WHERE + "l." + projection.childColumn + "=" + "c." + IdentifiableColumns.UID +
            AND + "l." + projection.parentColumn + "='" + parentUid + "'" +
            whereClauseStr +
            orderBySortOrderClause() + ";"
    }

    private fun orderBySortOrderClause(): String {
        return if (hasSortOrder) ORDER_BY + TableInfo.SORT_ORDER else ""
    }

    override fun selectByUid(): String {
        return selectWhere(andSeparatedColumnEqualInterrogationMark(IdentifiableColumns.UID))
    }

    override fun selectDistinct(column: String): String {
        return SELECT + "DISTINCT " + column + FROM + tableName
    }

    override fun selectWhere(whereClause: String): String {
        return "$SELECT*$FROM$tableName$WHERE$whereClause;"
    }

    override fun selectWhere(whereClause: String, limit: Int): String {
        return selectWhere(whereClause + getLimit(limit))
    }

    override fun selectWhere(whereClause: String, orderByClause: String?): String {
        return selectWhere(whereClause + getOrderBy(orderByClause))
    }

    override fun selectWhere(whereClause: String, orderByClause: String?, limit: Int): String {
        return selectWhere(whereClause + getOrderBy(orderByClause) + getLimit(limit))
    }

    override fun selectWhere(whereClause: String, orderByClause: String?, limit: Int, offset: Int?): String {
        return selectWhere(whereClause + getOrderBy(orderByClause) + getLimit(limit) + getOffset(offset))
    }

    override fun selectAll(): String {
        return SELECT + "*" + FROM + tableName
    }

    override fun count(): String {
        return SELECT + "COUNT(*)" + FROM + tableName + ";"
    }

    override fun countWhere(whereClause: String): String {
        return SELECT + "COUNT(*)" + FROM + tableName + WHERE + whereClause + ";"
    }

    override fun countAndGroupBy(column: String): String {
        return "$SELECT$column , COUNT(*)$FROM$tableName GROUP BY $column;"
    }

    override fun update(): String {
        return "UPDATE " + tableName + " SET " + commaSeparatedColumnEqualInterrogationMark(*columns) +
            WHERE + IdentifiableColumns.UID + "=?;"
    }

    override fun updateWhere(): String {
        // TODO refactor to only generate for object without uids store.
        val whereClause =
            if (whereColumns.isEmpty()) {
                CoreColumns.ID + " = -1"
            } else {
                andSeparatedColumnEqualInterrogationMark(
                    *whereColumns,
                )
            }
        return "UPDATE " + tableName + " SET " + commaSeparatedColumnEqualInterrogationMark(*columns) +
            WHERE + whereClause + ";"
    }

    override fun deleteWhere(): String {
        val whereClause =
            if (whereColumns.isEmpty()) {
                CoreColumns.ID + " = -1"
            } else {
                andSeparatedColumnEqualInterrogationMark(
                    *whereColumns,
                )
            }
        return "DELETE$FROM$tableName$WHERE$whereClause;"
    }

    companion object {
        private const val WHERE = " WHERE "
        private const val LIMIT = " LIMIT "
        private const val FROM = " FROM "
        private const val SELECT = "SELECT "
        private const val AND = " AND "
        private const val ORDER_BY = " ORDER BY "
        private const val OFFSET = " OFFSET "

        internal fun getOrderBy(orderByClause: String?): String {
            return orderByClause?.let { ORDER_BY + it } ?: ""
        }

        internal fun getLimit(limit: Int): String {
            return LIMIT + limit
        }

        internal fun getOffset(offset: Int?): String {
            return offset?.let { OFFSET + it } ?: ""
        }
    }
}

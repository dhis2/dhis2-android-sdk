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
import org.hisp.dhis.android.core.arch.db.stores.projections.internal.LinkTableChildProjection
import org.hisp.dhis.android.core.arch.db.tableinfos.TableInfo
import org.hisp.dhis.android.core.common.IdentifiableColumns

internal open class SQLStatementBuilderImpl(
    private val tableInfo: TableInfo,
) : ReadOnlySQLStatementBuilderImpl(tableInfo), SQLStatementBuilder {
    val hasSortOrder = tableInfo.hasSortOrder()

    override fun getTableName(): String {
        return tableName
    }

    override fun selectUids(): RoomRawQuery {
        return RoomRawQuery(
            SELECT + IdentifiableColumns.UID + FROM + tableName + ";",
        )
    }

    override fun selectUidsWhere(whereClause: String): RoomRawQuery {
        return RoomRawQuery(
            SELECT + IdentifiableColumns.UID + FROM + tableName + WHERE + whereClause + ";",
        )
    }

    override fun selectUidsWhere(whereClause: String, orderByClause: String?): RoomRawQuery {
        return RoomRawQuery(
            SELECT + IdentifiableColumns.UID + FROM + tableName + WHERE + whereClause +
                getOrderBy(orderByClause) + ";",
        )
    }

    override fun selectColumnWhere(column: String, whereClause: String): RoomRawQuery {
        return RoomRawQuery(
            SELECT + column + FROM + tableName + WHERE + whereClause + ";",
        )
    }

    override fun selectChildrenWithLinkTable(
        projection: LinkTableChildProjection,
        parentUid: String,
        whereClause: String?,
    ): RoomRawQuery {
        val whereClauseStr = if (whereClause == null) "" else AND + whereClause

        return RoomRawQuery(
            SELECT + "c.*" + FROM + tableName + " AS l, " +
                projection.childTableInfo.name() + " AS c" +
                WHERE + "l." + projection.childColumn + "=" + "c." + IdentifiableColumns.UID +
                AND + "l." + projection.parentColumn + "='" + parentUid + "'" +
                whereClauseStr +
                orderBySortOrderClause() + ";",
        )
    }

    private fun orderBySortOrderClause(): String {
        return if (hasSortOrder) ORDER_BY + TableInfo.SORT_ORDER else ""
    }

    override fun selectByUid(uid: String): RoomRawQuery {
        return selectWhere(IdentifiableColumns.UID + " = '$uid'")
    }

    override fun selectDistinct(column: String): RoomRawQuery {
        return RoomRawQuery(
            SELECT + "DISTINCT " + column + FROM + tableName + ";",
        )
    }

    override fun deleteByUid(uid: String): RoomRawQuery {
        return RoomRawQuery(
            "DELETE" + FROM + tableName + WHERE + IdentifiableColumns.UID + " = '$uid' ;",
        )
    }
}

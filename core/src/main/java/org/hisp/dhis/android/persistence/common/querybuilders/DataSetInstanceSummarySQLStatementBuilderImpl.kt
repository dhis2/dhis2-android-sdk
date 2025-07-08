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
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.SQLStatementBuilderImpl.Companion.getLimit
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.SQLStatementBuilderImpl.Companion.getOffset
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.SQLStatementBuilderImpl.Companion.getOrderBy
import org.hisp.dhis.android.core.arch.db.sqlorder.internal.SQLOrderType
import org.hisp.dhis.android.core.common.IdentifiableColumns
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.dataset.internal.DataSetInstanceSQLStatementBuilder
import org.hisp.dhis.android.persistence.dataset.DataSetTableInfo

internal class DataSetInstanceSummarySQLStatementBuilderImpl : DataSetInstanceSQLStatementBuilderImpl() {
    override fun selectWhere(whereClause: String): RoomRawQuery {
        val innerSelectClause = super.selectWhere(whereClause).sql
        return RoomRawQuery(wrapInnerClause(innerSelectClause))
    }

    override fun selectWhere(whereClause: String, limit: Int): RoomRawQuery {
        val innerSelectClause = super.selectWhere(whereClause).sql
        return RoomRawQuery(wrapInnerClause(innerSelectClause) + getLimit(limit))
    }

    override fun selectAll(): RoomRawQuery {
        val innerSelectClause = super.selectAll().sql
        return RoomRawQuery(wrapInnerClause(innerSelectClause))
    }

    override fun count(): RoomRawQuery {
        return RoomRawQuery("SELECT count(*) FROM (${selectAll()})")
    }

    override fun countWhere(whereClause: String): RoomRawQuery {
        return RoomRawQuery("SELECT count(*) FROM (${selectWhere(whereClause)})")
    }

    override fun selectWhere(whereClause: String, orderByClause: String?): RoomRawQuery {
        return RoomRawQuery(selectWhere(whereClause).sql + getOrderBy(orderByClause))
    }

    override fun selectWhere(whereClause: String, orderByClause: String?, limit: Int): RoomRawQuery {
        return RoomRawQuery(selectWhere(whereClause, orderByClause).sql + getLimit(limit))
    }

    override fun selectWhere(whereClause: String, orderByClause: String?, limit: Int, offset: Int?): RoomRawQuery {
        return RoomRawQuery(selectWhere(whereClause, orderByClause).sql + getLimit(limit) + getOffset(offset))
    }

    override fun selectOneOrderedBy(orderingColumnName: String, orderingType: SQLOrderType): RoomRawQuery {
        return selectWhere("1", "$orderingColumnName $orderingType", 1)
    }

    private fun wrapInnerClause(innerClause: String): String {
        return SELECT_CLAUSE +
            " FROM ($DATASET_LIST_CLAUSE) $DS_LIST_TABLE_ALIAS" +
            " LEFT JOIN ($innerClause) $DS_INSTANCE_ALIAS" +
            " ON " + dot(DS_LIST_TABLE_ALIAS, IdentifiableColumns.UID) + " = " +
            dot(DS_INSTANCE_ALIAS, DataSetInstanceSQLStatementBuilder.DATASET_UID_ALIAS) +
            " " + GROUP_BY_CLAUSE
    }

    companion object {
        private const val DATASETINSTANCE_COUNT_ALIAS = "dataSetInstanceCount"
        private const val DS_LIST_TABLE_ALIAS = "dslist"
        private const val DS_INSTANCE_ALIAS = "dsinstance"
        private const val STATE = DataSetInstanceSQLStatementBuilder.STATE_ALIAS

        private val SELECT_STATE_ORDERING = " MAX(CASE " +
            "WHEN $STATE IN ('${State.SYNCED}','${State.SYNCED_VIA_SMS}') THEN 1 " +
            "WHEN $STATE = '${State.SENT_VIA_SMS}' THEN 2 " +
            "WHEN $STATE IN ('${State.TO_POST}','${State.TO_UPDATE}') THEN 3 " +
            "WHEN $STATE = '${State.UPLOADING}' THEN 4 " +
            "ELSE 5 END)"

        private val SELECT_CLAUSE = "SELECT " +
            IdentifiableColumns.UID + DataSetInstanceSQLStatementBuilder.AS + DataSetInstanceSQLStatementBuilder.DATASET_UID_ALIAS + "," +
            IdentifiableColumns.NAME + DataSetInstanceSQLStatementBuilder.AS + DataSetInstanceSQLStatementBuilder.DATASET_NAME_ALIAS + "," +
            "SUM(${DataSetInstanceSQLStatementBuilder.VALUE_COUNT_ALIAS})" + DataSetInstanceSQLStatementBuilder.AS + DataSetInstanceSQLStatementBuilder.VALUE_COUNT_ALIAS + "," +
            "COUNT(${DataSetInstanceSQLStatementBuilder.VALUE_COUNT_ALIAS})" + DataSetInstanceSQLStatementBuilder.AS + DATASETINSTANCE_COUNT_ALIAS + "," +
            "IFNULL($STATE,'SYNCED')" + DataSetInstanceSQLStatementBuilder.AS + STATE + "," +
            "MAX(${DataSetInstanceSQLStatementBuilder.LAST_UPDATED_ALIAS})" + DataSetInstanceSQLStatementBuilder.AS + DataSetInstanceSQLStatementBuilder.LAST_UPDATED_ALIAS + "," +
            SELECT_STATE_ORDERING

        private val DATASET_LIST_CLAUSE = "SELECT " +
            IdentifiableColumns.UID + ", " +
            IdentifiableColumns.NAME + " " +
            "FROM " + DataSetTableInfo.TABLE_INFO.name()

        private const val GROUP_BY_CLAUSE = "GROUP BY " + IdentifiableColumns.UID

        private fun dot(string1: String, string2: String): String {
            return "$string1.$string2"
        }
    }
}

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
package org.hisp.dhis.android.core.dataset.internal

import org.hisp.dhis.android.core.arch.db.querybuilders.internal.SQLStatementBuilderImpl.Companion.getLimit
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.SQLStatementBuilderImpl.Companion.getOffset
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.SQLStatementBuilderImpl.Companion.getOrderBy
import org.hisp.dhis.android.core.arch.db.sqlorder.internal.SQLOrderType
import org.hisp.dhis.android.core.common.IdentifiableColumns
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.persistence.dataset.DataSetTableInfo

class DataSetInstanceSummarySQLStatementBuilder : DataSetInstanceSQLStatementBuilder() {
    override fun selectWhere(whereClause: String): String {
        val innerSelectClause = super.selectWhere(whereClause)
        return wrapInnerClause(innerSelectClause)
    }

    override fun selectWhere(whereClause: String, limit: Int): String {
        val innerSelectClause = super.selectWhere(whereClause)
        return wrapInnerClause(innerSelectClause) + getLimit(limit)
    }

    override fun selectAll(): String {
        val innerSelectClause = super.selectAll()
        return wrapInnerClause(innerSelectClause)
    }

    override fun count(): String {
        return "SELECT count(*) FROM (${selectAll()})"
    }

    override fun countWhere(whereClause: String): String {
        return "SELECT count(*) FROM (${selectWhere(whereClause)})"
    }

    override fun selectWhere(whereClause: String, orderByClause: String?): String {
        return selectWhere(whereClause) + getOrderBy(orderByClause)
    }

    override fun selectWhere(whereClause: String, orderByClause: String?, limit: Int): String {
        return selectWhere(whereClause, orderByClause) + getLimit(limit)
    }

    override fun selectWhere(whereClause: String, orderByClause: String?, limit: Int, offset: Int?): String {
        return selectWhere(whereClause, orderByClause) + getLimit(limit) + getOffset(offset)
    }

    override fun selectOneOrderedBy(orderingColumnName: String, orderingType: SQLOrderType): String {
        return selectWhere("1", "$orderingColumnName $orderingType", 1)
    }

    private fun wrapInnerClause(innerClause: String): String {
        return SELECT_CLAUSE +
            " FROM ($DATASET_LIST_CLAUSE) $DS_LIST_TABLE_ALIAS" +
            " LEFT JOIN ($innerClause) $DS_INSTANCE_ALIAS" +
            " ON " + dot(DS_LIST_TABLE_ALIAS, IdentifiableColumns.UID) + " = " +
            dot(DS_INSTANCE_ALIAS, DATASET_UID_ALIAS) +
            " " + GROUP_BY_CLAUSE
    }

    companion object {
        private const val DATASETINSTANCE_COUNT_ALIAS = "dataSetInstanceCount"
        private const val DS_LIST_TABLE_ALIAS = "dslist"
        private const val DS_INSTANCE_ALIAS = "dsinstance"
        private const val STATE = STATE_ALIAS

        private val SELECT_STATE_ORDERING = " MAX(CASE " +
            "WHEN $STATE IN ('${State.SYNCED}','${State.SYNCED_VIA_SMS}') THEN 1 " +
            "WHEN $STATE = '${State.SENT_VIA_SMS}' THEN 2 " +
            "WHEN $STATE IN ('${State.TO_POST}','${State.TO_UPDATE}') THEN 3 " +
            "WHEN $STATE = '${State.UPLOADING}' THEN 4 " +
            "ELSE 5 END)"

        private val SELECT_CLAUSE = "SELECT " +
            IdentifiableColumns.UID + AS + DATASET_UID_ALIAS + "," +
            IdentifiableColumns.NAME + AS + DATASET_NAME_ALIAS + "," +
            "SUM($VALUE_COUNT_ALIAS)" + AS + VALUE_COUNT_ALIAS + "," +
            "COUNT($VALUE_COUNT_ALIAS)" + AS + DATASETINSTANCE_COUNT_ALIAS + "," +
            "IFNULL($STATE,'SYNCED')" + AS + STATE + "," +
            "MAX($LAST_UPDATED_ALIAS)" + AS + LAST_UPDATED_ALIAS + "," +
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

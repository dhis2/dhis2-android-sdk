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

package org.hisp.dhis.android.core.dataset.internal;

import org.hisp.dhis.android.core.arch.db.sqlorder.internal.SQLOrderType;
import org.hisp.dhis.android.core.common.DeletableDataColumns;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.dataset.DataSetTableInfo;

import static org.hisp.dhis.android.core.common.IdentifiableColumns.NAME;
import static org.hisp.dhis.android.core.common.IdentifiableColumns.UID;

public class DataSetInstanceSummarySQLStatementBuilder extends DataSetInstanceSQLStatementBuilder {

    private static final String DATASETINSTANCE_COUNT_ALIAS = "dataSetInstanceCount";

    private static final String DS_LIST_TABLE_ALIAS = "dslist";
    private static final String DS_INSTANCE_ALIAS = "dsinstance";

    private static final String STATE = STATE_ALIAS;

    private static final String SELECT_STATE_ORDERING = " MAX(CASE " +
            "WHEN " + STATE + " IN ('" + State.SYNCED + "','" + State.SYNCED_VIA_SMS + "') THEN 1 " +
            "WHEN " + STATE + " = '" + State.SENT_VIA_SMS + "' THEN 2 " +
            "WHEN " + STATE + " IN ('" + State.TO_POST + "','" + State.TO_UPDATE + "') THEN 3 " +
            "WHEN " + STATE + " = '" + State.UPLOADING + "' THEN 4 " +
            "ELSE 5 END)";

    private static final String SELECT_CLAUSE = "SELECT " +
            dot(DS_LIST_TABLE_ALIAS, DeletableDataColumns.ID) + AS + DeletableDataColumns.ID + "," +
            UID + AS + DATASET_UID_ALIAS + "," +
            NAME + AS + DATASET_NAME_ALIAS + "," +
            "SUM(" + VALUE_COUNT_ALIAS + ")" + AS + VALUE_COUNT_ALIAS +  "," +
            "COUNT(" + VALUE_COUNT_ALIAS + ")" + AS + DATASETINSTANCE_COUNT_ALIAS + "," +
            "IFNULL(" + STATE + ",'SYNCED')" + AS + STATE + "," +
            "MAX(" + LAST_UPDATED_ALIAS + ")" + AS + LAST_UPDATED_ALIAS + "," +
            SELECT_STATE_ORDERING;

    private static final String DATASET_LIST_CLAUSE = "SELECT " +
            DeletableDataColumns.ID + ", " +
            UID + ", " +
            NAME + " " +
            "FROM " + DataSetTableInfo.TABLE_INFO.name();

    private static final String GROUP_BY_CLAUSE = "GROUP BY " + UID;

    @Override
    public String selectWhere(String whereClause) {
        String innerSelectClause = super.selectWhere(whereClause);

        return wrapInnerClause(innerSelectClause);
    }

    @Override
    public String selectWhere(String whereClause, int limit) {
        String innerSelectClause = super.selectWhere(whereClause);

        return wrapInnerClause(innerSelectClause) + " LIMIT " + limit;
    }

    @Override
    public String selectAll() {
        String innerSelectClause = super.selectAll();

        return wrapInnerClause(innerSelectClause);
    }

    @Override
    public String count() {
        return "SELECT count(*) FROM (" + selectAll() + ")";
    }

    @Override
    public String countWhere(String whereClause) {
        return "SELECT count(*) FROM (" + selectWhere(whereClause) + ")";
    }

    @Override
    public String selectWhere(String whereClause, String orderByClause) {
        return selectWhere(whereClause) + " ORDER BY " + orderByClause;
    }

    @Override
    public String selectWhere(String whereClause, String orderByClause, int limit) {
        return selectWhere(whereClause, orderByClause) + " LIMIT " + limit;
    }

    @Override
    public String selectOneOrderedBy(String orderingColumnName, SQLOrderType orderingType) {
        return selectWhere("1", orderingColumnName + " " + orderingType, 1);
    }

    private String wrapInnerClause(String innerClause) {
        return SELECT_CLAUSE +
                " FROM (" + DATASET_LIST_CLAUSE + ") " + DS_LIST_TABLE_ALIAS +
                " LEFT JOIN (" + innerClause + ") " + DS_INSTANCE_ALIAS +
                " ON " + dot(DS_LIST_TABLE_ALIAS, UID) + " = " +
                        dot(DS_INSTANCE_ALIAS, DATASET_UID_ALIAS) +
                " " + GROUP_BY_CLAUSE;
    }

    private static String dot(String string1, String string2) {
        return string1 + "." + string2;
    }
}

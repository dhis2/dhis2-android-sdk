/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.datavalue;

import org.hisp.dhis.android.core.arch.db.querybuilders.internal.ReadOnlySQLStatementBuilder;
import org.hisp.dhis.android.core.arch.db.sqlorder.internal.SQLOrderType;
import org.hisp.dhis.android.core.common.BaseDataModel;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.dataset.DataSetDataElementLinkTableInfo;
import org.hisp.dhis.android.core.period.PeriodTableInfo;

public class DataSetValueSummarySQLStatementBuilder implements ReadOnlySQLStatementBuilder {

    private static final String DATAVALUE_ALIAS = "dv";
    private static final String PERIOD_ALIAS = "pe";
    private static final String DATASETELEMENT_ALIAS = "dse";

    static final String VALUE_COUNT = "valueCount";

    static final String DATASET_UID = DATASETELEMENT_ALIAS + "." + DataSetDataElementLinkTableInfo.Columns.DATA_SET;
    static final String PERIOD = DATAVALUE_ALIAS + "." + DataValueFields.PERIOD;
    static final String ORGANISATION_UNIT = DATAVALUE_ALIAS + "." + DataValueTableInfo.ORGANISATION_UNIT;
    static final String ATTRIBUTE_OPTION_COMBO = DATAVALUE_ALIAS + "." + DataValueFields.ATTRIBUTE_OPTION_COMBO;

    static final String PERIOD_START_DATE = PERIOD_ALIAS + "." + PeriodTableInfo.Columns.START_DATE;
    static final String PERIOD_END_DATE = PERIOD_ALIAS + "." + PeriodTableInfo.Columns.END_DATE;
    static final String PERIOD_TYPE = PERIOD_ALIAS + "." + PeriodTableInfo.Columns.PERIOD_TYPE;

    private final String STATE = DATAVALUE_ALIAS + "." + BaseDataModel.Columns.STATE;

    private final String SELECT_STATE_ORDERING = " MAX(CASE " +
            "WHEN " + STATE + " = '" + State.SYNCED + "' THEN 1 " +
            "WHEN " + STATE + " = '" + State.TO_DELETE + "' THEN 2 " +
            "WHEN " + STATE + " IN ('" + State.TO_POST + "','" + State.TO_UPDATE + "') THEN 3 " +
            "ELSE 4 END)";

    private final String FROM_CLAUSE =
            " FROM " + DataValueTableInfo.TABLE_INFO.name() + " as " + DATAVALUE_ALIAS +
            " INNER JOIN " + PeriodTableInfo.TABLE_INFO.name() + " as " + PERIOD_ALIAS +
            " ON " + PERIOD + " = " + PERIOD_ALIAS + "." + PeriodTableInfo.Columns.PERIOD_ID +
            " INNER JOIN " + DataSetDataElementLinkTableInfo.TABLE_INFO.name() + " as " + DATASETELEMENT_ALIAS +
            " ON " + DATAVALUE_ALIAS + "." + DataValueFields.DATA_ELEMENT + " = " +
                    DATASETELEMENT_ALIAS + "." + DataSetDataElementLinkTableInfo.Columns.DATA_ELEMENT;

    private final String SELECT_CLAUSE = "SELECT " +
            DATAVALUE_ALIAS + "." + BaseDataModel.Columns.ID + ", " +
            DATASET_UID + ", " +
            PERIOD + ", " +
            ORGANISATION_UNIT + ", " +
            ATTRIBUTE_OPTION_COMBO + ", " +
            "COUNT(*) AS " + VALUE_COUNT + ", " +
            STATE + ", " +
            // Auxiliary field to order the 'state' column and to prioritize TO_POST and TO_UPDATE
            SELECT_STATE_ORDERING +
            FROM_CLAUSE;

    private final String SELECT_COUNT_CLAUSE = "SELECT count(*) " + FROM_CLAUSE;

    private final String GROUP_BY_CLAUSE = " GROUP BY " +
            DATASET_UID + "," +
            PERIOD + "," +
            ORGANISATION_UNIT + "," +
            ATTRIBUTE_OPTION_COMBO;

    @Override
    public String selectWhere(String whereClause) {
        return SELECT_CLAUSE + " WHERE " + whereClause + GROUP_BY_CLAUSE;
    }

    @Override
    public String selectWhere(String whereClause, int limit) {
        return selectWhere(whereClause) + " LIMIT " + limit;
    }

    @Override
    public String selectAll() {
        return  SELECT_CLAUSE + GROUP_BY_CLAUSE;
    }

    @Override
    public String count() {
        return SELECT_COUNT_CLAUSE + GROUP_BY_CLAUSE;
    }

    @Override
    public String countWhere(String whereClause) {
        return SELECT_COUNT_CLAUSE + " WHERE " + whereClause + GROUP_BY_CLAUSE;
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
    public String selectOneOrderedBy(String orderingColumName, SQLOrderType orderingType) {
        return selectWhere("1", orderingColumName + " " + orderingType, 1);
    }
}

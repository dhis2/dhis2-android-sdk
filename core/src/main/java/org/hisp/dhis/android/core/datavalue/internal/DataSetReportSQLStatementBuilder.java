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

package org.hisp.dhis.android.core.datavalue.internal;

import org.hisp.dhis.android.core.arch.db.querybuilders.internal.ReadOnlySQLStatementBuilder;
import org.hisp.dhis.android.core.arch.db.sqlorder.internal.SQLOrderType;
import org.hisp.dhis.android.core.category.CategoryOptionComboTableInfo;
import org.hisp.dhis.android.core.common.BaseDataModel;
import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.dataset.DataSetDataElementLinkTableInfo;
import org.hisp.dhis.android.core.dataset.DataSetTableInfo;
import org.hisp.dhis.android.core.datavalue.DataValueTableInfo;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitTableInfo;
import org.hisp.dhis.android.core.period.PeriodTableInfo;

public class DataSetReportSQLStatementBuilder implements ReadOnlySQLStatementBuilder {

    private static final String AS = " AS ";
    private static final String INNER_JOIN = " INNER JOIN ";
    private static final String ON = " ON ";
    private static final String EQ = " = ";

    private static final String DATAVALUE_TABLE_ALIAS = "dv";
    private static final String PERIOD_TABLE_ALIAS = "pe";
    private static final String DATASETELEMENT_TABLE_ALIAS = "dse";
    private static final String ORGUNIT_TABLE_ALIAS = "ou";
    private static final String DATASET_TABLE_ALIAS = "ds";
    private static final String AOC_TABLE_ALIAS = "aoc";

    private static final String VALUE_COUNT_ALIAS = "valueCount";
    private static final String DATASET_UID_ALIAS = "dataSetUid";
    private static final String DATASET_NAME_ALIAS = "dataSetDisplayName";
    private static final String PERIOD_ALIAS = "period";
    private static final String PERIOD_TYPE_ALIAS = "periodType";
    private static final String ORGANISATION_UNIT_UID_ALIAS = "organisationUnitUid";
    private static final String ORGANISATION_UNIT_NAME_ALIAS = "organisationUnitDisplayName";
    private static final String ATTRIBUTE_OPTION_COMBO_UID_ALIAS = "attributeOptionComboUid";
    private static final String ATTRIBUTE_OPTION_COMBO_NAME_ALIAS = "attributeOptionComboDisplayName";

    public static final String DATAVALUE_ID = DATAVALUE_TABLE_ALIAS + "." + BaseDataModel.Columns.ID;
    public static final String DATASET_UID = DATASET_TABLE_ALIAS + "." + BaseIdentifiableObjectModel.Columns.UID;
    static final String DATASET_NAME = DATASET_TABLE_ALIAS + "." + BaseIdentifiableObjectModel.Columns.DISPLAY_NAME;
    public static final String PERIOD = DATAVALUE_TABLE_ALIAS + "." + DataValueFields.PERIOD;
    public static final String PERIOD_TYPE = PERIOD_TABLE_ALIAS + "." + PeriodTableInfo.Columns.PERIOD_TYPE;
    public static final String ORGANISATION_UNIT_UID = ORGUNIT_TABLE_ALIAS + "." +
            BaseIdentifiableObjectModel.Columns.UID;
    static final String ORGANISATION_UNIT_NAME =
            ORGUNIT_TABLE_ALIAS + "." + BaseIdentifiableObjectModel.Columns.DISPLAY_NAME;
    public static final String ATTRIBUTE_OPTION_COMBO_UID = AOC_TABLE_ALIAS + "." +
            BaseIdentifiableObjectModel.Columns.UID;
    static final String ATTRIBUTE_OPTION_COMBO_NAME =
            AOC_TABLE_ALIAS + "." + BaseIdentifiableObjectModel.Columns.DISPLAY_NAME;

    public static final String PERIOD_START_DATE = PERIOD_TABLE_ALIAS + "." + PeriodTableInfo.Columns.START_DATE;
    public static final String PERIOD_END_DATE = PERIOD_TABLE_ALIAS + "." + PeriodTableInfo.Columns.END_DATE;


    private static final String STATE = DATAVALUE_TABLE_ALIAS + "." + BaseDataModel.Columns.STATE;

    private static final String SELECT_STATE_ORDERING = " MAX(CASE " +
            "WHEN " + STATE + " = '" + State.SYNCED + "' THEN 1 " +
            "WHEN " + STATE + " = '" + State.TO_DELETE + "' THEN 2 " +
            "WHEN " + STATE + " IN ('" + State.TO_POST + "','" + State.TO_UPDATE + "') THEN 3 " +
            "ELSE 4 END)";

    private static final String FROM_CLAUSE =
            " FROM " + DataValueTableInfo.TABLE_INFO.name() + AS + DATAVALUE_TABLE_ALIAS +
                    getJoinPeriod() +
                    getJoinDataSetElement() +
                    getJoinDataSet() +
                    getJoinOrganisationUnit() +
                    getJoinAttributeOptionCombo();

    private static final String SELECT_CLAUSE = "SELECT " +
            DATAVALUE_ID + ", " +
            DATASET_UID + AS + DATASET_UID_ALIAS + "," +
            DATASET_NAME + AS + DATASET_NAME_ALIAS + "," +
            PERIOD + AS + PERIOD_ALIAS + "," +
            PERIOD_TYPE + AS + PERIOD_TYPE_ALIAS + "," +
            ORGANISATION_UNIT_UID + AS + ORGANISATION_UNIT_UID_ALIAS + "," +
            ORGANISATION_UNIT_NAME + AS + ORGANISATION_UNIT_NAME_ALIAS + "," +
            ATTRIBUTE_OPTION_COMBO_UID + AS + ATTRIBUTE_OPTION_COMBO_UID_ALIAS + "," +
            ATTRIBUTE_OPTION_COMBO_NAME + AS + ATTRIBUTE_OPTION_COMBO_NAME_ALIAS + "," +
            "COUNT(*)" + AS + VALUE_COUNT_ALIAS + ", " +
            STATE + ", " +
            // Auxiliary field to order the 'state' column and to prioritize TO_POST and TO_UPDATE
            SELECT_STATE_ORDERING +
            FROM_CLAUSE;

    private static final String GROUP_BY_CLAUSE = " GROUP BY " +
            DATASET_UID + "," +
            PERIOD + "," +
            ORGANISATION_UNIT_UID + "," +
            ATTRIBUTE_OPTION_COMBO_UID;

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
    public String selectOneOrderedBy(String orderingColumName, SQLOrderType orderingType) {
        return selectWhere("1", orderingColumName + " " + orderingType, 1);
    }

    private static String getJoinPeriod() {
        return INNER_JOIN + PeriodTableInfo.TABLE_INFO.name() + AS + PERIOD_TABLE_ALIAS +
                ON + PERIOD + EQ + PERIOD_TABLE_ALIAS + "." + PeriodTableInfo.Columns.PERIOD_ID;
    }

    private static String getJoinDataSetElement() {
        return INNER_JOIN + DataSetDataElementLinkTableInfo.TABLE_INFO.name() + AS + DATASETELEMENT_TABLE_ALIAS +
                ON + DATAVALUE_TABLE_ALIAS + "." + DataValueFields.DATA_ELEMENT + EQ +
                DATASETELEMENT_TABLE_ALIAS + "." + DataSetDataElementLinkTableInfo.Columns.DATA_ELEMENT;
    }

    private static String getJoinOrganisationUnit() {
        return INNER_JOIN + OrganisationUnitTableInfo.TABLE_INFO.name() + AS + ORGUNIT_TABLE_ALIAS +
                ON + DATAVALUE_TABLE_ALIAS + "." + DataValueTableInfo.ORGANISATION_UNIT + EQ +
                ORGUNIT_TABLE_ALIAS + "." + BaseIdentifiableObjectModel.Columns.UID;
    }

    private static String getJoinDataSet() {
        return INNER_JOIN + DataSetTableInfo.TABLE_INFO.name() + AS + DATASET_TABLE_ALIAS +
                ON + DATASETELEMENT_TABLE_ALIAS + "." + DataSetDataElementLinkTableInfo.Columns.DATA_SET + EQ +
                DATASET_TABLE_ALIAS + "." + BaseIdentifiableObjectModel.Columns.UID;
    }

    private static String getJoinAttributeOptionCombo() {
        return INNER_JOIN + CategoryOptionComboTableInfo.TABLE_INFO.name() + AS + AOC_TABLE_ALIAS +
                ON + DATAVALUE_TABLE_ALIAS + "." + DataValueFields.ATTRIBUTE_OPTION_COMBO + EQ +
                AOC_TABLE_ALIAS + "." + BaseIdentifiableObjectModel.Columns.UID;
    }
}

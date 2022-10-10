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

import org.hisp.dhis.android.core.arch.db.querybuilders.internal.ReadOnlySQLStatementBuilder;
import org.hisp.dhis.android.core.arch.db.sqlorder.internal.SQLOrderType;
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper;
import org.hisp.dhis.android.core.category.CategoryOptionComboTableInfo;
import org.hisp.dhis.android.core.common.DataColumns;
import org.hisp.dhis.android.core.common.DeletableDataColumns;
import org.hisp.dhis.android.core.common.IdentifiableColumns;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.dataelement.DataElementTableInfo;
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistrationTableInfo;
import org.hisp.dhis.android.core.dataset.DataSetDataElementLinkTableInfo;
import org.hisp.dhis.android.core.dataset.DataSetElementLinkTableInfo;
import org.hisp.dhis.android.core.dataset.DataSetTableInfo;
import org.hisp.dhis.android.core.datavalue.DataValueTableInfo;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitTableInfo;
import org.hisp.dhis.android.core.period.PeriodTableInfo;

public class DataSetInstanceSQLStatementBuilder implements ReadOnlySQLStatementBuilder {

    /**
     * Builder class that creates a complex query as the result of the join of several tables. The purpose is to
     * obtain an overview of the datavalues associated to a dataset.
     *
     * - Discriminate the values associated to a dataSet using the DataSetElement table. The difficult part is to
     * know valid the attributeOptionCombos for the dataElements in that particular dataSet. Clause
     * COC_BY_DATASET_WHERE_CLAUSE
     *
     * - Prioritize data states. When grouping by dataset-orgunit-period-aoc, data values states must be prioritize.
     * In order to do that, an auxiliary column is used. This column is assigend a numeric value (1, 2, 3 or 4)
     * depending on the data value state (SYNCED lower, ERROR higher). Then, the aggregation function MAX is used to
     * get the highest value. Clause SELECT_VALUE_STATE_ORDERING
     *
     * - There is value state and a completion state. An extra column "state" is added to have a single state for the
     * dataSetInstance. Prioritization is respected. Clause SELECT_STATE.
     *
     * - The "where" clause is build as usual by using the collection repository scope.
     */

    static final String AS = " AS ";
    private static final String INNER_JOIN = " INNER JOIN ";
    private static final String LEFT_JOIN = " LEFT JOIN ";
    private static final String ON = " ON ";
    private static final String EQ = " = ";
    private static final String AND = " AND ";
    private static final String OR = " OR ";
    private static final String WHEN = " WHEN ";
    private static final String THEN = " THEN ";

    private static final String DATAVALUE_TABLE_ALIAS = "dv";
    private static final String PERIOD_TABLE_ALIAS = "pe";
    private static final String DATASETELEMENT_TABLE_ALIAS = "dse";
    private static final String DATAELEMENT_TABLE_ALIAS = "de";
    private static final String ORGUNIT_TABLE_ALIAS = "ou";
    private static final String DATASET_TABLE_ALIAS = "ds";
    private static final String COC_TABLE_ALIAS = "coc";
    private static final String AOC_TABLE_ALIAS = "aoc";
    private static final String COMPLETE_TABLE_ALIAS = "dscr";

    static final String VALUE_COUNT_ALIAS = "valueCount";
    public static final String DATASET_UID_ALIAS = "dataSetUid";
    static final String DATASET_NAME_ALIAS = "dataSetDisplayName";
    public static final String PERIOD_ALIAS = "period";
    public static final String PERIOD_TYPE_ALIAS = "periodType";
    public static final String PERIOD_START_DATE_ALIAS = "periodStartDate";
    public static final String PERIOD_END_DATE_ALIAS = "periodEndDate";
    public static final String ORGANISATION_UNIT_UID_ALIAS = "organisationUnitUid";
    private static final String ORGANISATION_UNIT_NAME_ALIAS = "organisationUnitDisplayName";
    public static final String ATTRIBUTE_OPTION_COMBO_UID_ALIAS = "attributeOptionComboUid";
    private static final String ATTRIBUTE_OPTION_COMBO_NAME_ALIAS = "attributeOptionComboDisplayName";
    private static final String COMPLETION_DATE_ALIAS = "completionDate";
    private static final String COMPLETED_BY_ALIAS = "completedBy";
    static final String LAST_UPDATED_ALIAS = "lastUpdated";
    public static final String VALUE_STATE_ALIAS = "dataValueState";
    public static final String COMPLETION_STATE_ALIAS = "completionState";
    public static final String STATE_ALIAS = "state";

    public static final String DATAVALUE_ID = DATAVALUE_TABLE_ALIAS + "." + DeletableDataColumns.ID;
    private static final String DATASET_UID = DATASET_TABLE_ALIAS + "." + IdentifiableColumns.UID;
    private static final String DATASET_NAME =
            DATASET_TABLE_ALIAS + "." + IdentifiableColumns.DISPLAY_NAME;
    private static final String PERIOD = DATAVALUE_TABLE_ALIAS + "." + DataValueTableInfo.Columns.PERIOD;
    private static final String PERIOD_TYPE = PERIOD_TABLE_ALIAS + "." + PeriodTableInfo.Columns.PERIOD_TYPE;
    private static final String PERIOD_START_DATE = PERIOD_TABLE_ALIAS + "." + PeriodTableInfo.Columns.START_DATE;
    private static final String PERIOD_END_DATE = PERIOD_TABLE_ALIAS + "." + PeriodTableInfo.Columns.END_DATE;
    private static final String ORGANISATION_UNIT_UID = ORGUNIT_TABLE_ALIAS + "." +
            IdentifiableColumns.UID;
    private static final String ORGANISATION_UNIT_NAME =
            ORGUNIT_TABLE_ALIAS + "." + IdentifiableColumns.DISPLAY_NAME;
    private static final String ATTRIBUTE_OPTION_COMBO_UID = AOC_TABLE_ALIAS + "." +
            IdentifiableColumns.UID;
    private static final String ATTRIBUTE_OPTION_COMBO_NAME =
            AOC_TABLE_ALIAS + "." + IdentifiableColumns.DISPLAY_NAME;
    private static final String COMPLETION_DATE =
            COMPLETE_TABLE_ALIAS + "." + DataSetCompleteRegistrationTableInfo.Columns.DATE;
    private static final String COMPLETED_BY =
            COMPLETE_TABLE_ALIAS + "." + DataSetCompleteRegistrationTableInfo.Columns.STORED_BY;
    private static final String DSE_CATEGORY_COMBO =
            DATASETELEMENT_TABLE_ALIAS + "." + DataSetElementLinkTableInfo.Columns.CATEGORY_COMBO;
    private static final String LAST_UPDATED_VALUES =
            "MAX(" + DATAVALUE_TABLE_ALIAS + "." + DataValueTableInfo.Columns.LAST_UPDATED + ")";
    private static final String LAST_UPDATED =
            "MAX(" + LAST_UPDATED_VALUES + ", COALESCE(" + COMPLETION_DATE + ", 0))";

    private static final String VALUE_STATE = DATAVALUE_TABLE_ALIAS + "." + DataColumns.SYNC_STATE;
    private static final String COMPLETION_STATE = COMPLETE_TABLE_ALIAS + "." + DataColumns.SYNC_STATE;

    private static final String SELECT_VALUE_STATE_ORDERING = " MAX(CASE " +
            "WHEN " + VALUE_STATE + " IN ('" + State.SYNCED + "','" + State.SYNCED_VIA_SMS + "') THEN 1 " +
            "WHEN " + VALUE_STATE + " = '" + State.SENT_VIA_SMS + "' THEN 2 " +
            "WHEN " + VALUE_STATE + " IN ('" + State.TO_POST + "','" + State.TO_UPDATE + "') THEN 3 " +
            "WHEN " + VALUE_STATE + " = '" + State.UPLOADING + "' THEN 4 " +
            "ELSE 5 END)";

    private static final String SELECT_STATE = "CASE" +
            WHEN + eq(COMPLETION_STATE, State.ERROR) + OR + eq(VALUE_STATE, State.ERROR) +
                THEN + quotes(State.ERROR) +
            WHEN + eq(COMPLETION_STATE, State.WARNING) + OR + eq(VALUE_STATE, State.WARNING) +
                THEN + quotes(State.WARNING) +
            WHEN + eq(COMPLETION_STATE, State.UPLOADING) + OR + eq(VALUE_STATE, State.UPLOADING) +
                THEN + quotes(State.UPLOADING) +
            WHEN + eq(COMPLETION_STATE, State.TO_UPDATE) + OR + eq(VALUE_STATE, State.TO_UPDATE) +
                THEN + quotes(State.TO_UPDATE) +
            WHEN + eq(COMPLETION_STATE, State.TO_POST) + OR + eq(VALUE_STATE, State.TO_POST) +
                THEN + quotes(State.TO_POST) +
            WHEN + eq(COMPLETION_STATE, State.SENT_VIA_SMS) + OR + eq(VALUE_STATE, State.SENT_VIA_SMS) +
                THEN + quotes(State.SENT_VIA_SMS) +
            WHEN + eq(COMPLETION_STATE, State.SYNCED_VIA_SMS) + OR + eq(VALUE_STATE, State.SYNCED_VIA_SMS) +
                THEN + quotes(State.SYNCED_VIA_SMS) +
            WHEN + eq(COMPLETION_STATE, State.SYNCED) + OR + eq(VALUE_STATE, State.SYNCED) +
                THEN + quotes(State.SYNCED) +
            " ELSE " + quotes(State.SYNCED) + " END" +
            AS + STATE_ALIAS;

    private static final String FROM_CLAUSE =
            " FROM " + DataValueTableInfo.TABLE_INFO.name() + AS + DATAVALUE_TABLE_ALIAS +
                    getJoinPeriod() +
                    getJoinDataSetElement() +
                    getJoinDataelement() +
                    getJoinDataSet() +
                    getJoinOrganisationUnit() +
                    getJoinCategoryOptionCombo() +
                    getJoinAttributeOptionCombo() +
                    getJoinDataSetCompleteRegistration();

    private static final String INNER_SELECT_CLAUSE = "SELECT " +
            DATAVALUE_ID + AS + DeletableDataColumns.ID + "," +
            DATASET_UID + AS + DATASET_UID_ALIAS + "," +
            DATASET_NAME + AS + DATASET_NAME_ALIAS + "," +
            PERIOD + AS + PERIOD_ALIAS + "," +
            PERIOD_TYPE + AS + PERIOD_TYPE_ALIAS + "," +
            PERIOD_START_DATE + AS + PERIOD_START_DATE_ALIAS + "," +
            PERIOD_END_DATE + AS + PERIOD_END_DATE_ALIAS + "," +
            ORGANISATION_UNIT_UID + AS + ORGANISATION_UNIT_UID_ALIAS + "," +
            ORGANISATION_UNIT_NAME + AS + ORGANISATION_UNIT_NAME_ALIAS + "," +
            ATTRIBUTE_OPTION_COMBO_UID + AS + ATTRIBUTE_OPTION_COMBO_UID_ALIAS + "," +
            ATTRIBUTE_OPTION_COMBO_NAME + AS + ATTRIBUTE_OPTION_COMBO_NAME_ALIAS + "," +
            "COUNT(*)" + AS + VALUE_COUNT_ALIAS + "," +
            COMPLETION_DATE + AS + COMPLETION_DATE_ALIAS + "," +
            COMPLETED_BY + AS + COMPLETED_BY_ALIAS + "," +
            LAST_UPDATED + AS + LAST_UPDATED_ALIAS + "," +
            VALUE_STATE + AS + VALUE_STATE_ALIAS + "," +
            // Auxiliary field to order the 'state' column and to prioritize TO_POST and TO_UPDATE
            SELECT_VALUE_STATE_ORDERING + "," +
            COMPLETION_STATE + AS + COMPLETION_STATE_ALIAS + "," +
            SELECT_STATE +
            FROM_CLAUSE;

    private static final String COC_BY_DATASET_WHERE_CLAUSE = " WHERE " +
            "(CASE WHEN " + DSE_CATEGORY_COMBO + " IS NOT NULL THEN " + DSE_CATEGORY_COMBO +
            " ELSE " + DATAELEMENT_TABLE_ALIAS + "." + DataElementTableInfo.Columns.CATEGORY_COMBO + " END)" +
            EQ + COC_TABLE_ALIAS + "." + CategoryOptionComboTableInfo.Columns.CATEGORY_COMBO;

    private static final String GROUP_BY_CLAUSE = " GROUP BY " +
            DATASET_UID + "," +
            PERIOD + "," +
            ORGANISATION_UNIT_UID + "," +
            ATTRIBUTE_OPTION_COMBO_UID;

    private static final String SELECT_CLAUSE =
            "SELECT * FROM (" + INNER_SELECT_CLAUSE + COC_BY_DATASET_WHERE_CLAUSE + GROUP_BY_CLAUSE +")";

    @Override
    public String selectWhere(String whereClause) {
        return SELECT_CLAUSE + " WHERE " + whereClause;
    }

    @Override
    public String selectWhere(String whereClause, int limit) {
        return selectWhere(whereClause) + " LIMIT " + limit;
    }

    @Override
    public String selectAll() {
        return  SELECT_CLAUSE;
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
    public String countAndGroupBy(String column) {
        return "SELECT " + column + " , COUNT(*) FROM (" + selectAll() + ") GROUP BY " + column + ";";
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

    private static String getJoinPeriod() {
        return INNER_JOIN + PeriodTableInfo.TABLE_INFO.name() + AS + PERIOD_TABLE_ALIAS +
                ON + PERIOD + EQ + PERIOD_TABLE_ALIAS + "." + PeriodTableInfo.Columns.PERIOD_ID;
    }

    private static String getJoinDataSetElement() {
        return INNER_JOIN + DataSetDataElementLinkTableInfo.TABLE_INFO.name() + AS + DATASETELEMENT_TABLE_ALIAS +
                ON + DATAVALUE_TABLE_ALIAS + "." + DataValueTableInfo.Columns.DATA_ELEMENT + EQ +
                DATASETELEMENT_TABLE_ALIAS + "." + DataSetDataElementLinkTableInfo.Columns.DATA_ELEMENT;
    }

    private static String getJoinDataelement() {
        return INNER_JOIN + DataElementTableInfo.TABLE_INFO.name() + AS + DATAELEMENT_TABLE_ALIAS +
                ON + DATAVALUE_TABLE_ALIAS + "." + DataValueTableInfo.Columns.DATA_ELEMENT + EQ +
                DATAELEMENT_TABLE_ALIAS + "." + IdentifiableColumns.UID;
    }

    private static String getJoinOrganisationUnit() {
        return INNER_JOIN + OrganisationUnitTableInfo.TABLE_INFO.name() + AS + ORGUNIT_TABLE_ALIAS +
                ON + DATAVALUE_TABLE_ALIAS + "." + DataValueTableInfo.Columns.ORGANISATION_UNIT + EQ +
                ORGUNIT_TABLE_ALIAS + "." + IdentifiableColumns.UID;
    }

    private static String getJoinDataSet() {
        return INNER_JOIN + DataSetTableInfo.TABLE_INFO.name() + AS + DATASET_TABLE_ALIAS +
                ON + DATASETELEMENT_TABLE_ALIAS + "." + DataSetDataElementLinkTableInfo.Columns.DATA_SET + EQ +
                DATASET_TABLE_ALIAS + "." + IdentifiableColumns.UID;
    }

    private static String getJoinCategoryOptionCombo() {
        return INNER_JOIN + CategoryOptionComboTableInfo.TABLE_INFO.name() + AS + COC_TABLE_ALIAS +
                ON + DATAVALUE_TABLE_ALIAS + "." + DataValueTableInfo.Columns.CATEGORY_OPTION_COMBO + EQ +
                COC_TABLE_ALIAS + "." + IdentifiableColumns.UID;
    }

    private static String getJoinAttributeOptionCombo() {
        return INNER_JOIN + CategoryOptionComboTableInfo.TABLE_INFO.name() + AS + AOC_TABLE_ALIAS +
                ON + DATAVALUE_TABLE_ALIAS + "." + DataValueTableInfo.Columns.ATTRIBUTE_OPTION_COMBO + EQ +
                AOC_TABLE_ALIAS + "." + IdentifiableColumns.UID;
    }

    private static String getJoinDataSetCompleteRegistration() {
        return LEFT_JOIN + DataSetCompleteRegistrationTableInfo.TABLE_INFO.name() + AS + COMPLETE_TABLE_ALIAS +
                ON + DATASET_UID + EQ +
                    COMPLETE_TABLE_ALIAS + "." + DataSetCompleteRegistrationTableInfo.Columns.DATA_SET +
                AND + PERIOD + EQ +
                    COMPLETE_TABLE_ALIAS + "." + DataSetCompleteRegistrationTableInfo.Columns.PERIOD +
                AND + ORGANISATION_UNIT_UID + EQ +
                    COMPLETE_TABLE_ALIAS + "." + DataSetCompleteRegistrationTableInfo.Columns.ORGANISATION_UNIT +
                AND + ATTRIBUTE_OPTION_COMBO_UID + EQ +
                    COMPLETE_TABLE_ALIAS + "." + DataSetCompleteRegistrationTableInfo.Columns.ATTRIBUTE_OPTION_COMBO;
    }

    private static String eq(String column, State state) {
       return column + " = " + quotes(state);
    }

    private static String quotes(State value) {
        return CollectionsHelper.withSingleQuotationMarks(value.name());
    }
}

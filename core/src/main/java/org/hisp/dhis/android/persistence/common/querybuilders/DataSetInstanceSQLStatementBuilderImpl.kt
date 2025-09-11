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
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper
import org.hisp.dhis.android.core.common.DataColumns
import org.hisp.dhis.android.core.common.IdentifiableColumns
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.persistence.category.CategoryOptionComboTableInfo
import org.hisp.dhis.android.persistence.dataelement.DataElementTableInfo
import org.hisp.dhis.android.persistence.dataset.DataSetCompleteRegistrationTableInfo
import org.hisp.dhis.android.persistence.dataset.DataSetDataElementLinkTableInfo
import org.hisp.dhis.android.persistence.dataset.DataSetOrganisationUnitLinkTableInfo
import org.hisp.dhis.android.persistence.dataset.DataSetTableInfo
import org.hisp.dhis.android.persistence.datavalue.DataValueTableInfo
import org.hisp.dhis.android.persistence.organisationunit.OrganisationUnitTableInfo
import org.hisp.dhis.android.persistence.period.PeriodTableInfo

@Suppress("TooManyFunctions")
internal open class DataSetInstanceSQLStatementBuilderImpl : ReadOnlySQLStatementBuilder {
    override fun selectWhere(whereClause: String): RoomRawQuery {
        return RoomRawQuery("$SELECT_CLAUSE WHERE $whereClause")
    }

    override fun selectWhere(whereClause: String, limit: Int): RoomRawQuery {
        return RoomRawQuery(selectWhere(whereClause).sql + getLimit(limit))
    }

    override fun selectAll(): RoomRawQuery {
        return RoomRawQuery(SELECT_CLAUSE)
    }

    override fun selectStringColumn(column: String, clause: String): RoomRawQuery {
        throw UnsupportedOperationException("Not to be implemented")
    }

    override fun count(): RoomRawQuery {
        return RoomRawQuery("SELECT count(*) FROM (${selectAll()})")
    }

    override fun countWhere(whereClause: String): RoomRawQuery {
        return RoomRawQuery("SELECT count(*) FROM (${selectWhere(whereClause)})")
    }

    override fun countAndGroupBy(column: String): RoomRawQuery {
        return RoomRawQuery("SELECT $column , COUNT(*) FROM (${selectAll()}) GROUP BY $column;")
    }

    override fun deleteTable(): RoomRawQuery {
        return RoomRawQuery("DELETE FROM $SELECT_CLAUSE")
    }

    override fun deleteWhere(whereClause: String): RoomRawQuery {
        throw UnsupportedOperationException("Not to be implemented")
    }

    override fun updateWhere(updates: Map<String, Any>, whereClause: String): RoomRawQuery {
        throw UnsupportedOperationException("Not to be implemented")
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

    companion object {
        /**
         * Builder class that creates a complex query as the result of the join of several tables. The purpose is to
         * obtain an overview of the datavalues associated to a dataset.
         *
         * - Discriminate the values associated to a dataSet using the DataSetElement table. The difficult part is to
         * know valid the attributeOptionCombos for the dataElements in that particular dataSet. Clause
         * COC_BY_DATASET_WHERE_CLAUSE
         *
         * - Prioritize data states. When grouping by dataset-orgunit-period-aoc, data values states must be prioritize.
         * In order to do that, an auxiliary column is used. This column is assigned a numeric value (1, 2, 3 or 4)
         * depending on the data value state (SYNCED lower, ERROR higher). Then, the aggregation function MAX is used to
         * get the highest value. Clause SELECT_VALUE_STATE_ORDERING
         *
         * - There is value state and a completion state. An extra column "state" is added to have a single state for
         * the dataSetInstance. Prioritization is respected. Clause SELECT_STATE.
         *
         * - Only capture orgunits assigned to each dataSet are taken into account.
         *
         * - The "where" clause is build as usual by using the collection repository scope.
         */
        const val AS = " AS "
        private const val INNER_JOIN = " INNER JOIN "
        private const val LEFT_JOIN = " LEFT JOIN "
        private const val ON = " ON "
        private const val EQ = " = "
        private const val AND = " AND "
        private const val OR = " OR "
        private const val WHEN = " WHEN "
        private const val THEN = " THEN "
        private const val DATAVALUE_TABLE_ALIAS = "dv"
        private const val PERIOD_TABLE_ALIAS = "pe"
        private const val DATASETELEMENT_TABLE_ALIAS = "dse"
        private const val DATAELEMENT_TABLE_ALIAS = "de"
        private const val ORGUNIT_TABLE_ALIAS = "ou"
        private const val DATASET_TABLE_ALIAS = "ds"
        private const val COC_TABLE_ALIAS = "coc"
        private const val AOC_TABLE_ALIAS = "aoc"
        private const val COMPLETE_TABLE_ALIAS = "dscr"
        const val VALUE_COUNT_ALIAS = "valueCount"
        const val DATASET_UID_ALIAS = "dataSetUid"
        const val DATASET_NAME_ALIAS = "dataSetDisplayName"
        const val PERIOD_ALIAS = "period"
        const val PERIOD_TYPE_ALIAS = "periodType"
        const val PERIOD_START_DATE_ALIAS = "periodStartDate"
        const val PERIOD_END_DATE_ALIAS = "periodEndDate"
        const val ORGANISATION_UNIT_UID_ALIAS = "organisationUnitUid"
        private const val ORGANISATION_UNIT_NAME_ALIAS = "organisationUnitDisplayName"
        const val ATTRIBUTE_OPTION_COMBO_UID_ALIAS = "attributeOptionComboUid"
        private const val ATTRIBUTE_OPTION_COMBO_NAME_ALIAS = "attributeOptionComboDisplayName"
        private const val COMPLETION_DATE_ALIAS = "completionDate"
        private const val COMPLETED_BY_ALIAS = "completedBy"
        const val LAST_UPDATED_ALIAS = "lastUpdated"
        const val VALUE_STATE_ALIAS = "dataValueState"
        const val COMPLETION_STATE_ALIAS = "completionState"
        const val STATE_ALIAS = "state"
        private const val DATASET_UID = DATASET_TABLE_ALIAS + "." + IdentifiableColumns.UID
        private const val DATASET_NAME = DATASET_TABLE_ALIAS + "." + IdentifiableColumns.DISPLAY_NAME
        private const val PERIOD = DATAVALUE_TABLE_ALIAS + "." + DataValueTableInfo.Columns.PERIOD
        private const val PERIOD_TYPE = PERIOD_TABLE_ALIAS + "." + PeriodTableInfo.Columns.PERIOD_TYPE
        private const val PERIOD_START_DATE = PERIOD_TABLE_ALIAS + "." + PeriodTableInfo.Columns.START_DATE
        private const val PERIOD_END_DATE = PERIOD_TABLE_ALIAS + "." + PeriodTableInfo.Columns.END_DATE
        private const val ORGANISATION_UNIT_UID = ORGUNIT_TABLE_ALIAS + "." +
            IdentifiableColumns.UID
        private const val ORGANISATION_UNIT_NAME = ORGUNIT_TABLE_ALIAS + "." + IdentifiableColumns.DISPLAY_NAME
        private const val ATTRIBUTE_OPTION_COMBO_UID = AOC_TABLE_ALIAS + "." +
            IdentifiableColumns.UID
        private const val ATTRIBUTE_OPTION_COMBO_NAME = AOC_TABLE_ALIAS + "." + IdentifiableColumns.DISPLAY_NAME
        private const val COMPLETION_DATE =
            COMPLETE_TABLE_ALIAS + "." + DataSetCompleteRegistrationTableInfo.Columns.DATE
        private const val COMPLETED_BY =
            COMPLETE_TABLE_ALIAS + "." + DataSetCompleteRegistrationTableInfo.Columns.STORED_BY
        private const val DSE_CATEGORY_COMBO =
            DATASETELEMENT_TABLE_ALIAS + "." + DataSetDataElementLinkTableInfo.Columns.CATEGORY_COMBO
        private const val LAST_UPDATED_VALUES = "MAX($DATAVALUE_TABLE_ALIAS.${DataValueTableInfo.Columns.LAST_UPDATED})"
        private const val LAST_UPDATED = "MAX($LAST_UPDATED_VALUES, COALESCE($COMPLETION_DATE, 0))"
        private const val VALUE_STATE = "$DATAVALUE_TABLE_ALIAS.${DataColumns.SYNC_STATE}"
        private const val COMPLETION_STATE = "$COMPLETE_TABLE_ALIAS.${DataColumns.SYNC_STATE}"

        private val SELECT_VALUE_STATE_ORDERING = " MAX(CASE " +
            "WHEN $VALUE_STATE IN ('${State.SYNCED}','${State.SYNCED_VIA_SMS}') THEN 1 " +
            "WHEN $VALUE_STATE = '${State.SENT_VIA_SMS}' THEN 2 " +
            "WHEN $VALUE_STATE IN ('${State.TO_POST}','${State.TO_UPDATE}') THEN 3 " +
            "WHEN $VALUE_STATE = '${State.UPLOADING}' THEN 4 " +
            "ELSE 5 END)"

        private val SELECT_STATE = "CASE" +
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
            AS + STATE_ALIAS

        private val FROM_CLAUSE = " FROM " + DataValueTableInfo.TABLE_INFO.name() + AS + DATAVALUE_TABLE_ALIAS +
            joinPeriod +
            joinDataSetElement +
            joinDataelement +
            joinDataSet +
            joinOrganisationUnit +
            joinCategoryOptionCombo +
            joinAttributeOptionCombo +
            joinDataSetCompleteRegistration

        private val INNER_SELECT_CLAUSE = "SELECT " +
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
            // Auxiliary field to order the 'state' column and to prioritize TO_POST and TO_UPDATE
            VALUE_STATE + AS + VALUE_STATE_ALIAS + "," +
            SELECT_VALUE_STATE_ORDERING + "," +
            COMPLETION_STATE + AS + COMPLETION_STATE_ALIAS + "," +
            SELECT_STATE +
            FROM_CLAUSE

        private const val COC_BY_DATASET_WHERE_CLAUSE = "(CASE " +
            "WHEN " + DSE_CATEGORY_COMBO + " IS NOT NULL THEN " + DSE_CATEGORY_COMBO +
            " ELSE " + DATAELEMENT_TABLE_ALIAS + "." + DataElementTableInfo.Columns.CATEGORY_COMBO + " END)" +
            EQ + COC_TABLE_ALIAS + "." + CategoryOptionComboTableInfo.Columns.CATEGORY_COMBO

        private const val AOC_WHERE_CLAUSE = DATASET_TABLE_ALIAS + "." + DataSetTableInfo.Columns.CATEGORY_COMBO +
            EQ + AOC_TABLE_ALIAS + "." + CategoryOptionComboTableInfo.Columns.CATEGORY_COMBO

        private val ORGUNIT_ASSIGNED_WHERE_CLAUSE =
            "EXISTS (SELECT 1 " +
                "FROM ${DataSetOrganisationUnitLinkTableInfo.TABLE_INFO.name()} AS dsou " +
                "WHERE dsou.${DataSetOrganisationUnitLinkTableInfo.Columns.DATA_SET} " +
                "= $DATASET_UID_ALIAS " +
                "AND dsou.${DataSetOrganisationUnitLinkTableInfo.Columns.ORGANISATION_UNIT} " +
                "= $ORGANISATION_UNIT_UID_ALIAS" +
                ")"

        private const val GROUP_BY_CLAUSE = " GROUP BY " +
            DATASET_UID + "," +
            PERIOD + "," +
            ORGANISATION_UNIT_UID + "," +
            ATTRIBUTE_OPTION_COMBO_UID

        private val SELECT_CLAUSE =
            "SELECT * FROM (" +
                "$INNER_SELECT_CLAUSE " +
                "WHERE $COC_BY_DATASET_WHERE_CLAUSE AND $AOC_WHERE_CLAUSE AND $ORGUNIT_ASSIGNED_WHERE_CLAUSE " +
                "$GROUP_BY_CLAUSE)"

        private val joinPeriod: String
            get() = INNER_JOIN + PeriodTableInfo.TABLE_INFO.name() + AS + PERIOD_TABLE_ALIAS +
                ON + PERIOD + EQ + PERIOD_TABLE_ALIAS + "." + PeriodTableInfo.Columns.PERIOD_ID

        private val joinDataSetElement: String
            get() = INNER_JOIN + DataSetDataElementLinkTableInfo.TABLE_INFO.name() + AS + DATASETELEMENT_TABLE_ALIAS +
                ON + DATAVALUE_TABLE_ALIAS + "." + DataValueTableInfo.Columns.DATA_ELEMENT + EQ +
                DATASETELEMENT_TABLE_ALIAS + "." + DataSetDataElementLinkTableInfo.Columns.DATA_ELEMENT

        private val joinDataelement: String
            get() = INNER_JOIN + DataElementTableInfo.TABLE_INFO.name() + AS + DATAELEMENT_TABLE_ALIAS +
                ON + DATAVALUE_TABLE_ALIAS + "." + DataValueTableInfo.Columns.DATA_ELEMENT + EQ +
                DATAELEMENT_TABLE_ALIAS + "." + IdentifiableColumns.UID

        private val joinOrganisationUnit: String
            get() = INNER_JOIN + OrganisationUnitTableInfo.TABLE_INFO.name() + AS + ORGUNIT_TABLE_ALIAS +
                ON + DATAVALUE_TABLE_ALIAS + "." + DataValueTableInfo.Columns.ORGANISATION_UNIT + EQ +
                ORGUNIT_TABLE_ALIAS + "." + IdentifiableColumns.UID

        private val joinDataSet: String
            get() = INNER_JOIN + DataSetTableInfo.TABLE_INFO.name() + AS + DATASET_TABLE_ALIAS +
                ON + DATASETELEMENT_TABLE_ALIAS + "." + DataSetDataElementLinkTableInfo.Columns.DATA_SET + EQ +
                DATASET_TABLE_ALIAS + "." + IdentifiableColumns.UID +
                AND + PERIOD_TYPE + EQ + DATASET_TABLE_ALIAS + "." + DataSetTableInfo.Columns.PERIOD_TYPE

        private val joinCategoryOptionCombo: String
            get() = INNER_JOIN + CategoryOptionComboTableInfo.TABLE_INFO.name() + AS + COC_TABLE_ALIAS +
                ON + DATAVALUE_TABLE_ALIAS + "." + DataValueTableInfo.Columns.CATEGORY_OPTION_COMBO + EQ +
                COC_TABLE_ALIAS + "." + IdentifiableColumns.UID

        private val joinAttributeOptionCombo: String
            get() = INNER_JOIN + CategoryOptionComboTableInfo.TABLE_INFO.name() + AS + AOC_TABLE_ALIAS +
                ON + DATAVALUE_TABLE_ALIAS + "." + DataValueTableInfo.Columns.ATTRIBUTE_OPTION_COMBO + EQ +
                AOC_TABLE_ALIAS + "." + IdentifiableColumns.UID

        private val joinDataSetCompleteRegistration: String
            get() = LEFT_JOIN + DataSetCompleteRegistrationTableInfo.TABLE_INFO.name() + AS + COMPLETE_TABLE_ALIAS +
                ON + DATASET_UID + EQ +
                COMPLETE_TABLE_ALIAS + "." + DataSetCompleteRegistrationTableInfo.Columns.DATA_SET +
                AND + PERIOD + EQ +
                COMPLETE_TABLE_ALIAS + "." + DataSetCompleteRegistrationTableInfo.Columns.PERIOD +
                AND + ORGANISATION_UNIT_UID + EQ +
                COMPLETE_TABLE_ALIAS + "." + DataSetCompleteRegistrationTableInfo.Columns.ORGANISATION_UNIT +
                AND + ATTRIBUTE_OPTION_COMBO_UID + EQ +
                COMPLETE_TABLE_ALIAS + "." + DataSetCompleteRegistrationTableInfo.Columns.ATTRIBUTE_OPTION_COMBO

        private fun eq(column: String, state: State): String {
            return column + " = " + quotes(state)
        }

        private fun quotes(value: State): String {
            return CollectionsHelper.withSingleQuotationMarks(value.name)
        }
    }
}

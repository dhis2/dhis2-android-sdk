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

package org.hisp.dhis.android.core.arch.db.querybuilders.internal;

import org.hisp.dhis.android.core.arch.db.sqlorder.internal.SQLOrderType;
import org.hisp.dhis.android.core.arch.db.stores.projections.internal.LinkTableChildProjection;
import org.hisp.dhis.android.core.arch.db.tableinfos.TableInfo;
import org.hisp.dhis.android.core.common.CoreColumns;

import static org.hisp.dhis.android.core.arch.db.tableinfos.TableInfo.SORT_ORDER;
import static org.hisp.dhis.android.core.arch.helpers.CollectionsHelper.commaAndSpaceSeparatedArrayValues;
import static org.hisp.dhis.android.core.common.IdentifiableColumns.UID;

public class SQLStatementBuilderImpl implements SQLStatementBuilder {
    // TODO save TableInfo instead of separate files when architecture 1.0 is ready
    private final String tableName;
    private final String[] columns;
    private final String[] whereColumns;
    private final boolean hasSortOrder;

    private final static String WHERE = " WHERE ";
    private final static String LIMIT = " LIMIT ";
    private final static String FROM = " FROM ";
    private final static String SELECT = "SELECT ";
    private final static String AND = " AND ";
    private final static String ORDER_BY = " ORDER BY ";

    @SuppressWarnings("PMD.UseVarargs")
    SQLStatementBuilderImpl(String tableName, String[] columns, String[] updateWhereColumns, boolean hasSortOrder) {
        this.tableName = tableName;
        this.columns = columns.clone();
        this.whereColumns = updateWhereColumns.clone();
        this.hasSortOrder = hasSortOrder;
    }

    @SuppressWarnings("PMD.UseVarargs")
    public SQLStatementBuilderImpl(String tableName, String[] columns, String[] updateWhereColumns) {
        this(tableName, columns, updateWhereColumns, false);
    }

    public SQLStatementBuilderImpl(String tableName, CoreColumns columns) {
        this(tableName, columns.all().clone(), columns.whereUpdate().clone(), false);
    }

    public SQLStatementBuilderImpl(TableInfo tableInfo) {
        this(tableInfo.name(), tableInfo.columns().all().clone(), tableInfo.columns().whereUpdate().clone(),
                tableInfo.hasSortOrder());
    }

    private String commaSeparatedColumns() {
        return commaAndSpaceSeparatedArrayValues(columns);
    }

    private String commaSeparatedInterrogationMarks() {
        String[] array = new String[columns.length];
        for (int i = 0; i < columns.length; i++) {
            array[i] = "?";
        }
        return commaAndSpaceSeparatedArrayValues(array);
    }

    private String commaSeparatedColumnEqualInterrogationMark(String... cols) {
        String[] array = new String[cols.length];
        for (int i = 0; i < cols.length; i++) {
            array[i] = cols[i] + "=?";
        }
        return commaAndSpaceSeparatedArrayValues(array);
    }

    private String andSeparatedColumnEqualInterrogationMark(String... cols) {
        return commaSeparatedColumnEqualInterrogationMark(cols)
                .replace(",", " AND");
    }

    @Override
    public String getTableName() {
        return tableName;
    }

    @Override
    public String[] getColumns() {
        return columns.clone();
    }

    @Override
    public String insert() {
        return "INSERT INTO " + tableName + " (" + commaSeparatedColumns() + ") " +
                "VALUES (" + commaSeparatedInterrogationMarks() + ");";
    }

    @Override
    public String deleteById() {
        return "DELETE" + FROM + tableName + WHERE + UID + "=?;";
    }

    @Override
    public String selectUids() {
        return SELECT + UID + FROM + tableName;
    }

    @Override
    public String selectUidsWhere(String whereClause) {
        return SELECT + UID + FROM + tableName + WHERE + whereClause + ";";
    }

    @Override
    public String selectUidsWhere(String whereClause, String orderByClause) {
        return SELECT + UID + FROM + tableName + WHERE + whereClause + ORDER_BY + orderByClause + ";";
    }

    @Override
    public String selectColumnWhere(String column, String whereClause) {
        return SELECT + column + FROM + tableName + WHERE + whereClause + ";";
    }

    public String selectOneOrderedBy(String orderingColumName, SQLOrderType orderingType) {

        return SELECT + "*" +
                FROM + tableName +
                ORDER_BY + orderingColumName + " " + orderingType.name() +
                LIMIT + "1;";
    }

    public String selectChildrenWithLinkTable(LinkTableChildProjection projection, String parentUid,
                                              String whereClause) {
        String whereClauseStr = whereClause == null ? "" : AND + whereClause;

        return SELECT + "c.*" + FROM + tableName + " AS l, " +
                projection.childTableInfo.name() + " AS c" +
                WHERE + "l." + projection.childColumn + "=" + "c." + UID +
                AND + "l." + projection.parentColumn + "='" + parentUid + "'" +
                whereClauseStr +
                orderBySortOrderClause() + ";";
    }

    private String orderBySortOrderClause() {
        return hasSortOrder ? ORDER_BY + SORT_ORDER : "";
    }

    @Override
    public String selectByUid() {
        return selectWhere(andSeparatedColumnEqualInterrogationMark(UID));
    }

    @Override
    public String selectDistinct(String column) {
        return SELECT + "DISTINCT " + column + FROM + tableName;
    }

    @Override
    public String selectWhere(String whereClause) {
        return SELECT + "*" + FROM + tableName + WHERE + whereClause + ";";
    }

    @Override
    public String selectWhere(String whereClause, int limit) {
        return selectWhere(whereClause + LIMIT + limit);
    }

    @Override
    public String selectWhere(String whereClause, String orderByClause) {
        return selectWhere(whereClause + ORDER_BY + orderByClause);
    }

    @Override
    public String selectWhere(String whereClause, String orderByClause, int limit) {
        return selectWhere(whereClause + ORDER_BY + orderByClause + LIMIT + limit);
    }

    @Override
    public String selectAll() {
        return  SELECT + "*" + FROM + tableName;
    }

    @Override
    public String count() {
        return SELECT + "COUNT(*)" + FROM + tableName + ";";
    }

    @Override
    public String countWhere(String whereClause) {
        return SELECT + "COUNT(*)" + FROM + tableName + WHERE + whereClause + ";";
    }

    @Override
    public String countAndGroupBy(String column) {
        return SELECT + column + " , COUNT(*)" + FROM + tableName + " GROUP BY " + column + ";";
    }

    public String update() {
        return "UPDATE " + tableName + " SET " + commaSeparatedColumnEqualInterrogationMark(columns) +
                WHERE + UID + "=?;";
    }

    public String updateWhere() {
        // TODO refactor to only generate for object without uids store.
        String whereClause = whereColumns.length == 0 ? CoreColumns.ID + " = -1" :
                andSeparatedColumnEqualInterrogationMark(whereColumns);
        return "UPDATE " + tableName + " SET " + commaSeparatedColumnEqualInterrogationMark(columns) +
                WHERE + whereClause + ";";
    }

    public String deleteWhere() {
        String whereClause = whereColumns.length == 0 ? CoreColumns.ID + " = -1" :
                andSeparatedColumnEqualInterrogationMark(whereColumns);
        return "DELETE" + FROM + tableName + WHERE + whereClause + ";";
    }
}

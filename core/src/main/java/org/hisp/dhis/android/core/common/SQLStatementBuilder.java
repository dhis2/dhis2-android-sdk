/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
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

package org.hisp.dhis.android.core.common;

import org.hisp.dhis.android.core.utils.Utils;

import static org.hisp.dhis.android.core.utils.Utils.commaAndSpaceSeparatedArrayValues;

public class SQLStatementBuilder {
    final String tableName;
    public final String[] columns;
    private final String[] updateWhereColumns;
    private final String[] deleteWhereColumns;
    private final String[] selectWhereColumns;
    private final static String TEXT = " TEXT";

    @SuppressWarnings("PMD.UseVarargs")
    SQLStatementBuilder(String tableName, String[] columns, String[] updateWhereColumns, String[] deleteWhereColumns,
                        String[] selectWhereColumns) {
        this.tableName = tableName;
        this.columns = columns.clone();
        this.updateWhereColumns = updateWhereColumns.clone();
        this.deleteWhereColumns = deleteWhereColumns.clone();
        this.selectWhereColumns = selectWhereColumns.clone();
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

    public String insert() {
        return "INSERT INTO " + tableName + " (" + commaSeparatedColumns() + ") " +
                "VALUES (" + commaSeparatedInterrogationMarks() + ");";
    }

    String deleteById() {
        return "DELETE FROM " + tableName +
                " WHERE " + BaseIdentifiableObjectModel.Columns.UID + "=?;";
    }

    String deleteWhere() {
        String whereClause = deleteWhereColumns.length == 0 ? BaseModel.Columns.ID + " = -1" :
                andSeparatedColumnEqualInterrogationMark(deleteWhereColumns);
        return "DELETE FROM " + tableName + " WHERE " + whereClause + ";";
    }

    String selectWhere() {
        String whereClause = selectWhereColumns.length == 0 ? BaseModel.Columns.ID + " = -1" :
                andSeparatedColumnEqualInterrogationMark(selectWhereColumns);
        return  "SELECT * FROM " + tableName + " WHERE " + whereClause;
    }

    String selectUids() {
        return  "SELECT " + BaseIdentifiableObjectModel.Columns.UID + " FROM " + tableName;
    }

    String selectAll() {
        return  "SELECT " + commaSeparatedColumns() + " FROM " + tableName;
    }

    public String update() {
        return "UPDATE " + tableName + " SET " + commaSeparatedColumnEqualInterrogationMark(columns) +
                " WHERE " + BaseIdentifiableObjectModel.Columns.UID + "=?;";
    }

    String updateWhere() {
        // TODO refactor to only generate for object without uids store.
        String whereClause = updateWhereColumns.length == 0 ? BaseModel.Columns.ID + " = -1" :
                andSeparatedColumnEqualInterrogationMark(updateWhereColumns);
        return "UPDATE " + tableName + " SET " + commaSeparatedColumnEqualInterrogationMark(columns) +
                " WHERE " + whereClause + ";";
    }

    @SuppressWarnings("PMD.UseVarargs")
    private static String createTableWrapper(String tableName, String[] columnsWithAttributes) {
        return "CREATE TABLE " + tableName + " (" +
                commaAndSpaceSeparatedArrayValues(columnsWithAttributes) + ");";
    }

    private static String[] idColumn() {
        return new String[]{BaseModel.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT"};
    }

    private static String[] identifiableColumns() {
        return Utils.appendInNewArray(idColumn(),
                BaseIdentifiableObjectModel.Columns.UID + TEXT + " NOT NULL UNIQUE",
                BaseIdentifiableObjectModel.Columns.CODE + TEXT,
                BaseIdentifiableObjectModel.Columns.NAME + TEXT,
                BaseIdentifiableObjectModel.Columns.DISPLAY_NAME + TEXT,
                BaseIdentifiableObjectModel.Columns.CREATED + TEXT,
                BaseIdentifiableObjectModel.Columns.LAST_UPDATED + TEXT
        );
    }

    private static String[] nameableColumns() {
        return Utils.appendInNewArray(identifiableColumns(),
                BaseNameableObjectModel.Columns.SHORT_NAME + TEXT,
                BaseNameableObjectModel.Columns.DISPLAY_SHORT_NAME + TEXT,
                BaseNameableObjectModel.Columns.DESCRIPTION + TEXT,
                BaseNameableObjectModel.Columns.DISPLAY_DESCRIPTION + TEXT
        );
    }

    public static String createModelTable(String tableName, String... columnsWithAttributes) {
        return createTableWrapper(tableName, Utils.appendInNewArray(idColumn(), columnsWithAttributes));
    }

    public static String createIdentifiableModelTable(String tableName, String... columnsWithAttributes) {
        return createTableWrapper(tableName, Utils.appendInNewArray(identifiableColumns(), columnsWithAttributes));
    }

    public static String createNameableModelTable(String tableName, String... columnsWithAttributes) {
        return createTableWrapper(tableName, Utils.appendInNewArray(nameableColumns(), columnsWithAttributes));
    }
}

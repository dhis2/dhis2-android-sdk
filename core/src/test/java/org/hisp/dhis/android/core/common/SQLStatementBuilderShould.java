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
package org.hisp.dhis.android.core.common;

import org.hisp.dhis.android.core.arch.db.tableinfos.LinkTableChildProjection;
import org.hisp.dhis.android.core.category.CategoryTableInfo;
import org.hisp.dhis.android.core.dataset.DataSetOrganisationUnitLinkTableInfo;
import org.hisp.dhis.android.core.dataset.DataSetTableInfo;
import org.hisp.dhis.android.core.legendset.LegendSetModel;
import org.hisp.dhis.android.core.legendset.LegendTableInfo;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitTableInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class SQLStatementBuilderShould {

    private final static String TABLE_NAME = "Test_Table";
    private final static String COL_1 = "Test_Column_Name1";
    private final static String COL_2 = "Test_Column_Name2";

    private final static String[] columns = new String[]{COL_1, COL_2};

    private SQLStatementBuilder builder = new SQLStatementBuilder(TABLE_NAME, columns, columns, false);

    private static final LinkTableChildProjection CHILD_PROJECTION = new LinkTableChildProjection(
            CategoryTableInfo.TABLE_INFO,
            COL_1,
            COL_2);

    @Before
    public void setUp() throws IOException {

    }

    @Test
    public void generate_insert_statement() {
        assertThat(builder.insert()).isEqualTo(
                "INSERT INTO Test_Table (Test_Column_Name1, Test_Column_Name2) VALUES (?, ?);"
        );
    }

    @Test
    public void generate_update_statement() {
        assertThat(builder.update()).isEqualTo(
                "UPDATE Test_Table SET Test_Column_Name1=?, Test_Column_Name2=? WHERE uid=?;"
        );
    }

    @Test
    public void generate_delete_statement() {
        assertThat(builder.deleteById()).isEqualTo(
                "DELETE FROM Test_Table WHERE uid=?;"
        );
    }

    @Test
    public void generate_create_model_table_statement() {
        String statement = SQLStatementBuilder.createModelTable("Test_Table",
                "Test_Column_Name TEXT");

        assertThat(statement).isEqualTo(
                "CREATE TABLE Test_Table (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "Test_Column_Name TEXT);");
    }

    @Test
    public void generate_create_identifiable_table_statement() {
        String statement = SQLStatementBuilder.createIdentifiableModelTable("Test_Table",
                "Test_Column_Name TEXT");

        assertThat(statement).isEqualTo(
                "CREATE TABLE Test_Table (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        BaseIdentifiableObjectModel.Columns.UID + " TEXT NOT NULL UNIQUE, " +
                        BaseIdentifiableObjectModel.Columns.CODE + " TEXT, " +
                        BaseIdentifiableObjectModel.Columns.NAME + " TEXT, " +
                        BaseIdentifiableObjectModel.Columns.DISPLAY_NAME + " TEXT, " +
                        BaseIdentifiableObjectModel.Columns.CREATED + " TEXT, " +
                        BaseIdentifiableObjectModel.Columns.LAST_UPDATED + " TEXT, " +
                        "Test_Column_Name TEXT);");
    }

    @Test
    public void generate_create_nameable_table_statement() {
        String statement = SQLStatementBuilder.createNameableModelTable("Test_Table",
                "Test_Column_Name TEXT");

        assertThat(statement).isEqualTo(
                "CREATE TABLE Test_Table (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        BaseIdentifiableObjectModel.Columns.UID + " TEXT NOT NULL UNIQUE, " +
                        BaseIdentifiableObjectModel.Columns.CODE + " TEXT, " +
                        BaseIdentifiableObjectModel.Columns.NAME + " TEXT, " +
                        BaseIdentifiableObjectModel.Columns.DISPLAY_NAME + " TEXT, " +
                        BaseIdentifiableObjectModel.Columns.CREATED + " TEXT, " +
                        BaseIdentifiableObjectModel.Columns.LAST_UPDATED + " TEXT, " +
                        BaseNameableObjectModel.Columns.SHORT_NAME + " TEXT, " +
                        BaseNameableObjectModel.Columns.DISPLAY_SHORT_NAME + " TEXT, " +
                        BaseNameableObjectModel.Columns.DESCRIPTION + " TEXT, " +
                        BaseNameableObjectModel.Columns.DISPLAY_DESCRIPTION + " TEXT, " +
                        "Test_Column_Name TEXT);");
    }

    @Test
    public void generate_create_organisation_unit_table_statement() {
        String createOrganisationUnitTable =
                SQLStatementBuilder.createNameableModelTable(OrganisationUnitTableInfo.TABLE_INFO.name(),
                        "path TEXT," +
                                "openingDate TEXT," +
                                "closedDate TEXT," +
                                "level INTEGER," +
                                "parent TEXT," +
                                "displayNamePath TEXT"
                );

        assertThat(createOrganisationUnitTable).isEqualTo("CREATE TABLE OrganisationUnit (_id INTEGER PRIMARY KEY AUTOINCREMENT, uid TEXT NOT NULL UNIQUE, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, shortName TEXT, displayShortName TEXT, description TEXT, displayDescription TEXT, path TEXT,openingDate TEXT,closedDate TEXT,level INTEGER,parent TEXT,displayNamePath TEXT);");
    }

    @Test
    public void generate_create_data_set_organisation_unit_table_statement() {
        String createDataSetOrganisationUnitLinkTable =
                SQLStatementBuilder.createModelTable(DataSetOrganisationUnitLinkTableInfo.TABLE_INFO.name(),
                        DataSetOrganisationUnitLinkTableInfo.Columns.DATA_SET + " TEXT NOT NULL," +
                                DataSetOrganisationUnitLinkTableInfo.Columns.ORGANISATION_UNIT + " TEXT NOT NULL," +
                                " FOREIGN KEY (" + DataSetOrganisationUnitLinkTableInfo.Columns.DATA_SET + ") " +
                                " REFERENCES " + DataSetTableInfo.TABLE_INFO.name() + " (" + BaseIdentifiableObjectModel.Columns.UID + ")" +
                                " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED," +
                                " FOREIGN KEY (" + DataSetOrganisationUnitLinkTableInfo.Columns.ORGANISATION_UNIT + ") " +
                                " REFERENCES " + OrganisationUnitTableInfo.TABLE_INFO.name() + " (" +
                                BaseIdentifiableObjectModel.Columns.UID + ")" +
                                " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED," +
                                " UNIQUE (" + DataSetOrganisationUnitLinkTableInfo.Columns.DATA_SET + ", " +
                                DataSetOrganisationUnitLinkTableInfo.Columns.ORGANISATION_UNIT + ")"
                );

        assertThat(createDataSetOrganisationUnitLinkTable).isEqualTo("CREATE TABLE DataSetOrganisationUnitLink (_id INTEGER PRIMARY KEY AUTOINCREMENT, dataSet TEXT NOT NULL,organisationUnit TEXT NOT NULL, FOREIGN KEY (dataSet)  REFERENCES DataSet (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY (organisationUnit)  REFERENCES OrganisationUnit (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, UNIQUE (dataSet, organisationUnit));");
    }

    @Test
    public void generate_create_legend_table_statement() {
        String LEGEND_SET = "legendSet";
        String START_VALUE = "startValue";
        String END_VALUE = "endValue";
        String COLOR = "color";

        String createLegendTable =
                SQLStatementBuilder.createIdentifiableModelTable(LegendTableInfo.TABLE_INFO.name(),
                        START_VALUE + " REAL," +
                                END_VALUE + " REAL," +
                                COLOR + " TEXT," +
                                LEGEND_SET + " TEXT," +
                                " FOREIGN KEY ( " + LEGEND_SET + ")" +
                                " REFERENCES " + LegendSetModel.TABLE + " (" + LegendSetModel.Columns.UID + ")" +
                                " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED"
                );

        assertThat(createLegendTable).isEqualTo("CREATE TABLE Legend (_id INTEGER PRIMARY KEY AUTOINCREMENT, uid TEXT NOT NULL UNIQUE, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, startValue REAL,endValue REAL,color TEXT,legendSet TEXT, FOREIGN KEY ( legendSet) REFERENCES LegendSet (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);");
    }

    @Test
    public void generate_select_where_statement() {
        assertThat(builder.selectWhere("WH_CLAUSE")).isEqualTo(
                "SELECT * FROM Test_Table WHERE WH_CLAUSE;"
        );
    }

    @Test
    public void generate_select_where_with_limit_statement() {
        assertThat(builder.selectWhereWithLimit("WH_CLAUSE", 3)).isEqualTo(
                "SELECT * FROM Test_Table WHERE WH_CLAUSE LIMIT 3;"
        );
    }

    @Test
    public void generate_count_where_statement() {
        assertThat(builder.countWhere("WH_CLAUSE")).isEqualTo(
                "SELECT COUNT(*) FROM Test_Table WHERE WH_CLAUSE;"
        );
    }

    @Test
    public void generate_count_statement() {
        assertThat(builder.count()).isEqualTo(
                "SELECT COUNT(*) FROM Test_Table;"
        );
    }

    @Test
    public void generate_select_by_uid_statement() {
        assertThat(builder.selectByUid()).isEqualTo(
                "SELECT * FROM Test_Table WHERE uid=?;"
        );
    }

    @Test
    public void generate_select_children_with_link_table() {
        assertThat(builder.selectChildrenWithLinkTable(CHILD_PROJECTION, "UID", null)).isEqualTo(
                "SELECT c.* FROM Test_Table AS l, Category AS c WHERE l." + COL_2 + "=c.uid AND l." + COL_1 + "='UID';"
        );
    }

    @Test
    public void generate_select_children_with_link_table_and_where_clause() {
        assertThat(builder.selectChildrenWithLinkTable(CHILD_PROJECTION, "UID", "l.bla=1")).isEqualTo(
                "SELECT c.* FROM Test_Table AS l, Category AS c WHERE l." + COL_2 + "=c.uid AND l." + COL_1 + "='UID' AND l.bla=1;"
        );
    }

    @Test
    public void generate_select_children_with_link_table_with_sort_order() {
        SQLStatementBuilder builderWithSortOrder = new SQLStatementBuilder(TABLE_NAME, columns, columns, true);
        assertThat(builderWithSortOrder.selectChildrenWithLinkTable(CHILD_PROJECTION, "UID", null)).isEqualTo(
                "SELECT c.* FROM Test_Table AS l, Category AS c WHERE l." + COL_2 + "=c.uid AND l." + COL_1 + "='UID' ORDER BY sortOrder;"
        );
    }
}
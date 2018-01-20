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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class SQLStatementBuilderShould {

    private SQLStatementBuilder builder;

    @Before
    public void setUp() throws IOException {
        String[] columns = new String[]{
                "Test_Column_Name1",
                "Test_Column_Name2"
        };
        this.builder = new SQLStatementBuilder("Test_Table", columns, columns);
    }

    @Test
    public void generate_insert_statement() throws Exception {
        assertThat(builder.insert()).isEqualTo(
                "INSERT INTO Test_Table (Test_Column_Name1, Test_Column_Name2) VALUES (?, ?);"
        );
    }

    @Test
    public void generate_update_statement() throws Exception {
        assertThat(builder.update()).isEqualTo(
                "UPDATE Test_Table SET Test_Column_Name1=?, Test_Column_Name2=? WHERE uid=?;"
        );
    }

    @Test
    public void generate_delete_statement() throws Exception {
        assertThat(builder.deleteById()).isEqualTo(
                "DELETE FROM Test_Table WHERE uid=?;"
        );
    }

    @Test
    public void generate_create_model_table_statement() throws Exception {
        String statement = SQLStatementBuilder.createModelTable("Test_Table",
                "Test_Column_Name TEXT");

        assertThat(statement).isEqualTo(
                "CREATE TABLE Test_Table (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "Test_Column_Name TEXT);");
    }

    @Test
    public void generate_create_identifiable_table_statement() throws Exception {
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
    public void generate_create_nameable_table_statement() throws Exception {
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
}

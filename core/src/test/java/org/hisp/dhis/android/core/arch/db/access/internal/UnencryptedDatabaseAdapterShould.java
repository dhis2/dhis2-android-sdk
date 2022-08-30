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

package org.hisp.dhis.android.core.arch.db.access.internal;

import android.database.sqlite.SQLiteDatabase;

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

public class UnencryptedDatabaseAdapterShould {

    @Mock
    SQLiteDatabase database;
    
    @Mock
    StatementWrapper sqLiteStatement;

    private DatabaseAdapter sqLiteDatabaseAdapter; // the class we are testing

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        sqLiteDatabaseAdapter = new UnencryptedDatabaseAdapter(database, "dbName");
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_when_provide_null_db_open_helper() {
        new UnencryptedDatabaseAdapter(null, "dbName");
    }

    @Test
    public void verify_statement_is_compiled_when_set_a_query_to_be_compiled_in_database_adapter() {
        String sql = "INSERT VALUE INTO TABLE";
        sqLiteDatabaseAdapter.compileStatement(sql);
        verify(database).compileStatement(sql);
    }

    @Test
    public void verify_query_on_readable_data_base_when_set_query_in_data_base_adapter() {
        String sql = "SELECT * FROM TABLE";
        sqLiteDatabaseAdapter.rawQuery(sql, (String[]) null);
        verify(database).rawQuery(sql, null);
    }

    @Test
    public void delete_in_data_base_when_delete_in_data_base_adapter() {
        sqLiteDatabaseAdapter.delete(null, null, null);
        verify(database).delete(null, null, null);
    }

    @Test
    public void verify_the_statements_are_executed_in_data_base_when_execute_insert_in_data_base_adapter() {
        sqLiteDatabaseAdapter.executeInsert(sqLiteStatement);
        verify(sqLiteStatement).executeInsert();

        sqLiteDatabaseAdapter.executeUpdateDelete(sqLiteStatement);
        verify(sqLiteStatement).executeUpdateDelete();
    }

    @Test
    public void verify_the_transaction_begin_in_data_base_when_execute_begin_new_transaction_on_data_base_adapter() {
        sqLiteDatabaseAdapter.beginNewTransaction();
        verify(database).beginTransaction();
    }
}
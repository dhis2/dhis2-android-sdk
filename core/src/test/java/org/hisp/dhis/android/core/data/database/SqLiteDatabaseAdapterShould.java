package org.hisp.dhis.android.core.data.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

public class SqLiteDatabaseAdapterShould {

    @Mock
    SQLiteDatabase writableDatabase;

    @Mock
    SQLiteDatabase readableDatabase;

    @Mock
    DbOpenHelper dbOpenHelper;

    @Mock
    SQLiteStatement sqLiteStatement;

    SqLiteDatabaseAdapter sqLiteDatabaseAdapter; // the class we are testing

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(dbOpenHelper.getWritableDatabase()).thenReturn(writableDatabase);
        Mockito.when(dbOpenHelper.getReadableDatabase()).thenReturn(readableDatabase);
        sqLiteDatabaseAdapter = new SqLiteDatabaseAdapter(dbOpenHelper);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_when_provide_null_db_open_helper() throws Exception {
        new SqLiteDatabaseAdapter(null);
    }

    @Test
    public void verify_statement_is_compiled_when_set_a_query_to_be_compiled_in_database_adapter() throws Exception {
        String sql = "INSERT VALUE INTO TABLE";
        sqLiteDatabaseAdapter.compileStatement(sql);
        verify(writableDatabase).compileStatement(sql);
    }

    @Test
    public void verify_query_on_readable_data_base_when_set_query_in_data_base_adapter() throws Exception {
        String sql = "SELECT * FROM TABLE";
        sqLiteDatabaseAdapter.query(sql, (String[]) null);
        verify(readableDatabase).rawQuery(sql, null);
    }

    @Test
    public void delete_in_data_base_when_delete_in_data_base_adapter() throws Exception {
        sqLiteDatabaseAdapter.delete(null, null, null);
        verify(writableDatabase).delete(null, null, null);
    }

    @Test
    public void verify_the_statements_are_executed_in_data_base_when_execute_insert_in_data_base_adapter() throws Exception {
        sqLiteDatabaseAdapter.executeInsert("TABLE", sqLiteStatement);
        verify(sqLiteStatement).executeInsert();

        sqLiteDatabaseAdapter.executeUpdateDelete("TABLE", sqLiteStatement);
        verify(sqLiteStatement).executeUpdateDelete();
    }

    @Test
    public void verify_the_transaction_begin_in_data_base_when_execute_begin_new_transaction_on_data_base_adapter() throws Exception {
        sqLiteDatabaseAdapter.beginNewTransaction();
        verify(writableDatabase).beginTransaction();
    }
}
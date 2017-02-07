package org.hisp.dhis.android.core.data.database;

import android.database.sqlite.SQLiteDatabase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

public class SqLiteDatabaseAdapterTests {

    @Mock
    SQLiteDatabase writableDatabase;

    @Mock
    SQLiteDatabase readableDatabase;

    @Mock
    DbOpenHelper dbOpenHelper;

    SqLiteDatabaseAdapter sqLiteDatabaseAdapter; // the class we are testing

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(dbOpenHelper.getWritableDatabase()).thenReturn(writableDatabase);
        Mockito.when(dbOpenHelper.getReadableDatabase()).thenReturn(readableDatabase);
        sqLiteDatabaseAdapter = new SqLiteDatabaseAdapter(dbOpenHelper);
    }

    @Test(expected = IllegalArgumentException.class)
    public void providingNullDbOpenHelper_shouldThrowError() throws Exception {
        new SqLiteDatabaseAdapter(null);
    }

    @Test
    public void statementIsCompiledOnWritableDatabase() throws Exception {
        String sql = "INSERT VALUE INTO TABLE";
        sqLiteDatabaseAdapter.compileStatement(sql);
        verify(writableDatabase).compileStatement(sql);
    }

    @Test
    public void queryIsRunOnReadableDatabase() throws Exception {
        String sql = "SELECT * FROM TABLE";
        sqLiteDatabaseAdapter.query(sql, null);
        verify(readableDatabase).rawQuery(sql, null);
    }

    @Test
    public void deleteIsRunOnWritableDatabase() throws Exception {
        sqLiteDatabaseAdapter.delete(null, null, null);
        verify(writableDatabase).delete(null, null, null);
    }

    @Test
    public void transactionIsStartedOnWritableDatabase() throws Exception {
        sqLiteDatabaseAdapter.beginTransaction();
        verify(writableDatabase).beginTransaction();
    }

    @Test
    public void transactionSuccessfulIsSetOnWritableDatabase() throws Exception {
        sqLiteDatabaseAdapter.setTransactionSuccessful();
        verify(writableDatabase).setTransactionSuccessful();
    }

    @Test
    public void transactionIsEndedOnWritableDatabase() throws Exception {
        sqLiteDatabaseAdapter.endTransaction();
        verify(writableDatabase).endTransaction();
    }

}
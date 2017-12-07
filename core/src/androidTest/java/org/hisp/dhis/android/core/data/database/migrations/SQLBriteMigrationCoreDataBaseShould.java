package org.hisp.dhis.android.core.data.database.migrations;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hisp.dhis.android.core.data.database.SqliteChecker.ifTableExist;
import static org.hisp.dhis.android.core.data.database.SqliteChecker.ifValueExist;
import static org.hisp.dhis.android.core.data.database.SqliteChecker.isFieldExist;
import static org.junit.Assert.assertThat;

import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.common.truth.Truth;

import org.hisp.dhis.android.core.data.database.SqLiteDatabaseAdapter;
import org.hisp.dhis.android.core.user.UserModel;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class SQLBriteMigrationCoreDataBaseShould {
    public static final String migrationDir = "migrations/core_database";
    static String dbName= "test2db";

    @Before
    public void setup() throws IOException {
        restartCoreDataBase();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        InstrumentationRegistry.getContext().deleteDatabase(dbName);
    }

    public void restartCoreDataBase(){
        InstrumentationRegistry.getContext().deleteDatabase(dbName);
        DbOpenHelperMigrationTester dbOpenHelper = new DbOpenHelperMigrationTester(InstrumentationRegistry.getContext()
                , dbName);
        SQLiteDatabase sqLiteDatabase = dbOpenHelper.getWritableDatabase();
        SqLiteDatabaseAdapter databaseAdapter = new SqLiteDatabaseAdapter(dbOpenHelper);
        Truth.assertThat(sqLiteDatabase).isNotNull();
        sqLiteDatabase.close();
    }
    @Test
    public void be_able_to_load_and_parse_default_migrations_from_input_stream() throws IOException {
        SQLBriteTestOpenHelper briteOpenHelper = new SQLBriteTestOpenHelper(InstrumentationRegistry.getContext(), dbName, 1, true, migrationDir);
        Map parsed = briteOpenHelper.parse();

        assertThat(parsed.get("up"), is(equalTo(null)));
        assertThat(parsed.get("down"), is(equalTo(null)));
        assertThat(parsed.get("seeds"), is(equalTo(null)));
    }



    @Test
    public void have_new_column_when_migration_add_column() throws IOException {
        SQLBriteTestOpenHelper briteOpenHelper = new SQLBriteTestOpenHelper(InstrumentationRegistry.getContext(), dbName, 2, true, migrationDir);
        assertThat(isFieldExist(UserModel.TABLE, "testColumn", briteOpenHelper.getWritableDatabase()), is(true));
    }


    @Test
    public void have_new_table_when_migration_add_table() {
        SQLBriteTestOpenHelper briteOpenHelper = new SQLBriteTestOpenHelper(InstrumentationRegistry.getContext(), dbName, 3, true, migrationDir);
        assertThat(isFieldExist(UserModel.TABLE, "testColumn", briteOpenHelper.getWritableDatabase()), is(true));
        assertThat(ifTableExist("TestTable", briteOpenHelper.getWritableDatabase()), is(true));
    }

    @Test
    public void have_renamed_table_when_migration_rename_table() {
        SQLBriteTestOpenHelper briteOpenHelper = new SQLBriteTestOpenHelper(InstrumentationRegistry.getContext(), dbName, 5, true, migrationDir);
        assertThat(isFieldExist(UserModel.TABLE, "testColumn", briteOpenHelper.getWritableDatabase()), is(true));
        assertThat(ifTableExist("TestTableRenamed", briteOpenHelper.getWritableDatabase()), is(true));
    }
    @Test
    public void have_inserted_value_when_migration_insert_value() {
        SQLBriteTestOpenHelper briteOpenHelper = new SQLBriteTestOpenHelper(InstrumentationRegistry.getContext(), dbName, 5, true, migrationDir);

        assertThat(ifTableExist("TestTableRenamed", briteOpenHelper.getWritableDatabase()), is(true));
        assertThat(ifValueExist("TestTableRenamed", "testColumn", "not inserted value", briteOpenHelper.getWritableDatabase()), is(false));
        assertThat(ifValueExist("TestTableRenamed", "testColumn", "1", briteOpenHelper.getWritableDatabase()), is(true));
    }
    @Test
    public void not_have_inserted_value_when_migration_removes_value() {
        SQLBriteTestOpenHelper briteOpenHelper = new SQLBriteTestOpenHelper(InstrumentationRegistry.getContext(), dbName, 6, true, migrationDir);

        assertThat(ifTableExist("TestTableRenamed", briteOpenHelper.getWritableDatabase()), is(true));
        assertThat(ifValueExist("TestTableRenamed", "testColumn", "not inserted value", briteOpenHelper.getWritableDatabase()), is(false));
        assertThat(ifValueExist("TestTableRenamed", "testColumn", "1", briteOpenHelper.getWritableDatabase()), is(false));
    }
    @Test
    public void have_drop_table_when_migration_delete_table() {
        SQLBriteTestOpenHelper briteOpenHelper = new SQLBriteTestOpenHelper(InstrumentationRegistry.getContext(), dbName, 7, true, migrationDir);
        assertThat(ifTableExist("NewTestTable", briteOpenHelper.getWritableDatabase()), is(true));
        assertThat(ifTableExist("Temp_TestTableRenamed", briteOpenHelper.getWritableDatabase()), is(false));
        assertThat(ifTableExist("TestTableRenamed", briteOpenHelper.getWritableDatabase()), is(false));
    }
}

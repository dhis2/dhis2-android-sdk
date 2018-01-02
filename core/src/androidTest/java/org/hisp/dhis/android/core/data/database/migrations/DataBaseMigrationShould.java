package org.hisp.dhis.android.core.data.database.migrations;

import static org.hamcrest.CoreMatchers.is;
import static org.hisp.dhis.android.core.data.database.SqliteCheckerUtility.ifTableExist;
import static org.hisp.dhis.android.core.data.database.SqliteCheckerUtility.ifValueExist;
import static org.hisp.dhis.android.core.data.database.SqliteCheckerUtility.isFieldExist;
import static org.junit.Assert.assertThat;

import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.category.CategoryCategoryComboLinkModel;
import org.hisp.dhis.android.core.category.CategoryComboModel;
import org.hisp.dhis.android.core.category.CategoryModel;
import org.hisp.dhis.android.core.category.CategoryOptionComboModel;
import org.hisp.dhis.android.core.category.CategoryCategoryOptionLinkModel;
import org.hisp.dhis.android.core.category.CategoryOptionModel;
import org.hisp.dhis.android.core.configuration.ConfigurationModel;
import org.hisp.dhis.android.core.data.api.BasicAuthenticatorFactory;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.DbOpenHelper;
import org.hisp.dhis.android.core.data.database.SqLiteDatabaseAdapter;
import org.hisp.dhis.android.core.dataelement.DataElementModel;
import org.hisp.dhis.android.core.event.EventModel;
import org.hisp.dhis.android.core.program.ProgramModel;
import org.hisp.dhis.android.core.user.UserModel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockWebServer;

@RunWith(AndroidJUnit4.class)
public class DataBaseMigrationShould {
    private MockWebServer mockWebServer;

    private D2 d2;
    private DatabaseAdapter databaseAdapter;
    DbOpenHelper dbOpenHelper;

    public static final String realMigrationDir = "migrations/real_migrations";
    public static final String exampleMigrationsDir = "migrations/example_migrations";
    public static final String databaseSqlVersion1 = "db_version_1.sql";
    public static final String databaseSqlVersion2 = "db_version_2.sql";
    static String dbName= null;
    private SQLiteDatabase databaseInMemory;

    @Before
    public void deleteDB(){
        mockWebServer = new MockWebServer();
        try {
            mockWebServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(dbName!=null) {
            InstrumentationRegistry.getContext().deleteDatabase(dbName);
        }
        dbOpenHelper = null;
        databaseInMemory = null;
    }

    @After
    public void tearDown() throws Exception {
        if(dbName!=null) {
            InstrumentationRegistry.getContext().deleteDatabase(dbName);
        }
    }


    @Test
    @MediumTest
    public void have_user_table_after_first_migration() throws IOException {
        initCoreDataBase(dbName, 1, exampleMigrationsDir, databaseSqlVersion1);
        assertThat(ifTableExist(UserModel.TABLE, databaseAdapter), is(true));
    }

    @Test
    @MediumTest
    public void have_category_table_after_first_migration() throws IOException {
        initCoreDataBase(dbName, 1, realMigrationDir, databaseSqlVersion1);
        initCoreDataBase(dbName, 2, realMigrationDir, "");
        assertVersion2MigrationChanges(databaseAdapter);
    }

    @Test
    @MediumTest
    public void have_categoryCombo_columns_after_first_migration() throws IOException {
        initCoreDataBase(dbName, 1, realMigrationDir, databaseSqlVersion1);
        initCoreDataBase(dbName, 2, realMigrationDir, "");
        assertThat(isFieldExist(DataElementModel.TABLE, DataElementModel.Columns.CATEGORY_COMBO, databaseAdapter), is(true));
        assertThat(isFieldExist(ProgramModel.TABLE, ProgramModel.Columns.CATEGORY_COMBO, databaseAdapter), is(true));
    }

    @Test
    @MediumTest
    public void have_categoryCombo_columns_after_create_version_2() throws IOException {
        buildD2(initCoreDataBase(dbName, 2, realMigrationDir, databaseSqlVersion2));
        assertVersion2MigrationChanges(d2.databaseAdapter());
    }

    @Test
    @MediumTest
    public void have_categoryCombo_columns_after_create_last_version() throws IOException {
        buildD2(initCoreDataBase(dbName, 2, realMigrationDir, ""));
        assertThat(isFieldExist(DataElementModel.TABLE, DataElementModel.Columns.CATEGORY_COMBO, d2.databaseAdapter()), is(true));
        assertThat(isFieldExist(ProgramModel.TABLE, ProgramModel.Columns.CATEGORY_COMBO, d2.databaseAdapter()), is(true));
    }

    @Test
    @MediumTest
    public void have_new_column_when_up_migration_add_column() throws IOException {
        initCoreDataBase(dbName, 1, exampleMigrationsDir, databaseSqlVersion1);
        initCoreDataBase(dbName, 3, exampleMigrationsDir, "");
        assertThat(isFieldExist(UserModel.TABLE, "testColumn", databaseAdapter), is(true));
    }

    @Test
    @MediumTest
    public void have_new_value_when_seed_migration_add_row() {
        initCoreDataBase(dbName, 1, exampleMigrationsDir, databaseSqlVersion1);
        initCoreDataBase(dbName, 3, exampleMigrationsDir, "");
        assertThat(isFieldExist(UserModel.TABLE, "testColumn", databaseAdapter), is(true));
        assertThat(ifTableExist("TestTable", databaseAdapter), is(true));
        assertThat(ifValueExist("TestTable", "testColumn","1", databaseAdapter), is(true));
    }

    @Test
    @MediumTest
    public void not_have_category_table_after_downgrade_with_database_version_2() throws IOException {
        initCoreDataBase(dbName, 2, realMigrationDir, databaseSqlVersion2);
        initCoreDataBase(dbName, 1, realMigrationDir, "");
        //TODO  remove this tables in 2.yaml drop
        //assertThat(ifTableExist(CategoryModel.TABLE, databaseAdapter), is(false));
        //assertThat(ifTableExist(CategoryOptionModel.TABLE, databaseAdapter), is(false));
        assertThat(ifTableExist(CategoryOptionComboModel.TABLE, databaseAdapter), is(false));
        assertThat(ifTableExist(CategoryComboModel.TABLE, databaseAdapter), is(false));
        assertThat(ifTableExist(CategoryCategoryComboLinkModel.TABLE, databaseAdapter), is(false));
        assertThat(ifTableExist(CategoryCategoryOptionLinkModel.TABLE, databaseAdapter), is(false));
    }

    @Test
    @MediumTest
    public void not_have_category_table_after_downgrade_with_real_sql_database() throws IOException {
        initCoreDataBase(dbName, 2, realMigrationDir, "");
        initCoreDataBase(dbName, 1, realMigrationDir, "");
        //TODO remove Category and CategoryOption tables in 2.yaml drop migration
        //assertThat(ifTableExist(CategoryModel.TABLE, databaseAdapter), is(false));
        //assertThat(ifTableExist(CategoryOptionModel.TABLE, databaseAdapter), is(false));
        assertThat(ifTableExist(CategoryComboModel.TABLE, databaseAdapter), is(false));
        assertThat(ifTableExist(CategoryOptionComboModel.TABLE, databaseAdapter), is(false));
        assertThat(ifTableExist(CategoryCategoryComboLinkModel.TABLE, databaseAdapter), is(false));
        assertThat(ifTableExist(CategoryCategoryOptionLinkModel.TABLE, databaseAdapter), is(false));
    }

    @Test
    @MediumTest
    public synchronized void have_dropped_table_when_down_migration_drop_table() {
        initCoreDataBase(dbName, 1, exampleMigrationsDir, databaseSqlVersion1);
        initCoreDataBase(dbName, 3, exampleMigrationsDir, databaseSqlVersion1);
        initCoreDataBase(dbName, 1, exampleMigrationsDir, "");
        assertThat(isFieldExist(UserModel.TABLE, "testColumn", databaseAdapter), is(true));
        assertThat(ifTableExist("TestTable", databaseAdapter), is(false));
    }

    public DatabaseAdapter initCoreDataBase(String dbName, int databaseVersion, String testPath,
            String databaseSqlVersion) {
        if (databaseAdapter == null) {
            dbOpenHelper = new DbOpenHelper(
                    InstrumentationRegistry.getTargetContext().getApplicationContext()
                    , dbName, databaseVersion, testPath, databaseSqlVersion);
            databaseAdapter = new SqLiteDatabaseAdapter(dbOpenHelper);
            databaseInMemory = ((SqLiteDatabaseAdapter) databaseAdapter).database();
        } else if (dbName == null) {
            if (databaseInMemory.getVersion() < databaseVersion) {
                dbOpenHelper.onUpgrade(databaseInMemory, databaseInMemory.getVersion(),
                        databaseVersion);
                databaseInMemory.setVersion(databaseVersion);
            } else if (databaseInMemory.getVersion() > databaseVersion) {
                dbOpenHelper.onDowngrade(databaseInMemory, databaseInMemory.getVersion(),
                        databaseVersion);
                databaseInMemory.setVersion(databaseVersion);
            }
        }
        return databaseAdapter;
    }

    private void buildD2(DatabaseAdapter databaseAdapter) {
        ConfigurationModel config = ConfigurationModel.builder()
                .serverUrl(mockWebServer.url("/"))
                .build();
        d2 = new D2.Builder()
                .configuration(config)
                .okHttpClient(new OkHttpClient.Builder()
                        .addInterceptor(BasicAuthenticatorFactory.create(databaseAdapter))
                        .build())
                .databaseAdapter(databaseAdapter).build();
    }

    private void assertVersion2MigrationChanges(DatabaseAdapter databaseAdapter) {
        assertThat(ifTableExist(CategoryModel.TABLE, databaseAdapter), is(true));
        assertThat(ifTableExist(CategoryComboModel.TABLE, databaseAdapter), is(true));
        assertThat(ifTableExist(CategoryOptionComboModel.TABLE, databaseAdapter), is(true));
        assertThat(ifTableExist(CategoryOptionModel.TABLE, databaseAdapter), is(true));
        assertThat(ifTableExist(CategoryCategoryComboLinkModel.TABLE, databaseAdapter), is(true));
        assertThat(ifTableExist(CategoryCategoryOptionLinkModel.TABLE, databaseAdapter), is(true));
        assertThat(isFieldExist(ProgramModel.TABLE, ProgramModel.Columns.CATEGORY_COMBO, databaseAdapter), is(true));
        assertThat(isFieldExist(DataElementModel.TABLE, DataElementModel.Columns.CATEGORY_COMBO, databaseAdapter), is(true));
        assertThat(isFieldExist(EventModel.TABLE, EventModel.Columns.ATTRIBUTE_CATEGORY_OPTIONS, databaseAdapter), is(true));
        assertThat(isFieldExist(EventModel.TABLE, EventModel.Columns.ATTRIBUTE_OPTION_COMBO, databaseAdapter), is(true));
        assertThat(isFieldExist(EventModel.TABLE, EventModel.Columns.TRACKED_ENTITY_INSTANCE, databaseAdapter), is(true));
    }
}

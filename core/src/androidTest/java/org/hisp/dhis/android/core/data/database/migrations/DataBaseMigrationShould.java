package org.hisp.dhis.android.core.data.database.migrations;

import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.DbOpenHelper;
import org.hisp.dhis.android.core.data.database.SqLiteDatabaseAdapter;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeReservedValueModel;
import org.hisp.dhis.android.core.user.UserModel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.hisp.dhis.android.core.data.database.SqliteCheckerUtility.ifTableExist;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class DataBaseMigrationShould {
    private DatabaseAdapter databaseAdapter;
    private DbOpenHelper dbOpenHelper;
    private String dbName = null;
    private SQLiteDatabase databaseInMemory;

    @Before
    public void deleteDB() {
        if (dbName != null) {
            InstrumentationRegistry.getContext().deleteDatabase(dbName);
        }
        dbOpenHelper = null;
        databaseInMemory = null;
    }

    @After
    public void tearDown() {
        if (dbName != null) {
            InstrumentationRegistry.getContext().deleteDatabase(dbName);
        }
    }

    @Test
    public void have_user_table_after_migration_1() {
        initCoreDataBase(1);
        assertThat(ifTableExist(UserModel.TABLE, databaseAdapter), is(true));
    }

    @Test
    public void not_have_tracked_entity_attribute_reserved_value_table_after_migration_1() {
        initCoreDataBase(1);
        assertThat(ifTableExist(TrackedEntityAttributeReservedValueModel.TABLE, databaseAdapter), is(false));
    }

    @Test
    public void have_tracked_entity_attribute_reserved_value_table_after_first_migration_2() {
        initCoreDataBase(2);
        assertThat(ifTableExist(TrackedEntityAttributeReservedValueModel.TABLE, databaseAdapter), is(true));
    }

    public DatabaseAdapter initCoreDataBase(int databaseVersion) {
        if (databaseAdapter == null) {
            dbOpenHelper = new DbOpenHelper(
                    InstrumentationRegistry.getTargetContext().getApplicationContext()
                    , dbName, databaseVersion);
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
}

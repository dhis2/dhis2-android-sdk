/*
 *  Copyright (c) 2004-2023, University of Oslo
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
package org.hisp.dhis.android.core.data.database.migrations

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.db.access.internal.AppDatabase
import org.hisp.dhis.android.persistence.db.migrations.RoomGeneratedMigrations.ALL_MIGRATIONS
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityAttributeReservedValueTableInfo
import org.hisp.dhis.android.persistence.user.UserTableInfo
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DataBaseMigrationShould {

    private var supportSqliteDb: SupportSQLiteDatabase? = null
    private var dbName: String = "versioned_migration_test.db"
    private lateinit var context: Context

    private val FINAL_DB_VERSION = AppDatabase.VERSION

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        deleteDatabase()
    }

    @After
    fun tearDown() {
        closeDatabase()
        deleteDatabase()
    }

    private fun closeDatabase() {
        supportSqliteDb?.close()
        supportSqliteDb = null
    }

    private fun deleteDatabase() {
        if (::context.isInitialized) {
            context.deleteDatabase(dbName)
        }
    }

    private fun initDatabaseToVersion(targetVersion: Int): SupportSQLiteDatabase {
        closeDatabase()
        deleteDatabase()

        val openHelperFactory = FrameworkSQLiteOpenHelperFactory()
        val configuration = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(dbName)
            .callback(object : SupportSQLiteOpenHelper.Callback(FINAL_DB_VERSION) {
                override fun onCreate(db: SupportSQLiteDatabase) {}

                override fun onUpgrade(
                    db: SupportSQLiteDatabase,
                    oldV: Int,
                    newV: Int,
                ) {
                }

                override fun onDowngrade(db: SupportSQLiteDatabase, oldV: Int, newV: Int) {}
            })
            .build()

        val helper = openHelperFactory.create(configuration)
        val db = helper.writableDatabase
        supportSqliteDb = db

        var currentDbVersion = 0
        if (ALL_MIGRATIONS.isNotEmpty()) {
            currentDbVersion = ALL_MIGRATIONS.first().startVersion
        }
        db.execSQL("PRAGMA user_version = $currentDbVersion;")

        for (migration in ALL_MIGRATIONS) {
            if (migration.startVersion == currentDbVersion && migration.endVersion <= targetVersion) {
                db.beginTransaction()
                try {
                    migration.migrate(db)
                    db.execSQL("PRAGMA user_version = ${migration.endVersion};")
                    db.setTransactionSuccessful()
                    currentDbVersion = migration.endVersion
                } finally {
                    db.endTransaction()
                }
            }
            if (currentDbVersion >= targetVersion) {
                break
            }
        }

        val pragmacursor = db.query("PRAGMA user_version;")
        var finalPragmaVersion = -1
        if (pragmacursor.moveToFirst()) {
            finalPragmaVersion = pragmacursor.getInt(0)
        }
        pragmacursor.close()

        assertThat(finalPragmaVersion).isEqualTo(targetVersion)

        return db
    }

    fun ifTableExists(tableName: String, db: SupportSQLiteDatabase): Boolean {
        val cursor = db.query("SELECT 1 FROM sqlite_master WHERE type='table' AND name='$tableName'")
        val tableExists = cursor.moveToFirst()
        cursor.close()
        return tableExists
    }

    @Test
    fun have_user_table_after_migration_1() = runTest {
        val db = initDatabaseToVersion(1)
        assertThat(ifTableExists(UserTableInfo.TABLE_INFO.name(), db)).isTrue()
    }

    @Test
    fun not_have_tracked_entity_attribute_reserved_value_table_after_migration_1() = runTest {
        val db = initDatabaseToVersion(1)
        assertThat(ifTableExists(TrackedEntityAttributeReservedValueTableInfo.TABLE_INFO.name(), db)).isFalse()
    }

    @Test
    fun have_tracked_entity_attribute_reserved_value_table_after_first_migration_2() = runTest {
        val db = initDatabaseToVersion(2)
        assertThat(ifTableExists(TrackedEntityAttributeReservedValueTableInfo.TABLE_INFO.name(), db)).isTrue()
    }

    /**
     * ANDROSDK-2376: downloaded dataStore entries were persisted as the `toString()` of the
     * internal `JsonWrapper` value class. Migration 181 unwraps them in place so existing
     * installations are repaired without waiting for the app to trigger a dataStore download.
     */
    @Test
    fun repair_data_store_values_wrapped_by_json_wrapper_to_string_in_migration_181() = runTest {
        val db = initDatabaseToVersion(180)

        insertDataStoreEntry(db, "settings", """JsonWrapper(json={"skipMigration":true})""", "SYNCED")
        insertDataStoreEntry(db, "summary", """JsonWrapper(json=[{"id":"fKHBpN5v8Sj"}])""", "SYNCED")
        insertDataStoreEntry(db, "explicitNull", "JsonWrapper(json=null)", "SYNCED")
        insertDataStoreEntry(db, "absentValue", "null", "SYNCED")
        // Only the download produces the corrupted values, so anything written locally and still
        // pending upload must be left exactly as the app wrote it.
        insertDataStoreEntry(db, "locallyEdited", "null", "TO_UPDATE")

        applyMigrationEndingAt(db, 181)

        assertThat(dataStoreValue(db, "settings")).isEqualTo("""{"skipMigration":true}""")
        assertThat(dataStoreValue(db, "summary")).isEqualTo("""[{"id":"fKHBpN5v8Sj"}]""")
        // A genuine JSON null keeps its JSON representation, an absent value becomes SQL NULL.
        assertThat(dataStoreValue(db, "explicitNull")).isEqualTo("null")
        assertThat(dataStoreValue(db, "absentValue")).isNull()
        assertThat(dataStoreValue(db, "locallyEdited")).isEqualTo("null")
    }

    private fun applyMigrationEndingAt(db: SupportSQLiteDatabase, endVersion: Int) {
        val migration = ALL_MIGRATIONS.first { it.endVersion == endVersion }
        db.beginTransaction()
        try {
            migration.migrate(db)
            db.execSQL("PRAGMA user_version = ${migration.endVersion};")
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    private fun insertDataStoreEntry(
        db: SupportSQLiteDatabase,
        key: String,
        value: String,
        syncState: String,
    ) {
        db.execSQL(
            "INSERT INTO DataStore (namespace, key, value, syncState, deleted) VALUES (?, ?, ?, ?, 0)",
            arrayOf("capture", key, value, syncState),
        )
    }

    private fun dataStoreValue(db: SupportSQLiteDatabase, key: String): String? {
        val cursor = db.query("SELECT value FROM DataStore WHERE namespace = ? AND key = ?", arrayOf("capture", key))
        val value = if (cursor.moveToFirst() && !cursor.isNull(0)) cursor.getString(0) else null
        cursor.close()
        return value
    }
}

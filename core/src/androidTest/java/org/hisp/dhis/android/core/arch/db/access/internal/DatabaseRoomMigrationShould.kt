/*
 *  Copyright (c) 2004-2025, University of Oslo
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

package org.hisp.dhis.android.core.arch.db.access.internal

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.hisp.dhis.android.persistence.common.SchemaRow
import org.hisp.dhis.android.persistence.db.migrations.RoomGeneratedMigrations.ALL_MIGRATIONS
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test


class DatabaseRoomMigrationShould {

    private lateinit var context: Context
    private val MIGRATED_DB_NAME = "manual_room_migrated.db"
    private val NEW_DB_NAME = "manual_room_new_schema.db"
    private val FINAL_DB_VERSION = AppDatabase.VERSION

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        context.deleteDatabase(MIGRATED_DB_NAME)
        context.deleteDatabase(NEW_DB_NAME)
    }

    @After
    fun tearDown() {
        context.deleteDatabase(MIGRATED_DB_NAME)
        context.deleteDatabase(NEW_DB_NAME)
    }

    private fun normalizeAndSortSchema(schemaRows: List<SchemaRow>): List<SchemaRow> {
        return schemaRows.map { row ->
            val normalizedSql = row.sql
                ?.replace("\"", "")
                ?.replace("`", "")
                ?.replace(" ON UPDATE NO ACTION", "")
                ?.replace(" (", "(")
                ?.replace(Regex("\\s+"), " ")
                ?.trim()
            row.copy(sql = normalizedSql)
        }.sortedBy { it.name }
    }

    private suspend fun getRoomSchema(db: AppDatabase): List<SchemaRow> {
        val schemaFromDao = withContext(Dispatchers.IO) {
            db.d2Dao().getSchemaRows()
        }
        val excludedTableNames = setOf("android_metadata", "room_master_table", "sqlite_sequence")
        val filteredSchema = schemaFromDao.filter { row ->
            row.sql != null &&
                !excludedTableNames.contains(row.name) &&
                !row.name.startsWith("sqlite_stat")
        }

        return normalizeAndSortSchema(filteredSchema)
    }

    private fun getRawSchemaFromSupportDB(supportDb: SupportSQLiteDatabase): List<SchemaRow> {
        val schemaRows = mutableListOf<SchemaRow>()
        val query =
            "SELECT name, sql FROM sqlite_master WHERE name NOT LIKE 'android_%' AND name NOT LIKE 'sqlite_%' AND name NOT LIKE 'room_%' AND sql IS NOT NULL ORDER BY name"
        val cursor = supportDb.query(query)
        cursor.use {
            while (it.moveToNext()) {
                val nameIndex = it.getColumnIndex("name")
                val sqlIndex = it.getColumnIndex("sql")

                val name = if (nameIndex != -1) it.getString(nameIndex) else "ERROR_NO_NAME_COLUMN"
                val sql =
                    if (sqlIndex != -1) it.getString(sqlIndex) else null

                schemaRows.add(SchemaRow(name = name, sql = sql))
            }
        }
        return normalizeAndSortSchema(schemaRows)
    }

    private fun applyMigrationsManually(db: SupportSQLiteDatabase) {
        if (ALL_MIGRATIONS.isEmpty()) return

        var currentDbVersion = ALL_MIGRATIONS.first().startVersion
        db.execSQL("PRAGMA user_version = $currentDbVersion;")

        for (migration in ALL_MIGRATIONS) {
            if (migration.startVersion != currentDbVersion) {
                throw IllegalStateException(
                    "Migration order mismatch. Expected to migrate from version $currentDbVersion, " +
                        "but migration ${migration.javaClass.simpleName} (or object) starts at ${migration.startVersion}."
                )
            }
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
        Assert.assertEquals("Database version after all manual migrations", FINAL_DB_VERSION, db.version)
    }

    @Test
    fun migratedDbSchema_matches_newDbSchema() = runTest {
        val openHelperFactory = FrameworkSQLiteOpenHelperFactory()
        val configuration = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(MIGRATED_DB_NAME)
            .callback(object : SupportSQLiteOpenHelper.Callback(FINAL_DB_VERSION) {
                override fun onCreate(db: SupportSQLiteDatabase) {}

                override fun onUpgrade(db: SupportSQLiteDatabase, oldV: Int, newV: Int) {}

                override fun onDowngrade(db: SupportSQLiteDatabase, oldV: Int, newV: Int) {}
            })
            .build()

        val supportDbHelper = openHelperFactory.create(configuration)
        val writableSupportDb = supportDbHelper.writableDatabase

        applyMigrationsManually(writableSupportDb)
        writableSupportDb.close()
        supportDbHelper.close()

        lateinit var migratedRoomDb: AppDatabase
        try {
            migratedRoomDb = Room.databaseBuilder(context, AppDatabase::class.java, MIGRATED_DB_NAME)
                .setQueryCoroutineContext(Dispatchers.IO)
                .allowMainThreadQueries()
                .build()

            Assert.assertEquals(
                "Migrated Room DB version after Room opens it",
                FINAL_DB_VERSION,
                migratedRoomDb.openHelper.readableDatabase.version
            )
            println("Room abrió y validó con éxito MIGRATED_DB_NAME.")

        } catch (e: IllegalStateException) {
            println("Error al validar el esquema de la base de datos migrada por Room: ${e.message}")
            throw e
        }

        val migratedSchema = getRoomSchema(migratedRoomDb)
        migratedRoomDb.close()

        val newRoomDb = Room.databaseBuilder(context, AppDatabase::class.java, NEW_DB_NAME)
            .addMigrations(*ALL_MIGRATIONS.toTypedArray())
            .setQueryCoroutineContext(Dispatchers.IO)
            .allowMainThreadQueries()
            .build()

        newRoomDb.openHelper.writableDatabase.query("SELECT 1").close()
        Assert.assertEquals("New Room DB version", FINAL_DB_VERSION, newRoomDb.openHelper.readableDatabase.version)

        val newSchema = getRoomSchema(newRoomDb)
        newRoomDb.close()

        Assert.assertEquals(
            "Schema lists should have the same size. Migrated: ${migratedSchema.size}, New: ${newSchema.size}",
            migratedSchema.size,
            newSchema.size
        )

        for (i in migratedSchema.indices) {
            val migratedRow = migratedSchema[i]
            val newRow = newSchema[i]
            Assert.assertEquals(
                "SchemaRow name mismatch at index $i: Migrated='${migratedRow.name}', New='${newRow.name}'",
                migratedRow.name,
                newRow.name
            )
            Assert.assertEquals(
                "SchemaRow SQL mismatch for '${migratedRow.name}': Migrated='${migratedRow.sql}', New='${newRow.sql}'",
                migratedRow.sql,
                newRow.sql
            )
        }
    }
}

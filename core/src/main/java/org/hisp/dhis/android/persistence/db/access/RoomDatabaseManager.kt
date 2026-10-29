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

package org.hisp.dhis.android.persistence.db.access

import android.content.Context
import android.database.sqlite.SQLiteException
import android.util.Log
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import kotlinx.coroutines.Dispatchers
import net.zetetic.database.DatabaseErrorHandler
import net.zetetic.database.sqlcipher.SQLiteDatabaseHook
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.access.DatabaseManager
import org.hisp.dhis.android.core.arch.db.access.internal.AppDatabase
import org.hisp.dhis.android.core.common.internal.NativeLibraryLoader
import org.hisp.dhis.android.core.configuration.internal.DatabaseAccount
import org.hisp.dhis.android.core.configuration.internal.DatabaseEncryptionPasswordManager
import org.hisp.dhis.android.persistence.db.migrations.RoomGeneratedMigrations.ALL_MIGRATIONS
import org.koin.core.annotation.Singleton
import java.io.File
import java.nio.charset.StandardCharsets

/**
 * Room-based implementation of DatabaseManager with encryption capabilities.
 *
 * Room 3 has no SupportSQLite compatibility mode, so every database -- plaintext, encrypted or
 * in-memory -- is opened through a [SqliteDriverFactory]. That makes the only difference between
 * the variants the driver and whether migrations are applied.
 */
@Singleton
@Suppress("TooManyFunctions")
internal class RoomDatabaseManager(
    private val databaseAdapter: DatabaseAdapter,
    private val context: Context,
    private val passwordManager: DatabaseEncryptionPasswordManager,
    private val driverFactory: SqliteDriverFactory,
) : DatabaseManager {

    companion object {
        private const val TAG = "RoomDatabaseManager"
        private const val IN_MEMORY_DB_NAME = "inmemory-test-db"

        /**
         * Long-standing workaround: holding a strong reference to every file-backed database ever
         * opened prevents a close-related crash seen in the app. It has been in place for years and
         * is deliberately kept through the Room 3 migration. It may well be unnecessary now that
         * the adapter owns the close lifecycle and no `RoomDatabase` is left half-closed, but that
         * has to be proven on a real app before the list is removed -- see ANDROSDK-2382 notes.
         */
        private val dbListoToPreventCloseError: MutableList<RoomDatabase> = ArrayList()
    }

    /**
     * Applies the settings Room cannot express on the builder. Room 3 callbacks are suspending and
     * receive a raw [SQLiteConnection].
     */
    private fun foreignKeysOffCallback() = object : RoomDatabase.Callback() {
        override suspend fun onOpen(connection: SQLiteConnection) {
            connection.execSQL("PRAGMA foreign_keys=OFF;")
        }
    }

    @Suppress("SpreadOperator")
    private fun build(
        databaseName: String?,
        password: String?,
        applyMigrations: Boolean,
        callback: RoomDatabase.Callback? = null,
    ): AppDatabase {
        val builder = if (databaseName == null) {
            Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
        } else {
            Room.databaseBuilder(context, AppDatabase::class.java, databaseName)
        }

        return builder
            .setDriver(driverFactory.create(password))
            .setQueryCoroutineContext(Dispatchers.IO)
            .apply {
                if (applyMigrations) addMigrations(*ALL_MIGRATIONS.toTypedArray())
                if (callback != null) addCallback(callback)
            }
            .build()
    }

    private fun activate(database: AppDatabase, databaseName: String): DatabaseAdapter {
        databaseAdapter.activate(database, databaseName)
        return databaseAdapter
    }

    override fun createInMemoryDatabase(): DatabaseAdapter {
        Log.d(TAG, "createInMemoryDatabase called. Setting up PRAGMA foreign_keys=OFF.")
        val database = build(
            databaseName = null,
            password = null,
            applyMigrations = false,
            callback = foreignKeysOffCallback(),
        )
        return activate(database, IN_MEMORY_DB_NAME)
    }

    override fun createOrOpenUnencryptedDatabase(databaseName: String): DatabaseAdapter {
        val database = build(databaseName, password = null, applyMigrations = true)
        dbListoToPreventCloseError.add(database)
        return activate(database, databaseName)
    }

    override fun createOrOpenUnencryptedDatabaseWithoutMigration(databaseName: String): DatabaseAdapter {
        val database = build(databaseName, password = null, applyMigrations = false)
        dbListoToPreventCloseError.add(database)
        return activate(database, databaseName)
    }

    override fun createOrOpenEncryptedDatabase(databaseName: String, password: String): DatabaseAdapter {
        val database = build(databaseName, password, applyMigrations = true)
        dbListoToPreventCloseError.add(database)
        return activate(database, databaseName)
    }

    override fun createOrOpenDatabase(account: DatabaseAccount): DatabaseAdapter {
        return if (account.encrypted()) {
            val password = passwordManager.getPassword(account.databaseName())
            createOrOpenEncryptedDatabase(account.databaseName(), password)
        } else {
            createOrOpenUnencryptedDatabase(account.databaseName())
        }
    }

    override fun openSQLCipherDatabaseDirectly(
        databaseFile: File,
        password: String?,
        hook: SQLiteDatabaseHook?,
    ): net.zetetic.database.sqlcipher.SQLiteDatabase {
        // Load SQLCipher native library before opening database directly
        NativeLibraryLoader.loadSQLCipher()

        return net.zetetic.database.sqlcipher.SQLiteDatabase.openOrCreateDatabase(
            databaseFile,
            (password ?: "").toByteArray(StandardCharsets.UTF_8),
            null,
            LoggingErrorHandler(TAG),
            hook,
        )
    }

    @Suppress("TooGenericExceptionCaught")
    override fun deleteDatabase(databaseName: String, isEncrypted: Boolean): Boolean {
        return try {
            context.deleteDatabase(databaseName)
            if (isEncrypted) {
                passwordManager.deletePassword(databaseName)
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting database $databaseName", e)
            false
        }
    }

    override fun databaseExists(databaseName: String): Boolean {
        val dbFile = context.getDatabasePath(databaseName)
        return dbFile.exists()
    }

    override fun disableDatabase() {
        databaseAdapter.deactivate()
    }

    override fun getAdapter(): DatabaseAdapter {
        return databaseAdapter
    }
}

internal class LoggingErrorHandler(private val tag: String) : DatabaseErrorHandler {
    override fun onCorruption(dbObj: net.zetetic.database.sqlcipher.SQLiteDatabase?, exception: SQLiteException?) {
        Log.e(tag, "CORRUPTION DETECTED! DB Path: ${dbObj?.path}")
    }
}

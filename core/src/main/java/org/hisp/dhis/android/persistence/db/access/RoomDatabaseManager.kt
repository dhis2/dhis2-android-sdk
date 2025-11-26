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
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import net.zetetic.database.DatabaseErrorHandler
import net.zetetic.database.sqlcipher.SQLiteDatabaseHook
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
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
import java.util.concurrent.Executors

/**
 * Room-based implementation of DatabaseManager with encryption capabilities.
 */
@Singleton
internal class RoomDatabaseManager(
    private val databaseAdapter: DatabaseAdapter,
    private val context: Context,
    private val passwordManager: DatabaseEncryptionPasswordManager,
) : DatabaseManager {

    companion object {
        private const val TAG = "RoomDatabaseManager"
        private val singleThreadExecutor = Executors.newFixedThreadPool(1) { r ->
            Thread(r, "SQL-DB-Thread")
        }
        private val dbListoToPreventCloseError: MutableList<RoomDatabase> = ArrayList()
    }

    override fun createInMemoryDatabase(): DatabaseAdapter {
        Log.d(TAG, "createInMemoryDatabase called. Setting up PRAGMA foreign_keys=OFF.")
        val database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .setQueryExecutor(singleThreadExecutor)
            .setTransactionExecutor(singleThreadExecutor)
            .allowMainThreadQueries()
            .addCallback(object : RoomDatabase.Callback() {
                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                    db.execSQL("PRAGMA foreign_keys=OFF;")
                }
            })
            .build()
        databaseAdapter.activate(database, "inmemory-test-db")
        return databaseAdapter
    }

    @Suppress("SpreadOperator")
    override fun createOrOpenUnencryptedDatabase(databaseName: String): DatabaseAdapter {
        val database = Room.databaseBuilder(context, AppDatabase::class.java, databaseName)
            .setQueryExecutor(singleThreadExecutor)
            .setTransactionExecutor(singleThreadExecutor)
            .allowMainThreadQueries()
            .addMigrations(*ALL_MIGRATIONS.toTypedArray())
            .build()
        databaseAdapter.activate(database, databaseName)
        dbListoToPreventCloseError.add(database)
        return databaseAdapter
    }

    override fun createOrOpenUnencryptedDatabaseWithoutMigration(databaseName: String): DatabaseAdapter {
        val database = Room.databaseBuilder(context, AppDatabase::class.java, databaseName)
            .setQueryExecutor(singleThreadExecutor)
            .setTransactionExecutor(singleThreadExecutor)
            .allowMainThreadQueries()
            .build()
        databaseAdapter.activate(database, databaseName)
        dbListoToPreventCloseError.add(database)
        return databaseAdapter
    }

    @Suppress("SpreadOperator")
    override fun createOrOpenEncryptedDatabase(databaseName: String, password: String): DatabaseAdapter {
        // Load SQLCipher native library before creating encrypted database
        NativeLibraryLoader.loadSQLCipher()

        val hook = RoomDatabaseExport.Companion.EncryptionHook
        val factory = SupportOpenHelperFactory(password.toByteArray(StandardCharsets.UTF_8), hook, true)
        val database = Room.databaseBuilder(context, AppDatabase::class.java, databaseName)
            .setQueryExecutor(singleThreadExecutor)
            .setTransactionExecutor(singleThreadExecutor)
            .allowMainThreadQueries()
            .addMigrations(*ALL_MIGRATIONS.toTypedArray())
            .openHelperFactory(factory)
            .build()
        databaseAdapter.activate(database, databaseName)
        dbListoToPreventCloseError.add(database)
        return databaseAdapter
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

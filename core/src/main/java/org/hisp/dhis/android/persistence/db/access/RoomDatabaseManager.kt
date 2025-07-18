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
import android.util.Log
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.access.DatabaseManager
import org.hisp.dhis.android.core.arch.db.access.internal.AppDatabase
import org.hisp.dhis.android.core.configuration.internal.DatabaseAccount
import org.hisp.dhis.android.core.configuration.internal.DatabaseEncryptionPasswordManager
import org.koin.core.annotation.Singleton
import java.nio.charset.StandardCharsets

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
    }

    override fun createOrOpenUnencryptedDatabase(databaseName: String): DatabaseAdapter {
        val database = Room.databaseBuilder(context, AppDatabase::class.java, databaseName)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            // .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING) by default,
            // room uses Automatic, which depends on the API version
            // Add migration when ready
            .build()
        databaseAdapter.activate(database, databaseName)
        return databaseAdapter
    }

    override fun createOrOpenEncryptedDatabase(databaseName: String, password: String): DatabaseAdapter {
        val factory = SupportOpenHelperFactory(password.toByteArray(StandardCharsets.UTF_8))
        val database = Room.databaseBuilder(context, AppDatabase::class.java, databaseName)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            // .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING) by default,
            // room uses Automatic, which depends on the API version
            // Add migration when ready
            .openHelperFactory(factory)
            .build()
        databaseAdapter.activate(database, databaseName)
        return databaseAdapter
    }

    override fun createOrOpenDatabase(account: DatabaseAccount): DatabaseAdapter {
        if (account.encrypted()) {
            val password = passwordManager.getPassword(account.databaseName())
            return createOrOpenEncryptedDatabase(account.databaseName(), password)
        } else {
            return createOrOpenUnencryptedDatabase(account.databaseName())
        }
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

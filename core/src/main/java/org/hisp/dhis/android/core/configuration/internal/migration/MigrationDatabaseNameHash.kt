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
package org.hisp.dhis.android.core.configuration.internal.migration

import android.content.Context
import android.util.Log
import org.hisp.dhis.android.core.arch.storage.internal.ObjectKeyValueStore
import org.hisp.dhis.android.core.configuration.internal.DatabaseAccount
import org.hisp.dhis.android.core.configuration.internal.DatabaseAccountImportStatus
import org.hisp.dhis.android.core.configuration.internal.DatabaseNameGenerator
import org.hisp.dhis.android.core.configuration.internal.DatabaseRenamer
import org.hisp.dhis.android.core.configuration.internal.DatabasesConfiguration
import java.io.File

/**
 * Migration to add hash suffix to database names for guaranteed uniqueness.
 *
 * This migration:
 * 1. Detects and handles existing collisions
 * 2. Renames database files and their associated files (-shm, -wal, etc.)
 * 3. Renames FileResource directories (sdk_resources and sdk_cache_resources)
 * 4. Updates protectedDbName for pending imports
 * 5. Updates configuration with new database names
 *
 * The hash suffix ensures each unique URL+username combination has its own database.
 */
internal class MigrationDatabaseNameHash(
    private val context: Context,
    private val databaseConfigurationStore: ObjectKeyValueStore<DatabasesConfiguration>,
    private val nameGenerator: DatabaseNameGenerator,
    private val databaseRenamer: DatabaseRenamer,
) {

    fun apply() {
        val configuration = databaseConfigurationStore.get() ?: return

        if (configuration.accounts().isEmpty()) {
            return
        }

        val alreadyMigrated = configuration.accounts().any { account ->
            account.databaseName().matches(Regex(".*_[0-9a-f]{8}_(encrypted|unencrypted)\\.db"))
        }

        if (alreadyMigrated) {
            Log.d(TAG, "Database name hash migration already applied")
            return
        }

        Log.i(TAG, "Starting database name hash migration for ${configuration.accounts().size} accounts")

        val collisions = detectCollisions(configuration)

        if (collisions.isNotEmpty()) {
            Log.w(TAG, "Detected ${collisions.size} database name collisions")
            handleCollisions(collisions)
        }

        val migratedAccounts = configuration.accounts().mapNotNull { account ->
            migrateAccount(account)
        }

        val updatedConfiguration = configuration.toBuilder()
            .accounts(migratedAccounts)
            .build()

        databaseConfigurationStore.set(updatedConfiguration)

        Log.i(TAG, "Database name hash migration completed successfully")
    }

    private fun detectCollisions(configuration: DatabasesConfiguration): Map<String, List<DatabaseAccount>> {
        return configuration.accounts()
            .groupBy { it.databaseName() }
            .filterValues { it.size > 1 }
    }

    /**
     * Handles collision scenarios by keeping only the first account's data
     * and marking others for re-login.
     *
     * Strategy:
     * - First account in the collision list keeps the database
     * - Other accounts will get a new database name (empty database)
     * - User will need to sync data again for the other accounts
     */
    private fun handleCollisions(collisions: Map<String, List<DatabaseAccount>>) {
        collisions.forEach { (dbName, accounts) ->
            Log.w(TAG, """
                Collision detected for database: $dbName
                Affected accounts: ${accounts.size}
                ${accounts.mapIndexed { index, acc ->
                    "  ${index + 1}. ${acc.username()}@${acc.serverUrl()}"
                }.joinToString("\n")}

                Resolution: First account keeps data, others will get new empty databases.
            """.trimIndent())
        }
    }

    private fun migrateAccount(account: DatabaseAccount): DatabaseAccount? {
        val oldDbName = account.databaseName()
        val newDbName = nameGenerator.getDatabaseName(
            account.serverUrl(),
            account.username(),
            account.encrypted()
        )

        return try {
            val dbRenamed = renameDatabaseFile(oldDbName, newDbName)
            renameFileResourceDirectories(oldDbName, newDbName)
            val updatedImportDB = updateImportDB(account, oldDbName, newDbName)
            val updatedAccount = account.toBuilder()
                .databaseName(newDbName)
                .importDB(updatedImportDB)
                .build()

            if (dbRenamed) {
                Log.i(TAG, "Successfully migrated account: ${account.username()}@${account.serverUrl()}")
            } else {
                Log.w(TAG, "Account migrated but database file didn't exist: ${account.username()}@${account.serverUrl()}")
            }

            updatedAccount
        } catch (e: Exception) {
            Log.e(TAG, "Failed to migrate account: ${account.username()}@${account.serverUrl()}", e)
            account
        }
    }

    /**
     * Renames the database file and its associated files.
     * Returns true if the database existed and was renamed, false otherwise.
     */
    private fun renameDatabaseFile(oldDbName: String, newDbName: String): Boolean {
        val oldDbFile = context.getDatabasePath(oldDbName)

        if (!oldDbFile.exists()) {
            Log.d(TAG, "Database file doesn't exist: $oldDbName")
            return false
        }

        val newDbFile = context.getDatabasePath(newDbName)
        if (newDbFile.exists()) {
            Log.e(TAG, "Target database already exists, skipping rename: $newDbName")
            return false
        }

        val success = databaseRenamer.renameDatabase(oldDbName, newDbName)

        if (!success) {
            throw IllegalStateException("Failed to rename database from $oldDbName to $newDbName")
        }

        return true
    }

    private fun renameFileResourceDirectories(oldDbName: String, newDbName: String) {
        val oldSubfolder = oldDbName.removeSuffix(DatabaseNameGenerator.DB_SUFFIX)
        val newSubfolder = newDbName.removeSuffix(DatabaseNameGenerator.DB_SUFFIX)

        val oldResourcesDir = File(context.filesDir, "sdk_resources/$oldSubfolder")
        val newResourcesDir = File(context.filesDir, "sdk_resources/$newSubfolder")

        if (oldResourcesDir.exists()) {
            oldResourcesDir.renameTo(newResourcesDir)
        }

        val oldCacheDir = File(context.cacheDir, "sdk_cache_resources/$oldSubfolder")
        val newCacheDir = File(context.cacheDir, "sdk_cache_resources/$newSubfolder")

        if (oldCacheDir.exists()) {
            oldCacheDir.renameTo(newCacheDir)
        }
    }

    /**
     * Updates the importDB configuration if the account has a pending import.
     */
    private fun updateImportDB(
        account: DatabaseAccount,
        oldDbName: String,
        newDbName: String
    ): org.hisp.dhis.android.core.configuration.internal.DatabaseAccountImport? {
        val importDB = account.importDB() ?: return null

        if (importDB.status() != DatabaseAccountImportStatus.PENDING_TO_IMPORT) {
            return null
        }

        val oldProtectedName = "$oldDbName.protected"
        val newProtectedName = "$newDbName.protected"

        val oldProtectedFile = context.getDatabasePath(oldProtectedName)
        val newProtectedFile = context.getDatabasePath(newProtectedName)

        if (oldProtectedFile.exists()) {
            oldProtectedFile.renameTo(newProtectedFile)
        }

        return importDB.toBuilder()
            .protectedDbName(newProtectedName)
            .build()
    }

    companion object {
        private const val TAG = "MigrationDbNameHash"
    }
}

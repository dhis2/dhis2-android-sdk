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
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.access.DatabaseExportMetadata
import org.hisp.dhis.android.core.arch.db.access.internal.BaseDatabaseExport
import org.hisp.dhis.android.core.arch.helpers.FileResourceDirectoryHelper
import org.hisp.dhis.android.core.arch.storage.internal.Credentials
import org.hisp.dhis.android.core.configuration.internal.DatabaseAccount
import org.hisp.dhis.android.core.configuration.internal.DatabaseAccountImportStatus
import org.hisp.dhis.android.core.configuration.internal.DatabaseConfigurationHelper
import org.hisp.dhis.android.core.configuration.internal.DatabaseConfigurationInsecureStore
import org.hisp.dhis.android.core.configuration.internal.DatabaseEncryptionPasswordManager
import org.hisp.dhis.android.core.configuration.internal.DatabasesConfiguration
import org.hisp.dhis.android.core.configuration.internal.MultiUserDatabaseManager
import org.hisp.dhis.android.core.server.LoginConfig
import org.hisp.dhis.android.core.util.CipherUtil
import org.hisp.dhis.android.core.util.deleteIfExists
import org.koin.core.annotation.Singleton

/**
 * Room-based implementation of MultiUserDatabaseManager
 */
@Singleton
@Suppress("TooManyFunctions")
internal class RoomMultiUserDatabaseManager(
    private val context: Context,
    private val databaseAdapter: DatabaseAdapter,
    private val databaseConfigurationSecureStore: DatabaseConfigurationInsecureStore,
    private val configurationHelper: DatabaseConfigurationHelper,
    private val databaseManager: RoomDatabaseManager,
    private val passwordManager: DatabaseEncryptionPasswordManager,
    private val databaseExport: BaseDatabaseExport,
) : MultiUserDatabaseManager {

    override suspend fun loadExistingChangingEncryptionIfRequiredOtherwiseCreateNew(
        serverUrl: String,
        username: String,
        encrypt: Boolean,
    ) {
        val existing = loadExistingChangingEncryptionIfRequired(
            serverUrl,
            username,
            { encrypt },
            true,
        )

        if (!existing) {
            createNew(serverUrl, username, encrypt)
        }
    }

    override suspend fun loadExistingKeepingEncryptionOtherwiseCreateNew(
        serverUrl: String,
        username: String,
        encrypt: Boolean,
    ) {
        val existing = loadExistingChangingEncryptionIfRequired(
            serverUrl,
            username,
            { obj: DatabaseAccount -> obj.encrypted() },
            true,
        )

        if (!existing) {
            createNew(serverUrl, username, encrypt)
        }
    }

    override fun createNew(serverUrl: String, username: String, encrypt: Boolean) {
        removeExceedingAccounts()
        val userConfiguration = addOrUpdateAccountInternal(serverUrl, username, encrypt)
        openDatabase(userConfiguration)
    }

    override fun createNewPendingToImport(metadata: DatabaseExportMetadata): DatabaseAccount {
        removeExceedingAccounts()
        return addOrUpdateAccountInternal(
            metadata.serverUrl,
            metadata.username,
            metadata.encrypted,
            importStatus = DatabaseAccountImportStatus.PENDING_TO_IMPORT,
        )
    }

    override suspend fun changeEncryptionIfRequired(credentials: Credentials, encrypt: Boolean) {
        loadExistingChangingEncryptionIfRequired(
            credentials.serverUrl,
            credentials.username,
            { encrypt },
            false,
        )
    }

    override suspend fun loadExistingKeepingEncryption(serverUrl: String, username: String): Boolean {
        return loadExistingChangingEncryptionIfRequired(
            serverUrl,
            username,
            { obj: DatabaseAccount -> obj.encrypted() },
            true,
        )
    }

    override fun setMaxAccounts(maxAccounts: Int?) {
        require(!(maxAccounts != null && maxAccounts <= 0)) { "MaxAccounts must be greater than 0" }
        val configuration = databaseConfigurationSecureStore.get()
        val updatedConfiguration = (configuration?.toBuilder() ?: DatabasesConfiguration.builder())
            .maxAccounts(maxAccounts)
            .build()
        databaseConfigurationSecureStore.set(updatedConfiguration)
    }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun importAndLoadDb(account: DatabaseAccount, password: String) {
        val protectedDbPath = context.getDatabasePath(account.importDB()!!.protectedDbName())
        val dbPath = context.getDatabasePath(account.databaseName())
        val tempDbPath = context.filesDir.resolve("temp.db").also { it.deleteIfExists() }
        try {
            CipherUtil.extractEncryptedZipFile(protectedDbPath, tempDbPath, password)
            protectedDbPath.deleteIfExists()

            if (account.encrypted()) {
                databaseExport.encryptAndCopyTo(account, sourceFile = tempDbPath, targetFile = dbPath)
            } else {
                tempDbPath.copyTo(dbPath, overwrite = true)
            }
            openDatabase(account)
            val importedAccount = account.toBuilder()
                .importDB(
                    account.importDB()!!.toBuilder()
                        .status(DatabaseAccountImportStatus.IMPORTED)
                        .build(),
                )
                .build()
            addOrUpdatedAccountInternal(importedAccount)
        } catch (e: Exception) {
            dbPath.deleteIfExists()
            throw e
        } finally {
            tempDbPath.deleteIfExists()
        }
    }

    override fun getAccount(serverUrl: String, username: String): DatabaseAccount? {
        val configuration = databaseConfigurationSecureStore.get()
        return DatabaseConfigurationHelper.getAccount(configuration, serverUrl, username)
    }

    private suspend fun loadExistingChangingEncryptionIfRequired(
        serverUrl: String,
        username: String,
        encryptionExtractor: (config: DatabaseAccount) -> Boolean,
        alsoOpenWhenEncryptionDoesntChange: Boolean,
    ): Boolean {
        val existingAccount = getAccount(serverUrl, username) ?: return false
        val encrypt = encryptionExtractor(existingAccount)
        changeEncryptionIfRequired(serverUrl, existingAccount, encrypt)
        if (encrypt != existingAccount.encrypted() || alsoOpenWhenEncryptionDoesntChange) {
            val updatedAccount = addOrUpdateAccountInternal(
                serverUrl,
                username,
                encrypt,
                loginConfig = existingAccount.loginConfig(),
            )
            openDatabase(updatedAccount)
        }
        return true
    }

    private suspend fun changeEncryptionIfRequired(
        serverUrl: String,
        existingAccount: DatabaseAccount,
        encrypt: Boolean,
    ) {
        if (encrypt != existingAccount.encrypted()) {
            Log.w(
                RoomMultiUserDatabaseManager::class.java.name,
                "Encryption value changed for ${existingAccount.username()}: $encrypt",
            )
            if (encrypt && !existingAccount.encrypted()) {
                databaseExport.encrypt(serverUrl, existingAccount)
            } else if (!encrypt && existingAccount.encrypted()) {
                databaseExport.decrypt(serverUrl, existingAccount)
            }
            val databaseName = existingAccount.databaseName()
            if (databaseManager.databaseExists(databaseName)) {
                databaseManager.deleteDatabase(databaseName, !encrypt)
            }
        }
    }

    private fun addOrUpdateAccountInternal(
        serverUrl: String,
        username: String,
        encrypt: Boolean,
        loginConfig: LoginConfig? = null,
        importStatus: DatabaseAccountImportStatus? = null,
    ): DatabaseAccount {
        val updatedAccount = configurationHelper.addOrUpdateAccount(
            databaseConfigurationSecureStore.get(),
            serverUrl,
            username,
            encrypt,
            loginConfig,
            importStatus,
        )
        databaseConfigurationSecureStore.set(updatedAccount)
        return DatabaseConfigurationHelper.getLoggedAccount(updatedAccount, username, serverUrl)
    }

    private fun addOrUpdatedAccountInternal(account: DatabaseAccount) {
        val updatedAccount = configurationHelper.addOrUpdateAccount(
            databaseConfigurationSecureStore.get(),
            account,
        )
        databaseConfigurationSecureStore.set(updatedAccount)
    }

    private fun openDatabase(account: DatabaseAccount) {
        if (account.encrypted()) {
            val password = passwordManager.getPassword(account.databaseName())
            databaseManager.createOrOpenEncryptedDatabase(account.databaseName(), password)
        } else {
            databaseManager.createOrOpenUnencryptedDatabase(account.databaseName())
        }

        // Get a reference to the adapter that was opened
        if (databaseAdapter is RoomDatabaseAdapter) {
            // The adapter is already activated by the DatabaseManager
        } else {
            error("Expected a RoomDatabaseAdapter, but got ${databaseAdapter::class.java}")
        }
    }

    private fun removeExceedingAccounts() {
        val configuration = databaseConfigurationSecureStore.get()

        configuration?.maxAccounts()?.let { maxAccounts ->
            val exceedingAccounts = DatabaseConfigurationHelper.getOldestAccounts(
                configuration.accounts(),
                maxAccounts - 1,
            )

            val updatedConfiguration =
                DatabaseConfigurationHelper.removeAccount(configuration, exceedingAccounts)

            databaseConfigurationSecureStore.set(updatedConfiguration)
            exceedingAccounts.forEach {
                FileResourceDirectoryHelper.deleteFileResourceDirectories(context, it)
                val databaseName = it.databaseName()
                if (databaseManager.databaseExists(databaseName)) {
                    databaseManager.deleteDatabase(databaseName, it.encrypted())
                }
            }
        }
    }
}

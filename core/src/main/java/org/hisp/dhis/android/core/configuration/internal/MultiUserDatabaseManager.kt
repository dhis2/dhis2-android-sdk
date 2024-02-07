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
package org.hisp.dhis.android.core.configuration.internal

import android.content.Context
import android.util.Log
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.access.DatabaseExportMetadata
import org.hisp.dhis.android.core.arch.db.access.internal.DatabaseAdapterFactory
import org.hisp.dhis.android.core.arch.db.access.internal.DatabaseExport
import org.hisp.dhis.android.core.arch.helpers.FileResourceDirectoryHelper
import org.hisp.dhis.android.core.arch.storage.internal.Credentials
import org.hisp.dhis.android.core.util.CipherUtil
import org.hisp.dhis.android.core.util.deleteIfExists
import org.koin.core.annotation.Singleton

@Singleton
@Suppress("TooManyFunctions")
internal class MultiUserDatabaseManager(
    private val context: Context,
    private val databaseAdapter: DatabaseAdapter,
    private val databaseConfigurationSecureStore: DatabaseConfigurationInsecureStore,
    private val configurationHelper: DatabaseConfigurationHelper,
    private val databaseAdapterFactory: DatabaseAdapterFactory,
    private val databaseExport: DatabaseExport,
) {
    fun loadExistingChangingEncryptionIfRequiredOtherwiseCreateNew(
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

    fun loadExistingKeepingEncryptionOtherwiseCreateNew(serverUrl: String, username: String, encrypt: Boolean) {
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

    fun createNew(serverUrl: String, username: String, encrypt: Boolean) {
        removeExceedingAccounts()
        val userConfiguration = addOrUpdateAccountInternal(serverUrl, username, encrypt)
        databaseAdapterFactory.createOrOpenDatabase(databaseAdapter, userConfiguration)
    }

    fun createNewPendingToImport(metadata: DatabaseExportMetadata): DatabaseAccount {
        removeExceedingAccounts()
        return addOrUpdateAccountInternal(
            metadata.serverUrl,
            metadata.username,
            metadata.encrypted,
            importStatus = DatabaseAccountImportStatus.PENDING_TO_IMPORT,
        )
    }

    fun changeEncryptionIfRequired(credentials: Credentials, encrypt: Boolean) {
        loadExistingChangingEncryptionIfRequired(
            credentials.serverUrl,
            credentials.username,
            { encrypt },
            false,
        )
    }

    fun loadExistingKeepingEncryption(serverUrl: String, username: String): Boolean {
        return loadExistingChangingEncryptionIfRequired(
            serverUrl,
            username,
            { obj: DatabaseAccount -> obj.encrypted() },
            true,
        )
    }

    fun setMaxAccounts(maxAccounts: Int?) {
        if (maxAccounts != null && maxAccounts <= 0) {
            throw IllegalArgumentException("MaxAccounts must be greater than 0")
        } else {
            val configuration = databaseConfigurationSecureStore.get()
            val updatedConfiguration = (configuration?.toBuilder() ?: DatabasesConfiguration.builder())
                .maxAccounts(maxAccounts)
                .build()
            databaseConfigurationSecureStore.set(updatedConfiguration)
        }
    }

    @Suppress("TooGenericExceptionCaught")
    fun importAndLoadDb(account: DatabaseAccount, password: String) {
        val protectedDbPath = context.getDatabasePath(account.importDB()!!.protectedDbName())
        val dbPath = context.getDatabasePath(account.databaseName())
        try {
            CipherUtil.decryptFileUsingCredentials(protectedDbPath, dbPath, account.username(), password)
            protectedDbPath.deleteIfExists()
            databaseAdapterFactory.createOrOpenDatabase(databaseAdapter, account)
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
        }
    }

    private fun loadExistingChangingEncryptionIfRequired(
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
            )
            databaseAdapterFactory.createOrOpenDatabase(databaseAdapter, updatedAccount)
        }
        return true
    }

    private fun changeEncryptionIfRequired(
        serverUrl: String,
        existingAccount: DatabaseAccount,
        encrypt: Boolean,
    ) {
        if (encrypt != existingAccount.encrypted()) {
            Log.w(
                MultiUserDatabaseManager::class.java.name,
                "Encryption value changed for " + existingAccount.username() + ": " + encrypt,
            )
            if (encrypt && !existingAccount.encrypted()) {
                databaseExport.encrypt(serverUrl, existingAccount)
            } else if (!encrypt && existingAccount.encrypted()) {
                databaseExport.decrypt(serverUrl, existingAccount)
            }
            databaseAdapterFactory.deleteDatabase(existingAccount)
        }
    }

    fun getAccount(serverUrl: String, username: String): DatabaseAccount? {
        val configuration = databaseConfigurationSecureStore.get()
        return DatabaseConfigurationHelper.getAccount(configuration, serverUrl, username)
    }

    private fun addOrUpdateAccountInternal(
        serverUrl: String,
        username: String,
        encrypt: Boolean,
        importStatus: DatabaseAccountImportStatus? = null,
    ): DatabaseAccount {
        val updatedAccount = configurationHelper.addOrUpdateAccount(
            databaseConfigurationSecureStore.get(),
            serverUrl,
            username,
            encrypt,
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

    private fun removeExceedingAccounts() {
        val configuration = databaseConfigurationSecureStore.get()

        configuration?.maxAccounts()?.let { maxAccounts ->
            val exceedingAccounts = DatabaseConfigurationHelper
                .getOldestAccounts(configuration.accounts(), maxAccounts - 1)

            val updatedConfiguration =
                DatabaseConfigurationHelper.removeAccount(configuration, exceedingAccounts)

            databaseConfigurationSecureStore.set(updatedConfiguration)
            exceedingAccounts.forEach {
                FileResourceDirectoryHelper.deleteFileResourceDirectories(context, it)
                databaseAdapterFactory.deleteDatabase(it)
            }
        }
    }

    companion object {
        const val DefaultMaxAccounts = 1
        internal val DefaultTestMaxAccounts = null
    }
}

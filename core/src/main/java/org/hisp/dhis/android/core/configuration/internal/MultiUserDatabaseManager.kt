/*
 *  Copyright (c) 2004-2022, University of Oslo
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
import dagger.Reusable
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.access.internal.DatabaseAdapterFactory
import org.hisp.dhis.android.core.arch.db.access.internal.DatabaseExport
import org.hisp.dhis.android.core.arch.helpers.FileResourceDirectoryHelper
import org.hisp.dhis.android.core.arch.storage.internal.Credentials
import org.hisp.dhis.android.core.arch.storage.internal.ObjectKeyValueStore

@Reusable
internal class MultiUserDatabaseManager @Inject internal constructor(
    private val context: Context,
    private val databaseAdapter: DatabaseAdapter,
    private val databaseConfigurationSecureStore: ObjectKeyValueStore<DatabasesConfiguration>,
    private val configurationHelper: DatabaseConfigurationHelper,
    private val databaseAdapterFactory: DatabaseAdapterFactory,
    private val databaseExport: DatabaseExport
) {
    fun loadExistingChangingEncryptionIfRequiredOtherwiseCreateNew(
        serverUrl: String,
        username: String,
        encrypt: Boolean
    ) {
        val existing = loadExistingChangingEncryptionIfRequired(
            serverUrl,
            username,
            { encrypt },
            true
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
            true
        )

        if (!existing) {
            createNew(serverUrl, username, encrypt)
        }
    }

    fun createNew(serverUrl: String, username: String, encrypt: Boolean) {
        val configuration = databaseConfigurationSecureStore.get()

        configuration?.let {
            val exceedingAccounts = DatabaseConfigurationHelper
                .getOldestAccounts(configuration.accounts(), configuration.maxAccounts() - 1)

            val updatedConfiguration =
                DatabaseConfigurationHelper.removeAccount(configuration, exceedingAccounts)

            databaseConfigurationSecureStore.set(updatedConfiguration)
            exceedingAccounts.forEach {
                FileResourceDirectoryHelper.deleteFileResourceDirectory(context, it)
                databaseAdapterFactory.deleteDatabase(it)
            }
        }

        val userConfiguration = addNewAccountInternal(serverUrl, username, encrypt)
        databaseAdapterFactory.createOrOpenDatabase(databaseAdapter, userConfiguration)
    }

    fun changeEncryptionIfRequired(credentials: Credentials, encrypt: Boolean) {
        loadExistingChangingEncryptionIfRequired(
            credentials.serverUrl,
            credentials.username,
            { encrypt },
            false
        )
    }

    fun loadExistingKeepingEncryption(serverUrl: String, username: String): Boolean {
        return loadExistingChangingEncryptionIfRequired(
            serverUrl,
            username,
            { obj: DatabaseAccount -> obj.encrypted() },
            true
        )
    }

    fun setMaxAccounts(maxAccounts: Int) {
        if (maxAccounts <= 0) {
            throw IllegalArgumentException("MaxAccounts must be greater than 0")
        } else {
            val configuration = databaseConfigurationSecureStore.get()
            val updatedConfiguration = (configuration?.toBuilder() ?: DatabasesConfiguration.builder())
                .maxAccounts(maxAccounts)
                .build()
            databaseConfigurationSecureStore.set(updatedConfiguration)
        }
    }

    private fun loadExistingChangingEncryptionIfRequired(
        serverUrl: String,
        username: String,
        encryptionExtractor: (config: DatabaseAccount) -> Boolean,
        alsoOpenWhenEncryptionDoesntChange: Boolean
    ): Boolean {
        val existingAccount = getAccount(serverUrl, username) ?: return false
        val encrypt = encryptionExtractor(existingAccount)
        changeEncryptionIfRequired(serverUrl, existingAccount, encrypt)
        if (encrypt != existingAccount.encrypted() || alsoOpenWhenEncryptionDoesntChange) {
            val updatedAccount = addNewAccountInternal(
                serverUrl,
                username,
                encrypt
            )
            databaseAdapterFactory.createOrOpenDatabase(databaseAdapter, updatedAccount)
        }
        return true
    }

    private fun changeEncryptionIfRequired(
        serverUrl: String,
        existingAccount: DatabaseAccount,
        encrypt: Boolean
    ) {
        if (encrypt != existingAccount.encrypted()) {
            Log.w(
                MultiUserDatabaseManager::class.java.name,
                "Encryption value changed for " + existingAccount.username() + ": " + encrypt
            )
            if (encrypt && !existingAccount.encrypted()) {
                databaseExport.encrypt(serverUrl, existingAccount)
            } else if (!encrypt && existingAccount.encrypted()) {
                databaseExport.decrypt(serverUrl, existingAccount)
            }
            databaseAdapterFactory.deleteDatabase(existingAccount)
        }
    }

    private fun getAccount(serverUrl: String, username: String): DatabaseAccount? {
        val configuration = databaseConfigurationSecureStore.get()
        return DatabaseConfigurationHelper.getAccount(configuration, serverUrl, username)
    }

    private fun addNewAccountInternal(
        serverUrl: String,
        username: String,
        encrypt: Boolean
    ): DatabaseAccount {
        val updatedAccount = configurationHelper.addAccount(
            databaseConfigurationSecureStore.get(), serverUrl, username, encrypt
        )
        databaseConfigurationSecureStore.set(updatedAccount)
        return DatabaseConfigurationHelper.getLoggedAccount(updatedAccount, username, serverUrl)
    }

    companion object {
        const val DefaultMaxAccounts = 1
    }
}

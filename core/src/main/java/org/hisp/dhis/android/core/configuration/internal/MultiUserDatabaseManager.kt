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

import org.hisp.dhis.android.core.arch.db.access.DatabaseExportMetadata
import org.hisp.dhis.android.core.arch.storage.internal.Credentials

/**
 * Interface for managing multiple user databases in the DHIS2 Android SDK.
 * This interface defines the operations required for supporting multiple users,
 * including database creation, encryption, and import/export functionality.
 */
internal interface MultiUserDatabaseManager {

    /**
     * Loads an existing database, changes encryption if required, or creates a new database.
     *
     * @param serverUrl The server URL associated with the database
     * @param username The username associated with the database
     * @param encrypt Whether the database should be encrypted
     */
    suspend fun loadExistingChangingEncryptionIfRequiredOtherwiseCreateNew(
        serverUrl: String,
        username: String,
        encrypt: Boolean,
    )

    /**
     * Loads an existing database keeping its encryption status, or creates a new database.
     *
     * @param serverUrl The server URL associated with the database
     * @param username The username associated with the database
     * @param encrypt Whether the database should be encrypted if creating a new one
     */
    suspend fun loadExistingKeepingEncryptionOtherwiseCreateNew(
        serverUrl: String,
        username: String,
        encrypt: Boolean,
    )

    /**
     * Creates a new database for the specified user.
     *
     * @param serverUrl The server URL associated with the database
     * @param username The username associated with the database
     * @param encrypt Whether the database should be encrypted
     */
    fun createNew(serverUrl: String, username: String, encrypt: Boolean)

    /**
     * Creates a new database pending to be imported.
     *
     * @param metadata The metadata for the database to be imported
     * @return The created database account
     */
    fun createNewPendingToImport(metadata: DatabaseExportMetadata): DatabaseAccount

    /**
     * Changes encryption if required based on the provided credentials.
     *
     * @param credentials The user credentials
     * @param encrypt Whether encryption should be enabled
     */
    suspend fun changeEncryptionIfRequired(credentials: Credentials, encrypt: Boolean)

    /**
     * Loads an existing database keeping its current encryption status.
     *
     * @param serverUrl The server URL associated with the database
     * @param username The username associated with the database
     * @return True if the database was loaded, false otherwise
     */
    suspend fun loadExistingKeepingEncryption(serverUrl: String, username: String): Boolean

    /**
     * Sets the maximum number of accounts that can be stored.
     *
     * @param maxAccounts The maximum number of accounts, or null for unlimited
     */
    fun setMaxAccounts(maxAccounts: Int?)

    /**
     * Imports and loads a database.
     *
     * @param account The database account to import
     * @param password The encryption password if applicable
     */
    suspend fun importAndLoadDb(account: DatabaseAccount, password: String)

    /**
     * Gets an account by server URL and username.
     *
     * @param serverUrl The server URL associated with the account
     * @param username The username associated with the account
     * @return The database account, or null if not found
     */
    fun getAccount(serverUrl: String, username: String): DatabaseAccount?

    companion object {
        const val DefaultMaxAccounts = 1
        internal val DefaultTestMaxAccounts = null
    }
}

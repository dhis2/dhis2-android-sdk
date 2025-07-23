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
import androidx.room.Transactor
import androidx.room.execSQL
import androidx.room.useWriterConnection
import net.zetetic.database.sqlcipher.SQLiteConnection
import net.zetetic.database.sqlcipher.SQLiteDatabase
import net.zetetic.database.sqlcipher.SQLiteDatabaseHook
import org.hisp.dhis.android.core.arch.db.access.internal.BaseDatabaseExport
import org.hisp.dhis.android.core.common.internal.NativeLibraryLoader.loadSQLCipher
import org.hisp.dhis.android.core.configuration.internal.DatabaseAccount
import org.hisp.dhis.android.core.configuration.internal.DatabaseConfigurationHelper
import org.hisp.dhis.android.core.configuration.internal.DatabaseEncryptionPasswordManager
import org.koin.core.annotation.Singleton
import java.io.File

/**
 * Room implementation of database export functionality for encryption/decryption operations.
 * Uses SQLCipher for database encryption with Room.
 */
@Singleton
internal class RoomDatabaseExport(
    private val context: Context,
    private val passwordManager: DatabaseEncryptionPasswordManager,
    private val configurationHelper: DatabaseConfigurationHelper,
    private val databaseManager: RoomDatabaseManager,
) : BaseDatabaseExport {

    companion object {
        private const val TAG = "RoomDatabaseExport"
        private const val CIPHER_PAGE_SIZE = 16384
    }

    /**
     * Encrypts an existing database for the given server URL and user configuration.
     */
    override suspend fun encrypt(serverUrl: String, oldConfiguration: DatabaseAccount) {
        val newConfiguration = configurationHelper.changeEncryption(serverUrl, oldConfiguration)
        val oldDatabaseName = oldConfiguration.databaseName()
        val newDatabaseName = newConfiguration.databaseName()

        exportDatabase(
            oldDatabaseName,
            newDatabaseName,
            oldPassword = null, // Unencrypted source
            newPassword = passwordManager.getPassword(newConfiguration.databaseName()),
            encrypt = true,
        )
    }

    /**
     * Encrypts a database from a source file and copies it to a target file.
     */
    override suspend fun encryptAndCopyTo(newConfiguration: DatabaseAccount, sourceFile: File, targetFile: File) {
        val oldDatabaseName = newConfiguration.databaseName()
        val newDatabaseName = targetFile.name

        exportDatabase(
            oldDatabaseName,
            newDatabaseName,
            oldPassword = null, // Unencrypted source
            newPassword = passwordManager.getPassword(newConfiguration.databaseName()),
            encrypt = true,
        )
    }

    /**
     * Decrypts an existing database for the given server URL and user configuration.
     */
    override suspend fun decrypt(serverUrl: String, oldConfiguration: DatabaseAccount) {
        val newConfiguration = configurationHelper.changeEncryption(serverUrl, oldConfiguration)
        val oldDatabaseName = oldConfiguration.databaseName()
        val newDatabaseName = newConfiguration.databaseName()

        exportDatabase(
            oldDatabaseName,
            newDatabaseName,
            oldPassword = passwordManager.getPassword(oldConfiguration.databaseName()),
            newPassword = "", // Empty password for unencrypted database
            encrypt = false,
        )
    }

    /**
     * Decrypts a database and copies it to a destination file.
     */
    override suspend fun decryptAndCopyTo(account: DatabaseAccount, destinationFile: File) {
        val oldDatabaseName = account.databaseName()
        val newDatabaseName = destinationFile.name

        exportDatabase(
            oldDatabaseName,
            newDatabaseName,
            oldPassword = passwordManager.getPassword(account.databaseName()),
            newPassword = "", // Empty password for unencrypted database
            encrypt = false,
        )
    }

    /**
     * Exports a database from one file to another with optional encryption/decryption.
     *
     * @param sourceFile Source database file
     * @param targetFile Target database file
     * @param oldPassword Password for the source database (null if unencrypted)
     * @param newPassword Password for the target database (empty string if unencrypted)
     * @param encrypt Whether to encrypt (true) or decrypt (false)
     */
    @Suppress("LongParameterList")
    private suspend fun exportDatabase(
        // This is already suspend, which is good
        sourceName: String,
        targetName: String,
        oldPassword: String?,
        newPassword: String,
        encrypt: Boolean,
    ) {
        val operation = if (encrypt) "Encrypt" else "Decrypt"
        // Now wrapAction takes a suspend lambda
        wrapAction(operation) { // Pass the tag first, then the suspend lambda
            loadSQLCipher()

            // Open source database
            val sourceDb = if (oldPassword == null) {
                // Assuming these are suspend or blocking, ensure they are called appropriately
                databaseManager.createOrOpenUnencryptedDatabase(sourceName)
            } else {
                databaseManager.createOrOpenEncryptedDatabase(targetName, oldPassword)
            }

            // Create hook for target database if encrypting
            val hook = if (encrypt) createEncryptionHook() else null

            // Attach target database
            val dbFile = context.getDatabasePath(targetName)

            // This is a suspend call, now valid within the suspend lambda passed to wrapAction
            sourceDb.getCurrentDatabase().useWriterConnection { transactor ->
                // transactor.withTransaction itself might be a suspend function or take a suspend lambda
                // If it's from Room and you're using it correctly with coroutines, it should be fine.
                // Check its signature if issues persist here.
                // For Room's `Transactor.withTransaction`, the lambda block is executed within Room's
                // transaction dispatcher.
                transactor.withTransaction(Transactor.SQLiteTransactionType.IMMEDIATE) {
                    // 'this' inside withTransaction refers to a CoroutineScope or similar context provided by Room
                    this.execSQL("ATTACH DATABASE '${dbFile.absolutePath}' as roomExport KEY '$newPassword';")
                    if (encrypt) {
                        this.execSQL("PRAGMA roomExport.cipher_page_size = $CIPHER_PAGE_SIZE;")
                        this.execSQL("PRAGMA roomExport.cipher_memory_security = OFF;")
                    }
                    this.execSQL("SELECT sqlcipher_export('roomExport');")
                    this.execSQL("DETACH DATABASE roomExport;")
                }
            }

            // Set version on target database
            // Assuming getVersion() is not a suspend function. If it is, no change needed.
            val version = sourceDb.getVersion()

            // SQLiteDatabase.openDatabase is a blocking call.
            // It's generally fine in a suspend function that's on a background dispatcher (e.g., Dispatchers.IO)
            val targetDb = SQLiteDatabase.openDatabase(
                dbFile.absolutePath,
                newPassword,
                null,
                SQLiteDatabase.OPEN_READWRITE,
                hook,
            )
            targetDb.version = version

            // Close databases - these are blocking calls
            targetDb.close()
            sourceDb.close()
        }
    }

    /**
     * Wraps a suspending action with timing and error handling.
     */
    @Suppress("TooGenericExceptionCaught", "TooGenericExceptionThrown")
    private suspend fun wrapAction(tag: String, action: suspend () -> Unit) { // Changed to accept suspend lambda
        val startMillis = System.currentTimeMillis()
        try {
            action() // Simply invoke the suspend lambda
        } catch (e: Exception) {
            Log.e(TAG, "Error during database $tag: ${e.message}")
            throw RuntimeException("Exception thrown during database export action: $tag", e)
        }
        val endMillis = System.currentTimeMillis()
        Log.i(TAG, "$tag completed in ${endMillis - startMillis}ms")
    }

    /**
     * Creates a SQLCipher hook for encryption setup.
     */

    private fun createEncryptionHook(): SQLiteDatabaseHook {
        return object : SQLiteDatabaseHook {
            override fun preKey(connection: SQLiteConnection) {
                // Nothing to do here
            }

            override fun postKey(connection: SQLiteConnection) {
                connection.executeRaw("PRAGMA cipher_page_size = $CIPHER_PAGE_SIZE;", null, null)
                connection.executeRaw("PRAGMA cipher_memory_security = OFF;", null, null)
            }
        }
    }
}

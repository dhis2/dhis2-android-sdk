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

        internal object EncryptionHook : SQLiteDatabaseHook {
            override fun preKey(connection: SQLiteConnection) {
                // Nothing to do here
            }

            override fun postKey(connection: SQLiteConnection) {
                connection.executeRaw("PRAGMA cipher_page_size = $CIPHER_PAGE_SIZE;", null, null)
                connection.execute("PRAGMA cipher_memory_security = OFF;", null, null)
            }
        }
    }

    /**
     * Encrypts an existing database for the given server URL and user configuration.
     */
    override suspend fun encrypt(serverUrl: String, oldConfiguration: DatabaseAccount) {
        val newConfiguration = configurationHelper.changeEncryption(serverUrl, oldConfiguration)
        exportDatabase(
            context.getDatabasePath(oldConfiguration.databaseName()),
            context.getDatabasePath(newConfiguration.databaseName()),
            oldPassword = null, // Unencrypted source
            newPassword = passwordManager.getPassword(newConfiguration.databaseName()),
            encrypt = true,
            oldHook = null,
            newHook = Companion.EncryptionHook,
        )
    }

    /**
     * Encrypts a database from a source file and copies it to a target file.
     */
    override suspend fun encryptAndCopyTo(newConfiguration: DatabaseAccount, sourceFile: File, targetFile: File) {
        exportDatabase(
            sourceFile,
            targetFile,
            oldPassword = null, // Unencrypted source
            newPassword = passwordManager.getPassword(newConfiguration.databaseName()),
            encrypt = true,
            oldHook = null,
            newHook = Companion.EncryptionHook,
        )
    }

    /**
     * Decrypts an existing database for the given server URL and user configuration.
     */
    override suspend fun decrypt(serverUrl: String, oldConfiguration: DatabaseAccount) {
        val newConfiguration = configurationHelper.changeEncryption(serverUrl, oldConfiguration)
        exportDatabase(
            context.getDatabasePath(oldConfiguration.databaseName()),
            context.getDatabasePath(newConfiguration.databaseName()),
            oldPassword = passwordManager.getPassword(oldConfiguration.databaseName()),
            newPassword = "", // Empty password for unencrypted database
            encrypt = false,
            oldHook = Companion.EncryptionHook,
            newHook = null,
        )
    }

    /**
     * Decrypts a database and copies it to a destination file.
     */
    override suspend fun decryptAndCopyTo(account: DatabaseAccount, destinationFile: File) {
        exportDatabase(
            context.getDatabasePath(account.databaseName()),
            destinationFile,
            oldPassword = passwordManager.getPassword(account.databaseName()),
            newPassword = "", // Empty password for unencrypted database
            encrypt = false,
            oldHook = Companion.EncryptionHook,
            newHook = null,
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
        sourceFile: File,
        targetFile: File,
        oldPassword: String?,
        newPassword: String,
        encrypt: Boolean,
        oldHook: SQLiteDatabaseHook?,
        newHook: SQLiteDatabaseHook?,
    ) {
        val operation = if (encrypt) "Encrypt" else "Decrypt"
        wrapAction(operation) {
            loadSQLCipher()

            // Open source database
            val sourceDbDirect: SQLiteDatabase =
                databaseManager.openSQLCipherDatabaseDirectly(sourceFile, oldPassword, oldHook)
            try {
                // Attach target database
                sourceDbDirect.execSQL("ATTACH DATABASE '${targetFile.absolutePath}' AS roomExport KEY '$newPassword';")

                // Add encryption hook to target database if required
                if (newHook != null) {
                    sourceDbDirect.execSQL("PRAGMA roomExport.cipher_page_size = $CIPHER_PAGE_SIZE;")
                    sourceDbDirect.execSQL("PRAGMA roomExport.cipher_memory_security = OFF;")
                }
                // Export database
                val exportCursor =
                    sourceDbDirect.rawQuery("SELECT sqlcipher_export('roomExport') AS export_result;", emptyArray())
                try {
                    if (exportCursor.moveToFirst()) {
                        exportCursor.getInt(0)
                    }
                } finally {
                    exportCursor.close()
                }
//                Thread.sleep(200)
                // Detach target database
                sourceDbDirect.execSQL("DETACH DATABASE roomExport;")

                //Add version to target database
                val version = sourceDbDirect.version
                sourceDbDirect.close()
                val targetEncryptionHook = if (encrypt) Companion.EncryptionHook else null
                val targetDbDirect = databaseManager.openSQLCipherDatabaseDirectly(
                    targetFile,
                    newPassword,
                    targetEncryptionHook
                )
                try {
                    targetDbDirect.version = version
                } finally {
                    targetDbDirect.close()
                }

            } catch (e: Exception) {
                if (sourceDbDirect.isOpen) {
                    try {
                        sourceDbDirect.execSQL("DETACH DATABASE roomExport;")
                    } catch (detachEx: Exception) {
                    }
                    sourceDbDirect.close()
                }
                targetFile.delete()
                throw e
            }

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
}

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
package org.hisp.dhis.android.core.arch.db.access.internal

import android.content.Context
import android.util.Log
import io.reactivex.functions.Action
import net.zetetic.database.sqlcipher.SQLiteConnection
import net.zetetic.database.sqlcipher.SQLiteDatabase
import net.zetetic.database.sqlcipher.SQLiteDatabaseHook
import org.hisp.dhis.android.core.common.internal.NativeLibraryLoader.loadSQLCipher
import org.hisp.dhis.android.core.configuration.internal.DatabaseAccount
import org.hisp.dhis.android.core.configuration.internal.DatabaseConfigurationHelper
import org.hisp.dhis.android.core.configuration.internal.DatabaseEncryptionPasswordManager
import org.koin.core.annotation.Singleton
import java.io.File

@Singleton
internal class DatabaseExport(
    private val context: Context,
    private val passwordManager: DatabaseEncryptionPasswordManager,
    private val configurationHelper: DatabaseConfigurationHelper,
) : BaseDatabaseExport {
    override suspend fun encrypt(serverUrl: String, oldConfiguration: DatabaseAccount) {
        val newConfiguration = configurationHelper.changeEncryption(serverUrl, oldConfiguration)
        export(
            oldDatabaseFile = context.getDatabasePath(oldConfiguration.databaseName()),
            newDatabaseFile = context.getDatabasePath(newConfiguration.databaseName()),
            oldPassword = null,
            newPassword = passwordManager.getPassword(newConfiguration.databaseName()),
            tag = "Encrypt",
            oldHook = null,
            newHook = hook,
        )
    }

    override suspend fun encryptAndCopyTo(newConfiguration: DatabaseAccount, sourceFile: File, targetFile: File) {
        export(
            oldDatabaseFile = sourceFile,
            newDatabaseFile = targetFile,
            oldPassword = null,
            newPassword = passwordManager.getPassword(newConfiguration.databaseName()),
            tag = "Encrypt",
            oldHook = null,
            newHook = hook,
        )
    }

    override suspend fun decrypt(serverUrl: String, oldConfiguration: DatabaseAccount) {
        val newConfiguration = configurationHelper.changeEncryption(serverUrl, oldConfiguration)
        export(
            oldDatabaseFile = context.getDatabasePath(oldConfiguration.databaseName()),
            newDatabaseFile = context.getDatabasePath(newConfiguration.databaseName()),
            oldPassword = passwordManager.getPassword(oldConfiguration.databaseName()),
            newPassword = "",
            tag = "Decrypt",
            oldHook = hook,
            newHook = null,
        )
    }

    override suspend fun decryptAndCopyTo(account: DatabaseAccount, destinationFile: File) {
        export(
            oldDatabaseFile = context.getDatabasePath(account.databaseName()),
            newDatabaseFile = destinationFile,
            oldPassword = passwordManager.getPassword(account.databaseName()),
            newPassword = "",
            tag = "Decrypt",
            oldHook = hook,
            newHook = null,
        )
    }

    @Suppress("LongParameterList")
    private fun export(
        oldDatabaseFile: File,
        newDatabaseFile: File,
        oldPassword: String?,
        newPassword: String,
        tag: String,
        oldHook: SQLiteDatabaseHook?,
        newHook: SQLiteDatabaseHook?,
    ) {
        wrapAction({
            loadSQLCipher()

            val oldDatabase = SQLiteDatabase.openOrCreateDatabase(oldDatabaseFile, oldPassword, null, null, oldHook)
            oldDatabase.rawExecSQL(
                String.format(
                    "ATTACH DATABASE '%s' as alias KEY '%s';",
                    newDatabaseFile.absolutePath,
                    newPassword,
                ),
            )

            if (newHook != null) {
                oldDatabase.rawExecSQL("PRAGMA alias.cipher_page_size = 16384;")
                oldDatabase.rawExecSQL("PRAGMA alias.cipher_memory_security = OFF;")
            }
            oldDatabase.rawExecSQL("SELECT sqlcipher_export('alias');")
            oldDatabase.rawExecSQL("DETACH DATABASE alias;")

            val version = oldDatabase.version
            val newDatabase = SQLiteDatabase.openOrCreateDatabase(
                newDatabaseFile,
                newPassword,
                null,
                null,
                newHook,
            )
            newDatabase.version = version

            newDatabase.close()
            oldDatabase.close()
        }, tag)
    }

    @Suppress("TooGenericExceptionCaught", "TooGenericExceptionThrown")
    private fun wrapAction(action: Action, tag: String) {
        val startMillis = System.currentTimeMillis()
        try {
            action.run()
        } catch (e: Exception) {
            throw RuntimeException("Exception thrown during database export action: $tag")
        }
        val endMillis = System.currentTimeMillis()
        Log.e("DatabaseExport", tag + ": " + (endMillis - startMillis) + "ms")
    }

    companion object {
        val hook: SQLiteDatabaseHook = object : SQLiteDatabaseHook {
            override fun preKey(connection: SQLiteConnection) {
                // Nothing to do here
            }

            override fun postKey(connection: SQLiteConnection) {
                // Should we add a Cancellation signal here?
                connection.executeRaw("PRAGMA cipher_page_size = 16384;", null, null)
                connection.execute("PRAGMA cipher_memory_security = OFF;", null, null)
            }
        }
    }
}

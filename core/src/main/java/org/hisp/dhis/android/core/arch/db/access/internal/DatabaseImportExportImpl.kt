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
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.access.DatabaseExportMetadata
import org.hisp.dhis.android.core.arch.db.access.DatabaseImportExport
import org.hisp.dhis.android.core.arch.json.internal.ObjectMapperFactory.objectMapper
import org.hisp.dhis.android.core.arch.storage.internal.CredentialsSecureStore
import org.hisp.dhis.android.core.configuration.internal.DatabaseAccount
import org.hisp.dhis.android.core.configuration.internal.MultiUserDatabaseManager
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent
import org.hisp.dhis.android.core.user.UserModule
import org.hisp.dhis.android.core.util.CipherUtil
import org.hisp.dhis.android.core.util.FileUtils
import org.hisp.dhis.android.core.util.simpleDateFormat
import org.koin.core.annotation.Singleton
import java.io.File
import java.util.Date

@Singleton
internal class DatabaseImportExportImpl(
    private val context: Context,
    private val multiUserDatabaseManager: MultiUserDatabaseManager,
    private val userModule: UserModule,
    private val credentialsStore: CredentialsSecureStore,
    private val databaseAdapter: DatabaseAdapter,
) : DatabaseImportExport {

    companion object {
        const val ExportDatabase = "export-database.db"
        const val ExportDatabaseProtected = "export-database-protected.db"
        const val ExportMetadata = "export-metadata.json"
        const val ExportZip = "export-database.zip"
    }

    private val d2ErrorBuilder = D2Error.builder()
        .errorComponent(D2ErrorComponent.SDK)

    @Suppress("TooGenericExceptionCaught")
    override fun importDatabase(file: File): DatabaseExportMetadata {
        if (userModule.blockingIsLogged()) {
            throw d2ErrorBuilder
                .errorDescription("Please log out to import database")
                .errorCode(D2ErrorCode.DATABASE_IMPORT_LOGOUT_FIRST)
                .build()
        }

        val importMetadataFile = getWorkingDir().resolve(ExportMetadata).also { it.delete() }
        val importDatabaseFile = getWorkingDir().resolve(ExportDatabaseProtected).also { it.delete() }

        return try {
            FileUtils.unzipFiles(file, getWorkingDir())

            if (!importMetadataFile.exists() || !importDatabaseFile.exists()) {
                throw d2ErrorBuilder
                    .errorDescription("Import file is not valid")
                    .errorCode(D2ErrorCode.DATABASE_IMPORT_INVALID_FILE)
                    .build()
            }

            val metadataContent = importMetadataFile.readText(Charsets.UTF_8)
            val metadata = objectMapper().readValue(metadataContent, DatabaseExportMetadata::class.java)

            when {
                metadata.version > BaseDatabaseOpenHelper.VERSION ->
                    throw d2ErrorBuilder
                        .errorDescription("Import database version higher than supported")
                        .errorCode(D2ErrorCode.DATABASE_IMPORT_VERSION_HIGHER_THAN_SUPPORTED)
                        .build()

                getExistingAccountForMetadata(metadata) != null ->
                    throw d2ErrorBuilder
                        .errorDescription("Import database already exists")
                        .errorCode(D2ErrorCode.DATABASE_IMPORT_ALREADY_EXISTS)
                        .build()

                else -> {
                    val databaseAccount = multiUserDatabaseManager.createNewPendingToImport(metadata)
                    val destDatabase = context.getDatabasePath(databaseAccount.importDB()!!.protectedDbName())
                    importDatabaseFile.copyTo(destDatabase)

                    metadata
                }
            }
        } catch (e: Exception) {
            when (e) {
                is D2Error -> throw e
                else ->
                    throw d2ErrorBuilder
                        .errorDescription("Import database failed")
                        .errorCode(D2ErrorCode.DATABASE_IMPORT_FAILED)
                        .originalException(e)
                        .build()
            }
        } finally {
            importMetadataFile.delete()
            importDatabaseFile.delete()
        }
    }

    override fun exportLoggedUserDatabase(): File {
        val exportMetadataFile = getWorkingDir().resolve(ExportMetadata).also { it.delete() }
        val copiedDatabase = getWorkingDir().resolve(ExportDatabase).also { it.delete() }
        val protectedDatabase = getWorkingDir().resolve(ExportDatabaseProtected).also { it.delete() }
        val zipFile = getWorkingDir().resolve(ExportZip).also { it.delete() }

        if (!userModule.blockingIsLogged()) {
            throw d2ErrorBuilder
                .errorDescription("Please log in to export database")
                .errorCode(D2ErrorCode.DATABASE_EXPORT_LOGIN_FIRST)
                .build()
        }

        val credentials = credentialsStore.get()
        val userConfiguration = multiUserDatabaseManager.getAccount(
            username = credentials.username,
            serverUrl = credentials.serverUrl,
        )!!

        if (userConfiguration.encrypted()) {
            throw d2ErrorBuilder
                .errorDescription("Database export of encrypted database not supported")
                .errorCode(D2ErrorCode.DATABASE_EXPORT_ENCRYPTED_NOT_SUPPORTED)
                .build()
        }

        databaseAdapter.close()

        val databaseName = userConfiguration.databaseName()
        val databaseFile = getDatabaseFile(databaseName)
        databaseFile.copyTo(copiedDatabase)
        CipherUtil.encryptFileUsingCredentials(
            input = copiedDatabase,
            output = protectedDatabase,
            username = credentials.username,
            password = credentials.password!!,
        )

        val metadata = DatabaseExportMetadata(
            version = BaseDatabaseOpenHelper.VERSION,
            date = Date().simpleDateFormat()!!,
            serverUrl = userConfiguration.serverUrl(),
            username = userConfiguration.username(),
            encrypted = userConfiguration.encrypted(),
        )

        exportMetadataFile.bufferedWriter(Charsets.UTF_8).use {
            it.write(objectMapper().writeValueAsString(metadata))
        }

        FileUtils.zipFiles(
            files = listOf(exportMetadataFile, protectedDatabase),
            zipFile = zipFile,
        )

        exportMetadataFile.delete()
        copiedDatabase.delete()
        protectedDatabase.delete()

        return zipFile
    }

    private fun getWorkingDir(): File {
        return context.filesDir
    }

    private fun getDatabaseFile(dbName: String): File {
        return context.getDatabasePath(dbName)
    }

    private fun getExistingAccountForMetadata(metadata: DatabaseExportMetadata): DatabaseAccount? {
        return multiUserDatabaseManager.getAccount(
            metadata.serverUrl,
            metadata.username,
        )
    }
}

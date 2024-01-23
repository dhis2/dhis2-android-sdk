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
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.access.DatabaseImportExport
import org.hisp.dhis.android.core.arch.json.internal.ObjectMapperFactory.objectMapper
import org.hisp.dhis.android.core.arch.storage.internal.CredentialsSecureStore
import org.hisp.dhis.android.core.configuration.internal.DatabaseConfigurationHelper
import org.hisp.dhis.android.core.configuration.internal.DatabaseConfigurationInsecureStore
import org.hisp.dhis.android.core.configuration.internal.DatabaseNameGenerator
import org.hisp.dhis.android.core.configuration.internal.DatabaseRenamer
import org.hisp.dhis.android.core.configuration.internal.MultiUserDatabaseManager
import org.hisp.dhis.android.core.configuration.internal.ServerUrlParser
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent
import org.hisp.dhis.android.core.systeminfo.internal.SystemInfoStoreImpl
import org.hisp.dhis.android.core.user.UserModule
import org.hisp.dhis.android.core.user.internal.UserStoreImpl
import org.hisp.dhis.android.core.util.FileUtils
import org.hisp.dhis.android.core.util.simpleDateFormat
import org.koin.core.annotation.Singleton
import java.io.File
import java.util.Date

@Singleton
internal class DatabaseImportExportImpl(
    private val context: Context,
    private val nameGenerator: DatabaseNameGenerator,
    private val multiUserDatabaseManager: MultiUserDatabaseManager,
    private val userModule: UserModule,
    private val credentialsStore: CredentialsSecureStore,
    private val databaseConfigurationSecureStore: DatabaseConfigurationInsecureStore,
    private val databaseRenamer: DatabaseRenamer,
    private val databaseAdapter: DatabaseAdapter,
) : DatabaseImportExport {

    companion object {
        const val TmpDatabase = "tmp-database.db"
        const val ExportDatabase = "export-database.db"
        const val ExportMetadata = "export-metadata.json"
    }

    private val d2ErrorBuilder = D2Error.builder()
        .errorComponent(D2ErrorComponent.SDK)

    override fun importDatabase(file: File) {
        if (userModule.blockingIsLogged()) {
            throw d2ErrorBuilder
                .errorDescription("Please log out to import database")
                .errorCode(D2ErrorCode.DATABASE_IMPORT_LOGOUT_FIRST)
                .build()
        }

        var databaseAdapter: DatabaseAdapter? = null
        try {
            context.deleteDatabase(TmpDatabase)
            val tmpDatabase = context.getDatabasePath(TmpDatabase)
            file.copyTo(tmpDatabase)

            val openHelper = UnencryptedDatabaseOpenHelper(context, TmpDatabase, BaseDatabaseOpenHelper.VERSION)
            val database = openHelper.readableDatabase
            databaseAdapter = UnencryptedDatabaseAdapter(database, openHelper.databaseName)

            if (database.version > BaseDatabaseOpenHelper.VERSION) {
                throw d2ErrorBuilder
                    .errorDescription("Import database version higher than supported")
                    .errorCode(D2ErrorCode.DATABASE_IMPORT_VERSION_HIGHER_THAN_SUPPORTED)
                    .build()
            }

            val userStore = UserStoreImpl(databaseAdapter)
            val username = userStore.selectFirst()!!.username()

            val systemInfoStore = SystemInfoStoreImpl(databaseAdapter)
            val contextPath = systemInfoStore.selectFirst()!!.contextPath()!!
            val serverUrl = ServerUrlParser.parse(contextPath).toString()

            // TODO What to do if username is null?
            val databaseName = nameGenerator.getDatabaseName(serverUrl, username!!, false)

            if (!context.databaseList().contains(databaseName)) {
                val destDatabase = context.getDatabasePath(databaseName)
                file.copyTo(destDatabase)

                multiUserDatabaseManager.createNew(serverUrl, username, false)
            } else {
                throw d2ErrorBuilder
                    .errorDescription("Import database already exists")
                    .errorCode(D2ErrorCode.DATABASE_IMPORT_ALREADY_EXISTS)
                    .build()
            }
        } finally {
            databaseAdapter?.close()
            context.deleteDatabase(TmpDatabase)
        }
    }

    override fun exportLoggedUserDatabase(): File {
        context.deleteDatabase(ExportDatabase)

        if (!userModule.blockingIsLogged()) {
            throw d2ErrorBuilder
                .errorDescription("Please log in to export database")
                .errorCode(D2ErrorCode.DATABASE_EXPORT_LOGIN_FIRST)
                .build()
        }

        val credentials = credentialsStore.get()
        val databasesConfiguration = databaseConfigurationSecureStore.get()
        val userConfiguration = DatabaseConfigurationHelper.getLoggedAccount(
            configuration = databasesConfiguration,
            username = credentials.username,
            serverUrl = credentials.serverUrl,
        )

        if (userConfiguration.encrypted()) {
            throw d2ErrorBuilder
                .errorDescription("Database export of encrypted database not supported")
                .errorCode(D2ErrorCode.DATABASE_EXPORT_ENCRYPTED_NOT_SUPPORTED)
                .build()
        }

        databaseAdapter.close()

        val databaseName = userConfiguration.databaseName()
        val copiedDatabase = databaseRenamer.copyDatabase(databaseName, ExportDatabase)

        val metadata = DatabaseExportMetadata(
            version = "V1",
            date = Date().simpleDateFormat()!!,
            serverUrl = credentials.serverUrl,
            username = credentials.username,
        )

        val exportMetadataPath = copiedDatabase.parentFile?.let { "${it.path}/${ExportMetadata}".toPath() }
        FileSystem.SYSTEM.sink(exportMetadataPath!!).use { sinkFile ->
            sinkFile.buffer().use { bufferedSinkFile ->
                bufferedSinkFile.writeUtf8(objectMapper().writeValueAsString(metadata))
            }
        }

        FileUtils.zipFiles(exportMetadataPath, exportMetadataPath.parent!!.resolve("zipped.zip"))

        return copiedDatabase
    }
}

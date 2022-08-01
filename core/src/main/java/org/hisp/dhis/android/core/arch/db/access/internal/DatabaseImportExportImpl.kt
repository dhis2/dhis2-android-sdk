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
package org.hisp.dhis.android.core.arch.db.access.internal

import android.content.Context
import dagger.Reusable
import java.io.File
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.access.DatabaseImportExport
import org.hisp.dhis.android.core.arch.storage.internal.CredentialsSecureStore
import org.hisp.dhis.android.core.arch.storage.internal.ObjectKeyValueStore
import org.hisp.dhis.android.core.configuration.internal.*
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent
import org.hisp.dhis.android.core.systeminfo.internal.SystemInfoStore
import org.hisp.dhis.android.core.user.UserModule
import org.hisp.dhis.android.core.user.internal.UserCredentialsStoreImpl

@Reusable
internal class DatabaseImportExportImpl @Inject constructor(
    private val context: Context,
    private val nameGenerator: DatabaseNameGenerator,
    private val multiUserDatabaseManager: MultiUserDatabaseManager,
    private val userModule: UserModule,
    private val credentialsStore: CredentialsSecureStore,
    private val databaseConfigurationSecureStore: ObjectKeyValueStore<DatabasesConfiguration>,
    private val databaseRenamer: DatabaseRenamer,
    private val databaseAdapter: DatabaseAdapter
) : DatabaseImportExport {

    companion object {
        const val TmpDatabase = "tmp-database.db"
        const val ExportDatabase = "export-database.db"
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

            val userCredentialsStore = UserCredentialsStoreImpl.create(databaseAdapter)
            val username = userCredentialsStore.selectFirst()!!.username()

            val systemInfoStore = SystemInfoStore.create(databaseAdapter)
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
            databasesConfiguration,
            credentials.serverUrl, credentials.username
        )

        if (userConfiguration.encrypted()) {
            throw d2ErrorBuilder
                .errorDescription("Database export of encrypted database not supported")
                .errorCode(D2ErrorCode.DATABASE_EXPORT_ENCRYPTED_NOT_SUPPORTED)
                .build()
        }

        databaseAdapter.close()

        val databaseName = userConfiguration.databaseName()
        return databaseRenamer.copyDatabase(databaseName, ExportDatabase)
    }
}

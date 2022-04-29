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

import dagger.Reusable
import java.util.*
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.api.internal.ServerURLWrapper
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.access.internal.DatabaseAdapterFactory
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.arch.storage.internal.Credentials
import org.hisp.dhis.android.core.arch.storage.internal.ObjectKeyValueStore

@Reusable
internal class MultiUserDatabaseManagerForD2Manager @Inject constructor(
    private val databaseAdapter: DatabaseAdapter,
    private val migration: DatabaseConfigurationMigration,
    private val databaseAdapterFactory: DatabaseAdapterFactory,
    private val databaseConfigurationStore: ObjectKeyValueStore<DatabasesConfiguration>
) {
    fun loadIfLogged(credentials: Credentials?) {
        val databaseConfiguration = databaseConfigurationStore.get()
        if (databaseConfiguration != null && credentials != null) {
            ServerURLWrapper.setServerUrl(credentials.serverUrl)
            val account = DatabaseConfigurationHelper.getLoggedAccount(
                databaseConfiguration, credentials.username, credentials.serverUrl
            )
            databaseAdapterFactory.createOrOpenDatabase(databaseAdapter, account)
        }
    }

    fun loadDbForTesting(serverUrl: String?, name: String?, encrypt: Boolean, username: String?) {
        val config = DatabaseAccount.builder()
            .databaseName(name)
            .encrypted(encrypt)
            .username(username)
            .serverUrl(serverUrl)
            .databaseCreationDate(DateUtils.DATE_FORMAT.format(Date()))
            .build()
        databaseAdapterFactory.createOrOpenDatabase(databaseAdapter, config)
    }

    fun applyMigration() {
        migration.apply()
    }
}

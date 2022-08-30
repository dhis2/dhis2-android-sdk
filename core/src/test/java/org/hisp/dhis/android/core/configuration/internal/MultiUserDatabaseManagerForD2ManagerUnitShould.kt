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

import com.nhaarman.mockitokotlin2.*
import org.hisp.dhis.android.core.arch.db.access.internal.DatabaseAdapterFactory
import org.hisp.dhis.android.core.arch.storage.internal.Credentials
import org.hisp.dhis.android.core.arch.storage.internal.ObjectKeyValueStore
import org.hisp.dhis.android.core.common.BaseCallShould
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class MultiUserDatabaseManagerForD2ManagerUnitShould : BaseCallShould() {

    private val databaseAdapterFactory: DatabaseAdapterFactory = mock()
    private val migration: DatabaseConfigurationMigration = mock()
    private val databaseConfigurationStore: ObjectKeyValueStore<DatabasesConfiguration> = mock()

    private val username = "username"
    private val serverUrl = "https://dhis2.org"
    private val credentials = Credentials(username, serverUrl, "password", null)
    private val unencryptedDbName = "un.db"

    private val accountUnencrypted = DatabaseAccount.builder()
        .databaseName(unencryptedDbName)
        .username(username)
        .serverUrl(serverUrl)
        .encrypted(false)
        .databaseCreationDate(DATE)
        .build()

    private val databasesConfiguration = DatabasesConfiguration.builder()
        .accounts(listOf(accountUnencrypted))
        .build()

    private lateinit var manager: MultiUserDatabaseManagerForD2Manager

    @Before
    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()
        manager = MultiUserDatabaseManagerForD2Manager(
            databaseAdapter, migration, databaseAdapterFactory, databaseConfigurationStore
        )
        whenever(databaseConfigurationStore.get()).doReturn(databasesConfiguration)
    }

    @Test
    fun not_try_to_load_db_if_not_logged_when_calling_loadIfLogged() {
        manager.loadIfLogged(null)
        verifyNoMoreInteractions(databaseAdapterFactory)
    }

    @Test
    fun load_db_if_logged_when_calling_loadIfLogged() {
        manager.loadIfLogged(credentials)
        verify(databaseAdapterFactory).createOrOpenDatabase(databaseAdapter, accountUnencrypted)
    }

    companion object {
        private const val DATE = "2014-06-06T20:44:21.375"
    }
}

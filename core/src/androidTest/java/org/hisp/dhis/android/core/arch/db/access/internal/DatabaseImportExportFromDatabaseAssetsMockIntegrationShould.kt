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

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTest
import org.hisp.dhis.android.core.utils.integration.mock.MockIntegrationTestDatabaseContent
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.AfterClass
import org.junit.Assert.fail
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class DatabaseImportExportFromDatabaseAssetsMockIntegrationShould : BaseMockIntegrationTest() {

    companion object {

        const val username = "android"
        const val password = "Android123"
        const val host = "localhost"
        const val port = 60809
        const val serverUrl = "http://$host:$port"

        val importer = TestDatabaseImporter()

        @BeforeClass
        @JvmStatic
        fun setUpClass() {
            setUpClass(MockIntegrationTestDatabaseContent.DatabaseImportExport, port)
            dhis2MockServer.setRequestDispatcher()
        }

        @AfterClass
        @JvmStatic
        fun tearDownClass() {
            dhis2MockServer.shutdown()
        }
    }

    @Before
    fun setUp() {
        if (d2.userModule().blockingIsLogged()) {
            d2.userModule().blockingLogOut()
        }
    }

    @Test
    fun import_fail_when_logged_in() {
        d2.userModule().blockingLogIn("other_user", "other_password", serverUrl)

        try {
            d2.maintenanceModule().databaseImportExport().importDatabase(importer.validDatabaseFile(d2.context()))
            fail("It should throw an error")
        } catch (e: D2Error) {
            assertThat(e.errorCode()).isEqualTo(D2ErrorCode.DATABASE_IMPORT_LOGOUT_FIRST)
        } finally {
            d2.userModule().accountManager().deleteCurrentAccount()
        }
    }

    @Test
    fun import_fail_when_account_exists() {
        d2.userModule().blockingLogIn(username, password, serverUrl)
        d2.userModule().blockingLogOut()

        try {
            d2.maintenanceModule().databaseImportExport().importDatabase(importer.validDatabaseFile(d2.context()))
            fail("It should throw an error")
        } catch (e: D2Error) {
            assertThat(e.errorCode()).isEqualTo(D2ErrorCode.DATABASE_IMPORT_ALREADY_EXISTS)
        } finally {
            d2.userModule().blockingLogIn(username, password, serverUrl)
            d2.userModule().accountManager().deleteCurrentAccount()
        }
    }

    @Test
    fun import_fail_when_invalid_database_file() {
        d2.userModule().blockingLogIn(username, password, serverUrl)
        d2.userModule().blockingLogOut()

        try {
            d2.maintenanceModule().databaseImportExport().importDatabase(importer.invalidDatabaseFile(d2.context()))
            fail("It should throw an error")
        } catch (e: D2Error) {
            assertThat(e.errorCode()).isEqualTo(D2ErrorCode.DATABASE_IMPORT_INVALID_FILE)
        } finally {
            d2.userModule().blockingLogIn(username, password, serverUrl)
            d2.userModule().accountManager().deleteCurrentAccount()
        }
    }

    @Test
    fun import_fail_when_no_zip_file() {
        d2.userModule().blockingLogIn(username, password, serverUrl)
        d2.userModule().blockingLogOut()

        try {
            d2.maintenanceModule().databaseImportExport().importDatabase(importer.noZipFile(d2.context()))
            fail("It should throw an error")
        } catch (e: D2Error) {
            assertThat(e.errorCode()).isEqualTo(D2ErrorCode.DATABASE_IMPORT_FAILED)
        } finally {
            d2.userModule().blockingLogIn(username, password, serverUrl)
            d2.userModule().accountManager().deleteCurrentAccount()
        }
    }

    @Test
    fun export_fail_when_not_logged() {
        try {
            d2.maintenanceModule().databaseImportExport().exportLoggedUserDatabase()
            fail("It should throw an error")
        } catch (e: D2Error) {
            assertThat(e.errorCode()).isEqualTo(D2ErrorCode.DATABASE_EXPORT_LOGIN_FIRST)
        }
    }

    @Test
    fun export_and_reimport() {
        d2.userModule().blockingLogIn(username, password, serverUrl)
        d2.metadataModule().blockingDownload()

        assertThat(d2.programModule().programs().blockingCount()).isEqualTo(3)

        val exportedFile = d2.maintenanceModule().databaseImportExport().exportLoggedUserDatabase()

        d2.userModule().accountManager().deleteCurrentAccount()

        val fileMetadata = d2.maintenanceModule().databaseImportExport().importDatabase(exportedFile)

        assertThat(fileMetadata.username).isEqualTo(username)
        assertThat(fileMetadata.serverUrl).isEqualTo(serverUrl)

        try {
            d2.userModule().blockingLogIn(username, "other-password", serverUrl)
            fail("It should throw an error")
        } catch (e: RuntimeException) {
            assertThat((e.cause as D2Error).errorCode()).isEqualTo(D2ErrorCode.BAD_CREDENTIALS)
        }

        d2.userModule().blockingLogIn(username, password, serverUrl)

        assertThat(d2.programModule().programs().blockingCount()).isEqualTo(3)

        d2.userModule().accountManager().deleteCurrentAccount()
    }
}

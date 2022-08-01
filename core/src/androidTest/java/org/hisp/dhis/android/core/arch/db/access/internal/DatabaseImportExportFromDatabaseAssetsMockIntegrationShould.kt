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

// @RunWith(D2JunitRunner::class)
class DatabaseImportExportFromDatabaseAssetsMockIntegrationShould {

    /*companion object LocalAnalyticsAggregatedLargeDataMockIntegrationShould {

        val context = InstrumentationRegistry.getInstrumentation().context
        val server = Dhis2MockServer(60809)
        val importer = TestDatabaseImporter()

        const val expectedDatabaseName = "localhost-60809_android_unencrypted.db"
        const val serverUrl = "http://localhost:60809/"

        @BeforeClass
        @JvmStatic
        fun setUpClass() {
            server.setRequestDispatcher()
        }

        @AfterClass
        @JvmStatic
        fun tearDownClass() {
            server.shutdown()
        }
    }

    @After
    fun tearDown() {
        context.deleteDatabase(expectedDatabaseName)
        context.databaseList().forEach { dbName -> context.deleteDatabase(dbName) }
    }

    @Test
    fun import_database_when_not_logged() {
        importer.copyDatabaseFromAssets()

        val d2 = D2Factory.forNewDatabase()

        d2.maintenanceModule().databaseImportExport().importDatabase(importer.databaseFile(context))

        d2.userModule().blockingLogIn("android", "Android123", serverUrl)

        assertThat(d2.programModule().programs().blockingCount()).isEqualTo(2)
    }

    @Test(expected = D2Error::class)
    fun import_fail_when_logged_in() {
        importer.copyDatabaseFromAssets()

        val d2 = D2Factory.forNewDatabase()

        d2.userModule().blockingLogIn("other", "Pw1010", serverUrl)

        d2.maintenanceModule().databaseImportExport().importDatabase(importer.databaseFile(context))
    }

    @Test(expected = D2Error::class)
    fun import_fail_when_database_exists() {
        importer.copyDatabaseFromAssets(expectedDatabaseName)

        val d2 = D2Factory.forNewDatabase()

        d2.maintenanceModule().databaseImportExport().importDatabase(importer.databaseFile(context, expectedDatabaseName))
    }

    @Test
    fun export_when_logged() {
        val d2 = D2Factory.forNewDatabase()

        d2.userModule().blockingLogIn("android", "Pw1010", serverUrl)

        val exportedFile = d2.maintenanceModule().databaseImportExport().exportLoggedUserDatabase()

        assertThat(exportedFile.path).isEqualTo("/data/user/0/org.hisp.dhis.android.test/databases/export-database.db")
    }

    @Test(expected = D2Error::class)
    fun export_fail_when_not_logged() {
        val d2 = D2Factory.forNewDatabase()
        d2.maintenanceModule().databaseImportExport().exportLoggedUserDatabase()
    }

    @Test
    fun export_and_reimport() {
        var d2 = D2Factory.forNewDatabase()

        d2.userModule().blockingLogIn("android", "Android123", serverUrl)

        d2.metadataModule().blockingDownload()

        assertThat(d2.programModule().programs().blockingCount()).isEqualTo(2)

        val systemInfoWithExpectedContextPath = d2.systemInfoModule().systemInfo().blockingGet()
            .toBuilder().contextPath(serverUrl).build()

        d2.databaseAdapter().delete(SystemInfoTableInfo.TABLE_INFO.name())
        d2.databaseAdapter().insert(SystemInfoTableInfo.TABLE_INFO.name(), null,
            systemInfoWithExpectedContextPath.toContentValues())


        val exportedFile = d2.maintenanceModule().databaseImportExport().exportLoggedUserDatabase()

        d2.userModule().blockingLogOut()

        context.deleteDatabase(expectedDatabaseName)

        // We won't need to create a new D2 when we support database deletion (multi-user)
        d2 = D2Factory.forNewDatabase()

        d2.maintenanceModule().databaseImportExport().importDatabase(exportedFile)

        d2.userModule().blockingLogIn("android", "Android123", serverUrl)

        assertThat(d2.programModule().programs().blockingCount()).isEqualTo(2)
    }*/
}

/*
 *  Copyright (c) 2004-2026, University of Oslo
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
import kotlinx.coroutines.test.runTest
import okio.IOException
import okio.Path
import okio.fakefilesystem.FakeFileSystem
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.file.internal.ArchiveHelper
import org.hisp.dhis.android.core.arch.file.internal.FakePlatformDirectories
import org.hisp.dhis.android.core.arch.storage.internal.Credentials
import org.hisp.dhis.android.core.arch.storage.internal.CredentialsSecureStore
import org.hisp.dhis.android.core.configuration.internal.DatabaseAccount
import org.hisp.dhis.android.core.configuration.internal.MultiUserDatabaseManager
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.user.UserModule
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.util.Date

@RunWith(JUnit4::class)
class DatabaseImportExportImplShould {

    private val fileSystem = FakeFileSystem()
    private val platformDirectories = FakePlatformDirectories(fileSystem)
    private val archiveHelper: ArchiveHelper = mock()
    private val multiUserDatabaseManager: MultiUserDatabaseManager = mock()
    private val userModule: UserModule = mock()
    private val credentialsStore: CredentialsSecureStore = mock()
    private val databaseExport: BaseDatabaseExport = mock()
    private val databaseAdapter: DatabaseAdapter = mock()

    private val importExport = DatabaseImportExportImpl(
        fileSystem,
        platformDirectories,
        archiveHelper,
        multiUserDatabaseManager,
        userModule,
        credentialsStore,
        databaseExport,
        databaseAdapter,
    )

    @Before
    fun setUp() = runTest {
        fileSystem.createDirectories(platformDirectories.filesDir)
        val database = platformDirectories.databasePath(DATABASE_NAME)
        fileSystem.createDirectories(database.parent!!)
        fileSystem.write(database) { writeUtf8("database") }

        whenever(userModule.suspendIsLogged()).thenReturn(true)
        whenever(credentialsStore.get()).thenReturn(Credentials(USERNAME, SERVER_URL, PASSWORD, null, null))
        doAnswer { writeFile(it.getArgument(1)) }.whenever(archiveHelper).createEncrypted(any(), any(), any())
    }

    @After
    fun tearDown() {
        fileSystem.checkNoOpenFiles()
    }

    @Test
    fun delete_the_working_files_when_the_archive_cannot_be_written() = runTest {
        whenever(multiUserDatabaseManager.getAccount(any(), any())).thenReturn(account(encrypted = false))
        doAnswer { throw IOException("No space left") }.whenever(archiveHelper).zip(any(), any())

        assertExportFails()

        assertThat(fileSystem.list(platformDirectories.filesDir)).isEmpty()
    }

    @Test
    fun delete_the_decrypted_copy_when_the_encrypted_archive_cannot_be_created() = runTest {
        whenever(multiUserDatabaseManager.getAccount(any(), any())).thenReturn(account(encrypted = true))
        doAnswer { writeFile(it.getArgument(1)) }.whenever(databaseExport).decryptAndCopyTo(any(), any())
        doAnswer { throw IOException("Cannot encrypt") }.whenever(archiveHelper).createEncrypted(any(), any(), any())

        assertExportFails()

        assertThat(fileSystem.list(platformDirectories.filesDir)).isEmpty()
    }

    private suspend fun assertExportFails() {
        val error = runCatching { importExport.exportLoggedUserDatabaseToPath() }.exceptionOrNull()

        assertThat(error).isInstanceOf(D2Error::class.java)
        assertThat((error as D2Error).errorCode()).isEqualTo(D2ErrorCode.DATABASE_EXPORT_FAILED)
    }

    private fun writeFile(path: Path) {
        fileSystem.write(path) { writeUtf8("content") }
    }

    private fun account(encrypted: Boolean): DatabaseAccount {
        return DatabaseAccount.builder()
            .username(USERNAME)
            .serverUrl(SERVER_URL)
            .databaseName(DATABASE_NAME)
            .encrypted(encrypted)
            .databaseCreationDate(Date())
            .build()
    }

    private companion object {
        const val USERNAME = "android"
        const val SERVER_URL = "https://play.dhis2.org"
        const val PASSWORD = "Android123"
        const val DATABASE_NAME = "play-android.db"
    }
}

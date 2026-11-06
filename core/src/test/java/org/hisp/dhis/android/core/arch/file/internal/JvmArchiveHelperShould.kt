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

package org.hisp.dhis.android.core.arch.file.internal

import com.google.common.truth.Truth.assertThat
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.model.enums.EncryptionMethod
import okio.FileSystem
import okio.IOException
import okio.Path
import okio.Path.Companion.toOkioPath
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.junit.After
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@RunWith(JUnit4::class)
class JvmArchiveHelperShould {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private val fileSystem = FakeFileSystem()
    private val archiveHelper = JvmArchiveHelper(fileSystem)

    private val directory = "/app/files".toPath()
    private val zipFile = "/external/import.zip".toPath()

    /**
     * zip4j only works on `java.io.File`, so the encrypted archives are tested on the file system of the platform.
     */
    private val systemArchiveHelper = JvmArchiveHelper(FileSystem.SYSTEM)
    private lateinit var systemDirectory: Path

    @Before
    fun setUp() {
        fileSystem.createDirectories(directory)
        fileSystem.createDirectories(zipFile.parent!!)
        systemDirectory = temporaryFolder.newFolder("app", "files").toOkioPath()
    }

    @After
    fun tearDown() {
        fileSystem.checkNoOpenFiles()
    }

    @Test
    fun extract_what_zip_writes() {
        val metadata = writeFile("/external/metadata.json".toPath(), "metadata")
        val database = writeFile("/external/database.zip".toPath(), "database")

        archiveHelper.zip(listOf(metadata, database), zipFile)
        archiveHelper.unzip(zipFile, directory, setOf("metadata.json", "database.zip"))

        assertThat(readFile(directory / "metadata.json")).isEqualTo("metadata")
        assertThat(readFile(directory / "database.zip")).isEqualTo("database")
    }

    @Test
    fun delete_the_partial_archive_when_a_file_cannot_be_read() {
        val metadata = writeFile("/external/metadata.json".toPath(), "metadata")

        assertThrows(IOException::class.java) {
            archiveHelper.zip(listOf(metadata, "/external/missing.zip".toPath()), zipFile)
        }

        assertThat(fileSystem.exists(zipFile)).isFalse()
    }

    @Test
    fun extract_only_the_requested_entries() {
        writeZip("metadata.json" to "metadata", "database.zip" to "database", "other.txt" to "other")

        archiveHelper.unzip(zipFile, directory, setOf("metadata.json", "database.zip", "missing.txt"))

        assertThat(fileSystem.list(directory).map { it.name }).containsExactly("metadata.json", "database.zip")
    }

    @Test
    fun ignore_entries_that_point_outside_the_directory() {
        writeZip("../shared_prefs/prefs.xml" to "overwritten", "metadata.json" to "metadata")

        archiveHelper.unzip(zipFile, directory, setOf("metadata.json"))

        assertThat(readFile(directory / "metadata.json")).isEqualTo("metadata")
        assertThat(fileSystem.exists("/app/shared_prefs/prefs.xml".toPath())).isFalse()
    }

    @Test
    fun reject_requested_names_that_are_not_plain_file_names() {
        writeZip("../shared_prefs/prefs.xml" to "overwritten")

        assertThrows(IllegalArgumentException::class.java) {
            archiveHelper.unzip(zipFile, directory, setOf("../shared_prefs/prefs.xml"))
        }

        assertThat(fileSystem.exists("/app/shared_prefs/prefs.xml".toPath())).isFalse()
    }

    @Test
    fun fail_when_the_file_is_not_a_zip() {
        writeFile(zipFile, "not a zip")

        assertThrows(IOException::class.java) {
            archiveHelper.unzip(zipFile, directory, setOf("metadata.json"))
        }
    }

    @Test
    fun extract_what_create_encrypted_writes() {
        val database = systemDirectory / "export-database.db"
        FileSystem.SYSTEM.write(database) { writeUtf8("database") }
        val protectedDatabase = systemDirectory / "export-database-protected.db.zip"
        val extracted = systemDirectory / "temp.db"

        systemArchiveHelper.createEncrypted(database, protectedDatabase, PASSWORD)
        systemArchiveHelper.extractEncrypted(protectedDatabase, extracted, PASSWORD)

        assertThat(FileSystem.SYSTEM.read(extracted) { readUtf8() }).isEqualTo("database")
    }

    @Test
    fun extract_the_encrypted_entry_to_the_target_whatever_its_name() {
        val protectedDatabase = systemDirectory / "export-database-protected.db.zip"
        writeEncryptedZip(protectedDatabase, "../databases/target.db" to "overwritten")
        val extracted = systemDirectory / "temp.db"

        systemArchiveHelper.extractEncrypted(protectedDatabase, extracted, PASSWORD)

        assertThat(FileSystem.SYSTEM.read(extracted) { readUtf8() }).isEqualTo("overwritten")
        assertThat(FileSystem.SYSTEM.exists(systemDirectory.parent!! / "databases" / "target.db")).isFalse()
    }

    @Test
    fun fail_when_the_encrypted_archive_does_not_have_a_single_entry() {
        val protectedDatabase = systemDirectory / "export-database-protected.db.zip"
        writeEncryptedZip(protectedDatabase, "first.db" to "first", "second.db" to "second")

        val error = assertThrows(D2Error::class.java) {
            systemArchiveHelper.extractEncrypted(protectedDatabase, systemDirectory / "temp.db", PASSWORD)
        }

        assertThat(error.errorCode()).isEqualTo(D2ErrorCode.DATABASE_IMPORT_INVALID_FILE)
    }

    private fun writeFile(path: Path, content: String): Path {
        fileSystem.write(path) { writeUtf8(content) }
        return path
    }

    private fun readFile(path: Path): String {
        return fileSystem.read(path) { readUtf8() }
    }

    private fun writeZip(vararg entries: Pair<String, String>) {
        fileSystem.write(zipFile) {
            ZipOutputStream(outputStream()).use { zos ->
                entries.forEach { (name, content) ->
                    zos.putNextEntry(ZipEntry(name))
                    zos.write(content.toByteArray())
                    zos.closeEntry()
                }
            }
        }
    }

    private fun writeEncryptedZip(zipPath: Path, vararg entries: Pair<String, String>) {
        ZipFile(zipPath.toFile(), PASSWORD.toCharArray()).use { zip ->
            entries.forEach { (name, content) ->
                val source = temporaryFolder.newFile().apply { writeText(content) }
                val parameters = ZipParameters().apply {
                    isEncryptFiles = true
                    encryptionMethod = EncryptionMethod.AES
                    fileNameInZip = name
                }
                zip.addFile(source, parameters)
            }
        }
    }

    private companion object {
        const val PASSWORD = "password"
    }
}

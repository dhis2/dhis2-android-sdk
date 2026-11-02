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

package org.hisp.dhis.android.core.util

import com.google.common.truth.Truth.assertThat
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipException
import java.util.zip.ZipOutputStream

@RunWith(JUnit4::class)
class FileUtilsShould {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private lateinit var unzipDirectory: File
    private lateinit var zipFile: File

    @Before
    fun setUp() {
        unzipDirectory = temporaryFolder.newFolder("app", "files")
        zipFile = File(temporaryFolder.root, "import.zip")
    }

    @Test
    fun extract_only_the_requested_entries() {
        writeZip("metadata.json" to "metadata", "database.zip" to "database", "other.txt" to "other")

        FileUtils.unzipFiles(zipFile, unzipDirectory, setOf("metadata.json", "database.zip"))

        assertThat(File(unzipDirectory, "metadata.json").readText()).isEqualTo("metadata")
        assertThat(File(unzipDirectory, "database.zip").readText()).isEqualTo("database")
        assertThat(File(unzipDirectory, "other.txt").exists()).isFalse()
    }

    @Test
    fun ignore_entries_that_point_outside_the_directory() {
        writeZip("../shared_prefs/prefs.xml" to "overwritten", "metadata.json" to "metadata")

        FileUtils.unzipFiles(zipFile, unzipDirectory, setOf("metadata.json"))

        assertThat(File(unzipDirectory, "metadata.json").readText()).isEqualTo("metadata")
        assertThat(File(unzipDirectory.parentFile, "shared_prefs/prefs.xml").exists()).isFalse()
    }

    @Test
    fun reject_a_requested_entry_that_resolves_outside_the_directory() {
        File(unzipDirectory.parentFile, "shared_prefs").mkdirs()
        writeZip("../shared_prefs/prefs.xml" to "overwritten")

        assertThrows(IllegalArgumentException::class.java) {
            FileUtils.unzipFiles(zipFile, unzipDirectory, setOf("../shared_prefs/prefs.xml"))
        }

        assertThat(File(unzipDirectory.parentFile, "shared_prefs/prefs.xml").exists()).isFalse()
    }

    @Test
    fun fail_when_the_file_is_not_a_zip() {
        zipFile.writeText("not a zip")

        assertThrows(ZipException::class.java) {
            FileUtils.unzipFiles(zipFile, unzipDirectory, setOf("metadata.json"))
        }
    }

    @Test
    fun extract_what_zip_files_writes() {
        val metadata = File(temporaryFolder.root, "metadata.json").apply { writeText("metadata") }
        val database = File(temporaryFolder.root, "database.zip").apply { writeText("database") }

        FileUtils.zipFiles(listOf(metadata, database), zipFile)
        FileUtils.unzipFiles(zipFile, unzipDirectory, setOf("metadata.json", "database.zip"))

        assertThat(File(unzipDirectory, "metadata.json").readText()).isEqualTo("metadata")
        assertThat(File(unzipDirectory, "database.zip").readText()).isEqualTo("database")
    }

    private fun writeZip(vararg entries: Pair<String, String>) {
        ZipOutputStream(FileOutputStream(zipFile)).use { zos ->
            entries.forEach { (name, content) ->
                zos.putNextEntry(ZipEntry(name))
                zos.write(content.toByteArray())
                zos.closeEntry()
            }
        }
    }
}

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
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.model.enums.EncryptionMethod
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File

@RunWith(JUnit4::class)
class CipherUtilShould {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private lateinit var workingDirectory: File

    @Before
    fun setUp() {
        workingDirectory = temporaryFolder.newFolder("app", "files")
    }

    @Test
    fun extract_what_create_encrypted_zip_file_writes() {
        val database = File(workingDirectory, "export-database.db").apply { writeText("database") }
        val zipFile = File(workingDirectory, "export-database-protected.db.zip")
        val extracted = File(workingDirectory, "temp.db")

        CipherUtil.createEncryptedZipFile(database, zipFile, PASSWORD)
        CipherUtil.extractEncryptedZipFile(zipFile, extracted, PASSWORD)

        assertThat(extracted.readText()).isEqualTo("database")
    }

    /**
     * The entry is always written with the name of the output file, so a name with parent segments in the archive
     * cannot place it anywhere else.
     */
    @Test
    fun write_to_the_output_file_whatever_the_name_of_the_entry() {
        val zipFile = File(workingDirectory, "export-database-protected.db.zip")
        writeEncryptedZip(zipFile, "../databases/target.db", "overwritten")
        val extracted = File(workingDirectory, "temp.db")

        CipherUtil.extractEncryptedZipFile(zipFile, extracted, PASSWORD)

        assertThat(extracted.readText()).isEqualTo("overwritten")
        assertThat(File(workingDirectory.parentFile, "databases/target.db").exists()).isFalse()
    }

    private fun writeEncryptedZip(zipFile: File, entryName: String, content: String) {
        val source = File(temporaryFolder.root, "source.db").apply { writeText(content) }
        val parameters = ZipParameters().apply {
            isEncryptFiles = true
            encryptionMethod = EncryptionMethod.AES
            fileNameInZip = entryName
        }
        ZipFile(zipFile, PASSWORD.toCharArray()).use { it.addFile(source, parameters) }
    }

    private companion object {
        const val PASSWORD = "password"
    }
}

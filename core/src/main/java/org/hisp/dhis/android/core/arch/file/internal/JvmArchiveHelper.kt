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

import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.model.enums.AesKeyStrength
import net.lingala.zip4j.model.enums.EncryptionMethod
import okio.FileSystem
import okio.IOException
import okio.Path
import okio.Path.Companion.toPath
import okio.openZip
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent
import org.koin.core.annotation.Singleton
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * [ArchiveHelper] for the JVM, Android included. Plain archives are written with `java.util.zip` and read with the zip
 * support of Okio, both through the [FileSystem]. Encrypted archives use zip4j, which only works on `java.io.File`, so
 * those always go through the file system of the platform.
 */
@Singleton
internal class JvmArchiveHelper(
    private val fileSystem: FileSystem,
) : ArchiveHelper {

    override fun zip(files: List<Path>, zipFile: Path) {
        try {
            fileSystem.write(zipFile) {
                ZipOutputStream(outputStream()).use { zos ->
                    files.forEach { file ->
                        zos.putNextEntry(ZipEntry(file.name))
                        fileSystem.read(file) { inputStream().copyTo(zos) }
                        zos.closeEntry()
                    }
                }
            }
        } catch (e: IOException) {
            try {
                fileSystem.delete(zipFile)
            } catch (deleteError: IOException) {
                e.addSuppressed(deleteError)
            }
            throw e
        }
    }

    override fun unzip(zipFile: Path, directory: Path, entryNames: Set<String>) {
        val targetDirectory = directory.normalized()
        val targets = entryNames.associateWith { name ->
            targetDirectory.resolve(name, normalize = true).also { target ->
                require(target.parent == targetDirectory) { "Zip entry $name is not a plain file name" }
            }
        }

        fileSystem.openZip(zipFile).use { zip ->
            targets.forEach { (name, target) -> extractEntry(zip, ZIP_ROOT / name, target) }
        }
    }

    private fun extractEntry(zip: FileSystem, entry: Path, target: Path) {
        if (zip.metadataOrNull(entry)?.isRegularFile == true) {
            zip.source(entry).use { source -> fileSystem.write(target) { writeAll(source) } }
        }
    }

    override fun createEncrypted(file: Path, zipFile: Path, password: String) {
        val parameters = ZipParameters().apply {
            isEncryptFiles = true
            encryptionMethod = EncryptionMethod.AES
            aesKeyStrength = AesKeyStrength.KEY_STRENGTH_256
        }
        ZipFile(zipFile.toFile(), password.toCharArray()).use { it.addFile(file.toFile(), parameters) }
    }

    /**
     * zip4j writes the entry with the name it is given, and checks that the canonical path of the result stays in the
     * target directory, so a name with parent segments in the archive cannot place it anywhere else.
     */
    override fun extractEncrypted(zipFile: Path, target: Path, password: String) {
        val targetDirectory = checkNotNull(target.parent) { "$target has no parent directory" }
        ZipFile(zipFile.toFile(), password.toCharArray()).use { zip ->
            val header = zip.fileHeaders.singleOrNull()
                ?: throw D2Error.builder()
                    .errorComponent(D2ErrorComponent.SDK)
                    .errorDescription("Database zip file must contain a single file")
                    .errorCode(D2ErrorCode.DATABASE_IMPORT_INVALID_FILE)
                    .build()
            zip.extractFile(header, targetDirectory.toString(), target.name)
        }
    }

    private companion object {
        val ZIP_ROOT = "/".toPath()
    }
}

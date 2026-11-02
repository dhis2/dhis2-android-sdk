/*
 *  Copyright (c) 2004-2024, University of Oslo
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

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

@Suppress("NestedBlockDepth")
internal object FileUtils {

    private const val bufferSize = 512

    @Suppress("TooGenericExceptionCaught")
    fun zipFiles(files: List<File>, zipFile: File) {
        val buffer = ByteArray(bufferSize)

        try {
            val fos = FileOutputStream(zipFile.path)
            val zos = ZipOutputStream(fos)

            files.forEach { file ->
                val ze = ZipEntry(file.name)
                zos.putNextEntry(ze)
                val inStream = FileInputStream(file)
                while (true) {
                    val len = inStream.read(buffer)
                    if (len <= 0) break
                    zos.write(buffer, 0, len)
                }

                zos.closeEntry()
                inStream.close()
            }

            zos.close()
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Extract the entries of [zipFile] named in [entryNames] into [unzipDirectory]. Any other entry, and every
     * directory entry, is ignored, so an archive cannot leave unexpected files behind.
     *
     * @throws java.util.zip.ZipException if [zipFile] is not a zip file.
     * @throws IllegalArgumentException if one of [entryNames] resolves outside [unzipDirectory].
     */
    fun unzipFiles(zipFile: File, unzipDirectory: File, entryNames: Set<String>) {
        val directory = unzipDirectory.canonicalFile
        ZipFile(zipFile).use { zip ->
            zip.entries().asSequence()
                .filter { entry -> !entry.isDirectory && entry.name in entryNames }
                .forEach { entry ->
                    val target = File(directory, entry.name).canonicalFile
                    require(target.path.startsWith(directory.path + File.separator)) {
                        "Zip entry ${entry.name} resolves outside ${directory.path}"
                    }
                    zip.getInputStream(entry).use { input -> FileOutputStream(target).use { input.copyTo(it) } }
                }
        }
    }
}

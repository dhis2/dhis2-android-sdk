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
import java.util.zip.ZipInputStream
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

    fun unzipFiles(zipFile: File, unzipDirectory: File) {
        val buffer = ByteArray(bufferSize)

        val zip = ZipFile(zipFile)
        val enum = zip.entries()
        while (enum.hasMoreElements()) {
            val entry = enum.nextElement()
            val entryName = entry.name
            val fis = FileInputStream(zip.name)
            val zis = ZipInputStream(fis)

            while (true) {
                val nextEntry = zis.nextEntry ?: break
                if (nextEntry.name == entryName) {
                    val fout = FileOutputStream(File(unzipDirectory, nextEntry.name))
                    while (true) {
                        val len = zis.read(buffer)
                        if (len <= 0) break
                        fout.write(buffer, 0, len)
                    }

                    zis.closeEntry()
                    fout.close()
                }
            }

            zis.close()
            fis.close()
        }
        zip.close()
    }
}

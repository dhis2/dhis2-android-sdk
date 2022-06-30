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
package org.hisp.dhis.android.core.fileresource.internal

import android.content.Context
import android.util.Log
import java.io.*
import java.net.URLConnection
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.hisp.dhis.android.core.arch.helpers.FileResourceDirectoryHelper
import org.hisp.dhis.android.core.fileresource.FileResource

internal object FileResourceUtil {

    fun getFile(context: Context, fileResource: FileResource): File? {
        val file = File(
            FileResourceDirectoryHelper.getFileResourceDirectory(context),
            generateFileName(MediaType.get(fileResource.contentType()!!), fileResource.uid()!!)
        )
        return if (file.exists()) {
            file
        } else {
            null
        }
    }

    fun renameFile(file: File, newFileName: String, context: Context): File {
        val contentType = URLConnection.guessContentTypeFromName(file.name)
        val generatedName = generateFileName(MediaType.get(contentType), newFileName)
        val newFile = File(
            FileResourceDirectoryHelper.getFileResourceDirectory(context),
            generatedName
        )

        if (!file.renameTo(newFile)) {
            Log.d(FileResourceUtil::class.java.canonicalName, "Fail renaming " + file.name + " to " + generatedName)
        }

        return newFile
    }

    @JvmStatic
    @Throws(IOException::class)
    fun saveFile(sourceFile: File, fileResourceUid: String, context: Context): File {
        val inputStream: InputStream = FileInputStream(sourceFile)
        val contentType = URLConnection.guessContentTypeFromName(sourceFile.name)
        val generatedName = generateFileName(MediaType.get(contentType), fileResourceUid)
        val destinationFile = File(FileResourceDirectoryHelper.getFileResourceDirectory(context), generatedName)

        return writeInputStream(inputStream, destinationFile, sourceFile.length())
    }

    fun saveFileFromResponse(body: ResponseBody, generatedFileName: String, context: Context): File {
        val destinationFile = File(
            FileResourceDirectoryHelper.getFileResourceDirectory(context),
            generateFileName(body.contentType()!!, generatedFileName)
        )
        writeInputStream(body.byteStream(), destinationFile, body.contentLength())
        return destinationFile
    }

    @JvmStatic
    fun writeInputStream(inputStream: InputStream, file: File, fileSize: Long): File {
        var outputStream: OutputStream? = null
        try {
            @Suppress("MagicNumber")
            val fileReader = ByteArray(1024)
            var fileSizeDownloaded: Long = 0
            outputStream = FileOutputStream(file)

            while (true) {
                val read = inputStream.read(fileReader)
                if (read == -1) {
                    break
                }
                outputStream.write(fileReader, 0, read)
                fileSizeDownloaded += read.toLong()
                Log.d(
                    FileResourceUtil::class.java.canonicalName,
                    "file download: $fileSizeDownloaded of $fileSize"
                )
            }
            outputStream.flush()
        } catch (e: IOException) {
            logMessage(e)
        } finally {
            try {
                inputStream.close()
                outputStream?.close()
            } catch (e: IOException) {
                logMessage(e)
            }
        }
        if (outputStream != null) {
            try {
                outputStream.close()
            } catch (e: IOException) {
                logMessage(e)
            }
        }
        return file
    }

    private fun generateFileName(mediaType: MediaType, fileName: String): String {
        return String.format("%s.%s", fileName, mediaType.subtype())
    }

    private fun logMessage(e: Exception) {
        e.message?.let { Log.v(FileResourceUtil::class.java.canonicalName, it) }
    }
}

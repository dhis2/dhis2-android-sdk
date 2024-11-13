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
package org.hisp.dhis.android.core.fileresource.internal

import android.content.Context
import android.util.Log
import org.hisp.dhis.android.core.arch.helpers.FileResizerHelper.DimensionSize
import org.hisp.dhis.android.core.arch.helpers.FileResourceDirectoryHelper
import org.hisp.dhis.android.core.fileresource.FileResource
import java.io.*
import java.net.URLConnection

@Suppress("TooManyFunctions")
internal object FileResourceUtil {

    fun getFile(fileResource: FileResource): File? {
        val file = fileResource.path()?.let { File(it) }
        return if (file != null && file.exists()) {
            file
        } else {
            null
        }
    }

    fun renameFile(file: File, newFileName: String, context: Context): File {
        val generatedName = generateFileName(newFileName, getExtension(file.name))

        val parentPath = file.parentFile?.absolutePath
            ?: FileResourceDirectoryHelper.getFileResourceDirectory(context).absolutePath

        val newFile = File(parentPath, generatedName)

        if (!file.renameTo(newFile)) {
            Log.d(FileResourceUtil::class.java.canonicalName, "Fail renaming " + file.name + " to " + generatedName)
        }

        return newFile
    }

    @JvmStatic
    @Throws(IOException::class)
    fun saveFile(sourceFile: File, fileResourceUid: String, context: Context): File {
        val inputStream: InputStream = FileInputStream(sourceFile)
        val generatedName = generateFileName(fileResourceUid, getExtension(sourceFile.name))
        val destinationFile = File(FileResourceDirectoryHelper.getFileResourceDirectory(context), generatedName)

        return writeInputStream(inputStream, destinationFile, sourceFile.length())
    }

    fun saveFileFromResponse(body: ByteArray, fileResource: FileResource, context: Context): File {
        val destinationFile = File(
            FileResourceDirectoryHelper.getFileResourceDirectory(context),
            generateFileName(fileResource.uid()!!, getExtension(fileResource.name()!!)),
        )
        writeInputStream(ByteArrayInputStream(body), destinationFile, body.size.toLong())
        return destinationFile
    }

    @JvmStatic
    fun writeInputStream(inputStream: InputStream, file: File, fileSize: Long): File {
        var outputStream: OutputStream? = null
        try {
            @Suppress("MagicNumber")
            val fileReader = ByteArray(1024)
            outputStream = FileOutputStream(file)

            while (true) {
                val read = inputStream.read(fileReader)
                if (read == -1) {
                    break
                }
                outputStream.write(fileReader, 0, read)
            }
            outputStream.flush()
        } catch (e: IOException) {
            logMessage(e)
        } finally {
            try {
                inputStream.close()
                outputStream?.close()
                Log.d(
                    FileResourceUtil::class.java.canonicalName,
                    "file saved. Size: $fileSize",
                )
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

    @JvmStatic
    fun getContentTypeFromName(name: String): String {
        return URLConnection.guessContentTypeFromName(name) ?: "application/octet-stream"
    }

    private fun generateFileName(fileName: String, extension: String?): String {
        return if (extension != null) {
            "$fileName.$extension"
        } else {
            fileName
        }
    }

    @JvmStatic
    fun getExtension(fileName: String): String? {
        return fileName.split(".").let { tokens ->
            if (tokens.size > 1) {
                tokens.last()
            } else {
                null
            }
        }
    }

    private fun logMessage(e: Exception) {
        e.message?.let { Log.v(FileResourceUtil::class.java.canonicalName, it) }
    }

    internal fun computeScalingDimension(
        orignalContentLength: Long?,
        maxContentLength: Long?,
        fileIsImage: Boolean,
    ): String {
        return if (fileIsImage) {
            if (orignalContentLength == null || maxContentLength == null) return DimensionSize.Medium.name

            // Create list with dynamically set ORIGINAL size
            val originalSize = DimensionSize.Original(orignalContentLength)
            val sizes = listOf(
                DimensionSize.Small,
                DimensionSize.Medium,
                originalSize,
            )

            // Filter out sizes requiring upscaling
            val validSizes = sizes.filter {
                it.name == DimensionSize.ORIGIANL_NAME || it.maxSizeB < orignalContentLength
            }

            // Step 3: Remove ORIGINAL if it exceeds MEDIUM max size
            val limitedSizes = validSizes.filterNot {
                it.name == originalSize.name && it.maxSizeB > DimensionSize.Medium.maxSizeB
            }

            // Select the largest size within maxContentLength
            limitedSizes.lastOrNull { it.maxSizeB <= maxContentLength }?.name ?: DimensionSize.NotSupported.name
        } else {
            when {
                orignalContentLength == null || maxContentLength == null -> DimensionSize.ORIGIANL_NAME
                orignalContentLength <= maxContentLength -> DimensionSize.ORIGIANL_NAME
                else -> DimensionSize.NotSupported.name
            }
        }
    }
}

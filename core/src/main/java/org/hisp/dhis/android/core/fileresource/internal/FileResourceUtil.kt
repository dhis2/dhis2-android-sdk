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

import io.ktor.http.ContentType
import io.ktor.http.fromFilePath
import org.hisp.dhis.android.core.arch.helpers.FileResizerHelper.DimensionSize

internal object FileResourceUtil {

    // Extensions whose content type in Ktor differs from Android's MimeTypeMap, kept to avoid behaviour changes.
    private val contentTypeOverrides = mapOf(
        "amr" to "audio/amr",
        "bmp" to "image/x-ms-bmp",
        "csv" to "text/comma-separated-values",
        "m4a" to "audio/mpeg",
        "opus" to "audio/ogg",
        "ppt" to "application/vnd.ms-powerpoint",
        "rtf" to "text/rtf",
        "wav" to "audio/x-wav",
        "xls" to "application/vnd.ms-excel",
        "xml" to "text/xml",
    )

    @JvmStatic
    fun getContentTypeFromName(
        name: String,
        defaultType: String = ContentType.Application.OctetStream.toString(),
    ): String {
        return getExtension(name)?.lowercase()?.let { contentTypeOverrides[it] }
            ?: ContentType.fromFilePath(name).firstOrNull()?.toString()
            ?: defaultType
    }

    fun generateFileName(fileName: String, extension: String?): String {
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
                it.name == DimensionSize.ORIGINAL_NAME || it.maxSizeB < orignalContentLength
            }

            // Step 3: Remove ORIGINAL if it exceeds MEDIUM max size
            val limitedSizes = validSizes.filterNot {
                it.name == originalSize.name && it.maxSizeB > DimensionSize.Medium.maxSizeB
            }

            // Select the largest size within maxContentLength
            limitedSizes.lastOrNull { it.maxSizeB <= maxContentLength }?.name ?: DimensionSize.NotSupported.name
        } else {
            when {
                orignalContentLength == null || maxContentLength == null -> DimensionSize.ORIGINAL_NAME
                orignalContentLength <= maxContentLength -> DimensionSize.ORIGINAL_NAME
                else -> DimensionSize.NotSupported.name
            }
        }
    }
}

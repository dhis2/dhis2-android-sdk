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
package org.hisp.dhis.android.core.arch.helpers

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import org.hisp.dhis.android.core.fileresource.internal.FileResourceUtil.getExtension
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent

object FileResizerHelper {

    /**
     * Resize an image file to a given dimension. The possible dimensions are small (256px), medium (512px) and
     * large (1024px). The method will scale the largest between height and width to the given dimension
     * without change the relation between them. In case both height and width are smaller than the given dimension
     * the method will return the given file without modifications.
     *
     * @param fileToResize  Image file to resize.
     * @param dimension     The dimension to resize.
     * @return The resized [File].
     */
    @JvmStatic
    @Throws(D2Error::class)
    fun resizeFile(fileToResize: File, dimension: Dimension): File {
        val bitmap = BitmapFactory.decodeFile(fileToResize.absolutePath)
        val width = bitmap.width.toFloat()
        val height = bitmap.height.toFloat()
        val scaleFactor = width / height
        return if (scaleFactor > 1) {
            if (width < dimension.dimension) {
                fileToResize
            } else resize(
                fileToResize, bitmap,
                dimension.dimension, (dimension.dimension / scaleFactor).toInt(), dimension
            )
        } else {
            if (height < dimension.dimension) {
                fileToResize
            } else resize(
                fileToResize,
                bitmap,
                (scaleFactor * dimension.dimension).toInt(),
                dimension.dimension,
                dimension
            )
        }
    }

    @Throws(D2Error::class)
    @Suppress("MagicNumber")
    private fun resize(fileToResize: File, bitmap: Bitmap, dstWidth: Int, dstHeight: Int, dimension: Dimension): File {
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, false)
        val resizedFile = File(fileToResize.parent, "resized-${dimension.name}-${fileToResize.name}")
        try {
            FileOutputStream(resizedFile).use { fileOutputStream ->
                scaledBitmap.compress(getCompressFormat(resizedFile), 100, fileOutputStream)
                fileOutputStream.flush()
                fileOutputStream.close()
                bitmap.recycle()
                scaledBitmap.recycle()
            }
        } catch (e: IOException) {
            throw buildD2Error(e)
        }
        return resizedFile
    }

    private fun getCompressFormat(file: File): CompressFormat {
        val extension = getExtension(file.name)
        val isJpeg = extension != null && (extension == "jpeg" || extension == "jpg")
        return if (isJpeg) CompressFormat.JPEG else CompressFormat.PNG
    }

    private fun buildD2Error(e: Exception): D2Error {
        return D2Error.builder()
            .errorComponent(D2ErrorComponent.SDK)
            .errorCode(D2ErrorCode.FAIL_RESIZING_IMAGE)
            .errorDescription(e.message)
            .build()
    }

    @Suppress("MagicNumber")
    enum class Dimension(val dimension: Int) {
        SMALL(256),
        MEDIUM(512),
        LARGE(1024)
    }
}

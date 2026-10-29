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
package org.hisp.dhis.android.core.arch.helpers

import okio.Path.Companion.toOkioPath
import org.hisp.dhis.android.core.arch.helpers.internal.ImageProcessorLocator
import org.hisp.dhis.android.core.maintenance.D2Error
import java.io.File

object FileResizerHelper {

    /**
     * Resize an image file to a given dimension. The possible dimensions are small (256px), medium (512px) and
     * large (1024px). The method will scale the largest between height and width to the given dimension
     * without change the relation between them. In case both height and width are smaller than the given dimension
     * the method will return the given file without modifications.
     *
     * The EXIF orientation is baked into the pixels of the resized image, since re-encoding it drops the metadata.
     *
     * @param fileToResize  Image file to resize.
     * @param dimension     The dimension to resize.
     * @return The resized [File].
     */
    @JvmStatic
    @Throws(D2Error::class)
    fun resizeFile(fileToResize: File, dimension: Dimension): File {
        val source = fileToResize.toOkioPath()
        val outputDirectory = ImageProcessorLocator.outputDirectory(fileToResize)
        val resized = ImageProcessorLocator.processor.resize(source, dimension, outputDirectory)
        return if (resized == source) fileToResize else resized.toFile()
    }

    @Suppress("MagicNumber")
    enum class Dimension(val dimension: Int) {
        SMALL(256),
        MEDIUM(512),
        LARGE(1024),
    }

    @Suppress("MagicNumber")
    internal sealed class DimensionSize(val name: String, val maxSizeB: Long) {
        object Small : DimensionSize("SMALL", 400000L)
        object Medium : DimensionSize("MEDIUM", 1600000L)
        object NotSupported : DimensionSize("NOT_SUPPORTED", 0L)
        data class Original(val originalMaxSizeB: Long) : DimensionSize(ORIGINAL_NAME, originalMaxSizeB)

        companion object {
            const val ORIGINAL_NAME = "ORIGINAL"
        }
    }
}

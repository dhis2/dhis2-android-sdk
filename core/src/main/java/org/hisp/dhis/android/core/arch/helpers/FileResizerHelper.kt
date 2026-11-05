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
import org.hisp.dhis.android.core.fileresource.ImageDimension
import org.hisp.dhis.android.core.maintenance.D2Error
import java.io.File

@Deprecated("Use FileResourceModule.resizeImage(), which works on okio.Path.")
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
    @Deprecated(
        "Use FileResourceModule.resizeImage(), which works on okio.Path.",
        ReplaceWith(
            "D2Manager.getD2().fileResourceModule()" +
                ".resizeImage(fileToResize.toOkioPath(), ImageDimension.valueOf(dimension.name)).toFile()",
            "org.hisp.dhis.android.core.D2Manager",
            "okio.Path.Companion.toOkioPath",
            "org.hisp.dhis.android.core.fileresource.ImageDimension",
        ),
    )
    @JvmStatic
    @Throws(D2Error::class)
    fun resizeFile(fileToResize: File, dimension: Dimension): File {
        val source = fileToResize.toOkioPath()
        val outputDirectory = ImageProcessorLocator.outputDirectory(fileToResize)
        val resized = ImageProcessorLocator.processor.resize(source, dimension.toImageDimension(), outputDirectory)
        return if (resized == source) fileToResize else resized.toFile()
    }

    @Deprecated(
        "Use ImageDimension.",
        ReplaceWith("ImageDimension", "org.hisp.dhis.android.core.fileresource.ImageDimension"),
    )
    @Suppress("MagicNumber")
    enum class Dimension(val dimension: Int) {
        SMALL(256),
        MEDIUM(512),
        LARGE(1024),
    }

    private fun Dimension.toImageDimension(): ImageDimension {
        return when (this) {
            Dimension.SMALL -> ImageDimension.SMALL
            Dimension.MEDIUM -> ImageDimension.MEDIUM
            Dimension.LARGE -> ImageDimension.LARGE
        }
    }
}

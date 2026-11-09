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

package org.hisp.dhis.android.core.fileresource.internal

import okio.Path
import org.hisp.dhis.android.core.arch.helpers.internal.ImageProcessor
import org.hisp.dhis.android.core.fileresource.ImageDimension
import org.hisp.dhis.android.core.fileresource.ImageTransformer
import org.koin.core.annotation.Singleton

@Singleton
internal class ImageTransformerImpl(
    private val imageProcessor: ImageProcessor,
    private val fileResourceDirectories: FileResourceDirectoriesImpl,
) : ImageTransformer {

    override fun resize(image: Path, dimension: ImageDimension): Path {
        return imageProcessor.resize(image, dimension, fileResourceDirectories.fileCacheResourceDirectory())
    }

    override fun compress(image: Path): Path {
        return compress(image, IMAGE_TARGET_SIZE_BYTES)
    }

    override fun compress(image: Path, targetSizeBytes: Long): Path {
        return imageProcessor.compress(image, targetSizeBytes, fileResourceDirectories.fileCacheResourceDirectory())
    }

    companion object {
        const val IMAGE_TARGET_SIZE_BYTES = 600 * 1024L // 600 KB
    }
}

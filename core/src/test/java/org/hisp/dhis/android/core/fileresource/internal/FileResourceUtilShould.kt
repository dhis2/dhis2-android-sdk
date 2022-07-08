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

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.fileresource.internal.FileResourceUtil.getContentTypeFromName
import org.hisp.dhis.android.core.fileresource.internal.FileResourceUtil.getExtension
import org.junit.Test

class FileResourceUtilShould {

    @Test
    fun should_guess_content_type_by_name() {
        assertThat(getContentTypeFromName("document.pdf")).isEqualTo("application/pdf")
        assertThat(getContentTypeFromName("image.jpg")).isEqualTo("image/jpeg")
        assertThat(getContentTypeFromName("image.jpeg")).isEqualTo("image/jpeg")
        assertThat(getContentTypeFromName("image.png")).isEqualTo("image/png")

        assertThat(getContentTypeFromName("file.pak")).isEqualTo("application/octet-stream")
    }

    @Test
    fun should_guess_extension_by_name() {
        assertThat(getExtension("document.pdf")).isEqualTo("pdf")
        assertThat(getExtension("image.jpg")).isEqualTo("jpg")
        assertThat(getExtension("image.jpeg")).isEqualTo("jpeg")
        assertThat(getExtension("image.png")).isEqualTo("png")
        assertThat(getExtension("file.pak")).isEqualTo("pak")

        assertThat(getExtension("file.name.pdf")).isEqualTo("pdf")

        assertThat(getExtension("file")).isNull()
    }
}

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
import org.hisp.dhis.android.core.fileresource.internal.FileResourceUtil.computeScalingDimension
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
        assertThat(getContentTypeFromName("image.gif")).isEqualTo("image/gif")
        assertThat(getContentTypeFromName("image.webp")).isEqualTo("image/webp")
        assertThat(getContentTypeFromName("image.heic")).isEqualTo("image/heic")
        assertThat(getContentTypeFromName("text.txt")).isEqualTo("text/plain")
        assertThat(getContentTypeFromName("document.doc")).isEqualTo("application/msword")
        assertThat(getContentTypeFromName("document.docx"))
            .isEqualTo("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
        assertThat(getContentTypeFromName("sheet.xlsx"))
            .isEqualTo("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
        assertThat(getContentTypeFromName("archive.zip")).isEqualTo("application/zip")
        assertThat(getContentTypeFromName("video.mp4")).isEqualTo("video/mp4")
        assertThat(getContentTypeFromName("video.3gp")).isEqualTo("video/3gpp")
        assertThat(getContentTypeFromName("audio.mp3")).isEqualTo("audio/mpeg")

        assertThat(getContentTypeFromName("file.pak")).isEqualTo("application/octet-stream")
        assertThat(getContentTypeFromName("file")).isEqualTo("application/octet-stream")
    }

    @Test
    fun should_guess_content_type_ignoring_case_and_previous_dots() {
        assertThat(getContentTypeFromName("IMAGE.JPG")).isEqualTo("image/jpeg")
        assertThat(getContentTypeFromName("photo.2024.png")).isEqualTo("image/png")
        assertThat(getContentTypeFromName("SHEET.XLS")).isEqualTo("application/vnd.ms-excel")
    }

    @Test
    fun should_keep_android_content_type_where_ktor_differs() {
        assertThat(getContentTypeFromName("audio.amr")).isEqualTo("audio/amr")
        assertThat(getContentTypeFromName("image.bmp")).isEqualTo("image/x-ms-bmp")
        assertThat(getContentTypeFromName("table.csv")).isEqualTo("text/comma-separated-values")
        assertThat(getContentTypeFromName("audio.m4a")).isEqualTo("audio/mpeg")
        assertThat(getContentTypeFromName("audio.opus")).isEqualTo("audio/ogg")
        assertThat(getContentTypeFromName("slides.ppt")).isEqualTo("application/vnd.ms-powerpoint")
        assertThat(getContentTypeFromName("text.rtf")).isEqualTo("text/rtf")
        assertThat(getContentTypeFromName("audio.wav")).isEqualTo("audio/x-wav")
        assertThat(getContentTypeFromName("sheet.xls")).isEqualTo("application/vnd.ms-excel")
        assertThat(getContentTypeFromName("data.xml")).isEqualTo("text/xml")
    }

    @Test
    fun should_use_default_content_type_when_unknown() {
        assertThat(getContentTypeFromName("file.pak", defaultType = "image/*")).isEqualTo("image/*")
        assertThat(getContentTypeFromName("image.png", defaultType = "image/*")).isEqualTo("image/png")
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

    @Test
    fun should_compute_correct_image_dimension() {
        // Original image is smaller than SMALL
        assertThat(computeScalingDimension(300000L, 200000L, true)).isEqualTo("NOT_SUPPORTED")
        assertThat(computeScalingDimension(300000L, 1500000L, true)).isEqualTo("ORIGINAL")

        // Original image is smaller than MEDIUM
        assertThat(computeScalingDimension(1000000L, 300000L, true)).isEqualTo("NOT_SUPPORTED")
        assertThat(computeScalingDimension(1000000L, 500000L, true)).isEqualTo("SMALL")
        assertThat(computeScalingDimension(1000000L, 1200000L, true)).isEqualTo("ORIGINAL")

        // Original image is larger than MEDIUM
        assertThat(computeScalingDimension(2000000L, 300000L, true)).isEqualTo("NOT_SUPPORTED")
        assertThat(computeScalingDimension(2000000L, 500000L, true)).isEqualTo("SMALL")
        assertThat(computeScalingDimension(2000000L, 2000000L, true)).isEqualTo("MEDIUM")

        // null content lengths default to MEDIUM
        assertThat(computeScalingDimension(null, null, true)).isEqualTo("MEDIUM")
        assertThat(computeScalingDimension(300000L, null, true)).isEqualTo("MEDIUM")
        assertThat(computeScalingDimension(null, 300000L, true)).isEqualTo("MEDIUM")
    }

    @Test
    fun should_compute_correct_non_image_dimension() {
        // content length over max content length downaload is NOT supported
        assertThat(computeScalingDimension(10, 5L, false)).isEqualTo("NOT_SUPPORTED")
        // content lenght smaller than max content length is allowed
        assertThat(computeScalingDimension(5, 10L, false)).isEqualTo("ORIGINAL")

        // null content lengths default to ORIGINAL
        assertThat(computeScalingDimension(100, null, false)).isEqualTo("ORIGINAL")
        assertThat(computeScalingDimension(null, 100L, false)).isEqualTo("ORIGINAL")
    }
}

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

package org.hisp.dhis.android.core.fileresource

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.*
import org.hisp.dhis.android.core.fileresource.internal.FileResourceDownloadCall
import org.hisp.dhis.android.core.fileresource.internal.FileResourceDownloadParams
import org.junit.Before
import org.junit.Test

class FileResourceDownloaderShould {

    private val call: FileResourceDownloadCall = mock()

    private val paramsCapture: KArgumentCaptor<FileResourceDownloadParams> = argumentCaptor()

    private lateinit var downloader: FileResourceDownloader

    @Before
    fun setUp() {
        downloader = FileResourceDownloader(call, FileResourceDownloadParams())
    }

    @Test
    fun should_use_default_params() {
        downloader.download()

        verify(call).download(paramsCapture.capture())
        val params = paramsCapture.firstValue

        assertThat(params.domainTypes).isNotEmpty()
        assertThat(params.elementTypes).isNotEmpty()
        assertThat(params.valueTypes).isNotEmpty()
        assertThat(params.maxContentLength).isNotNull()
    }

    @Test
    fun should_override_default_params() {
        downloader
            .byDomainType().eq(FileResourceDomainType.TRACKER)
            .byElementType().eq(FileResourceElementType.DATA_ELEMENT)
            .byValueType().eq(FileResourceValueType.IMAGE)
            .byMaxContentLength().eq(400)
            .download()

        verify(call).download(paramsCapture.capture())
        val params = paramsCapture.firstValue

        assertThat(params.domainTypes).isEqualTo(listOf(FileResourceDomainType.TRACKER))
        assertThat(params.elementTypes).isEqualTo(listOf(FileResourceElementType.DATA_ELEMENT))
        assertThat(params.valueTypes).isEqualTo(listOf(FileResourceValueType.IMAGE))
        assertThat(params.maxContentLength).isEqualTo(400)
    }

    @Test
    fun should_accept_null_max_content_length() {
        downloader
            .byMaxContentLength().eq(null)
            .download()

        verify(call).download(paramsCapture.capture())
        val params = paramsCapture.firstValue

        assertThat(params.maxContentLength).isNull()
    }
}

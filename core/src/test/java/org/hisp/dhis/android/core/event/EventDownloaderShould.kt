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

package org.hisp.dhis.android.core.event

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.*
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.event.internal.EventDownloadCall
import org.hisp.dhis.android.core.program.internal.ProgramDataDownloadParams
import org.junit.Before
import org.junit.Test

class EventDownloaderShould {

    private val call: EventDownloadCall = mock()

    private val paramsCapture: KArgumentCaptor<ProgramDataDownloadParams> = argumentCaptor()

    private lateinit var downloader: EventDownloader

    @Before
    fun setUp() {
        downloader = EventDownloader(RepositoryScope.empty(), call)
    }

    @Test
    fun should_parse_uid_eq_params() {
        downloader.byUid().eq("uid").download()

        verify(call).download(paramsCapture.capture())
        val params = paramsCapture.firstValue

        assertThat(params.uids().size).isEqualTo(1)
        assertThat(params.uids()[0]).isEqualTo("uid")
    }

    @Test
    fun should_parse_uid_in_params() {
        downloader.byUid().`in`("uid0", "uid1", "uid2").download()

        verify(call).download(paramsCapture.capture())
        val params = paramsCapture.firstValue

        assertThat(params.uids().size).isEqualTo(3)
        assertThat(params.uids()[0]).isEqualTo("uid0")
        assertThat(params.uids()[1]).isEqualTo("uid1")
        assertThat(params.uids()[2]).isEqualTo("uid2")
    }
}

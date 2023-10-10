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
package org.hisp.dhis.android.core.program.internal

import com.google.common.truth.Truth
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.same
import com.nhaarman.mockitokotlin2.stub
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Single
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import net.bytebuddy.asm.Advice
import org.hisp.dhis.android.core.arch.api.executors.internal.APIDownloader
import org.hisp.dhis.android.core.arch.api.executors.internal.APIDownloaderImpl
import org.hisp.dhis.android.core.arch.api.fields.internal.Fields
import org.hisp.dhis.android.core.arch.api.filters.internal.Filter
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload
import org.hisp.dhis.android.core.arch.handlers.internal.Handler
import org.hisp.dhis.android.core.common.BaseCallShould
import org.hisp.dhis.android.core.program.Program
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.internal.util.collections.Sets
import kotlin.jvm.functions.Function1

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class ProgramEndpointCallShould : BaseCallShould() {

    private val programService: ProgramService = mock()
    private val programHandler: ProgramHandler = mock()

    @Captor
    private val fieldsCaptor: ArgumentCaptor<Fields<Program>>? = null

    @Captor
    private val filterCaptor: ArgumentCaptor<Filter<Program, String>>? = null

    @Captor
    private val accessDataReadFilter: ArgumentCaptor<String>? = null
    private val apiCall = Single.just(Payload.emptyPayload<Program>())

    private val mockedApiDownloader: APIDownloader = mock()
    private val programCallResult = Single.just(emptyList<Program>())
    private val programUids = Sets.newSet("programUid")

    @Before
    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()

        mockedApiDownloader.stub {
            onBlocking {  }
        }

    }

    @Test
    fun call_api_downloader() = runTest{
        ProgramCall(programService, programHandler, mockedApiDownloader).download(programUids)

       verify(mockedApiDownloader).downloadPartitionedCoroutines(same(programUids), any(),any<Handler::class>(), any())
    }

    @Test
    fun call_service_for_real_api_downloader() = runTest {
        Mockito.`when`(
            programService.getPrograms(
                fieldsCaptor!!.capture(),
                filterCaptor!!.capture(),
                accessDataReadFilter!!.capture(),
                ArgumentMatchers.anyBoolean()
            )
        ).thenReturn(apiCall)
        ProgramCall(programService, programHandler, APIDownloaderImpl(resourceHandler)).download(
            programUids
        )
        Truth.assertThat(fieldsCaptor.value).isEqualTo(ProgramFields.allFields)
        Truth.assertThat(filterCaptor.value.values().iterator().next()).isEqualTo("programUid")
        Truth.assertThat(accessDataReadFilter.value).isEqualTo("access.data.read:eq:true")
    }
}